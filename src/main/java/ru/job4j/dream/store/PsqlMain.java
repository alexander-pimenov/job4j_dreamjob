package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        // Post
        // save()-create and findAllPosts()
        PsqlStore store = (PsqlStore) PsqlStore.instOf();
        store.savePost(new Post(0, "Junior Java Job", "Description for Junior"));
        store.savePost(new Post(0, "Middle Java Job", "Description for Middle"));

        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName() + " " + post.getDescription());
        }
        System.out.println("==========");

        // save()-update
        Post post = new Post(2, "Senior Java Job", "Advanced level");
        store.savePost(post);

        store.findAllPosts().forEach(System.out::println);
        System.out.println("==========");

        // findById()
        final Post postById = store.findPostById(6);
        System.out.println(postById);
        System.out.println("==========");

        // Candidates
        // saveCandidate()-create and findAllCandidates()
        store.saveCandidate(new Candidate(0, "Middle Java"));
        store.saveCandidate(new Candidate(0, "Senior Java"));

        store.findAllCandidates().forEach(System.out::println);
        System.out.println("==========");

        // saveCandidate()-update
        Candidate candidate = new Candidate(20, "Intern Java");
        store.saveCandidate(candidate);

        store.findAllCandidates().forEach(System.out::println);
        System.out.println("==========");

        // findCandidateById()
        final Candidate candidateById = store.findCandidateById(1);
        System.out.println(candidateById);
    }
}

