package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nl.matshofman.saxrssreader.RssReader;


public class SubscribeActivity extends Activity {

    private UrlWatcher mUrlWatcher;
    private EditText mEditTitle;
    private EditText mEditUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscribe);
        mEditTitle = (EditText) findViewById(R.id.subscribe_title);
        mEditUrl = (EditText) findViewById(R.id.subscribe_url);
        mUrlWatcher = new UrlWatcher();
        mEditUrl.addTextChangedListener(mUrlWatcher);
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

    private final class UrlWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!Patterns.WEB_URL.matcher(editable.toString()).matches() && URLUtil.isHttpsUrl(editable.toString())) {
                TextView validUrl = (TextView) findViewById(R.id.is_url_valid);
                validUrl.setText(R.string.url_is_not_valid);
                validUrl.setTextColor(Color.RED);


            } else {
                TextView valid = (TextView) findViewById(R.id.is_url_valid);
                valid.setText(R.string.url_is_valid);
                valid.setTextColor(Color.GREEN);
                URL url;
                try {
                    url = new URL(editable.toString());
                } catch (MalformedURLException e) {
                    //this shouldn't happen as url is checked before this step
                    return;
                }
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    TextView foundUrl = (TextView) findViewById(R.id.url_found);
                    foundUrl.setText(R.string.url_not_found);
                    foundUrl.setTextColor(Color.RED);
                    return;
                }
                TextView foundUrl = (TextView) findViewById(R.id.url_found);
                foundUrl.setText(R.string.url_found);
                foundUrl.setTextColor(Color.GREEN);
                try {
                    RssReader.read(url);
                } catch (IOException e) {
                    //this shouldn't happen as it's been done in the step before this
                    return;
                } catch (SAXException e) {
                    //This is a Parse Error so the link is not an well formed RSS FEED
                    TextView validRSS = (TextView) findViewById(R.id.valid_rss);
                    validRSS.setText(R.string.not_valid_rss);
                    validRSS.setTextColor(Color.RED);
                    return;
                }
                TextView validRSS = (TextView) findViewById(R.id.valid_rss);
                validRSS.setText(R.string.valid_rss);
                validRSS.setTextColor(Color.GREEN);

                Button addFeed = (Button) findViewById(R.id.add_feed);
            }


        }
    }
}
