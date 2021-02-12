package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Класс Store - хранилище вакансий.
 */
public class Store {
    private static final Store INST = new Store();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    //ключ для генерации ID
    private static AtomicInteger POST_ID = new AtomicInteger(4);
    private static AtomicInteger CANDIDATE_ID = new AtomicInteger(4);

    private Store() {
        posts.put(1, new Post(1, "Junior Java Job", "Some description"));
        posts.put(2, new Post(2, "Middle Java Job", "Some description"));
        posts.put(3, new Post(3, "Senior Java Job", "Some description"));
        candidates.put(1, new Candidate(1, "Junior Java"));
        candidates.put(2, new Candidate(2, "Middle Java"));
        candidates.put(3, new Candidate(3, "Senior Java"));
    }

    public static Store instOf() {
        return INST;
    }

    /*Метод поиска всех вакансий в хранилище.*/
    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    /*Метод поиска всех кандидатов в хранилище.*/
    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    /*Метод добавления в хранилище вакансий.*/
    public void save(Post post) {
        if (post.getId() == 0) {
            post.setId(POST_ID.incrementAndGet());
        }
        posts.put(post.getId(), post);
    }

    /*Поиск вакансии по Id*/
    public Post findById(int id) {
        return posts.get(id);
    }

    /*Метод добавления в хранилище кандидатов.*/
    public void saveCandidate(Candidate candidate) {
//        if (candidate.getId() == 0) {
            candidate.setId(CANDIDATE_ID.incrementAndGet());
//        }
        candidates.put(candidate.getId(), candidate);
    }

    /*Поиск кандидата по Id*/
    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }
}
