package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.kurojishi.feedrss.dummy.DummyContent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RssListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private FeedDB mFeedDatabase;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssListFragment() {
    }

    // TODO: Rename and change types of parameters
    public static RssListFragment newInstance(String param1, String param2) {
        RssListFragment fragment = new RssListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshList();

    }

    public void refreshList() {
        mFeedDatabase = new FeedDB(getActivity().getBaseContext());
        String[] feedsProjection = {FeedDB.FeedEntry.COLUMN_NAME_URL, FeedDB.FeedEntry._ID};
        SQLiteDatabase db = mFeedDatabase.getReadableDatabase();
        Cursor c = db.query(FeedDB.FeedEntry.TABLE_NAME, feedsProjection, null, null, null, null, null);
        List<URL> urls = new ArrayList<>();
        Log.d("Feed Count", Integer.toString(c.getCount()));
        for (int i = 0; i <= c.getCount() - 1; i++) {
            c.moveToPosition(i);
            String url = c.getString(c.getColumnIndex(FeedDB.FeedEntry.COLUMN_NAME_URL));
            try {
                urls.add(new URL(url));
            } catch (MalformedURLException e) {
                Log.e("Malformed URL", e.getMessage());
                String selection = FeedDB.FeedEntry._ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(c.getString(c.getColumnIndex(FeedDB.FeedEntry._ID)))};
                db.delete(FeedDB.FeedEntry.TABLE_NAME, selection, selectionArgs);
            }
        }
        c.close();
        RssFetcher fetcher = new RssFetcher(this);
        fetcher.execute(urls);
    }

    public void setListAdapter(ListAdapter adapter) {
        mAdapter = adapter;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitem, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
