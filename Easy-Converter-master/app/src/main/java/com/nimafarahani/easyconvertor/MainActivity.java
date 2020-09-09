/*
    Daniel Sledd
    David Clay
    Nima Farahani

    **This is the Main Activity**
    * implements PickListener which provides listeners for the mutliImage pick gallery

 */


package com.nimafarahani.easyconvertor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
//<<<<<<< Updated upstream
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

//>>>>>>> Stashed changes

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity implements Picker.PickListener {



    public static final String EXTRA_MESSAGE = "com.example.sledd.helloworld";
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final int TAKE_PICTURE_REQUEST_CODE = 2;
    private File myPDF;
    private static LinkedList<Uri> imageList;
    private ArrayList<ImageEntry> mSelectedImages;// = new ArrayList<ImageEntry>();
    private RecyclerView mImageSampleRecycler;
    private RecyclerView.Adapter myAdapter;
    private ViewSwitcher switcher;
    File pdfFolder;

    private MenuItem mPortraitMenuItem;
    private MenuItem mLandscapeMenuItem;
    private boolean isPortrait = true;



    // initial code to initialize variables, set up up the recycle view and the toolbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // switcher which will be used to switch to a separate layout after the gallery button is pressed
        switcher = (ViewSwitcher) findViewById(R.id.profileSwitcher);

        mImageSampleRecycler = (RecyclerView) findViewById(R.id.my_recycler_view);

        setupRecycler();

        // recycle view must start with an adaptor or else layout will become onresponsive
        myAdapter = new ImageSamplesAdapter(mSelectedImages, MainActivity.this);
        mImageSampleRecycler.setAdapter(myAdapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);




    }

    // Populate toolbar with items from the menu xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mPortraitMenuItem = menu.findItem(R.id.action_portrait);
        mLandscapeMenuItem = menu.findItem(R.id.action_landscape);

        mPortraitMenuItem.setChecked(true);

        //menu.add(1, 1, 0, "Open the file");

        //menu.add(1, 2, 1, "Save the file");

       // menu.add(1, 3, 2, "Close the file");
        return true;
    }


    // listener which executes code for when items in the option menu are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gallery:
                // opens up gallery image picker to add additional images to recycle view
                pickImages();
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_clear:
                // Removes all the images from the recycle view
                mSelectedImages = null;
                myAdapter = new ImageSamplesAdapter(mSelectedImages, MainActivity.this);
                mImageSampleRecycler.setAdapter(myAdapter);
                return true;

            case R.id.action_portrait:
                // check marks portrait and disables landscape
                // changes flag
                if (mLandscapeMenuItem.isChecked()) {
                    item.setChecked(true);
                    mLandscapeMenuItem.setChecked(false);
                    isPortrait = true;
                }


                return true;

            case R.id.action_landscape:
                // check marks landscape and disables portrait
                // changes flag
                if (mPortraitMenuItem.isChecked()) {
                    item.setChecked(true);
                    mPortraitMenuItem.setChecked(false);
                    isPortrait = false;
                }



                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // initializes the recycle view with a grid layout with vertical orientation
    private void setupRecycler() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.num_columns_image_samples));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mImageSampleRecycler.setLayoutManager(gridLayoutManager);

    }

    //Button Listener for opening gallery page
    public void btnGallHandler(View view) {

        pickImages();

    }

    // Starts the multi image picker gallery
    private void pickImages(){

        //You can change many settings in builder like limit , Pick mode and colors
        new Picker.Builder(this, this ,R.style.AppTheme)
                .build()
                .startActivity();

    }

    // listeners for multi image picker
    // When the selected pictures are returned from picker gallery...
    @Override
    public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
        // call adaptor here for listview

        // if our global image array is empty, initialized it with the images returned from the multi-image picker gallery
        if (mSelectedImages == null)
            mSelectedImages = images;

        // else, add additional images to the bottom of the list
        else
            mSelectedImages.addAll(images);

        //setupImageSamples();
        Log.d(TAG, "Picked images  " + images.toString());

        // refresh adapter to display changes
        myAdapter = new ImageSamplesAdapter(mSelectedImages, MainActivity.this);
        mImageSampleRecycler.setAdapter(myAdapter);

        // if we aren't on the second layout, switch. else do nothing
        if (switcher.getNextView() ==  findViewById(R.id.myRelativeLayout1) )
            switcher.showNext();

        myAdapter.notifyDataSetChanged();
    }

    // When there are no picture selected from the picker gallery...
    @Override
    public void onCancel() {
        //Log.i(TAG, "User canceled picker activity");
        Toast.makeText(this, "User canceled picker activity", Toast.LENGTH_SHORT).show();

    }

    // button listener for converting images to pdf file
    public void onConvertPdfClick(View view) throws DocumentException, java.io.IOException
    {

        createPdf();
        Toast.makeText(this, "Pdf file created", Toast.LENGTH_SHORT).show();
    }



    // function that converts image data into a pdf file
    public void createPdf() throws  DocumentException, java.io.IOException
    {



       try {

           // creates folder with a pathname including the android storage directory
           pdfFolder = new File(Environment.getExternalStorageDirectory(), "EasyConvert"); // check this warning, may be important for diff API levels

           //ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

            // if the directory doesn't already exist, create it
           if (!pdfFolder.exists()) {
               pdfFolder.mkdirs();
               Log.i(TAG, "Folder successfully created");
           }

           // as long as we have images in the recycle view...
           if (mSelectedImages != null) {

               // progress.setVisibility(View.VISIBLE);

               // name the pdf with the current timestamp by default
               Date date = new Date();
               final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
               myPDF = new File(pdfFolder + "/" + timeStamp + ".pdf");



               // point an output stream to our created document
               OutputStream output = new FileOutputStream(myPDF);
               Document document;

               // create a document with difference page sizes depending on orientation
               if (isPortrait)
                   document = new Document(PageSize.A4, 50, 50, 50, 50);
               else
                   document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);

               PdfWriter.getInstance(document, output);

               long startTime, estimatedTime;

               document.open();
               //document.add(new Paragraph("~~~~Hello World!!~~~~"));


               // loop through all the images in the array
               for (int i = 0; i < mSelectedImages.size(); i++) {

                   // create bitmap from URI in our list
                   Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(mSelectedImages.get(i).path)));

                   ByteArrayOutputStream stream = new ByteArrayOutputStream();

                   startTime = System.currentTimeMillis();

                   // changed from png to jpeg, lowered processing time greatly
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                   estimatedTime = System.currentTimeMillis() - startTime;

                   Log.e(TAG, "compressed image into stream: " + estimatedTime);

                   byte[] byteArray = stream.toByteArray();

                   // instantiate itext image
                   com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(byteArray);

                   //img.scalePercent(40, 40);
                   //img.setAlignment(Element.ALIGN_CENTER);

                   //img.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());


                    // scale the image and set it to center
                   if (isPortrait) {
                       img.scaleToFit(PageSize.A4);
                       img.setAbsolutePosition(
                               (PageSize.A4.getWidth() - img.getScaledWidth()) / 2,
                               (PageSize.A4.getHeight() - img.getScaledHeight()) / 2
                       );
                   }
                   else
                   {
                       img.scaleToFit(PageSize.A4.rotate());
                       img.setAbsolutePosition(
                               (PageSize.A4.rotate().getWidth() - img.getScaledWidth()) / 2,
                               (PageSize.A4.rotate().getHeight() - img.getScaledHeight()) / 2
                               );
                   }
                   document.add(img);

                   // add a new page to the document to maintain 1 image per page
                   document.newPage();

                   float fractionalProgress = (i + 1) / mSelectedImages.size() * 100;




               }

               //progress.cancel();
               mSelectedImages = null;
               document.close();



               //start renaming
               LayoutInflater li = LayoutInflater.from(MainActivity.this);
               View promptsView = li.inflate(R.layout.layout, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       MainActivity.this);

               // set prompts.xml to alertdialog builder
               alertDialogBuilder.setView(promptsView);

               final EditText userInput = (EditText) promptsView
                       .findViewById(R.id.editTextDialogUserInput);

               // set dialog message
               alertDialogBuilder
                       .setCancelable(false)
                       .setPositiveButton("OK",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog,int id) {
                                       // get user input and set it to result
                                       // edit text

                                       String fileName = userInput.getText().toString();
                                       //myPDF = new File(pdfFolder + "/" + fileName + ".pdf");
                                       File newFile = new File(pdfFolder + "/" + fileName + ".pdf");
                                       boolean result = myPDF.renameTo(newFile);

                                       myPDF = newFile;

                                       Log.w(TAG, "myPDF renamed to: " + myPDF.toString() );
                                       promptForNextAction();
                                   }
                               })
                       .setNegativeButton("Cancel",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog,int id) {
                                       dialog.cancel();
                                       promptForNextAction();


                                   }
                               });

               // create alert dialog
               //AlertDialog alertDialog = alertDialogBuilder.create();

               // show it

               Log.e(TAG, "Before alertdialogue.show");


               alertDialogBuilder.show();



              // promptForNextAction();


               Log.e(TAG, "After alertdialogue.show, and before promptfornextaction");

               Log.e(TAG, "prompt for next action has completed");

               myAdapter = new ImageSamplesAdapter(mSelectedImages, MainActivity.this);
               mImageSampleRecycler.setAdapter(myAdapter);

               //progress.setVisibility(View.GONE);

           }
       }catch (DocumentException e){
           e.printStackTrace();
           myPDF.delete();

           // check if pdf file exists
           // if so, remove pdf file

       }
        catch (IOException e){
            e.printStackTrace();
            myPDF.delete();
            // check if pdf file exists
            // if so, remove pdf file
        }
    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.w(TAG, "Opening:  " + myPDF.toString());
        intent.setDataAndType(Uri.fromFile(myPDF), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void emailNote()
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        //email.putExtra(Intent.EXTRA_SUBJECT,"hello world");
        //email.putExtra(Intent.EXTRA_TEXT, "hello world");
        Uri uri = Uri.parse(myPDF.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    public void promptForNextAction()
    {
        final String[] options = { "email", "preview",
                "cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("PDF Saved, What Next?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("email")) {
                    emailNote();
                } else if (options[which].equals("preview")) {
                    viewPdf();
                } else if (options[which].equals("cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }



    // on activity result for old gallery and camera code (possibly obsolete)
    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            Uri uri = data.getData();

            if (imageList == null)
                imageList = new LinkedList<>();

            imageList.add(uri);
            Log.i(TAG, "This is the Image name: " + uri.getLastPathSegment());
            Log.i(TAG, "This the length of the list: " + imageList.size());


        } else if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            // data.getData() is null here
            Uri uri = data.getData();

            if (imageList == null)
                imageList = new LinkedList<>();

            imageList.add(uri);

            Log.e(TAG, "Added image from camera!");
        }
        else {
            Log.e(TAG, "oops");
            Log.e(TAG, Integer.toString(requestCode));
            Log.e(TAG, Integer.toString(resultCode) + " " +  Integer.toString(RESULT_OK) + " " + Integer.toString(RESULT_CANCELED));
            Log.e(TAG, data == null ? "data is null" : "data is not null");
            //Log.e(TAG, data.getData() == null ? "data.getData() is null" : "data.getData() is not null");

        }

    }
    */

}
