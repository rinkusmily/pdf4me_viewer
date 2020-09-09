package com.labters.documentscannerandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.labters.documentscanner.helpers.ScannerConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewFullImageActivity extends AppCompatActivity {

    ImageView imageVie;
    Context mContext;
    String urlstr;
    int position;
    ArrayList<String> imagelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_image);
        mContext = this;
        urlstr = getIntent().getStringExtra("Image");
        position = getIntent().getIntExtra("position",0);
        imagelist = getIntent().getStringArrayListExtra("IMAGELIST");

     /*   Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(urlstr));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScannerConstants.selectedImageBitmap = bitmap;
        Intent intent = new Intent(mContext, EditImageActivity.class);
        startActivityForResult(intent, 5000);
*/


     Log.e("IMAGEEEEE",">>>>"+Uri.parse(urlstr));
        imageVie = findViewById(R.id.imgBitmap);
        imageVie.setImageURI(Uri.parse(urlstr));

        // Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        // imageVie.startAnimation(animZoomIn);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(urlstr));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ScannerConstants.selectedImageBitmap = bitmap;
                Intent intent = new Intent(mContext, EditImageActivity.class);
                startActivityForResult(intent, 5000);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            try {
                String striamge = data.getStringExtra("filterimage");
                Uri imageuri = Uri.fromFile(new File(striamge));
                Log.e("DATATAAA", ">>>>.  " + imageuri);
                imageVie.setImageURI(imageuri);
                imagelist.set(position,striamge);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*Intent intent = new Intent(mContext,PDFCreatorActivity.class);
        intent.putStringArrayListExtra("IMAGELIST",imagelist);
        finish();
        startActivity(intent);
*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext,PDFCreatorActivity.class);
        intent.putStringArrayListExtra("IMAGELIST",imagelist);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
