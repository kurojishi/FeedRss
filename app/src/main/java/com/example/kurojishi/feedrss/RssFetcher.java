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
        RssListAdapter adapter = new RssListAdapter(context, articles);
        rssListFragment.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }


}