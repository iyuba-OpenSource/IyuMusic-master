package com.iyuba.music.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.iyuba.music.data.model.TalkLesson;

import java.util.ArrayList;
import java.util.List;

public class CollectTable implements CollectTableInter{

    private final SQLiteDatabase db;

    CollectTable(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public boolean setCollect(String voaId, String uId, String title, String desc, String image, String series, String titleCn, String hot, String read, String sound) {
        return true;
    }
    @Override
    public boolean setCollect(String voaId, String uId, String title, String desc, String image, String series) {
//            try {
//                db.delete(TABLE_NAME,COLUMN_VOA_ID + " = ? " +" and "+ COLUMN_UID + " = ? ",
//                        new String[]{voaId, uId});
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
        int collect = 0;
        if (!getCollect(voaId,uId)) {
            collect = 1 ;//如果没收藏过就收藏
        }

        int isDown = getDownLoad(voaId)?1:0;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaId);
            values.put(COLUMN_UID, uId);
            values.put(TITLE, title);
            values.put(DESC, desc);
            values.put(IMAGE, image);
            values.put(SERIES, series);
            values.put(IS_COLLECT, collect);
            values.put(IS_DOWNLOAD, isDown);
            db.replace(TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setDownload(String voaId, String uId, String title, String desc, String image, String series) {
        int isCollect = getCollect(voaId,uId)?1:0;
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOA_ID, voaId);
        values.put(COLUMN_UID, uId);
        values.put(TITLE, title);
        values.put(DESC, desc);
        values.put(IMAGE, image);
        values.put(SERIES, series);
        values.put(IS_DOWNLOAD, 1);
        values.put(IS_COLLECT, isCollect);//keep
        db.replace(TABLE_NAME, null, values);
    }

    @Override
    public void deleteCollect(String voaId, String uId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOA_ID, voaId);
        values.put(COLUMN_UID, uId);
        values.put(IS_COLLECT, 0);
        Pair<String, String[]> p = makeClause(uId, voaId);
        db.update(TABLE_NAME, values, p.first, p.second);

    }

    private Pair<String, String[]> makeClause(String userId, String voaId) {
        String whereClause = COLUMN_UID + " =? and " + COLUMN_VOA_ID + " =? ";
        String[] args = {userId,voaId};
        return new Pair<>(whereClause, args);
    }

    @Override
    public void deleteDown(String voaId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOA_ID, voaId);
        values.put(IS_DOWNLOAD, 0);
        Pair<String, String[]> p = makeClause(voaId);
        db.update(TABLE_NAME, values, p.first, p.second);
    }

    private Pair<String, String[]> makeClause(String voaId) {
        String whereClause =  COLUMN_VOA_ID + " =? ";
        String[] args = {voaId};
        return new Pair<>(whereClause, args);
    }

    @Override
    public boolean getCollect(String voaId, String uId) {
        @SuppressLint("Recycle") Cursor cursor = null;
        try {
            String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? "+ " and "
                    + COLUMN_UID + " = ?"+ " and " + IS_COLLECT + " = ?";
            String[] args = new String[]{voaId,uId,"1"};
            cursor = db.rawQuery(sql, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor!=null&&cursor.getCount()>0;
    }

    public boolean getDownLoad(String voaId) {
        @SuppressLint("Recycle") Cursor cursor = null;
        try {
            String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? " + " and "
                    + IS_DOWNLOAD + " = ?";
            String[] args = new String[]{voaId,"1"};
            cursor = db.rawQuery(sql, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor!=null&&cursor.getCount()>0;
    }

    @Override
    public List<TalkLesson> getCollectList(String uId) {
        String sql = "select * from " + TABLE_NAME + " where " + COLUMN_UID + " = ? "+ " and " + IS_COLLECT + " = ?";
        String[] args = new String[]{uId,"1"};
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
        List<TalkLesson> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(parseCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }
        return list;
    }

    @Override
    public List<TalkLesson> getDownList() {
        String sql = "select * from " + TABLE_NAME + " where " + IS_DOWNLOAD + " = ? ";
        String[] args = new String[]{"1"};
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
        List<TalkLesson> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(parseCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }
        return list;
    }

    private TalkLesson parseCursor(Cursor cursor) {
        TalkLesson score = new TalkLesson();
        score.Id = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)));
        score.Title = (cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
        score.DescCn = (cursor.getString(cursor.getColumnIndexOrThrow(DESC)));
        score.Pic = (cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)));
        score.series = (cursor.getString(cursor.getColumnIndexOrThrow(SERIES)));
        return score;
    }
}
