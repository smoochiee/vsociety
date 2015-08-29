package io.b1ackr0se.vsociety.model;

public class Forum {

    private String name;
    private String url;

    public Forum(String n, String u) {
        name = n;
        url = u;
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
