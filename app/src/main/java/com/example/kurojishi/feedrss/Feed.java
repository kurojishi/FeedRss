package com.example.kurojishi.feedrss;

import java.net.URL;

/**
 * Created by kurojishi on 6/16/15.
 */
public class Feed {
    private String title;
    private URL url;
    private Integer id;

    public Feed(Integer id, String title, URL url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
