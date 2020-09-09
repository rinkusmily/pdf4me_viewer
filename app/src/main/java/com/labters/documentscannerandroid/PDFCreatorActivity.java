package com.labters.documentscannerandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.labters.documentscannerandroid.helper.ItemClickListener;
import com.labters.documentscannerandroid.helper.OnStartDragListener;
import com.labters.documentscannerandroid.helper.SimpleItemTouchHelperCallback;
import com.labters.documentscannerandroid.permision.PermissionsActivity;
import com.labters.documentscannerandroid.permision.PermissionsChecker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.labters.documentscannerandroid.LogUtils.LOGE;

public class PDFCreatorActivity extends AppCompatActivity implements OnStartDragListener {

    Context mContext;
    PermissionsChecker checker;
    ProgressDialog mProgressDialog;

    ArrayList<String> imagelist;
    RecyclerView recyclerViewallimages;
    TextView tvno_imagesfound;
    ImageView button_createpdf;
    String strpdfname;
    private ItemTouchHelper mItemTouchHelper;

    ImageView addImages;

    int clickposition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_creator);
        mContext = this;

        recyclerViewallimages = findViewById(R.id.idrecycleviewallimags);
        tvno_imagesfound = findViewById(R.id.noimagesfound);
        button_createpdf = findViewById(R.id.idcreatepdf);
        addImages = findViewById(R.id.idaddimages);

        imagelist = getIntent().getStringArrayListExtra("IMAGELIST");

        Log.e("IMAGELISTTT", ">>>>" + imagelist);
        setUiRecycleview();

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("CALLTYPE","CAMERA");
                finish();
                startActivity(intent);
            }
        });


        button_createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("CALLTYPE","GALARY");
                finish();
                startActivity(intent);


               // checker = new PermissionsChecker(mContext);




            }
        });

        // viewPdf("newFileimage.pdf", "Dir");
    }


    void setUiRecycleview() {

        if (imagelist.size() > 0) {
            tvno_imagesfound.setVisibility(View.GONE);
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);
            recyclerViewallimages.setLayoutManager(manager);
            //  recyclerViewallimages.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            AllFilterdAdaptor adaptor = new AllFilterdAdaptor(mContext, imagelist, this, new ItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    clickposition = position;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imagelist.get(position)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ScannerConstants.selectedImageBitmap = bitmap;
                    Intent intent = new Intent(mContext, EditImageActivity.class);
                    startActivityForResult(intent, 5000);

                    /*
                    Intent intent = new Intent(mContext,ViewFullImageActivity.class);
                    intent.putExtra("Image",imagelist.get(position));
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("IMAGELIST",imagelist);
                    ((Activity)mContext).finish();
                    mContext.startActivity(intent);*/
                }
            });
            recyclerViewallimages.setHasFixedSize(true);
            recyclerViewallimages.setAdapter(adaptor);

            adaptor.notifyDataSetChanged();

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adaptor);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerViewallimages);

        } else {
            tvno_imagesfound.setVisibility(View.VISIBLE);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
           // Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }


        if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            try {
                String striamge = data.getStringExtra("filterimage");
                Uri imageuri = Uri.fromFile(new File(striamge));
                Log.e("DATATAAA", ">>>>.  " + imageuri);

                imagelist.set(clickposition,striamge);
                setUiRecycleview();
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


    private class CreatePDfTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("PDF Creating please wait");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document doc = new Document();

            try {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME";
                //  String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();

                String currentdtetime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                //  File file = new File(dir, currentdtetime +"pdf4mefile.pdf");
                File file = new File(dir, strpdfname + ".pdf");
                FileOutputStream fOut = new FileOutputStream(file);

                PdfWriter.getInstance(doc, fOut);

                //open the document
                doc.open();


                for (int i = 0; i < imagelist.size(); i++) {
                    Image image = Image.getInstance(imagelist.get(i));  // Change image's name and extension.
                    float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                            - doc.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    doc.add(image);
                    Paragraph p1 = new Paragraph(" ");
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    //add paragraph to document
                    doc.add(p1);
                }

                // Paragraph p1 = new Paragraph(text);
                // p1.setAlignment(Paragraph.ALIGN_CENTER);

                //add paragraph to document
                //  doc.add(p1);

            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
            } finally {
                doc.close();
            }

            //  viewPdf("newFileimage.pdf", "Dir");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            ScannerConstants.imageliststatic.clear();
            Toast.makeText(mContext, "Your PDF Create Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext,AllPdfActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( intent);
            finish();

        }

    }

    public void createandDisplayPdf(String text) {

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME";
            //  String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            String currentdtetime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            File file = new File(dir, currentdtetime + "pdf4mefile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();


            for (int i = 0; i < imagelist.size(); i++) {
                Image image = Image.getInstance(imagelist.get(i));  // Change image's name and extension.
                float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                        - doc.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                doc.add(image);
                Paragraph p1 = new Paragraph(" ");
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                //add paragraph to document
                doc.add(p1);
            }

            // Paragraph p1 = new Paragraph(text);
            // p1.setAlignment(Paragraph.ALIGN_CENTER);

            //add paragraph to document
            //  doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
        mProgressDialog.dismiss();
        Toast.makeText(mContext, "Your PDF Create Success", Toast.LENGTH_SHORT).show();

        //  viewPdf("newFileimage.pdf", "Dir");
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri data = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

        // Uri data = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID +".provider",pdfFile);
        // Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(data, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(PDFCreatorActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    public URL getImageUri(Context inContext, Bitmap inImage) {
        URL url = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewpdf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_viewpdf:

                if (imagelist.size()>0) {
                    showDialog(mContext);
                }else {
                    Toast.makeText(mContext, "Please Add Image First to create PDF", Toast.LENGTH_SHORT).show();
                }

               /* Intent intent = new Intent(mContext, AllPdfActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);*/
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    public void showDialog(Context activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = PDFCreatorActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pdf_name_layout, null);
       alertDialog.setView(dialogView);


        final EditText et = (EditText) dialogView.findViewById(R.id.et);

        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String strfilename = et.getText().toString();
                if (strfilename.isEmpty()){
                    et.setError("Enter PDF Name");
                }else {

                    String currentdtetime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                    strpdfname = strfilename.replace(" ","")+"  "+currentdtetime;
                    new CreatePDfTask().execute();
                    dialog.cancel();
                }

               // dialog.cancel(); // Your custom code
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish(); // Your custom code
            }
        });
        alertDialog.show();
    }








      /*  final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_pdf_name_layout);

        final EditText et = dialog.findViewById(R.id.et);


        Button btnok = (Button) dialog.findViewById(R.id.btnok);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 strpdfname = et.getText().toString();
                if (strpdfname.isEmpty()){
                    et.setError("Enter PDF Name");
                }else {
                    new CreatePDfTask().execute();
                    dialog.dismiss();
                }


            }
        });

        Button btncn = (Button) dialog.findViewById(R.id.btncn);
        btncn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();*/




    @Override
    public void onBackPressed() {

        Intent intent = new Intent(mContext,AllPdfActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}
