package gagan.storemgmtapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static android.widget.Toast.makeText;
import static gagan.storemgmtapp.database.ProductContract.ProductTable;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView price_edit_field;
    private TextView quantity_edit_field;
    private TextView name_edit_field;
    private ImageView edit_activity_image;
    private Uri item_uri = null;
    private Boolean insert_mode = true;
    private String[] projection = {ProductTable._ID, ProductTable.name, ProductTable.quantity, ProductTable.price, ProductTable.photo_id};
    private Boolean product_changed = false;
    private Boolean delete_executed = false;
    private Boolean finish_flag = true;
    private int final_quantity;
    private Toast activity_toast;
    private ContentValues productValues = new ContentValues();
    private Integer Access_Camera = 45;
    private Boolean image_added = false;
    private View.OnTouchListener changeListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            product_changed = true;
            return false;
        }
    };

    private void deleteConfirmation() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setMessage("You Sure Deleting this Item");
        alert_builder.setPositiveButton("Yes,Delete it", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete_executed = true;
                int del = getContentResolver().delete(item_uri, null, null);
                if (del != 0) {
                    if (activity_toast != null)
                        activity_toast.cancel();
                    activity_toast = makeText(EditActivity.this, "Item Deleted", Toast.LENGTH_SHORT);
                    activity_toast.show();
                    finish();
                }
            }
        });
        alert_builder.setNegativeButton("No! Wait", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alert_builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!product_changed) {
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setMessage("You have Unsaved Changes");
        alert_builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert_builder.setNegativeButton("No! Wait", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alert_builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        name_edit_field = (TextView) findViewById(R.id.edit_name_field);
        quantity_edit_field = (TextView) findViewById(R.id.edit_quantity_field);
        price_edit_field = (TextView) findViewById(R.id.edit_price_field);
        name_edit_field.setOnTouchListener(changeListener);
        quantity_edit_field.setOnTouchListener(changeListener);
        price_edit_field.setOnTouchListener(changeListener);
        final Button add_quantity = (Button) findViewById(R.id.add_quantity);
        final Button dec_quantity = (Button) findViewById(R.id.decrease_quantity);
        Button order_product = (Button) findViewById(R.id.order_intent_button);
        Button capture_image_button = (Button) findViewById(R.id.edit_activity_add_image);
        Button remove_image_button = (Button) findViewById(R.id.edit_activity_remove_image);
        edit_activity_image = (ImageView) findViewById(R.id.edit_activity_image_view);
        edit_activity_image.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.dummy));
        add_quantity.setOnTouchListener(changeListener);
        dec_quantity.setOnTouchListener(changeListener);
        capture_image_button.setOnTouchListener(changeListener);
        remove_image_button.setOnTouchListener(changeListener);
        order_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Order Details are As Below \nProduct > " + name_edit_field.getText().toString() + "\nQuantity " +
                        quantity_edit_field.getText().toString() + "\nPrice> " + price_edit_field.getText().toString() + "\n Sent via StoreMgmtApp Rights Reserved Gagan";
                Intent order_intent = new Intent(Intent.ACTION_SEND);
                order_intent.setData(Uri.parse("mailto:"));
                order_intent.setType("text/plain");
                order_intent.putExtra(Intent.EXTRA_SUBJECT, name_edit_field.getText().toString());
                order_intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(order_intent);
            }
        });
        Intent item_intent = getIntent();
        item_uri = item_intent.getData();
        if (item_uri == null) {
            setTitle("Add a Product");
            insert_mode = true;
            order_product.setVisibility(View.INVISIBLE);
            remove_image_button.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else {
            setTitle("Edit a Product");
            insert_mode = false;
            order_product.setVisibility(View.VISIBLE);
            remove_image_button.setVisibility(View.VISIBLE);
            price_edit_field.setEnabled(false);
            invalidateOptionsMenu();
        }
        add_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity_edit_field.getText().toString().trim().length() == 0) {
                    final_quantity = 0;
                    Log.e("LOG", "onClick: ZERO THA YA NAHI");
                    quantity_edit_field.setText(String.valueOf(0));
                } else {
                    final_quantity = Integer.parseInt(quantity_edit_field.getText().toString());
                    if (final_quantity > 0) {
                        if (dec_quantity.getVisibility() == View.INVISIBLE) {
                            dec_quantity.setVisibility(View.VISIBLE);
                        }
                    }
                    if (final_quantity > 29) {
                        add_quantity.setVisibility(View.INVISIBLE);
                        quantity_edit_field.setText(String.valueOf(30));
                        if (activity_toast != null)
                            activity_toast.cancel();
                        activity_toast = makeText(getApplicationContext(), "Product Quantity Limit : 30", Toast.LENGTH_SHORT);
                        activity_toast.show();
                    } else {
                        final_quantity++;
                        quantity_edit_field.setText(Integer.toString(final_quantity));
                    }
                }
            }
        });
        quantity_edit_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (quantity_edit_field.getText().toString().trim().length() == 0) {
                    Log.e("LOG ", "onClick: ZERO THA YA NAHI");
                } else {
                    if (Integer.parseInt(quantity_edit_field.getText().toString()) > 30) {
                        quantity_edit_field.setText(String.valueOf(30));
                        add_quantity.setVisibility(View.INVISIBLE);
                        if (activity_toast != null)
                            activity_toast.cancel();
                        activity_toast = makeText(getApplicationContext(), "Product Quantity Limit : 30", Toast.LENGTH_SHORT);
                        activity_toast.show();
                    }
                }
            }
        });
        dec_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_quantity = Integer.parseInt(quantity_edit_field.getText().toString());
                if (final_quantity < 1)
                    dec_quantity.setVisibility(View.INVISIBLE);
                if (final_quantity != 0) {
                    final_quantity--;
                    quantity_edit_field.setText(Integer.toString(final_quantity));
                }
                if (final_quantity > 30) {
                    quantity_edit_field.setText(String.valueOf(30));
                }
                if (final_quantity <= 30) {
                    if (add_quantity.getVisibility() == View.INVISIBLE) {
                        add_quantity.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        capture_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Access_Camera);
            }
        });
        remove_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap photo = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.dummy);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo_output = baos.toByteArray();
                productValues.put(ProductTable.photo_id, photo_output);
                edit_activity_image.setImageBitmap(photo);
            }
        });
        if (!insert_mode)
            getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Access_Camera && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            edit_activity_image.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] photo_output = baos.toByteArray();
            productValues.put(ProductTable.photo_id, photo_output);
            image_added = true;
        }
    }

    private void saveProduct() {
        if (quantity_edit_field.getText().toString().trim().length() == 0) {
            finish_flag = false;
            if (activity_toast != null)
                activity_toast.cancel();
            activity_toast = makeText(this, "Please Check Input", Toast.LENGTH_SHORT);
            activity_toast.show();
        } else {
            if (Integer.parseInt(quantity_edit_field.getText().toString()) > 30) {
                quantity_edit_field.setText("30");
                Toast.makeText(getApplicationContext(), "Product Quantity Limit : 30", Toast.LENGTH_SHORT).show();
            }
        }
        productValues.put(ProductTable.name, name_edit_field.getText().toString().trim());
        productValues.put(ProductTable.quantity, quantity_edit_field.getText().toString().trim());
        productValues.put(ProductTable.price, price_edit_field.getText().toString().trim());
        if (!image_added && insert_mode) {
            Bitmap photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.dummy);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] photo_output = baos.toByteArray();
            productValues.put(ProductTable.photo_id, photo_output);
            edit_activity_image.setImageBitmap(photo);
        }
        if (productValues.getAsString(ProductTable.name).isEmpty() || productValues.getAsString(ProductTable.quantity).isEmpty() || productValues.getAsString(ProductTable.price).isEmpty() || !image_added) {
            finish_flag = false;
            if (activity_toast != null)
                activity_toast.cancel();
            activity_toast = makeText(this, "Please Check Input", Toast.LENGTH_SHORT);
            activity_toast.show();
        } else {
            Uri responseUri = null;
            if (insert_mode) {
                responseUri = getContentResolver().insert(ProductTable.final_uri, productValues);
            } else {
                getContentResolver().update(item_uri, productValues, null, null);
                if (activity_toast != null)
                    activity_toast.cancel();
                activity_toast = makeText(this, "Product was Saved", Toast.LENGTH_SHORT);
                activity_toast.show();
            }
            if (responseUri != null && insert_mode && finish_flag) {
                if (activity_toast != null)
                    activity_toast.cancel();
                activity_toast = makeText(this, "Product was Inserted", Toast.LENGTH_SHORT);
                activity_toast.show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, item_uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!delete_executed) {
            data.moveToFirst();
            name_edit_field.setText(data.getString(data.getColumnIndexOrThrow(ProductTable.name)));
            quantity_edit_field.setText(data.getString(data.getColumnIndexOrThrow(ProductTable.quantity)));
            price_edit_field.setText(data.getString(data.getColumnIndexOrThrow(ProductTable.price)));
            final_quantity = (data.getInt(data.getColumnIndexOrThrow(ProductTable.quantity)));
            byte[] photo = data.getBlob(data.getColumnIndexOrThrow(ProductTable.photo_id));
            ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
            Bitmap final_image = BitmapFactory.decodeStream(imageStream);
            edit_activity_image.setImageBitmap(final_image);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name_edit_field.setText(null);
        quantity_edit_field.setText(null);
        price_edit_field.setText(null);
        edit_activity_image.setImageBitmap(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu_items, menu);
        if (!insert_mode)
            menu.findItem(R.id.action_delete_edit).setVisible(true);
        else {
            menu.findItem(R.id.action_delete_edit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_edit:
                finish_flag = true;
                saveProduct();
                if (finish_flag)
                    finish();
                break;
            case R.id.action_delete_edit:
                deleteConfirmation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
