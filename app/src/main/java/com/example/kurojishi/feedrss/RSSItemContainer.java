package com.example.kurojishi.feedrss;

import android.net.Uri;

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
    private Uri link;

    public RSSItemContainer(String author, Boolean isRead, RSSItem item) {
        Author = author;
        this.isRead = isRead;
        pubDate = item.getPubDate();
        title = item.getTitle();
        description = item.getDescription();
        content = item.getContent();
        link = item.getLink();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getLink() {
        return link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
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
