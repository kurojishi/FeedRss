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
    private Integer feedId;
    private Integer dbId;
    private Boolean favourite;

    public RSSItemContainer(String author, String content, Integer dbId, String description, Integer feedId, Boolean isRead, String link, Date pubDate, String title, Boolean favourite) {
        Author = author;
        this.content = content;
        this.dbId = dbId;
        this.description = description;
        this.feedId = feedId;
        this.isRead = isRead;
        this.link = link;
        this.pubDate = pubDate;
        this.title = title;
        this.favourite = favourite;
    }

    public RSSItemContainer(String author, Boolean isRead, RSSItem item, Integer id) {
        Author = author;
        this.isRead = isRead;
        pubDate = item.getPubDate();
        title = item.getTitle();
        description = item.getDescription();
        content = item.getContent();
        link = item.getLink().toString();
        feedId = id;
        favourite = false;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public Integer getFeedId() {
        return feedId;
    }

    public void setFeedId(Integer feedId) {
        this.feedId = feedId;
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
