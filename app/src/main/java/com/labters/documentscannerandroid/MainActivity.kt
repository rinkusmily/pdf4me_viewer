/*
 * *
 *  * Created by Ali YÜCE on 3/2/20 11:18 PM
 *  * https://github.com/mayuce/
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/2/20 11:17 PM
 *
 */

package com.labters.documentscannerandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kotlinpermissions.KotlinPermissions
import com.kotlinpermissions.ifNotNullOrElse
import com.labters.documentscanner.ImageCropActivity
import com.labters.documentscanner.helpers.ScannerConstants
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var btnPick: Button
    lateinit var btnPickpicknext: Button
    lateinit var btnviewimages: Button
    lateinit var layoutbotom: LinearLayout
    lateinit var layoutcameragalary: LinearLayout
    lateinit var tvnoimagefound: TextView

    lateinit var imgcamera: ImageView
    lateinit var imggalary: ImageView

    lateinit var imgBitmap: ImageView
    lateinit var mCurrentPhotoPath: String
    var indexposition: Int = 0;
    var imagelist: ArrayList<Bitmap> = ArrayList()
    var imageliststring: ArrayList<String> = ArrayList()
    var FLAGCALL: Boolean = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //  Log.e("arinkuu",">>"+requestCode+" -- "+resultCode)
        if (requestCode == 1111 && resultCode == RESULT_OK && data != null) {
            var selectedImage = data.data
            var btimap: Bitmap? = null
            try {
                val inputStream = selectedImage?.let { contentResolver.openInputStream(it) }
                btimap = BitmapFactory.decodeStream(inputStream)
                ScannerConstants.selectedImageBitmap = btimap

                startActivityForResult(
                    Intent(MainActivity@ this, ImageCropActivity::class.java),
                    1234
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (requestCode == 1231 && resultCode == Activity.RESULT_OK) {
            ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                Uri.parse(mCurrentPhotoPath)
            )
            startActivityForResult(Intent(MainActivity@ this, ImageCropActivity::class.java), 1234)

        } else if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            if (ScannerConstants.selectedImageBitmap != null) {
                imgBitmap.setImageBitmap(ScannerConstants.selectedImageBitmap)
                imgBitmap.visibility = View.VISIBLE
                tvnoimagefound.visibility = View.GONE
                btnPick.visibility = View.GONE

                imagelist.add(ScannerConstants.selectedImageBitmap)


                // imageliststring.add(FileUtils.getImageUri(MainActivity@ this,ScannerConstants.selectedImageBitmap))
                imageliststring.add(ScannerConstants.sleectedImagePath)
                ScannerConstants.imageliststatic.add(ScannerConstants.sleectedImagePath)
                indexposition = ScannerConstants.imageliststatic.size
                Log.e("RINKU", ">>> " + imagelist);

                layoutcameragalary.visibility = View.GONE
                layoutbotom.visibility = View.VISIBLE

                invalidateOptionsMenu()

            } else {
                Toast.makeText(MainActivity@ this, "Not OK", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            try {
                var striamge = data!!.getStringExtra("filterimage");
                var imageuri = Uri.fromFile(File(striamge))
                Log.e("DATATAAA", ">>>>.  " + indexposition)

                imageliststring.set(indexposition - 1, striamge)
                ScannerConstants.imageliststatic.set(indexposition - 1, striamge)
                FLAGCALL = true
                imgBitmap.setImageURI(imageuri)
                imgBitmap.visibility = View.VISIBLE
                tvnoimagefound.visibility = View.GONE
                btnPick.visibility = View.GONE


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPick = findViewById(R.id.btnPick)
        btnPickpicknext = findViewById(R.id.pick_next_image)
        btnviewimages = findViewById(R.id.view_images)
        layoutbotom = findViewById(R.id.idlayoutbottom)
        layoutcameragalary = findViewById(R.id.idlayoutcameragalary)
        tvnoimagefound = findViewById(R.id.noimagesfound)

        imgcamera = findViewById(R.id.idaddimages)
        imggalary = findViewById(R.id.idcreatepdf)
        imgBitmap = findViewById(R.id.imgBitmap)


        layoutcameragalary.visibility = View.VISIBLE
        layoutbotom.visibility = View.GONE

        imgBitmap.visibility = View.GONE
        tvnoimagefound.visibility = View.VISIBLE
        // imagelist = emptyList();

        askPermission()


        btnPickpicknext.setOnClickListener(View.OnClickListener {
            showDialogCamera()
        })

        btnviewimages.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    MainActivity@ this,
                    PDFCreatorActivity::class.java
                ).putStringArrayListExtra("IMAGELIST", ScannerConstants.imageliststatic)
            )

            finish()
        })


        imgcamera.setOnClickListener(View.OnClickListener {

            FLAGCALL = false

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    Log.i("Main", "IOException")
                }
                if (photoFile != null) {
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    startActivityForResult(cameraIntent, 1231)
                }
            }


        })
        imggalary.setOnClickListener(View.OnClickListener {
            FLAGCALL = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1111)
        })
    }

    fun askPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            KotlinPermissions.with(this)
                .permissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .onAccepted { permissions ->
                    setView()
                }
                .onDenied { permissions ->
                    askPermission()
                }
                .onForeverDenied { permissions ->
                    Toast.makeText(
                        MainActivity@ this,
                        "You have to grant permissions! Grant them from app settings please.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .ask()
        } else {

            try {
                val calltype:String = intent.getStringExtra("CALLTYPE")
                if (calltype.equals("CAMERA")){

                    FLAGCALL = false

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (cameraIntent.resolveActivity(packageManager) != null) {
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                            Log.i("Main", "IOException")
                        }
                        if (photoFile != null) {
                            val builder = StrictMode.VmPolicy.Builder()
                            StrictMode.setVmPolicy(builder.build())
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                            startActivityForResult(cameraIntent, 1231)
                        }
                    }
                }else if (calltype.equals("GALARY")){
                    FLAGCALL = false
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 1111)
                }else{
                    setView()
                }
            } catch (e: Exception) {
                setView()
            }

            //setView()
        }
    }

    fun setView() {
        btnPick.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Hello")
            builder.setMessage("Where would you like to choose the image?\n")
            builder.setPositiveButton("Gallery") { dialog, which ->
                dialog.dismiss()
                FLAGCALL = false
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 1111)
            }
            builder.setNegativeButton("Camera") { dialog, which ->
                dialog.dismiss()
                FLAGCALL = false

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        Log.i("Main", "IOException")
                    }
                    if (photoFile != null) {
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                        startActivityForResult(cameraIntent, 1231)
                    }
                }
            }
            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()



            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        })





        showDialogCamera()

    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val image = File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            storageDir      // directory
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menusearch: MenuItem
        val menufrofile: MenuItem
        menusearch = menu!!.findItem(R.id.action_search);
        menufrofile = menu!!.findItem(R.id.action_profile);

        if (ScannerConstants.imageliststatic.size > 0) {
            menusearch!!.isVisible = true
            menufrofile!!.isVisible = true
        } else {
            menusearch!!.isVisible = false
            menufrofile!!.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)


    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {


            startActivity(
                Intent(
                    MainActivity@ this,
                    AllPdfActivity::class.java
                )
            )

            finish()
            true
        }
        R.id.action_profile -> {

            Log.e("BITMAPPP", ">>>> " + imgBitmap)
            if (ScannerConstants.selectedImageBitmap != null) {
                startActivityForResult(
                    Intent(MainActivity@ this, EditImageActivity::class.java),
                    5000
                )
            } else {
                Toast.makeText(MainActivity@ this, "Please add image First", Toast.LENGTH_LONG)
                    .show()
            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        startActivity(
            Intent(
                MainActivity@ this,
                AllPdfActivity::class.java
            )
        )

        finish()
    }

    fun showdialog() {

        val alertDialog = AlertDialog.Builder(this)
            //set icon
            .setIcon(android.R.drawable.ic_dialog_alert)
            //set title
            .setTitle("Are you sure to Exit")
            //set message
            .setMessage("If yes then application will close")
            //set positive button
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, i ->
                //set what would happen when positive button is clicked
                finish()
            })
            //set negative button
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                //set what should happen when negative button is clicked
                Toast.makeText(applicationContext, "Nothing Happened", Toast.LENGTH_LONG).show()
            })
            .show()
    }


    fun showDialogCamera() {

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Hello")
        builder.setMessage("Where would you like to choose the image?\n")
        builder.setPositiveButton("Gallery") { dialog, which ->
            dialog.dismiss()
            FLAGCALL = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1111)
        }
        builder.setNegativeButton("Camera") { dialog, which ->
            dialog.dismiss()

            FLAGCALL = false

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    Log.i("Main", "IOException")
                }
                if (photoFile != null) {
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    startActivityForResult(cameraIntent, 1231)
                }
            }
        }
        builder.setNeutralButton("Cancel") { dialog, _ ->
            startActivity(
                Intent(
                    MainActivity@ this,
                    PDFCreatorActivity::class.java
                ).putStringArrayListExtra("IMAGELIST", ScannerConstants.imageliststatic)
            )
            dialog.dismiss()
            finish()

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
