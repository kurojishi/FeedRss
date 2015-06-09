package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class SubscribeActivity extends Activity {


    private EditText mEditTitle;
    private EditText mEditUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscribe);
        mEditTitle = (EditText) findViewById(R.id.subscribe_title);
        mEditUrl = (EditText) findViewById(R.id.subscribe_url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_feed, menu);
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

    public void addFeed(View view) {
        FeedDB feedDB = new FeedDB(view.getContext());
        SQLiteDatabase db = feedDB.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(FeedDB.FeedEntry.COLUMN_NAME_TITLE, mEditTitle.getText().toString());
        values.put(FeedDB.FeedEntry.COLUMN_NAME_URL, mEditUrl.getText().toString());

        long newRow = db.insert(FeedDB.FeedEntry.TABLE_NAME, null, values);
        finish();
    }

    public void validateUrl(View view) {
        TextView textUrl = (TextView) findViewById(R.id.subscribe_url);
            TextView validUrl = (TextView) findViewById(R.id.is_url_valid);
        if (textUrl.getText().toString().equals(getString(R.string.new_feed_url_hint))) {
            return;
        }
        if (!Patterns.WEB_URL.matcher(textUrl.getText().toString()).matches() && URLUtil.isHttpsUrl(textUrl.getText().toString())) {

                validUrl.setText(R.string.url_is_not_valid);
                validUrl.setTextColor(Color.RED);
            return;

        }
        validUrl.setText(R.string.url_is_valid);
        validUrl.setTextColor(Color.GREEN);
        URL url;
        try {
            url = new URL(textUrl.getText().toString());
        } catch (MalformedURLException e) {
            //this shouldn't happen as url is checked before this step
                return;
            }
            new CheckURLStatus().execute(url);


        }

        private final class CheckURLStatus extends AsyncTask<URL, Void, Integer>

        {

            @Override
            protected Integer doInBackground(URL... urls) {
                PkRSS.Builder builder = new PkRSS.Builder(getBaseContext());
                PkRSS fetcher = builder.build();
                if (urls.length > 1) {
                    return 3;
                }
                URL url = urls[0];
                try {
                    List<Article> rssItems = fetcher.load(url.toString()).get();
                } catch (IOException e) {
                    //This is a Connection Error
                    return 1;
                } catch (Exception e) {
                    //This is a Parse Error so the link is not an well formed RSS FEED
                    return 2;
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer valid) {
                TextView foundUrl = (TextView) findViewById(R.id.url_found);
                switch (valid) {
                    case 0:

                        foundUrl.setText(R.string.url_found_and_valid);
                        foundUrl.setTextColor(Color.GREEN);
                        Button addFeed = (Button) findViewById(R.id.add_feed);
                        addFeed.setEnabled(true);
                    case 1:
                        foundUrl.setText(R.string.url_not_found);
                        foundUrl.setTextColor(Color.RED);
                    case 2:
                        foundUrl.setText(R.string.invalid_rss);
                        foundUrl.setTextColor(Color.RED);
                }

            }
        }


    }
