package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class ReadFeedActivity extends Activity {

    private RSSItemContainer article;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        article = (RSSItemContainer) intent.getSerializableExtra("Article");
        setContentView(R.layout.activity_read_feed);

        TextView titleView = (TextView) findViewById(R.id.read_feed_title);
        titleView.setText(article.getTitle());
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        TextView authorView = (TextView) findViewById(R.id.author_view);
        authorView.setText(article.getAuthor());
        WebView webView = (WebView) findViewById(R.id.web_article_view);
        webView.loadData(article.getDescription(), "text/html", null);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    public void openFeed(View view) {
        Uri uri = Uri.parse(article.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_feed, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();

        MenuItem item = menu.findItem(R.id.menu_favourite);
        if (article.getFavourite()) {
            item.setIcon(android.R.drawable.star_on);
        } else {
            item.setIcon(android.R.drawable.star_off);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.menu_item_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, article.getLink());
                intent.setType("text/plain");
                setShareIntent(intent);
                return true;
            case R.id.menu_favourite:
                FeedDB helper = new FeedDB(getBaseContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if (article.getFavourite()) {
                    article.setFavourite(false);
                    values.put(FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE, 0);
                    String where = FeedDB.ArticleEntry._ID + " = " + article.getDbId();
                    db.update(FeedDB.ArticleEntry.TABLE_NAME, values, where, null);
                    db.close();
                    item.setIcon(android.R.drawable.star_off);
                } else {
                    article.setFavourite(true);
                    values.put(FeedDB.ArticleEntry.COLUMN_NAME_FAVOURITE, 1);
                    String where = FeedDB.ArticleEntry._ID + " = " + article.getDbId();
                    db.update(FeedDB.ArticleEntry.TABLE_NAME, values, where, null);
                    db.close();
                    item.setIcon(android.R.drawable.star_on);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }

    }
}
