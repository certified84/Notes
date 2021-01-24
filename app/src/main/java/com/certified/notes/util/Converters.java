package com.certified.notes.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class Converters {

    @TypeConverter
    public byte[] toByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outputStream.toByteArray();
        } else
            return null;
    }

    @TypeConverter
    public Bitmap toBitmap(byte[] byteArray) {
        if (byteArray != null)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        else
            return null;
    }
}
