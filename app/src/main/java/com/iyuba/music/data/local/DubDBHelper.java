package com.iyuba.music.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DubDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dubbing.db";
    private static final int DB_VERSION = 1;

    private Context mContext;

    public DubDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    private static final String CREATE_GROUP_DUB = "create table if not exists " + VoaTextTable.TABLE_NAME + " (" +
            VoaTextTable.COLUMN_VOA_ID + " int(10) not null," +
            VoaTextTable.COLUMN_PARA_ID + " int(10) not null," +
            VoaTextTable.COLUMN_ID_INDEX + " char(10)," +
            VoaTextTable.COLUMN_SENTENCE_CN + " char(10)," +
            VoaTextTable.COLUMN_SENTENCE + " char(10)," +
            VoaTextTable.COLUMN_IMG_WORDS + " char(10)," +
            VoaTextTable.COLUMN_IMG_PATH + " char(10)," +
            VoaTextTable.COLUMN_TIMING + " char(10)," +
            VoaTextTable.COLUMN_END_TIMING + " char(10))";

    private static final String CREATE_GROUP_COLLECT = "create table if not exists " + CollectTable.TABLE_NAME + " (" +
            CollectTable.COLUMN_VOA_ID + " char(10) not null," +
            CollectTable.COLUMN_UID + " char(10) not null," +
            CollectTable.IMAGE + " varchar(100)," +
            CollectTable.TITLE + " varchar(100)," +
            CollectTable.DESC + " varchar(100)," +
            CollectTable.IS_COLLECT + " int(10)," +
            CollectTable.IS_DOWNLOAD + " int(10)," +
            CollectTable.SERIES + " varchar(100)," +
            "primary key(" + CollectTable.COLUMN_UID + "," +
            CollectTable.COLUMN_VOA_ID + "))";

    private static final String CREATE_EVALUATE = "create table if not exists " + EvaluateTable.TABLE_NAME + " (" +
            EvaluateTable.COLUMN_VOA_ID + " char(10) not null," +
            EvaluateTable.COLUMN_UID + " char(10) not null," +
            EvaluateTable.COLUMN_PARA_ID + " char(10) not null," +
            EvaluateTable.COLUMN_FLUENT + " int(10)," +
            EvaluateTable.COLUMN_URL + " varchar(255)," +
            EvaluateTable.COLUMN_PATH + " varchar(255)," +
            EvaluateTable.COLUMN_PROGRESS + " int(10)," +
            EvaluateTable.COLUMN_PROGRESS_TWO + " int(10) ," +
            EvaluateTable.COLUMN_BEGIN_TIME + " float default 0 ," +
            EvaluateTable.COLUMN_END_TIME + " float default 0 ," +
            EvaluateTable.COLUMN_DURATION + " float default 0 ," +
            EvaluateTable.COLUMN_SCORE + " char(10) not null,"
            + "primary key(" + EvaluateTable.COLUMN_VOA_ID + "," +
            EvaluateTable.COLUMN_UID + "," +
            EvaluateTable.COLUMN_PARA_ID + "))";


    private static final String CREATE_PRAISE = "create table if not exists " + PraiseTable.TABLE_NAME + " (" +
            PraiseTable.COLUMN_UID + " int(10) not null," +
            PraiseTable.COLUMN_ID + " int(10) not null," +
            PraiseTable.COLUMN_OTHER + " char(10)," +
            "primary key(" + PraiseTable.COLUMN_UID + "," +
            PraiseTable.COLUMN_ID + "))";


    private static final String CREATE_EVALUATE_WORD = "create table if not exists " + EvWordTable.TABLE_NAME + " (" +
            EvWordTable.COLUMN_UID + " char(10) not null," +
            EvWordTable.COLUMN_VOA_ID + " char(10) not null," +
            EvWordTable.COLUMN_PARA_ID + " char(10) not null," +
            EvWordTable.COLUMN_SCORE + " float default 0 ," +
            EvWordTable.COLUMN_CONTENT + " varchar(100)," +
            EvWordTable.COLUMN_INDEX + " int(10) not null," +
            "primary key(" + EvWordTable.COLUMN_UID + "," +
            EvWordTable.COLUMN_VOA_ID + "," +
            EvWordTable.COLUMN_INDEX + "," +
            EvWordTable.COLUMN_PARA_ID + "))";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GROUP_DUB);
        db.execSQL(CREATE_GROUP_COLLECT);
        db.execSQL(CREATE_EVALUATE);
        db.execSQL(CREATE_PRAISE);
        db.execSQL(CREATE_EVALUATE_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
