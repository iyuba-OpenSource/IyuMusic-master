package com.iyuba.music.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class EvaluateTable implements EvaluateTableInter {

    private final SQLiteDatabase db;

    EvaluateTable(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void setEvaluate(String voaId, String uId, String pathId, String paraId, String score, int progress, int progress2) {
        if (!TextUtils.isEmpty(voaId) && !TextUtils.isEmpty(uId)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOA_ID, voaId);
            values.put(COLUMN_PARA_ID, paraId);
            values.put(COLUMN_UID, uId);
            values.put(COLUMN_PATH, pathId);
            values.put(COLUMN_SCORE, score);
            values.put(COLUMN_PROGRESS, progress);
            values.put(COLUMN_PROGRESS_TWO, progress2);
            db.replace(TABLE_NAME, null, values);
        }
    }

    @Override
    public void setFluent(String voaId, String uId, String paraId, int fluent, String url) {
        if (!TextUtils.isEmpty(voaId) && !TextUtils.isEmpty(uId)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FLUENT,fluent);
            values.put(COLUMN_URL,url);
            Pair<String, String[]> p = makeClause(uId, voaId, paraId);
            db.update(TABLE_NAME, values, p.first, p.second);
        }
    }

    @Override
    public void setEvaluateTime(String voaId, String uId, String paraId, float beginTime, float endTime, float duration) {
        if (!TextUtils.isEmpty(voaId) && !TextUtils.isEmpty(uId)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BEGIN_TIME,beginTime);
            values.put(COLUMN_END_TIME,endTime);
            values.put(COLUMN_DURATION,duration);
            Pair<String, String[]> p = makeClause(uId, voaId, paraId);
            db.update(TABLE_NAME, values, p.first, p.second);
        }
    }

    private Pair<String, String[]> makeClause(String userId, String voaId, String paraId) {
        String whereClause = COLUMN_UID + " =? and " + COLUMN_VOA_ID + " =? and " + COLUMN_PARA_ID + " =? ";
        String[] args = {userId,voaId, paraId};
        return new Pair<>(whereClause, args);
    }

    @Override
    public List<EvaluateScore> getEvaluate(String voaId, String uId) {
        String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? " +
                " and " + COLUMN_UID + " =? ";
        String[] args = new String[]{voaId, uId};
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
        List<EvaluateScore> list = new ArrayList<>();
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

    private EvaluateScore parseCursor(Cursor cursor) {
        EvaluateScore score = new EvaluateScore();
        score.paraId = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARA_ID)));
        score.voaId = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOA_ID)));
        score.userId = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
        score.score = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE)));
        score.fluent = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FLUENT)));
        score.url = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
        score.path = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH)));
        score.progress = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS)));
        score.progress2 = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_TWO)));
        score.beginTime = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_BEGIN_TIME)));
        score.endTime = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
        score.duration = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        return score;
    }
}
