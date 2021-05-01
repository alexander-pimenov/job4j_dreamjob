package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Класс для взаимодействия с базой данных.
 */
public class PsqlStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    /**
     * Инициализирует созданный PsqlStore объект, вычитывая данные из файла db.properties
     * для подключения к базе данных.
     * Активирует пул соединений, чтобы не открывать новое соединение при каждом запросе.
     * Пул инициализируется при первом вызове одного из следующих методов:
     * getConnection, setLogwriter,setLoginTimeout, getLoginTimeout, getLogWriter.
     */
    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader(PsqlStore.class.getResource("/db.properties").getFile())
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }


    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    /**
     * Метод поиска всех вакансий в базе данных.
     */
    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(
                            new Post(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("description")
                            ));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return posts;
    }

    /**
     * Метод поиска всех кандидатов в базе данных.
     */
    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate")
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(new Candidate(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("photo_id"))
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return candidates;
    }

    /**
     * Метод сохранение новой вакансии или ее обновление в базе данных.
     */
    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            createPost(post);
        } else {
            updatePost(post);
        }
    }

    /**
     * Метод создание вакансии.
     */
    private Post createPost(Post post) {
        String sql = "INSERT INTO post(name, description) VALUES (?, ?)";
        try (Connection cn = pool.getConnection(); //соединяемся с пулом
             PreparedStatement ps = cn.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName()); //заполняем знак 1-й ?
            ps.setString(2, post.getDescription()); //заполняем знак 2-й ?
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    //Записываем сгенерированный базой данных id в объект Post
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return post;
    }

    /**
     * Метод обновления вакансии в базе данных.
     */
    private Post updatePost(Post post) {
        String sql = "UPDATE post SET name = ?, description = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setInt(3, post.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    /**
     * Метод добавления в базу данных кандидата.
     */
    @Override
    public Candidate saveCandidate(Candidate candidate) {
        Candidate can = new Candidate();
        if (candidate.getId() == 0) {
            can = createCandidate(candidate);
        } else {
            can = updateCandidate(candidate);
        }
        return can;
    }

    /**
     * Метод создания кандидата и записи его в базу данных.
     */
    private Candidate createCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidate(name, photo_id) VALUES (?,?)";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    candidate.setId(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return candidate;
    }

    /**
     * Метод обновления кандидата в базе данных.
     */
    private Candidate updateCandidate(Candidate candidate) {
        String sql = "UPDATE candidate SET name = ?, photo_id = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.setInt(3, candidate.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return candidate;
    }

    /**
     * Поиск вакансии в базе данных по Id вакансии.
     */
    @Override
    public Post findPostById(int id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        Post post = new Post(0, "", "");
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    //Сохраняем найденную вакансию в объект Post
                    post = new Post(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    /**
     * Поиск кандидата в базе данных по Id кандидата.
     */
    @Override
    public Candidate findCandidateById(int id) {
        String sql = "SELECT * FROM candidate WHERE id = ?";
        Candidate candidate = new Candidate(0, "", 0);
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    //Сохраняем найденного кандидата в объект Candidate
                    candidate = new Candidate(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("photo_id")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return candidate;
    }

    /**
     * Метод удаления кандидата из базы данных по его Id.
     * В методе также производится удаление фото кандидата
     * из папки, где хранятся фото кандидатов.
     */
    @Override
    public void deleteCandidate(int id) {
        String delete = "DELETE FROM candidate WHERE id = ?";
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement(delete);
            ps.setInt(1, id);
            ps.execute();

            //Удаляем фото из папки-хранилища
            for (File file : Objects.requireNonNull(new File("C:\\images\\").listFiles())) {
                if (Integer.toString(id).equals(FilenameUtils.getBaseName(file.getName()))) {
                    file.delete();
                    break;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Метод сохранения id фото кандидата в колонку photo_id
     * в таблицу кандидатов в базе данных.
     */
    @Override
    public void savePhoto(int photoId) {
        String sql = "UPDATE candidate SET photo_id = ? " + "WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, photoId);
            ps.setInt(2, photoId);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}