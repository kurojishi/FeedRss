package com.example.kurojishi.feedrss;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FetchRssService extends Service {
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes TODO:make this configurable
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    Runnable Task = new Runnable() {
        @Override
        public void run() {
            fetchFeeds();
            mServiceHandler.postDelayed(Task, INTERVAL);
        }
    };

    public FetchRssService() {
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Task.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceHandler.removeCallbacks(Task);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchNow() {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                fetchFeeds();
            }
        });
    }

    private void fetchFeeds() {
        FeedDB feedDatabase = new FeedDB(this);
        String[] feedsProjection = {FeedDB.FeedEntry.COLUMN_NAME_URL, FeedDB.FeedEntry._ID, FeedDB.FeedEntry.COLUMN_NAME_TITLE};

        SQLiteDatabase db = feedDatabase.getWritableDatabase();
        Cursor c;
        c = db.query(FeedDB.FeedEntry.TABLE_NAME, feedsProjection, null, null, null, null, null);
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
        Collections.sort(articles);
        Collections.reverse(articles);
        String[] projection = {
                FeedDB.ArticleEntry.COLUMN_NAME_READ,
                FeedDB.ArticleEntry._ID
        };
        for (RSSItemContainer article : articles) {
            String selection = FeedDB.ArticleEntry.COLUMN_NAME_URL + " = '" + article.getLink() + "'";
            Cursor cursor = db.query(FeedDB.ArticleEntry.TABLE_NAME, projection, selection, null, null, null, null);
            if (cursor.getCount() <= 0) {
                ContentValues values = new ContentValues();
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_AUTHOR, article.getAuthor());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_CONTENT, article.getContent());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_TITLE, article.getTitle());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_DESCRIPTION, article.getDescription());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_URL, article.getLink());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_PUBDATE, article.getPubDate().toString());
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, 0);
                values.put(FeedDB.ArticleEntry.COLUMN_NAME_FEED_ID, article.getFeedId());
                db.insert(FeedDB.ArticleEntry.TABLE_NAME, null, values);
            }
            c.close();

        }
        db.close();

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        private int interval;

        public ServiceHandler(Looper looper) {
            super(looper);
        }
    }
}
