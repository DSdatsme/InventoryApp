package com.example.dsdatsme.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    // sql db file
    private static final String DATABASE_NAME = "petShop.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating a table
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NUMBER + " TEXT);";

        // execute query
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}