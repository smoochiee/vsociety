package io.b1ackr0se.vsociety.model;

public class User {
    private String name;
    private String url;
    private String avatar_url;

    public User() {
        //empty constructor
    }

    public User(String n, String u, String a) {
        name = n;
        url = u;
        avatar_url = a;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
