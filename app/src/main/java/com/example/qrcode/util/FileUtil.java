package com.example.qrcode.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {

    public static Uri saveBitmapToGallery(Context context, Bitmap bitmap, String fileName) throws IOException {
        OutputStream fos;
        Uri imageUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".png");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR Master");

            imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                fos = context.getContentResolver().openOutputStream(imageUri);
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                }
            }
        } else {
            // Older versions handled differently but since target is 36, Q logic is primary
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/QR Master";
            java.io.File file = new java.io.File(imagesDir);
            if (!file.exists()) file.mkdir();
            java.io.File image = new java.io.File(imagesDir, fileName + ".png");
            fos = new java.io.FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            // Trigger gallery scan
            android.media.MediaScannerConnection.scanFile(context, new String[]{image.getPath()}, null, null);
        }
        return imageUri;
    }
}
