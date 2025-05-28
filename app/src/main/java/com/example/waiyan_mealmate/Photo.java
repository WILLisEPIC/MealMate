package com.example.waiyan_mealmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Photo{

    private Context context;

    public Photo(Context context){
        this.context = context;
    }

    public byte[] stringToByteArray(Uri uri){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
            new Message(e.getMessage(), context).showErrorToast();
            return null;
        }
    }
}
