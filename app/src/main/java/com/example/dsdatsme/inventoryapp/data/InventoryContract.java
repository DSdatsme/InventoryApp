package com.example.dsdatsme.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.dsdatsme.inventoryapp";
    /// content URL
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String INVENTORY_PATH = "inventory";

    // inner class here
    public static abstract class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, INVENTORY_PATH);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;

        public final static String TABLE_NAME = "Inventory";

        //Column names
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_INVENTORY_NAME = "Name";
        public final static String COLUMN_INVENTORY_PRICE = "Price";
        public final static String COLUMN_INVENTORY_QUANTITY = "Quantity";

        public final static String COLUMN_INVENTORY_SUPPLIER_NAME = "Supplier_Name";
        public final static String COLUMN_INVENTORY_SUPPLIER_NUMBER = "Supplier_Number";

    }
}

