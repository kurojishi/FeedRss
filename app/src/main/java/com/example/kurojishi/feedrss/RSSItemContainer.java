package com.example.kurojishi.feedrss;

import org.mcsoxford.rss.RSSItem;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kurojishi on 6/15/15.
 */
public class RSSItemContainer implements Serializable, Comparable<RSSItemContainer> {
    private static final long serialVersionUID = 0L;

    private String Author;
    private Boolean isRead;
    private Date pubDate;
    private String title;
    private String description;
    private String content;
    private String link;

    public RSSItemContainer(String author, Boolean isRead, RSSItem item) {
        Author = author;
        this.isRead = isRead;
        pubDate = item.getPubDate();
        title = item.getTitle();
        description = item.getDescription();
        content = item.getContent();
        link = item.getLink().toString();
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return Author;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }


    @Override
    public int compareTo(RSSItemContainer article) {
        if (pubDate != null) {
            return pubDate.compareTo(article.getPubDate());
        } else {
            return 0;
        }
    }
}
