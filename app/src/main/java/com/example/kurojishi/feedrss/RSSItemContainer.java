package com.example.kurojishi.feedrss;

import org.mcsoxford.rss.RSSItem;

/**
 * Created by kurojishi on 6/15/15.
 */
public class RSSItemContainer implements Comparable<RSSItemContainer> {
    private RSSItem rssItem;
    private String Author;
    private Boolean isRead;

    public RSSItemContainer(String author, Boolean isRead, RSSItem item) {
        Author = author;
        this.isRead = isRead;
        this.rssItem = item;
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

    public RSSItem getRssItem() {
        return rssItem;
    }

    public void setRssItem(RSSItem item) {
        this.rssItem = item;
    }

    @Override
    public int compareTo(RSSItemContainer article) {
        if (rssItem.getPubDate() != null) {
            return rssItem.getPubDate().compareTo(article.getRssItem().getPubDate());
        } else {
            return 0;
        }
    }
}
