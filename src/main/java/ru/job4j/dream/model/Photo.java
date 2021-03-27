package ru.job4j.dream.model;

public class Photo {
    private int id;
    private String name;
    private String path;

    public Photo() {
    }

    public Photo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Photo(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
