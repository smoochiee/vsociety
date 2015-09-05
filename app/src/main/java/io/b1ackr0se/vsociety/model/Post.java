package io.b1ackr0se.vsociety.model;

/**
 * Model for every post in a thread.
 *
 * @author b1acKr0se
 */
public class Post {
    private User user;
    private String url;
    private String date;
    private String post_number;
    private String content;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPostNumber(String post_number) {
        this.post_number = post_number;
    }

    public String getPostNumber() {
        return post_number;
    }
}

