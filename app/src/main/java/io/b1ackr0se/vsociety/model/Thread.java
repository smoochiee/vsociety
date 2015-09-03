package io.b1ackr0se.vsociety.model;

public class Thread {
    private String name;
    private String url;
    private String starter;
    private String number_of_replies;
    private String latest_reply;
    private int number_of_pages;
    private boolean sticky = false;

    public Thread() {

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

    public int getNoOfPages() {
        return number_of_pages;
    }

    public void setNoOfPages(int number_of_pages) {
        this.number_of_pages = number_of_pages;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    public String getLatestReply() {
        return latest_reply;
    }

    public void setLatestReply(String latest_reply) {
        this.latest_reply = latest_reply;
    }

    public String getNoOfReplies() {
        return number_of_replies;
    }

    public void setNoOfReplies(String number_of_replies) {
        this.number_of_replies = number_of_replies;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean value) {
        sticky = value;
    }
}
