package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

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
        /*Активируем пул*/
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);

        /*Примечание: этот метод в настоящее время не действует после
        инициализации пула. Пул инициализируется при первом вызове
        одного из следующих методов: getConnection, setLogwriter,
        setLoginTimeout, getLoginTimeout, getLogWriter.*/
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection(); //соединяемся с пулом
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
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return posts;
    }

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
                            rs.getString("name"))
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return candidates;
    }

    /*Сохранение новой вакансии или ее обновление*/
    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            createPost(post);
        } else {
            updatePost(post);
        }
    }

    /*Создание вакансии*/
    private Post createPost(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO post(name, description) VALUES (?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName()); //заполняем знак 1-й ?
            ps.setString(2, post.getDescription()); //заполняем знак 2-й ?
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return post;
    }

    /*Обновление вакансии*/
    private Post updatePost(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE post SET name = ?, description = ? WHERE id = ?")
        ) {
            ps.setString(1, post.getName()); //заполняем 1-й знак ?
            ps.setString(2, post.getDescription()); //заполняем 2-й знак ?
            ps.setInt(3, post.getId()); //заполняем 3-й знак ?
            ps.executeUpdate();


        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return post;
    }

    /*Метод добавления в хранилище кандидатов.*/
    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            createCandidate(candidate);
        } else {
            updateCandidate(candidate);
        }
    }


    private Candidate createCandidate(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO candidate(name) VALUES (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName()); //заполняем 1-й знак ?
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    candidate.setId(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return candidate;
    }

    private Candidate updateCandidate(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE candidate SET name = ? WHERE id = ?")
        ) {
            ps.setString(1, candidate.getName()); //заполняем 1-й знак ?
            ps.setInt(2, candidate.getId()); //заполняем 2-й знак ?
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return candidate;
    }

    /*Поиск вакансии по Id*/
    @Override
    public Post findPostById(int id) {
        Post post = new Post(0, "", "");
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post = new Post(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return post;
    }

    /*Поиск кандидата по Id*/
    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = new Candidate(0, "");
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    candidate = new Candidate(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e); //удобно искать ошибку в логе
        }
        return candidate;
    }
}

//try (InputStream io = PsqlStore.class.getClassLoader().getResourceAsStream("db.properties")) {
// cfg.load(io);