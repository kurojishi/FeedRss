package com.example.kurojishi.feedrss;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kurojishi on 6/11/15.
 */
public class RssFetcher extends AsyncTask<List<URL>, Void, List<RSSItemContainer>> {

    private Context context;
    private RssListFragment rssListFragment;
    private ProgressDialog progressDialog;

    public RssFetcher(RssListFragment activity) {
        context = activity.getActivity();
        rssListFragment = activity;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
    }

    @Override
    protected List<RSSItemContainer> doInBackground(List<URL>... urls) {
        List<RSSItemContainer> articles = new ArrayList<>();

        for (Iterator<URL> iter = urls[0].listIterator(); iter.hasNext(); ) {
            URL url = iter.next();
            RSSReader reader = new RSSReader();
            try {
                RSSFeed feed = reader.load(url.toString());
                for (RSSItem item : feed.getItems()) {
                    articles.add(new RSSItemContainer(feed.getTitle(), false, item));
                }

            } catch (RSSReaderException e) {
                Log.d("Failed to load feed", "chiappomanzia");
            }
        }
        Collections.sort(articles);
        return articles;
    }

    @Override
    protected void onPostExecute(List<RSSItemContainer> articles) {
        /*FeedDB helper = new FeedDB(context);
        SQLiteDatabase rdb = helper.getReadableDatabase();
        SQLiteDatabase wdb = helper.getWritableDatabase();
        String[] projection = {
                FeedDB.ArticleEntry.COLUMN_NAME_READ
        };
        for (RSSItemContainer article: articles) {
            String selection = FeedDB.ArticleEntry.COLUMN_NAME_URL + " = '" + article.getRssItem().getLink().toString() + "'";
            Cursor c = rdb.query(FeedDB.ArticleEntry.TABLE_NAME,projection,selection, null, null, null, null);
            if (c.getCount() <=0){
                ContentValues values = new ContentValues();
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_AUTHOR, article.getAuthor());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_CONTENT, article.getRssItem().getContent());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_TITLE, article.getRssItem().getTitle());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_DESCRIPTION, article.getRssItem().getDescription());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_URL, article.getRssItem().getLink().toString());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_PUBDATE, article.getRssItem().getPubDate().toString());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, article.getIsRead());
            } else {
                c.moveToFirst();
                article.setIsRead(c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_READ))>0);
            }
            c.close();

        }
        rdb.close();
        wdb.close();*/
        RssListAdapter adapter = new RssListAdapter(context, articles);
        rssListFragment.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        rssListFragment.setArticles(articles);
    }


}