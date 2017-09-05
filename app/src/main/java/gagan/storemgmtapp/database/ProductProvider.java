package gagan.storemgmtapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static gagan.storemgmtapp.database.ProductContract.ProductTable;
import static gagan.storemgmtapp.database.ProductContract.content_auth;
import static gagan.storemgmtapp.database.ProductContract.path_product;

public class ProductProvider extends ContentProvider {
    ProductDatabase main_database;
    public static final int products = 100;
    public static final int productsId = 101;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(content_auth, "/" + path_product, products);
        uriMatcher.addURI(content_auth, path_product + "/#", productsId);
    }

    @Override
    public boolean onCreate() {
        main_database = new ProductDatabase(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int urimatch = uriMatcher.match(uri);
        SQLiteDatabase database_modifier = main_database.getReadableDatabase();
        Cursor returnCursor = null;
        switch (urimatch) {
            case products:
                returnCursor = database_modifier.query(ProductTable.Table_Name, projection, selection, selectionArgs, null, null, null);
                break;
            case productsId:
                selection = ProductTable.id + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                returnCursor = database_modifier.query(ProductTable.Table_Name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e("Query Log Tag", "Error While Quering");
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database_modifier = main_database.getWritableDatabase();
        Uri returnUri = null;
        int caseMatch = uriMatcher.match(uri);
        switch (caseMatch) {
            case products:
                long response = database_modifier.insert(ProductTable.Table_Name, null, values);
                if (response == -1) {
                    Log.e("insert Log", "Failed to insert row for " + uri);
                    return null;
                }
                if (response != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                returnUri = ContentUris.withAppendedId(uri, response);
                break;
            default:
                Log.e("insertLog", "Insertion not Available for ");
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database_modifier = main_database.getWritableDatabase();
        int uriMatch = uriMatcher.match(uri);
        int returnUpdate = 0;
        switch (uriMatch) {
            case products:
                returnUpdate = database_modifier.delete(ProductTable.Table_Name, selection, selectionArgs);
                if (returnUpdate != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case productsId:
                selection = ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                returnUpdate = database_modifier.delete(ProductTable.Table_Name, selection, selectionArgs);
                if (returnUpdate != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        return returnUpdate;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database_modifier = main_database.getWritableDatabase();
        int uriMatch = uriMatcher.match(uri);
        int returnUpdate = 0;
        switch (uriMatch) {
            case products:
                returnUpdate = database_modifier.update(ProductTable.Table_Name, values, selection, selectionArgs);
                if (returnUpdate != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case productsId:
                selection = ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                returnUpdate = database_modifier.update(ProductTable.Table_Name, values, selection, selectionArgs);
                if (returnUpdate != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        return returnUpdate;
    }
}
