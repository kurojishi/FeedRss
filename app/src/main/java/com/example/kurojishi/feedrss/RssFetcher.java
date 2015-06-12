package com.example.kurojishi.feedrss;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by kurojishi on 6/11/15.
 */
public class RssFetcher extends AsyncTask<List<URL>, Void, List<Article>> {

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
    protected List<Article> doInBackground(List<URL>... urls) {
        List<Article> articles = new ArrayList<>();
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();

        for (Iterator<URL> iter = urls[0].listIterator(); iter.hasNext(); ) {
            URL url = iter.next();

            try {


                SAXParser parser = parserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();

                RssHandler handler = new RssHandler();

                reader.setContentHandler(handler);
                reader.parse(new InputSource(url.openStream()));

                articles.addAll(handler.getArticleList());

            } catch (IOException e) {
                //This is a Connection Error
                Log.d("Connection Error", "Url ");
            } catch (SAXException e) {
                //This is a Parse Error so the link is not an well formed RSS FEED
                Log.d("Parsing Error", "feed invalid");
            } catch (ParserConfigurationException e) {
                Log.e("This Should not Happen", e.getMessage());
                return null;
            }
        }
        Collections.sort(articles);
        return articles;
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        RssListAdapter adapter = new RssListAdapter(context, articles);
        rssListFragment.setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}
