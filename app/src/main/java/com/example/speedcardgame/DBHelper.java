package com.example.speedcardgame;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASENAME = "USERDB";
    private static final int DATABAESVERSION = 1;

    private static final String TABLE_NAME = "tbluser";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_XP = "xp";
    private static final String COLUMN_BEST_SCORE = "score";
    // -----------------------
    private SQLiteDatabase database;


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "("
                + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT NOT NULL,"
                + COLUMN_PASSWORD + " text,"
                + COLUMN_XP + " INTEGER,"
                + COLUMN_BEST_SCORE + " INTEGER);";

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        } catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(@Nullable Context context) {
        super(context, DATABASENAME, null, DATABAESVERSION);
    }

    public void insertTblUser(String username, String password) {
        String insert = " INSERT INTO " + TABLE_NAME + "("
                + COLUMN_USERNAME + ","
                + COLUMN_PASSWORD + ","
                + COLUMN_XP + ","
                + COLUMN_BEST_SCORE +")"
                //       + COLUMN_LAST_LOGIN_DATE + ")"
                + "VALUES ("
                + "'" + username + "'" + ","
                + "'" + password + "'" + ","
                + 0 + ","
                + -1 +
                ");";
        database = getWritableDatabase(); // access for change
        try {
            database.execSQL(insert);
        } catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        ;
    }

    public Cursor selectByName (String name) {
        Cursor cursor = null;
        String selectQuery = " SELECT * FROM tbluser WHERE username = " + "'" + name + "'" + " ORDER by _id ";
        database = getReadableDatabase();
        try {
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();  //
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return cursor;
    }

    public Cursor validationPass(String name, String password) {
        Cursor cursor = null;
        String selectQuery = " SELECT * FROM tbluser WHERE username = " + "'" + name + "'" + "AND password = " + "'" + password + "'" + " ORDER by _id ";

        database = getReadableDatabase();
        try {
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();  //
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return cursor;
    }

    public void setXP(int xp, int user){
        /*String updateStatement = "UPDATE tbluser SET "
                + COLUMN_BEST_SCORE + " = " + seconds + ", "
                + COLUMN_XP + " += " + xp
                + " WHERE " + COLUMN_USERNAME + " = " + user;*/
        String updateStatement = "UPDATE tbluser SET xp = xp + " + xp + " WHERE _id = " + user;
        SQLiteStatement s = database.compileStatement(updateStatement);
        s.executeUpdateDelete();
    }
    public void setScore(int seconds, int user) {
        String updateStatement = "UPDATE tbluser SET score = " + seconds + " WHERE _id = " + user;
        SQLiteStatement s = database.compileStatement(updateStatement);
        s.executeUpdateDelete();
    }

    public Cursor TopScorers(){
        Cursor cursor = null;
        String selectQuery = " SELECT * FROM tbluser WHERE score != -1 ORDER by score";

        database = getReadableDatabase();
        try {
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();  //
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return cursor;
    }
}
