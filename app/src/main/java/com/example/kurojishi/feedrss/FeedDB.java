package com.example.kurojishi.feedrss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/**
 * Created by kurojishi on 6/7/15.
 */
public class FeedDB extends SQLiteOpenHelper {

    public static final String DATABSE_NAME = "FeedReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_FEEDS_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_URL + TEXT_TYPE + " )";
    private static final String CREATE_ARTICLE_TABLE =
            "CREATE TABLE " + ArticleEntry.TABLE_NAME + " (" +
                    ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ArticleEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_PUBDATE + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    ArticleEntry.COLUMN_NAME_READ + "BOOLEAN" + ")";

    private static final String DROP_ARTICLE_TABLE = "DROP TABLE IF EXISTS " + ArticleEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 12;

    public FeedDB(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_FEEDS_ENTRIES);
        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_ARTICLE_TABLE);
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "feeds";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_URL = "url";
    }

    public static abstract class ArticleEntry implements BaseColumns {
        public static final String TABLE_NAME = "articles";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PUBDATE = "pubdate";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_READ = "read";
    }


}
