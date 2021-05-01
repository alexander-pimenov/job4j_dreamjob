package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс Store - хранилище вакансий.
 */
public class MemStore implements Store {
    private static final MemStore INST = new MemStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    //ключ для генерации ID
    private static AtomicInteger postId = new AtomicInteger(4);
    private static AtomicInteger candidateId = new AtomicInteger(4);

    private MemStore() {
        posts.put(1, new Post(1, "Junior Java Job", "Some description about Junior Java"));
        posts.put(2, new Post(2, "Middle Java Job", "Some description about Middle Java"));
        posts.put(3, new Post(3, "Senior Java Job", "Some description about Senior Java"));
        candidates.put(1, new Candidate(1, "Junior Java"));
        candidates.put(2, new Candidate(2, "Middle Java"));
        candidates.put(3, new Candidate(3, "Senior Java"));
    }

    public static MemStore instOf() {
        return INST;
    }

    /**
     * Метод поиска всех вакансий в хранилище.
     */
    @Override
    public Collection<Post> findAllPosts() {
        return this.posts.values();
    }

    /**
     * Метод поиска всех кандидатов в хранилище.
     */
    @Override
    public Collection<Candidate> findAllCandidates() {
        return this.candidates.values();
    }

    /**
     * Метод добавления в хранилище вакансий.
     */
    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.incrementAndGet());
        }
        posts.put(post.getId(), post);
    }

    /**
     * Метод поиска вакансии по Id.
     */
    @Override
    public Post findPostById(int id) {
        return posts.get(id);
    }

    /**
     * Метод добавления в хранилище кандидатов.
     */
    @Override
    public Candidate saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(candidateId.incrementAndGet());
        }
        candidates.put(candidate.getId(), candidate);
        return candidates.get(candidate.getId());
    }

    /**
     * Метод поиска кандидата по Id
     */
    @Override
    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }

    /**
     * Метод удаления кандидата по его id.
     *
     * @param id id кандидата.
     */
    @Override
    public void deleteCandidate(int id) {
        candidates.remove(id);
    }

    /**
     * Метод сохранения фото кандидата.
     * (Не реализован)
     *
     * @param photoId id фото кандидата.
     */
    @Override
    public void savePhoto(int photoId) {
        System.out.println("Метод не реализован");
    }
}
