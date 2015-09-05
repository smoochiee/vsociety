package io.b1ackr0se.vsociety.model;

/**
 * Model for a typical user on the forum.
 *
 * @author b1acKr0se
 */
public class User {
    private String name;
    private String url;
    private String avatar_url;
    private String status_img;
    private String join_date;
    private String number_of_posts;

    public User() {
        //empty constructor
    }

    public User(String n, String u, String a) {
        name = n;
        url = u;
        avatar_url = a;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
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

    public String getStatusImg() {
        return status_img;
    }

    public void setStatusImg(String status_img) {
        this.status_img = status_img;
    }

    public String getJoinDate() {
        return join_date;
    }

    public void setJoinDate(String join_date) {
        this.join_date = join_date;
    }

    public String getNumberOfPosts() {
        return number_of_posts;
    }

    public void setNumberOfPosts(String number_of_posts) {
        this.number_of_posts = number_of_posts;
    }
}
