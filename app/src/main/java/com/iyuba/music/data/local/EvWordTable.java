package com.iyuba.music.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.data.model.SendEvaluateResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *  评测的单词得分表
 */
public class EvWordTable implements EvWordTableInter{

    private final SQLiteDatabase db;

    EvWordTable(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void setEvWord(String voaId, String uId, String paraId, SendEvaluateResponse.WordsBean wordsBean) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOA_ID, voaId);
        values.put(COLUMN_UID, uId);
        values.put(COLUMN_PARA_ID, paraId);
        values.put(COLUMN_CONTENT, wordsBean.getContent());
        values.put(COLUMN_INDEX, wordsBean.getIndex());
        values.put(COLUMN_SCORE, wordsBean.getScore());
        db.replace(TABLE_NAME, null, values);
    }

    @Override
    public List<SendEvaluateResponse.WordsBean> getEvWord(String voaId, String uId, String paraId) {
            String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? "+ " and "
                    + COLUMN_UID + " = ?"+ " and "
                    + COLUMN_PARA_ID + " = ?";
            String[] args = new String[]{voaId,uId,paraId};
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
        List<SendEvaluateResponse.WordsBean> list = new ArrayList<>();
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

    private SendEvaluateResponse.WordsBean parseCursor(Cursor cursor) {
        SendEvaluateResponse.WordsBean score = new SendEvaluateResponse.WordsBean();
        score.content = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
        score.index = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INDEX)));
        score.score = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_SCORE)));
        return score;
    }
}
