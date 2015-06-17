package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


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
        //TODO: don't add anything if there is a alrady the same feed
        FeedDB feedDB = new FeedDB(view.getContext());
        SQLiteDatabase db = feedDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedDB.FeedEntry.COLUMN_NAME_TITLE, mEditTitle.getText().toString());
        values.put(FeedDB.FeedEntry.COLUMN_NAME_URL, mEditUrl.getText().toString());

        long newRow = db.insert(FeedDB.FeedEntry.TABLE_NAME, null, values);
        Log.d("how many row", String.valueOf(newRow));
        finish();

    }

    public void validateUrl(View view) {
        TextView textUrl = (TextView) findViewById(R.id.subscribe_url);
        TextView validUrl = (TextView) findViewById(R.id.is_url_valid);
        if (textUrl.getText().toString().equals(getString(R.string.new_feed_url_hint))) {
            return;
        }
        if (!Patterns.WEB_URL.matcher(textUrl.getText().toString()).matches() || URLUtil.isHttpsUrl(textUrl.getText().toString())) {

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
        if (isNetworkAvailable()) {
            new CheckURLStatus().execute(url);
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private final class CheckURLStatus extends AsyncTask<URL, Void, Integer>

    {

        @Override
        protected Integer doInBackground(URL... urls) {
            if (urls.length > 1) {
                return null;
            }
            RSSReader reader = new RSSReader();

            URL url = urls[0];
            try {
                InputStream stream = url.openStream();
                stream.close();

            } catch (IOException e) {
                //This is a Connection Error
                Log.d("Connection Error", e.getMessage());
                return 1;
            }
            try {
                reader.load(url.toString());

            } catch (RSSReaderException e) {
                //This is a Parse Error so the link is not an well formed RSS FEED
                Log.d("Parsing Error", e.getMessage());
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
                    Button addFeedButton = (Button) findViewById(R.id.add_feed);
                    addFeedButton.setEnabled(true);
                    break;
                case 1:
                    foundUrl.setText(R.string.url_not_found);
                    foundUrl.setTextColor(Color.RED);
                    break;
                case 2:
                    foundUrl.setText(R.string.invalid_rss);
                    foundUrl.setTextColor(Color.RED);
                    break;
                default:
                    foundUrl.setText("WTF IS HAPPENING");
                    foundUrl.setTextColor(Color.YELLOW);
                    break;
            }

        }
    }

}
