package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
public class RssListFragment extends ListFragment implements AbsListView.OnItemClickListener {

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

    private List<RSSItemContainer> mArticles;

    public RssListFragment() {
    }

    public void setArticles(List<RSSItemContainer> articles) {
        mArticles = articles;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshList(null);

    }

    public void refreshList(String title) {

        RssFetcher fetcher = new RssFetcher(this);
        fetcher.execute(title);
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
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                FeedDB helper = new FeedDB(v.getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if (mArticles.get(position).getIsRead()) {
                    mArticles.get(position).setIsRead(false);
                    values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, 0);
                    String where = FeedDB.ArticleEntry._ID + " = " + mArticles.get(position).getDbId();
                    db.update(FeedDB.ArticleEntry.TABLE_NAME, values, where, null);
                    db.close();
                    DialogFragment dialog = new UnReadDialog();
                    dialog.show(getActivity().getFragmentManager(), "unread");
                    TextView titleView = (TextView) v.findViewById(R.id.article_title_text);
                    titleView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    mArticles.get(position).setIsRead(true);
                    values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, 1);
                    String where = FeedDB.ArticleEntry._ID + " = " + mArticles.get(position).getDbId();
                    db.update(FeedDB.ArticleEntry.TABLE_NAME, values, where, null);
                    db.close();
                    DialogFragment dialog = new ReadDialog();
                    dialog.show(getActivity().getFragmentManager(), "read");
                    TextView titleView = (TextView) v.findViewById(R.id.article_title_text);
                    titleView.setTypeface(Typeface.DEFAULT);
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        openItem(v, mArticles.get(position));
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
            mListener.onFragmentInteraction(mArticles.get(position).getTitle());
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

    public void openItem(View view, RSSItemContainer article) {
        Intent intent = new Intent(view.getContext(), ReadFeedActivity.class);
        if (!article.getIsRead()) {
            article.setIsRead(true);
            FeedDB helper = new FeedDB(view.getContext());
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedDB.ArticleEntry.COLUMN_NAME_READ, 1);
            String where = FeedDB.ArticleEntry._ID + " = " + article.getDbId();
            db.update(FeedDB.ArticleEntry.TABLE_NAME, values, where, null);
            db.close();
        }
        intent.putExtra("Article", article);
        startActivity(intent);
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
