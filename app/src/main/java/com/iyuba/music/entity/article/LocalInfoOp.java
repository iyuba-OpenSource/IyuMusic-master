package com.iyuba.music.entity.article;

import android.database.Cursor;
import android.database.SQLException;

import com.iyuba.music.entity.BaseEntityOp;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/2.
 * <p>
 * 已下载和下载中歌曲
 */
public class LocalInfoOp extends BaseEntityOp {
    public static final String TABLE_NAME = "news_local";
    public static final String ID = "id";
    public static final String FAVOURITE = "favourite";
    public static final String DOWNLOAD = "download";
    public static final String TIMES = "times";
    public static final String SYNCHRO = "synchro";
    public static final String APP = "app";
    public static final String FAVTIME = "fav_time";
    public static final String DOWNTIME = "down_time";
    public static final String SEETIME = "see_time";

    public LocalInfoOp() {
        super();
    }


    public void saveData(LocalInfo localInfo) {
        getDatabase();
        try {
            String stringBuilder = "insert into " + TABLE_NAME + " (" + ID +
                    "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES +
                    "," + SYNCHRO + "," + APP + "," + FAVTIME + "," +
                    DOWNTIME + "," + SEETIME + ") values(?,?,?,?,?,?,?,?,?)";
            db.execSQL(stringBuilder, new Object[]{localInfo.getId(), localInfo.getFavourite(),
                    localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                    , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
    }

    public void saveData(LocalInfo localInfo, boolean flag) {
        getDatabase();
        try {
            String stringBuilder = "insert into " + TABLE_NAME + " (" + ID +
                    "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES +
                    "," + SYNCHRO + "," + APP + "," + FAVTIME + "," +
                    DOWNTIME + "," + SEETIME + ") values(?,?,?,?,?,?,?,?,?)";
            db.execSQL(stringBuilder, new Object[]{localInfo.getId(), localInfo.getFavourite(),
                    localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                    , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(ArrayList<LocalInfo> localInfos) {
        getDatabase();
        if (localInfos != null && localInfos.size() != 0) {
            int size = localInfos.size();
            LocalInfo localInfo;
            StringBuilder StringBuilder;
            db.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    StringBuilder = new StringBuilder();
                    localInfo = localInfos.get(i);
                    StringBuilder.append("insert into ").append(TABLE_NAME).append(" (").append(ID)
                            .append(",").append(FAVOURITE).append(",").append(DOWNLOAD).append(",").append(TIMES)
                            .append(",").append(SYNCHRO).append(",").append(APP).append(",").append(FAVTIME).append(",")
                            .append(DOWNTIME).append(",").append(SEETIME).append(") values(?,?,?,?,?,?,?,?,?)");
                    db.execSQL(StringBuilder.toString(), new Object[]{localInfo.getId(), localInfo.getFavourite(),
                            localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                            , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 结束事务
                db.endTransaction();
//                db.close();
            }
        }
    }

    public void changeDownloadToStop() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=3  where " + DOWNLOAD + "=2",
                new String[]{});
//        db.close();
    }

    public LocalInfo findDataById(String app, int id) {
        getDatabase();
        LocalInfo localInfo = new LocalInfo();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where app=? and id = ?",
                    new String[]{app, String.valueOf(id)});
            if (cursor.moveToNext()) {
                localInfo = makeupLocalinfo(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfo;
    }

    public LocalInfo findDataById(String app, int id, boolean flag) {
        getDatabase();
        LocalInfo localInfo = new LocalInfo();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where app=? and id = ?",
                    new String[]{app, String.valueOf(id)});
            if (cursor.moveToNext()) {
                localInfo = makeupLocalinfo(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localInfo;
    }

    public ArrayList<LocalInfo> findDataByFavourite() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where " + FAVOURITE + "=1 order by " + FAVTIME + " desc limit  100",
                    new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                localInfos.add(makeupLocalinfo(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfos;
    }


    /**
     * 退出后重置favourite
     */
    public void resetFavourite() {
        getDatabase();

        String sqlStr = "update news_local set favourite = 0";
        db.execSQL(sqlStr);

    }

    public ArrayList<LocalInfo> findDataByDownloaded() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where " + DOWNLOAD + "=1 order by " + DOWNTIME + " desc",
                    new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                localInfos.add(makeupLocalinfo(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfos;
    }

    public ArrayList<LocalInfo> findDataByDownloading() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where " + DOWNLOAD + ">1 order by " + DOWNTIME + " desc",
                    new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                localInfos.add(makeupLocalinfo(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfos;
    }

    public ArrayList<LocalInfo> findDataByShouldContinue() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where " + DOWNLOAD + "=3 order by " + DOWNTIME + " desc",
                    new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                localInfos.add(makeupLocalinfo(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfos;
    }

    public ArrayList<LocalInfo> findDataByListen() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                            + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                            " from " + TABLE_NAME + " where " + TIMES + ">0 order by " + SEETIME + " desc limit  100",
                    new String[]{});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                localInfos.add(makeupLocalinfo(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
        return localInfos;
    }

    public void updateSee(int id, String app) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=" + TIMES + "+1" + ","
                        + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(id), app});
//        db.close();
    }

    public void deleteSee(int id, String app) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=0,"
                        + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(id), app});
//        db.close();
    }

    public void updateFavor(int id, String app, int state) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + FAVOURITE + "=? ,"
                        + FAVTIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(state), String.valueOf(id), app});
//        db.close();
    }

    public void updateDownload(int id, String app, int state) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=? ,"
                        + DOWNTIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(state), String.valueOf(id), app});
//        db.close();
    }

    public void updateDownload(int id, String app, int state, boolean flag) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=? ,"
                        + DOWNTIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(state), String.valueOf(id), app});

    }


    public void clearSee() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=0,"
                + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                + "' where " + TIMES + ">0", new String[]{});
//        db.close();
    }

    public void clearDownloading() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + ">1", new String[]{});
//        db.close();
    }

    public void clearDownloaded() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + "=1", new String[]{});
//        db.close();
    }

    public void clearAllDownload() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + ">0", new String[]{});
//        db.close();
    }

    private LocalInfo makeupLocalinfo(Cursor cursor) {
        LocalInfo localInfo = new LocalInfo();
        localInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
        localInfo.setDownload(cursor.getInt(cursor.getColumnIndexOrThrow(DOWNLOAD)));
        localInfo.setFavourite(cursor.getInt(cursor.getColumnIndexOrThrow(FAVOURITE)));
        localInfo.setTimes(cursor.getInt(cursor.getColumnIndexOrThrow(TIMES)));
        localInfo.setSynchro(cursor.getInt(cursor.getColumnIndexOrThrow(SYNCHRO)));
        localInfo.setApp(cursor.getString(cursor.getColumnIndexOrThrow(APP)));
        localInfo.setFavTime(cursor.getString(cursor.getColumnIndexOrThrow(FAVTIME)));
        localInfo.setDownTime(cursor.getString(cursor.getColumnIndexOrThrow(DOWNTIME)));
        localInfo.setSeeTime(cursor.getString(cursor.getColumnIndexOrThrow(SEETIME)));
        return localInfo;
    }

    public void closeDb() {

        if (db.isOpen()) {
            db.close();
        }
    }
}
