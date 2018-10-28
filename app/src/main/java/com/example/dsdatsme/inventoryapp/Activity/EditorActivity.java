package com.example.dsdatsme.inventoryapp.Activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsdatsme.inventoryapp.R;
import com.example.dsdatsme.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // vars
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentInventoryUri;
    // ui
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierNumEditText;
    private TextView quantityTextView;
    private Button plusButton;
    private Button minusButton;
    private Button orderButton;

    private int quantity;
    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        mNameEditText = findViewById(R.id.edit_inventory_name);
        mPriceEditText = findViewById(R.id.edit_inventory_price);
        mQuantityEditText = findViewById(R.id.edit_inventory_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_inventory_suplierName);
        mSupplierNumEditText = findViewById(R.id.edit_inventory_suplierNum);
        quantityTextView = findViewById(R.id.edit_quantity_text_view);
        plusButton =  findViewById(R.id.button_plus);
        minusButton =  findViewById(R.id.button_minus);
        orderButton = findViewById(R.id.button_order);

        // creating a new inventory.
        if (mCurrentInventoryUri == null) {
            // This is a new inventory, so change the app bar to say "Add a inventory"
            setTitle(getString(R.string.editor_activity_title_new_inventory));
            orderButton.setVisibility(View.GONE);
            plusButton.setVisibility(View.GONE);
            minusButton.setVisibility(View.GONE);
            quantityTextView.setVisibility(View.GONE);

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mSupplierNumEditText.getText().toString().trim(), null));
                startActivity(intent);
            }
        });

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumEditText.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);
        orderButton.setOnTouchListener(mTouchListener);
    }

    private boolean saveInventory() {
        boolean allFilledOut = false;

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierNumberString = mSupplierNumEditText.getText().toString().trim();

        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierNumberString)) {
            return true;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.productNameReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, nameString);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.supplierNameReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierNumberString)) {
            Toast.makeText(this, getString(R.string.supplierEmailReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NUMBER, supplierNumberString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, price);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);

        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_inventory_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_inventory_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_inventory_failed), Toast.LENGTH_SHORT).show();
            } else {
                //update successful
                Toast.makeText(this, getString(R.string.editor_update_inventory_successful), Toast.LENGTH_SHORT).show();
            }
        }
        allFilledOut = true;
        return allFilledOut;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveInventory()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // fall back when no changes
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // selecting columns
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NUMBER
        };
        return new CursorLoader(this, mCurrentInventoryUri, projection,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Columns
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME);
            int supNumColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NUMBER);

            // indexes
            String name = cursor.getString(nameColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supNum = cursor.getString(supNumColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Views
            mNameEditText.setText(name);
            mSupplierNameEditText.setText(supName);
            mSupplierNumEditText.setText(supNum);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            quantityTextView.setText(Integer.toString(quantity));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierNumEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        quantityTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteInventory() {
        // check whether item is present or not
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void displayQuantity() {
        quantityTextView.setText(String.valueOf(quantity));
        mQuantityEditText.setText(String.valueOf(quantity));
    }

    public void increment(View view) {
        quantity++;
        displayQuantity();
    }

    public void decrement(View view) {
        if (quantity == 0) {
            Toast.makeText(this, R.string.minQuantity, Toast.LENGTH_SHORT).show();
        } else {
            quantity--;
            displayQuantity();
        }
    }

}
