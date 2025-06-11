package com.iyuba.music.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.iyuba.music.data.model.VoaText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class VoaTextTable implements VoaTextTableInter {

    private final SQLiteDatabase db;

    VoaTextTable(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void setVoaTexts(Collection<VoaText> voaTexts, int voaId) {
        if (voaId != 0) {
            String whereClause = COLUMN_VOA_ID + " = ? ";
            String[] args = {String.valueOf(voaId)};
            Pair<String, String[]> p = new Pair<>(whereClause, args);
            db.delete(TABLE_NAME, p.first, p.second);

            for (VoaText data : voaTexts) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_VOA_ID, data.getVoaId());
                values.put(COLUMN_PARA_ID, data.paraId);
                values.put(COLUMN_ID_INDEX, data.idIndex);
                values.put(COLUMN_SENTENCE_CN, data.sentenceCn);
                values.put(COLUMN_SENTENCE, data.sentence);
                values.put(COLUMN_IMG_WORDS, data.imgWords);
                values.put(COLUMN_IMG_PATH, data.imgPath);
                values.put(COLUMN_TIMING, data.timing);
                values.put(COLUMN_END_TIMING, data.endTiming);
                db.replace(TABLE_NAME, null, values);
            }
        }
    }

    @Override
    public Observable<List<VoaText>> getVoaTexts(final int voaId) {

        return Observable.create(new ObservableOnSubscribe<List<VoaText>>() {
            @Override
            public void subscribe(ObservableEmitter<List<VoaText>> emitter) throws Exception {
                try {
                    String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? " + " ORDER BY " + COLUMN_PARA_ID + " ASC";
                    String[] args = new String[]{String.valueOf(voaId)};
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
                    if (cursor.moveToNext()) {
                        emitter.onNext(getList(cursor));
                    } else {
                        emitter.onNext(null);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//     try {
//        String sql = "select * from " + TABLE_NAME + " where " + COLUMN_VOA_ID + " = ? "+ " ORDER BY " + COLUMN_PARA_ID + " ASC";
//        String[] args = new String[]{String.valueOf(voaId)};
//        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, args);
//        if (cursor.moveToNext()) {
//            subscriber.onNext(getList(cursor));
//        } else {
//            subscriber.onNext(null);
//        }
//        subscriber.onCompleted();
//    } catch (Exception e) {
//        e.printStackTrace();
//    }

    private List<VoaText> getList(Cursor cursor){
        List<VoaText> list =new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                //cursor.moveToFirst();
                do {
                    list.add(parseCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }
        return list;
    }

    private VoaText parseCursor(Cursor cursor) {
        VoaText voaText = new VoaText();
        voaText.paraId = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PARA_ID)));
        voaText.idIndex = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_INDEX)));
        voaText.sentenceCn = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE_CN)));
        voaText.sentence = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENTENCE)));
        voaText.imgWords = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_WORDS)));
        voaText.imgPath = (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_PATH)));
        voaText.timing = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TIMING)));
        voaText.endTiming = (cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_END_TIMING)));
        return voaText;
    }
}
