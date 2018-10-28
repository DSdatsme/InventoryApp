package com.example.dsdatsme.inventoryapp.Activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dsdatsme.inventoryapp.R;
import com.example.dsdatsme.inventoryapp.data.InventoryContract;
import com.example.dsdatsme.inventoryapp.utility.InventoryCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // class vars
    InventoryCursorAdapter mCursorAdapter;
    private static final int INVENTORY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this,null);
        inventoryListView.setAdapter(mCursorAdapter);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI,id);
                intent.setData(currentInventoryUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INVENTORY_LOADER,null,this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY};

        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {mCursorAdapter.swapCursor(data);}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {mCursorAdapter.swapCursor(null);}

    private void deleteAllInventories() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from database");
    }

    private void insertIntoInventory(){
        // values
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, "Cheese Cake");
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, 450);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, 15);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME, "Cake Factory");
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NUMBER, "1234567890");

        Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }

/////////////// menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertIntoInventory();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllInventories();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
