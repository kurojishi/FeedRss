package com.example.kurojishi.feedrss;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by kurojishi on 6/11/15.
 */
public class RssFetcher extends AsyncTask<String, Void, List<RSSItemContainer>> {

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
    protected List<RSSItemContainer> doInBackground(String... title) {
        List<RSSItemContainer> articles;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (isNetworkAvailable() && !preference.getBoolean("offline_checkbox", false)) {
            articles = fetchFromInternet(title[0]);
        } else {
            articles = fetchFromDb(title[0]);
        }
        Collections.sort(articles);
        Collections.reverse(articles);
        return articles;
    }

    @Override
    protected void onPostExecute(List<RSSItemContainer> articles) {
        FeedDB helper = new FeedDB(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] projection = {
                FeedDB.ArticleEntry.COLUMN_NAME_READ,
                FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE,
                FeedDB.ArticleEntry._ID
        };
        for (RSSItemContainer article: articles) {
            String selection = FeedDB.ArticleEntry.COLUMN_NAME_URL + " = '" + article.getLink() + "'";
            Cursor c = db.query(FeedDB.ArticleEntry.TABLE_NAME, projection, selection, null, null, null, null);
            if (c.getCount() <=0){
                ContentValues values = new ContentValues();
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_AUTHOR, article.getAuthor());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_CONTENT, article.getContent());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_TITLE, article.getTitle());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_DESCRIPTION, article.getDescription());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_URL, article.getLink());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_PUBDATE, article.getPubDate().toString());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, 0);
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_FEED_ID, article.getFeedId());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE, 0);
                db.insert(FeedDB.ArticleEntry.TABLE_NAME, null, values);
            } else {
                c.moveToFirst();
                article.setIsRead(c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_READ)) > 0);
                article.setFavourite(c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE)) > 0);
                article.setDbId(c.getInt(c.getColumnIndex(FeedDB.ArticleEntry._ID)));
            }
            c.close();

        }
        db.close();
        RssListAdapter adapter = new RssListAdapter(context, articles);
        rssListFragment.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
        rssListFragment.setArticles(articles);
    }

    private List<RSSItemContainer> fetchFromInternet(String title) {
        FeedDB feedDatabase = new FeedDB(context);
        String[] feedsProjection = {FeedDB.FeedEntry.COLUMN_NAME_URL, FeedDB.FeedEntry._ID, FeedDB.FeedEntry.COLUMN_NAME_TITLE};

        SQLiteDatabase db = feedDatabase.getReadableDatabase();
        Cursor c;
        if (title != null) {
            String selectionF = FeedDB.FeedEntry.COLUMN_NAME_TITLE + " = '" + title + "'";
            c = db.query(FeedDB.FeedEntry.TABLE_NAME, feedsProjection, selectionF, null, null, null, null);
        } else {
            c = db.query(FeedDB.FeedEntry.TABLE_NAME, feedsProjection, null, null, null, null, null);
        }
        List<Feed> urls = new ArrayList<>();
        Log.d("Feed Count", Integer.toString(c.getCount()));
        for (int i = 0; i <= c.getCount() - 1; i++) {
            c.moveToPosition(i);
            String url = c.getString(c.getColumnIndex(FeedDB.FeedEntry.COLUMN_NAME_URL));
            try {
                urls.add(new Feed(c.getInt(c.getColumnIndex(FeedDB.FeedEntry._ID)), c.getString(c.getColumnIndex(FeedDB.FeedEntry.COLUMN_NAME_TITLE)), new URL(url)));
            } catch (MalformedURLException e) {
                Log.e("Malformed URL", e.getMessage());
                String selection = FeedDB.FeedEntry._ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(c.getString(c.getColumnIndex(FeedDB.FeedEntry._ID)))};
                db.delete(FeedDB.FeedEntry.TABLE_NAME, selection, selectionArgs);
            }
        }
        c.close();
        List<RSSItemContainer> articles = new ArrayList<>();

        for (Iterator<Feed> iter = urls.listIterator(); iter.hasNext(); ) {
            Feed feedContainer = iter.next();
            RSSReader reader = new RSSReader();
            try {
                RSSFeed feed = reader.load(feedContainer.getUrl().toString());
                for (RSSItem item : feed.getItems()) {
                    articles.add(new RSSItemContainer(feed.getTitle(), false, item, feedContainer.getId()));
                }

            } catch (RSSReaderException e) {
                Log.d("Failed to load feed", "");
            }
        }
        return articles;
    }

    private List<RSSItemContainer> fetchFromDb(String title) {
        List<RSSItemContainer> articles = new ArrayList<>();
        FeedDB feedDatabase = new FeedDB(context);
        String[] feedsProjection = {FeedDB.FeedEntry._ID};

        SQLiteDatabase db = feedDatabase.getReadableDatabase();
        Cursor c;
        if (title != null) {
            String selectionF = FeedDB.FeedEntry.COLUMN_NAME_TITLE + " = '" + title + "'";
            c = db.query(FeedDB.FeedEntry.TABLE_NAME, feedsProjection, selectionF, null, null, null, null);
            Integer id = c.getInt(c.getColumnIndex(FeedDB.FeedEntry._ID));
            c.close();
            String selection = FeedDB.ArticleEntry.COLUMN_NAME_FEED_ID + " = " + id.toString();
            c = db.query(FeedDB.ArticleEntry.TABLE_NAME, null, selection, null, null, null, null);
        } else {
            c = db.query(FeedDB.ArticleEntry.TABLE_NAME, null, null, null, null, null, null);
        }
        List<Feed> urls = new ArrayList<>();
        Log.d("Feed Count", Integer.toString(c.getCount()));
        for (int i = 0; i <= c.getCount() - 1; i++) {
            c.moveToPosition(i);
            DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.getDefault());
            Date pubDate;
            try {
                pubDate = formatter.parse(c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_PUBDATE)));
            } catch (ParseException e) {
                pubDate = null;
            }
            articles.add(new RSSItemContainer(
                    c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_AUTHOR)),
                    c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_CONTENT)),
                    c.getInt(c.getColumnIndex(FeedDB.ArticleEntry._ID)),
                    c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_DESCRIPTION)),
                    c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_FEED_ID)),
                    c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_READ)) > 0,
                    c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_URL)),
                    pubDate,
                    c.getString(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_TITLE)),
                    c.getInt(c.getColumnIndex(FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE)) > 0));
        }
        c.close();
        db.close();
        return articles;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}