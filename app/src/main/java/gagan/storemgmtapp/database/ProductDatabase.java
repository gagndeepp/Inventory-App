package gagan.storemgmtapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static gagan.storemgmtapp.database.ProductContract.ProductTable;

class ProductDatabase extends SQLiteOpenHelper {
    private static String databaseName = "products.db";
    private static Integer databaseVersion = 1;
    private String execTable = "CREATE TABLE " +
            ProductTable.Table_Name +
            " (" + ProductTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProductTable.name + " TEXT, " + ProductTable.quantity + " INTEGER, " +
            ProductTable.price + " INTEGER, " + ProductTable.photo_id + " BLOB)";

    ProductDatabase(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(execTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
