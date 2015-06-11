package com.example.kurojishi.feedrss;

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
    @Override
    protected List<Article> doInBackground(List<URL>... urls) {
        List<Article> articles = new ArrayList<Article>();
        RssHandler handler = new RssHandler();
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();

        for (Iterator<URL> iter = urls[0].listIterator(); iter.hasNext(); ) {
            URL url = iter.next();

            try {
                SAXParser parser = parserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();

                reader.setContentHandler(handler);

                reader.parse(new InputSource(url.openStream()));

                articles.addAll(handler.getArticleList());

            } catch (IOException e) {
                //This is a Connection Error
                Log.d("Connection Error", e.getMessage());
            } catch (SAXException e) {
                //This is a Parse Error so the link is not an well formed RSS FEED
                Log.d("Parsing Error", e.getMessage());
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
        super.onPostExecute(articles);
    }
}
