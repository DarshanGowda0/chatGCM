package com.dsi.darshan.samplegcm_chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by darshan on 21/11/15.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = "DAMM";

    public static final String DATABASE_NAME = "MyDb.db";
    public static final String DATABASE_TABLE_USERS = "users";
    public static final String DATABASE_TABLE_MSGS = "messages";
    public static final int DATABASE_NO = 2;
    public static final String USERS_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String USER_NUMBER = "user_number";
    public static final String MSG_ID = "msg_id";
    public static final String FROM_ID = "from_id";
    public static final String TO_ID = "to_id";
    public static final String MSG = "msg";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_NO);
        Log.d(TAG, "inside db helper");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersQuery = "create table " + DATABASE_TABLE_USERS + " (" + USERS_NAME + " varchar(50), "
                + USER_ID + " string, " + USER_NUMBER + " varchar(20) primary key)";

        String createMessagesQuery = "create table " + DATABASE_TABLE_MSGS +
                "(" + MSG_ID + " integer primary key autoincrement," + FROM_ID + " string," + TO_ID + " string," + MSG + " string)";
        db.execSQL(createUsersQuery);
        Log.d(TAG, "users table created successfully");
        db.execSQL(createMessagesQuery);
        Log.d(TAG, "messages table created successfully");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MSGS);
        onCreate(db);
    }

    public boolean insertUsers(String name, String user_id, String number) {
        String insertQuery = "insert or replace into "
                + DATABASE_TABLE_USERS + " (" + USERS_NAME + "," + USER_ID + "," + USER_NUMBER + ") values" +
                "(" +"\""+ name +"\""+ "," +"\""+ user_id+"\"" + ","+"\"" + number+"\"" + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertQuery);
        Log.d(TAG, "user "+name+" inserted successfully");

        return true;
    }

    public boolean insertMessage(String message, String from_id, String to_id) {
        String insertQuery = "insert into "
                + DATABASE_TABLE_MSGS + " (" + FROM_ID + "," + TO_ID + "," + MSG + ") values" +
                "(" +"\""+ from_id +"\""+ ","+"\"" + to_id+"\"" + ","+"\"" + message +"\""+ ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(insertQuery);
        Log.d(TAG, "message: " + message + " inserted successfully where from = "+from_id+" and to="+to_id);

        return true;
    }

    public ArrayList<String> getAllMessages(String from_id, String to_id) {
        ArrayList<String> array_list = new ArrayList<>();

        Log.d(TAG,"from "+from_id);

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DATABASE_TABLE_MSGS
                + " where " + FROM_ID + "="+"\"" + from_id
                +"\"" + " and " + TO_ID + "="+"\"" + to_id +"\""+
                " union select * from "+DATABASE_TABLE_MSGS+
                " where "+FROM_ID+"="+"\""+to_id+"\""+
                " and "+TO_ID+"="+"\""+from_id+"\""
                + " order by "+MSG_ID
                , null);
        Log.d(TAG,"in cursor");

        res.moveToFirst();
        Log.d(TAG, res.isAfterLast()+"");

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(MSG)));
            Log.d(TAG,res.getString(res.getColumnIndex(MSG)));
            res.moveToNext();
        }
        return array_list;
    }

}
