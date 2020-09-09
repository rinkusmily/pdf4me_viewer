package com.labters.documentscannerandroid;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AllPDFAdaptor extends RecyclerView.Adapter<AllPDFAdaptor.MyViewHolder> {
    Context mContext;
    List<String> pdflist;

    public AllPDFAdaptor(Context mContext, List<String> pdflist) {
        this.mContext = mContext;
        this.pdflist = pdflist;
        Collections.reverse(this.pdflist);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_layout_pdflist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String strdfdate = null;
        try {
            String[] separated = pdflist.get(position).split("  ");
            String strfilename = separated[0];
            strdfdate = separated[1];
            holder.tvpdftime.setText(strdfdate.replace(".pdf",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvapdname.setText(pdflist.get(position).replace(".pdf",""));


        holder.parentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME/";
                String pdflistitem = pdflist.get(position);
                String pdfurl = path + pdflistitem;

                 /*Intent intent = new Intent(mContext,PDFViewActivity.class);
                 intent.putExtra("PDFURL",pdfurl);
                 mContext.startActivity(intent);
*/


                File pdfFile = new File(pdfurl);
                Uri data = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);


                // Setting the intent for pdf reader
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(data, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    mContext.startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                }

            }
        });


        holder.imgsharefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME/";
                String pdflistitem = pdflist.get(position);
                String pdfurl = path + pdflistitem;

                File pdfFile = new File(pdfurl);
                Uri data = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);


                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("*/*");
                share.putExtra(Intent.EXTRA_STREAM, data);
                mContext.startActivity(Intent.createChooser(share, "Share File"));

            }
        });


        holder.imgdeletefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF4ME/";
                String pdflistitem = pdflist.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Alert!");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this File " + pdflistitem + " ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                File filePath = new File(path, pdflistitem);
                                boolean deleted = filePath.delete();
                                pdflist.remove(position);
                                notifyDataSetChanged();
                                dialog.cancel();
                                Toast.makeText(mContext, "File Deleted",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert!");
                alert.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return pdflist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvapdname;
        RelativeLayout parentlayout;
        ImageView imgsharefile, imgdeletefile;
        TextView tvpdftime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvapdname = itemView.findViewById(R.id.idpdfname);
            parentlayout = itemView.findViewById(R.id.idparentlayout);
            imgsharefile = itemView.findViewById(R.id.idsharefile);
            imgdeletefile = itemView.findViewById(R.id.iddeletefile);
            tvpdftime = itemView.findViewById(R.id.idpdfnamedate);

        }
    }


}
