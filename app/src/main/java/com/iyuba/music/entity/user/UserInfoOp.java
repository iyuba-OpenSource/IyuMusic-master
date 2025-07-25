package com.iyuba.music.entity.user;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;

/**
 * Created by 10202 on 2015/11/18.
 */
public class UserInfoOp extends BaseEntityOp {
    public static final String TABLE_NAME = "user";
    public static final String USERID = "userid";
    public static final String USERNAME = "username";
    public static final String USEREMAIL = "useremail";
    public static final String GENDER = "gender";
    public static final String FANS = "fans";
    public static final String ATTENTIONS = "attentions";
    public static final String NOTIFICATIONS = "notifications";
    public static final String SEETIMES = "seetimes";
    public static final String STATE = "state";
    public static final String VIP = "vip";
    public static final String IYUBI = "iyubi";
    public static final String DEADLINE = "deadline";
    public static final String STUDYTIME = "studytime";
    public static final String POSITION = "position";
    public static final String ICOIN = "credits";

    public UserInfoOp() {
        super();
    }

    /**
     * 插入数据
     */
    public void saveData(UserInfo userInfo) {
        getDatabase();
        db.execSQL(
                "insert or replace into " + TABLE_NAME + " (" + USERID
                        + "," + USERNAME + "," + USEREMAIL + "," + GENDER + ","
                        + FANS + "," + ATTENTIONS + "," + NOTIFICATIONS + "," + SEETIMES + ","
                        + STATE + "," + VIP + "," + IYUBI + "," + DEADLINE
                        + "," + STUDYTIME + "," + POSITION + "," + ICOIN
                        + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{userInfo.getUid(), userInfo.getUsername(), userInfo.getUserEmail(),
                        userInfo.getGender(), userInfo.getFollower(), userInfo.getFollowing(),
                        userInfo.getNotification(), userInfo.getViews(),
                        userInfo.getText(), userInfo.getVipStatus(),
                        userInfo.getIyubi(), userInfo.getDeadline(),
                        userInfo.getStudytime(), userInfo.getPosition(),
                        userInfo.getIcoins()});
//        db.close();
    }

    /**
     * 选择数据
     */
    public UserInfo selectData(String userId) {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select " + USERID + "," + USERNAME + "," + USEREMAIL + "," + GENDER + ","
                        + FANS + "," + ATTENTIONS + "," + NOTIFICATIONS + "," + SEETIMES
                        + "," + STATE + "," + VIP + "," + IYUBI + ","
                        + DEADLINE + "," + STUDYTIME + "," + POSITION + ","
                        + ICOIN + " from " + TABLE_NAME + " where " + USERID
                        + "=?", new String[]{userId});
        if (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(cursor.getString(0));
            userInfo.setUsername(cursor.getString(1));
            userInfo.setUserEmail(cursor.getString(2));
            userInfo.setGender(cursor.getString(3));
            userInfo.setFollower(cursor.getString(4));
            userInfo.setFollowing(cursor.getString(5));
            userInfo.setNotification(cursor.getString(6));
            userInfo.setViews(cursor.getString(7));
            userInfo.setText(cursor.getString(8));
            userInfo.setVipStatus(cursor.getString(9));
            userInfo.setIyubi(cursor.getString(10));
            userInfo.setDeadline(cursor.getString(11));
            userInfo.setStudytime(cursor.getInt(12));
            userInfo.setPosition(cursor.getString(13));
            userInfo.setIcoins(cursor.getString(14));
            cursor.close();
//            db.close();
            return userInfo;
        } else {
//            db.close();
            cursor.close();
            return null;
        }
    }

    /**
     * 选择数据
     */
    public UserInfo selectDataByName(String userName) {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select " + USERID + "," + USERNAME + "," + USEREMAIL + "," + GENDER + ","
                        + FANS + "," + ATTENTIONS + "," + NOTIFICATIONS + "," + SEETIMES
                        + "," + STATE + "," + VIP + "," + IYUBI + ","
                        + DEADLINE + "," + STUDYTIME + "," + POSITION + ","
                        + ICOIN + " from " + TABLE_NAME + " where " + USERNAME
                        + "=?", new String[]{userName});
        if (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(cursor.getString(0));
            userInfo.setUsername(cursor.getString(1));
            userInfo.setUserEmail(cursor.getString(2));
            userInfo.setGender(cursor.getString(3));
            userInfo.setFollower(cursor.getString(4));
            userInfo.setFollowing(cursor.getString(5));
            userInfo.setNotification(cursor.getString(6));
            userInfo.setViews(cursor.getString(7));
            userInfo.setText(cursor.getString(8));
            userInfo.setVipStatus(cursor.getString(9));
            userInfo.setIyubi(cursor.getString(10));
            userInfo.setDeadline(cursor.getString(11));
            userInfo.setStudytime(cursor.getInt(12));
            userInfo.setPosition(cursor.getString(13));
            userInfo.setIcoins(cursor.getString(14));
            cursor.close();
//            db.close();
            return userInfo;
        } else {
//            db.close();
            cursor.close();
            return null;
        }
    }

    public int selectStudyTime(String userId) {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select " + STUDYTIME + " from " + TABLE_NAME + " where "
                        + USERID + "=?", new String[]{userId});
        if (cursor.moveToNext()) {
            int temp = cursor.getInt(0);
            cursor.close();
//            db.close();
            return temp;
        } else {
            cursor.close();
//            db.close();
            return 0;
        }
    }

    public void updataByStudyTime(String userid, int time) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + STUDYTIME + "=" + time
                + " where " + USERID + "=?", new String[]{userid});
//        db.close();

    }

    public void updataByName(String userid, String name) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + USERNAME + "=?"
                + " where " + USERID + "=?", new String[]{name,userid});
//        db.close();

    }

    public void delete(String userid) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + USERID + "=?",
                new String[]{userid});
//        db.close();
    }
}
