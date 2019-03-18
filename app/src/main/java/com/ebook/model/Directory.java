package com.ebook.model;

public class Directory {

    public static final Integer FILE = 1;
    public static final Integer DIRECTORY = 2;

    private String name;
    private String path;
    private Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                '}';
    }
}
