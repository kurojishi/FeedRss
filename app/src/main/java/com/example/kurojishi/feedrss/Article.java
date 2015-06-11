/*Copyright (c) 2013 NerdAbility Ltd.


Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY,WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
https://github.com/nerdability/AndroidRssReader/blob/master/src/com/nerdability/android/rss/domain/Article.java
*/

package com.example.kurojishi.feedrss;

import android.util.Log;

import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Article implements Serializable, Comparable<Article> {

    public static final String KEY = "ARTICLE";

    private static final long serialVersionUID = 1L;
    private String guid;
    private String title;
    private String description;
    private String pubDate;
    private String author;
    private URL url;
    private String encodedContent;
    private boolean read;
    private boolean offline;
    private long dbId;


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = extractCData(description);
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    public void setEncodedContent(String encodedContent) {
        this.encodedContent = extractCData(encodedContent);
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    private String extractCData(String data) {
        data = data.replaceAll("<!\\[CDATA\\[", "");
        data = data.replaceAll("\\]\\]>", "");
        return data;
    }

    @Override
    public int compareTo(Article article) {
        try {
            Date current = DateFormat.getDateInstance().parse(pubDate);
            Date compDate = DateFormat.getDateInstance().parse(article.getPubDate());

            return current.compareTo(compDate);

        } catch (ParseException e) {
            Log.e("Parsing Date Error", e.getMessage());
        }
        return 0;
    }
}
