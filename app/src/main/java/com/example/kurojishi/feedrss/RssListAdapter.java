package com.example.kurojishi.feedrss;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kurojishi on 6/11/15.
 */
public class RssListAdapter extends ArrayAdapter<RSSItemContainer> {
    public RssListAdapter(Context context, List<RSSItemContainer> objects) {
        super(context, R.layout.article_item_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) getContext();
        View rowView = activity.getLayoutInflater().inflate(R.layout.article_item_list, null);
        final RSSItemContainer article = getItem(position);

        TextView titleView = (TextView) rowView.findViewById(R.id.article_title_text);
        titleView.setText(article.getTitle());

        TextView dateView = (TextView) rowView.findViewById(R.id.article_date_author);
        SimpleDateFormat dateformatter = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.getDefault());
        Date date;
        if (article.getPubDate() != null) {
            try {
                date = dateformatter.parse(article.getPubDate().toString());
                dateView.setText("published " + DateUtils.getRelativeTimeSpanString(date.getTime()) + " by " + article.getAuthor());
            } catch (ParseException e) {
                Log.w("DATE PARSING", "Error parsing date..");
                dateView.setText("published by " + article.getAuthor());
            }
        } else {
            dateView.setText("published by " + article.getAuthor());
        }
        TextView previewView = (TextView) rowView.findViewById(R.id.article_text_preview);
        previewView.setText(Jsoup.parse(article.getDescription()).text());

        if (!article.getIsRead()) {
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
        }

        return rowView;
    }
}
