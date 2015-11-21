package com.dsi.darshan.samplegcm_chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by darshan on 21/11/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDb.db";
    public static final String DATABASE_TABLE_USERS = "users";
    public static final String DATABASE_TABLE_MSGS = "messages";
    public static final int DATABASE_NO = 1;
    public static final String USERS_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String USER_NUMBER = "user_number";
    public static final String MSG_ID = "msg_id";
    public static final String FROM_ID = "from_id";
    public static final String TO_ID = "to_id";
    public static final String MSG = "msg";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_NO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersQuery = "create table " + DATABASE_TABLE_USERS + " (" + USERS_NAME + " varchar(50), "
                + USER_ID + " string, " + USER_NUMBER + " integer primary key)";

        String createMessagesQuery = "create table " + DATABASE_TABLE_MSGS +
                "(" + MSG_ID + " integer autoincrement primary key," + FROM_ID + " string," + TO_ID + " string," + MSG + " string)";
        db.execSQL(createUsersQuery);
        db.execSQL(createMessagesQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MSGS);
        onCreate(db);
    }

    public boolean insertUsers(String name, String user_id, int number) {
        String insertQuery = "insert or replace into "
                + DATABASE_TABLE_USERS + " (" + USERS_NAME + "," + USER_ID + "," + USER_NUMBER + ") values" +
                "(" + name + "," + user_id + "," + number + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertQuery);
        return true;
    }

    public boolean insertMessage(String message, String from_id, String to_id) {
        String insertQuery = "insert into "
                + DATABASE_TABLE_MSGS + " (" + FROM_ID + "," + TO_ID + "," + MSG + ") values" +
                "(" + from_id + "," + to_id + "," + message + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertQuery);
        return true;
    }

    public ArrayList<String> getAllMessages(String from_id, String to_id) {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_MSGS +
                " where " + FROM_ID + "=" + from_id + " and " + TO_ID + "=" + to_id + " order by "+MSG_ID, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(MSG)));
            res.moveToNext();
        }
        return array_list;
    }

}
