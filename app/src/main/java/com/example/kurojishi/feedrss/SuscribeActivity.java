package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class SuscribeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscribe);
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
        EditText editTitle = (EditText) findViewById(R.id.subscribe_title);
        EditText editUrl = (EditText) findViewById(R.id.subscribe_url);

        values.put(FeedDB.FeedEntry.COLUMN_NAME_TITLE, editTitle.getText().toString());
        values.put(FeedDB.FeedEntry.COLUMN_NAME_URL, editUrl.getText().toString());

        long newRow = db.insert(FeedDB.FeedEntry.TABLE_NAME, null, values);
        finish();
    }
}
