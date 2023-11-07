package com.example.cameinw.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class RealPathUtil {
    public static String realPath(Uri uri, Context context) {

        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};
        return getDataColumn(context, contentUri, selection, selectionArgs);
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
