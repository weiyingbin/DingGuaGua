package com.ebook.model;

public class Shelf {
    private Integer id;
    private String name;//书名
    private String localPath;//本地路径
    private String image;//图片
    private String createTime;//创建时间
    private String htmlPath;//html路径

    public String getHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", localPath='" + localPath + '\'' +
                ", image='" + image + '\'' +
                ", createTime='" + createTime + '\'' +
                ", htmlPath='" + htmlPath + '\'' +
                '}';
    }
}
