package gagan.storemgmtapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import gagan.storemgmtapp.database.ProductCursorAdapter;

import static gagan.storemgmtapp.database.ProductContract.ProductTable;
import static gagan.storemgmtapp.database.ProductContract.ProductTable.quantity;

public class ItemsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProductCursorAdapter adapter = null;
    private String[] projection = {ProductTable._ID, ProductTable.name, quantity, ProductTable.price, ProductTable.photo_id};
    private Toast activity_toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView empty_list_view = (TextView) findViewById(R.id.empty_list_view);
        setSupportActionBar(toolbar);
        setTitle("Store Management App");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent outIntent = new Intent(ItemsActivity.this, EditActivity.class);
                startActivity(outIntent);
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
        ListView main_list_view = (ListView) findViewById(R.id.products_list_view);
        adapter = new ProductCursorAdapter(this, null);
        main_list_view.setAdapter(adapter);
        main_list_view.setEmptyView(empty_list_view);
        main_list_view.setFocusable(true);
        main_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent output_intent = new Intent(ItemsActivity.this, EditActivity.class);
                Uri item_uri = ContentUris.withAppendedId(ProductTable.final_uri, id);
                output_intent.setData(item_uri);
                if (output_intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(output_intent);
                } else {
                    if (activity_toast != null)
                        activity_toast.cancel();
                    activity_toast = Toast.makeText(ItemsActivity.this, "No Compatible App Installed", Toast.LENGTH_SHORT);
                    activity_toast.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    private void input_dummy_entry() {
        ContentValues ret = new ContentValues();
        ret.put(ProductTable.name, "Dummy Name");
        ret.put(quantity, 10);
        ret.put(ProductTable.price, 100);
        Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.dummy);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo_output = baos.toByteArray();
        ret.put(ProductTable.photo_id, photo_output);
        Uri response = getContentResolver().insert(ProductTable.final_uri, ret);
        if (response != null) {
            if (activity_toast != null)
                activity_toast.cancel();
            activity_toast = Toast.makeText(ItemsActivity.this, "Dummy Entry Added", Toast.LENGTH_SHORT);
            activity_toast.show();
        }
    }

    private void delete_all_entry() {
        if (!adapter.isEmpty()) {
            int response = getContentResolver().delete(ProductTable.final_uri, null, null);
            if (response != 0) {
                if (activity_toast != null)
                    activity_toast.cancel();
                activity_toast = Toast.makeText(ItemsActivity.this, "All Products Are Deleted", Toast.LENGTH_SHORT);
                activity_toast.show();
            }
        } else {
            if (activity_toast != null)
                activity_toast.cancel();
            activity_toast = Toast.makeText(ItemsActivity.this, "Catalog is Already Epty", Toast.LENGTH_SHORT);
            activity_toast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_insert_dummy) {
            input_dummy_entry();
            return true;
        }
        if (id == R.id.action_delete_all_product) {
            delete_all_entry();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ProductTable.final_uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
