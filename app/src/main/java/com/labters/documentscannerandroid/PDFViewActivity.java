package com.labters.documentscannerandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewActivity extends AppCompatActivity {

    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_view);
        String pdfurl = getIntent().getStringExtra("PDFURL");

        Log.e("DATAURI",">>>> "+Uri.fromFile(new File(pdfurl)));
        pdfView = findViewById(R.id.pdfView);

        pdfView.fromUri(Uri.fromFile(new File(pdfurl)))
                .defaultPage(0)
                .spacing(10)
                .load();
    }
}
