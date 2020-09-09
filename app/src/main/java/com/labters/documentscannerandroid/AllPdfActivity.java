package com.labters.documentscannerandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllPdfActivity extends AppCompatActivity {
    TextView tv_nodatafound;
    RecyclerView recyclerViewpdf;
    Context mContext;
    List<String> pdflist;
    FloatingActionButton floataddiomages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pdf);
        mContext = this;
        tv_nodatafound = findViewById(R.id.nopdffound);
        recyclerViewpdf = findViewById(R.id.idallpdf);
        floataddiomages = findViewById(R.id.btnaddimages);
        pdflist = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME";
        File dir = new File(path);
        Search_Dir(dir);


        floataddiomages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,MainActivity.class));
                finish();
            }
        });
    }


    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)){
                        pdflist.add(FileList[i].getName());
                        Log.e("FILELISTDATA",">>>>  "+FileList[i].getName());
                        //here you have that file.

                    }
                }
            }
        }


        setRecycleviewpdf();
    }


    void setRecycleviewpdf() {
        if (pdflist.size()>0) {
            tv_nodatafound.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
            recyclerViewpdf.setLayoutManager(layoutManager);
            AllPDFAdaptor adaptor = new AllPDFAdaptor(mContext, pdflist);
            recyclerViewpdf.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();
        }else {
            tv_nodatafound.setVisibility(View.VISIBLE);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.add_image_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
//        case R.id.action_add_images:
//
//            return(true);
//    }
//        return(super.onOptionsItemSelected(item));
//    }


    @Override
    public void onBackPressed() {



        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        AllPdfActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();


    }
}
