package com.example.dsdatsme.inventoryapp.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.dsdatsme.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private InventoryDbHelper mDbHelper;
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.INVENTORY_PATH,INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.INVENTORY_PATH + "/#",INVENTORY_ID);
    }
    // log filter
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // SQL cursor
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    // swl insert function
    private Uri insertInventory(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // checking for insert failure
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failure: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unable to update: " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // checking whether it has name or not
        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Inventory requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_PRICE)) {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_PRICE);
            // checking whether it has price to display or not
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Inventory requires valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            // checking whether it has some quantity or not
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Inventory requires valid quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME)) {
            String supName = values.getAsString(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME);
            // should have a supplier name
            if (supName == null) {
                throw new IllegalArgumentException("Inventory requires a Supplier Name");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }


    // sql deleting an entry
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                // Delete a single row given by the ID
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unable to delete this: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

}
