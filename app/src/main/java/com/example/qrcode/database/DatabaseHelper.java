package com.example.qrcode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.qrcode.model.QRCodeModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "qr_master.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_FAVORITE = "is_favorite";
    public static final String COLUMN_SCANNED = "is_scanned";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_TYPE + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER, " +
                    COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                    COLUMN_SCANNED + " INTEGER DEFAULT 1);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public long insertQR(QRCodeModel qr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, qr.getContent());
        values.put(COLUMN_TYPE, qr.getType());
        values.put(COLUMN_TIMESTAMP, qr.getTimestamp());
        values.put(COLUMN_FAVORITE, qr.isFavorite() ? 1 : 0);
        values.put(COLUMN_SCANNED, qr.isScanned() ? 1 : 0);
        return db.insert(TABLE_HISTORY, null, values);
    }

    public List<QRCodeModel> getAllHistory() {
        List<QRCodeModel> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                QRCodeModel qr = new QRCodeModel();
                qr.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                qr.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                qr.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                qr.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                qr.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE)) == 1);
                qr.setScanned(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCANNED)) == 1);
                list.add(qr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<QRCodeModel> getFavorites() {
        List<QRCodeModel> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " WHERE " + COLUMN_FAVORITE + " = 1 ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                QRCodeModel qr = new QRCodeModel();
                qr.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                qr.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                qr.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                qr.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                qr.setFavorite(true);
                qr.setScanned(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCANNED)) == 1);
                list.add(qr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteQR(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void clearHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null);
    }

    public void updateFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_HISTORY, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
