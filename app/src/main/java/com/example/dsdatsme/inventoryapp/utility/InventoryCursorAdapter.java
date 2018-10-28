package com.example.dsdatsme.inventoryapp.utility;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsdatsme.inventoryapp.R;
import com.example.dsdatsme.inventoryapp.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int mQuantity;
        final long id;

        // Views
        id = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView summarysecTextView = (TextView) view.findViewById(R.id.summarysec);
        TextView buyTextView = (TextView) view.findViewById(R.id.buy);

        // Columns
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);

        //indexes
        final String inventoryName = cursor.getString(nameColumnIndex);
        final String inventoryPrice = cursor.getString(priceColumnIndex);
        String inventoryQuantity = cursor.getString(quantityColumnIndex);
        mQuantity = Integer.parseInt(inventoryQuantity);

        nameTextView.setText(inventoryName);
        summaryTextView.setText(inventoryPrice);
        summarysecTextView.setText(inventoryQuantity);

        buyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, inventoryName);
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, inventoryPrice);
                    values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, mQuantity >= 1 ? mQuantity - 1 : 0);

                    Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);

                    int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);
                    if (rowsAffected == 0 || mQuantity == 0) {
                        Toast.makeText(context, context.getString(R.string.sell_product_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}