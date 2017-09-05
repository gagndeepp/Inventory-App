package gagan.storemgmtapp.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;

import gagan.storemgmtapp.R;

import static gagan.storemgmtapp.database.ProductContract.ProductTable;

public class ProductCursorAdapter extends CursorAdapter {
    private Toast product_sold;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item_view, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.product_name_view);
        TextView quantityView = (TextView) view.findViewById(R.id.product_quantity_view);
        TextView priceView = (TextView) view.findViewById(R.id.product_price_view);
        Button button = (Button) view.findViewById(R.id.sale_button);
        ImageView imageList = (ImageView) view.findViewById(R.id.list_image_view);
        final int uri_id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable._ID));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.quantity));
        byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductTable.photo_id));
        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        nameView.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.name)));
        quantityView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.quantity))));
        priceView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.price))));
        imageList.setImageBitmap(theImage);
        quantity--;
        final int finalQuantity = quantity;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalQuantity >= 0) {
                    Uri item_uri = ContentUris.withAppendedId(ProductTable.final_uri, uri_id);
                    ContentValues productValues = new ContentValues();
                    productValues.put(ProductTable.quantity, finalQuantity);
                    int response = context.getContentResolver().update(item_uri, productValues, null, null);
                    if (response != 0) {
                        context.getContentResolver().notifyChange(item_uri, null);
                        if (product_sold != null)
                            product_sold.cancel();
                        product_sold = Toast.makeText(context, "Product Sold", Toast.LENGTH_SHORT);
                        product_sold.show();
                    }
                } else {
                    if (product_sold != null)
                        product_sold.cancel();
                    Toast product_sold = Toast.makeText(context, "Product Out of Stock", Toast.LENGTH_SHORT);
                    product_sold.show();
                }
            }
        });
    }
}
