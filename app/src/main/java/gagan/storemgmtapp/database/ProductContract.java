package gagan.storemgmtapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    static final String content_auth = "gagan.storemgmtapp";
    private static final Uri base_uri = Uri.parse("content://" + content_auth);
    static final String path_product = "product";

    public static final class ProductTable implements BaseColumns {
        public static Uri final_uri = Uri.withAppendedPath(base_uri, path_product);
        static String Table_Name = "Products";
        public static String id = "_id";
        public static String name = "Name";
        public static String quantity = "Quantity";
        public static String price = "Price";
        public static String photo_id = "Photo";
    }
}
