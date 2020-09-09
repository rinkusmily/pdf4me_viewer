/*
 * *
 *  * Created by Ali YÜCE on 3/2/20 11:18 PM
 *  * https://github.com/mayuce/
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/2/20 11:10 PM
 *
 */

package com.labters.documentscanner.helpers;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ScannerConstants {
    public static Bitmap selectedImageBitmap;
    public static String sleectedImagePath;
    public  static ArrayList<String> imageliststatic = new ArrayList<>();

    //public static String cropText="KIRP",backText="KAPAT",
    public static String cropText="Crop",backText="Close",
            imageError="No images selected, please try again.",
            cropError="You have not selected a valid field. Please correct it until the lines turn blue.";
    public static String cropColor="#6666ff",backColor="#ff0000",progressColor="#331199";
    public static boolean saveStorage=false;
}
