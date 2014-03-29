package org.ligi.satoshiproof;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ImageFromIntentUriExtractor {

    final Context context;

    public ImageFromIntentUriExtractor(Context context) {
        this.context = context;
    }


    public File extract(Uri selectedImage) {
        final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        // some devices (OS versions return an URI of com.android instead of com.google.android
        if (selectedImage.toString().startsWith("content://com.android.gallery3d.provider")) {
            // use the com.google provider, not the com.android provider.
            selectedImage = Uri.parse(selectedImage.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
        }
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            // if it is a picasa image on newer devices with OS 3.0 and up
            if (selectedImage.toString().startsWith("content://com.google.android.gallery3d")) {
                columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    final Uri uriurl = selectedImage;
                    // Do this in a background thread, since we are fetching a large image from the web

                     return getBitmap("image_file_name.jpg", uriurl);

                }
            } else { // it is a regular local image file
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                return new File(filePath);
            }
        }
        // If it is a picasa image on devices running OS prior to 3.0
        else if (selectedImage != null && selectedImage.toString().length() > 0) {
            final Uri uriurl = selectedImage;
            // Do this in a background thread, since we are fetching a large image from the web
            return getBitmap("image_file_name.jpg", uriurl);

        }
        return null;
    }


    private File getBitmap(String tag, Uri url) {
        File cacheDir;
        // if the device has an SD card
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), ".OCFL311");
        } else {
            // it does not have an SD card
            cacheDir = context.getCacheDir();
        }


        cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        File f = new File(cacheDir, tag);

        try {
            InputStream is = null;
            if (url.toString().startsWith("content://com.google.android.gallery3d")) {
                is = context.getContentResolver().openInputStream(url);
            } else {
                is = new URL(url.toString()).openStream();
            }
            OutputStream os = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }


            os.close();
            return f;
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }
    }
}
