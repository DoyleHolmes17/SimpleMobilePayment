package com.ainosi.doyle.ainomobilepayment.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ainosi.doyle.ainomobilepayment.entity.Trx;
import com.ainosi.doyle.ainomobilepayment.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farid on 25-Jul-17.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AinoDb";

    // table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_TRX = "transaksi";

    // Table Columns names
//    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "username";
    private static final String KEY_PASS = "password";
    private static final String KEY_MID = "mid";
    private static final String KEY_TID = "tid";
    private static final String KEY_SHIFT = "shift";
    private static final String KEY_TRXID = "trxId";
    private static final String KEY_TRXAMT = "trxAmt";
    private static final String KEY_TRXDATE = "trxDate";
    private static final String KEY_TRXISSID = "trxIssId";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                 + KEY_NAME + " TEXT UNIQUE,"
//                + KEY_ID + " INTEGER UNIQUE," + KEY_NAME + " TEXT,"
                + KEY_PASS + " TEXT" + ")";
        String CREATE_TRX_TABLE = "CREATE TABLE " + TABLE_TRX + "("
                + KEY_TRXID + " INTEGER UNIQUE," + KEY_MID + " TEXT,"
                + KEY_TID + " TEXT," + KEY_SHIFT + " TEXT,"
                + KEY_TRXAMT + " TEXT," + KEY_TRXDATE + " TEXT,"
                + KEY_TRXISSID + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TRX_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRX);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new user
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, user.getId()); // id
        values.put(KEY_NAME, user.getUsername()); //  name
        values.put(KEY_PASS, user.getPassword()); //  pass

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    // Adding new trx
    public void addTrx(Trx trx) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRXID, trx.getTrxid()); // id
        values.put(KEY_MID, trx.getMid()); // mid
        values.put(KEY_TID, trx.getTid()); // tid
        values.put(KEY_SHIFT, trx.getShift()); // shift
        values.put(KEY_TRXAMT, trx.getTrxamt()); // amount
        values.put(KEY_TRXDATE, trx.getTrxdate()); // date
        values.put(KEY_TRXISSID, trx.getTrxissid()); // issid

        // Inserting Row
        db.insertWithOnConflict(TABLE_TRX, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    // Getting single user
    public User getUser(String id) {
        Cursor cursor;
        User user;

        SQLiteDatabase db = this.getReadableDatabase();

        cursor = db.query(TABLE_USER, new String[]{KEY_NAME,
                        KEY_PASS}, KEY_NAME + "=?",
                new String[]{id}, null, null, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(cursor.getString(1), cursor.getString(2));
            cursor.close();

        } else {
            user = new User("kosong", "kosong");
            cursor.close();
        }

        // return user
        return user;

    }

    public Trx getTrx(String id) {
        Cursor cursor;
        Trx trx;
        SQLiteDatabase db = this.getReadableDatabase();

        cursor = db.query(TABLE_TRX, new String[]{KEY_TRXID,
                        KEY_MID, KEY_TID, KEY_SHIFT, KEY_TRXAMT, KEY_TRXDATE,
                        KEY_TRXDATE}, KEY_TRXID + "=?",
                new String[]{id}, null, null, null);
        cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            trx = new Trx(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4), cursor.getString(5),
                    cursor.getString(6));
            cursor.close();
        } else {
            trx = new Trx("0", "0", "0", 0, "0", "27/03/1991", "0");
            cursor.close();
        }

        // return cart
        return trx;
    }

    // Getting All User
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst() && cursor.moveToFirst()) {
            do {
                User user = new User();
//                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setUsername(cursor.getString(0));
                user.setPassword(cursor.getString(1));
                // Adding kasir to list
                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            User user = new User();
            user.setId(0);
            user.setUsername("kosong");
            user.setPassword("kosong");
            // Adding kasir to list
            userList.add(user);
        }

        // return Kasir list
        return userList;
    }

    // Getting All Trxs
    public List<Trx> getAllTrxs() {
        List<Trx> trxList = new ArrayList<Trx>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TRX;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Trx trx = new Trx();
                trx.setTrxid(Integer.parseInt(cursor.getString(0)));
                trx.setMid(cursor.getString(1));
                trx.setTid(cursor.getString(2));
                trx.setShift(cursor.getString(3));
                trx.setTrxamt(cursor.getString(4));
                trx.setTrxdate(cursor.getString(5));
                trx.setTrxissid(cursor.getString(6));
                // Adding trx to list
                trxList.add(trx);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Trx trx = new Trx();
            trx.setTrxid(0);
            trx.setMid("0");
            trx.setTid("0");
            trx.setShift("0");
            trx.setTrxamt("0");
            trx.setTrxdate("27/03/1991");
            trx.setTrxissid("0");
            // Adding trx to list
            trxList.add(trx);
        }

        // return trx list
        return trxList;
    }

    // Updating single user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, user.getId());
        values.put(KEY_NAME, user.getUsername());
        values.put(KEY_PASS, user.getPassword());

        // updating row
        return db.update(TABLE_USER, values, KEY_NAME + " = ?",
                new String[]{String.valueOf(user.getUsername())});
    }

    // Updating single Trx
    public int updateTrx(Trx trx) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRXID, trx.getTrxid());
        values.put(KEY_MID, trx.getMid());
        values.put(KEY_TID, trx.getTid());
        values.put(KEY_SHIFT, trx.getShift());
        values.put(KEY_TRXAMT, trx.getTrxamt());
        values.put(KEY_TRXDATE, trx.getTrxdate());
        values.put(KEY_TRXISSID, trx.getTrxissid());

        // updating row
        return db.update(TABLE_TRX, values, KEY_TRXID + " = ?",
                new String[]{String.valueOf(trx.getTrxid())});
    }

    // Deleting single User
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_NAME + " = ?",
                new String[]{String.valueOf(user.getUsername())});
        db.close();
    }

//    public void deleteKasir1(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_USER, KEY_ID + " = ?", new String[]{
//                "" + id,});
//        db.close();
//    }
//
//    public void deleteCart1(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_TRX, KEY_ID + " = ?", new String[]{
//                "" + id,});
//        db.close();
//    }

    // Deleting single Trx
    public void deleteTrx(Trx trx) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRX, KEY_TRXID + " = ?",
                new String[]{String.valueOf(trx.getTrxid())});
        db.close();
    }

    // Getting User Count
    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Getting Trx Count
    public int getTrxCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRX;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteTrxAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRX, null, null);
        db.close();
    }

    public void deleteUserAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
    }

    public void tambahDatauser() {
        addUser(new User("qq", "q"));
        addUser(new User("aa", "a"));
    }

    public void tambahDatatrx() {
        addTrx(new Trx("12334", "3211", "1",
                123, "3500", "08/11/2018", "0"));
    }

}
