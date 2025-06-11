package com.iyuba.music.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Praise 表扬，点赞 表
 */
public class PraiseTable implements PraiseTableInter{

    private final SQLiteDatabase db;

    PraiseTable(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void setAgree(String uid, String id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_UID, uid);
        db.replace(TABLE_NAME, null, values);
    }

    @Override
    public boolean isAgree(String uid, String id) {
        @SuppressLint("Recycle") Cursor cursor = null;
        try {
            String sql = "select * from " + TABLE_NAME + " where " + COLUMN_ID + " = ? "+ " and "
                    + COLUMN_UID + " = ?";
            String[] args = new String[]{id,uid};
            cursor = db.rawQuery(sql, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor!=null&&cursor.getCount()>0;
    }
}
