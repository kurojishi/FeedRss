package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, RssListFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private RssListFragment mRssFragment;
    /**
     * Used to store the last screen title. For use in .
     */
    private CharSequence mTitle;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //TODO:azioni sulle activi quando premo un item del menu
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mRssFragment = new RssListFragment();
        getFragmentManager().beginTransaction().add(R.id.rss_fragment_container, mRssFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_drawer, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case (R.id.action_settings):
                return true;
            case (R.id.new_subscrition):
                startActivity(new Intent(this, SubscribeActivity.class));
            case (R.id.action_refresh):
                mRssFragment.refreshList();

        }

        return super.onOptionsItemSelected(item);
    }

    private class Menu implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}

