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

    /*Метод поиска всех вакансий в хранилище.*/
    @Override
    public Collection<Post> findAllPosts() {
        return this.posts.values();
    }

    /*Метод поиска всех кандидатов в хранилище.*/
    @Override
    public Collection<Candidate> findAllCandidates() {
        return this.candidates.values();
    }

    /*Метод добавления в хранилище вакансий.*/
    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.incrementAndGet());
        }
//        System.out.println("Вакансия: " + post);
//        System.out.println("Вакансия Id: " + post.getId());
//        System.out.println("Вакансия Name: " + post.getName());
//        System.out.println("Вакансия Description: " + post.getDescription());

        posts.put(post.getId(), post);

    }

    /*Поиск вакансии по Id*/
    @Override
    public Post findPostById(int id) {
        return posts.get(id);
    }

    /*Метод добавления в хранилище кандидатов.*/
    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(candidateId.incrementAndGet());
        }
//        System.out.println("Кандидат: " + candidate);
//        System.out.println("Кандидат Id: " + candidate.getId());
//        System.out.println("Кандидат Name: " + candidate.getName());

        candidates.put(candidate.getId(), candidate);
    }

    /*Поиск кандидата по Id*/
    @Override
    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }
}
