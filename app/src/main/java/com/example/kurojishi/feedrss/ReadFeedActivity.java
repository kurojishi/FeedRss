package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;


public class ReadFeedActivity extends Activity {

    private RSSItemContainer article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        article = (RSSItemContainer) intent.getSerializableExtra("RSSItemContainer");


        TextView titleView = (TextView) findViewById(R.id.title_view);
        titleView.setText(article.getTitle());
        TextView authorView = (TextView) findViewById(R.id.author_view);
        authorView.setText(article.getAuthor());
        WebView webView = (WebView) findViewById(R.id.web_article_visualizer);
        webView.loadData(article.getContent(), "text/html", null);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    public void openFeed(View view) {
        Uri uri = Uri.parse(article.getLink().toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
