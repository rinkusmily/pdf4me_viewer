package com.labters.documentscannerandroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.labters.documentscanner.helpers.ScannerConstants;
import com.labters.documentscannerandroid.helper.ItemClickListener;
import com.labters.documentscannerandroid.helper.ItemTouchHelperAdapter;
import com.labters.documentscannerandroid.helper.ItemTouchHelperViewHolder;
import com.labters.documentscannerandroid.helper.OnStartDragListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class AllFilterdAdaptor extends RecyclerView.Adapter<AllFilterdAdaptor.MyViewHolder>  implements ItemTouchHelperAdapter {


    Context mContext;
    ArrayList<String> imagelist;
    private final OnStartDragListener mDragStartListener;
      ItemClickListener itemClickListener;
      int pagenumber;

    public AllFilterdAdaptor(Context mContext, ArrayList<String> imagelist,OnStartDragListener mDragStartListener, ItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.imagelist = imagelist;
        this.mDragStartListener = mDragStartListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_layout_all_images, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Log.e("IMAGELIST",">>>>> "+imagelist.get(position));
        holder.imageView.setImageURI(Uri.parse(imagelist.get(position)));

        pagenumber = position+1;
        holder.tv_pagenumber.setText(""+pagenumber);

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });


        holder.imgedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickListener.onItemClick(position);

            }
        });

        holder.imageview_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Alert!");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this Image ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                imagelist.remove(position);
                                ScannerConstants.imageliststatic.remove(position);
                                notifyDataSetChanged();
                                dialog.cancel();
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
        return imagelist.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(imagelist, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        imagelist.remove(position);
        notifyItemRemoved(position);
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        ImageView imageView;
        ImageView imageview_delete;
        ImageView imgedit;
        TextView tv_pagenumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.idimages);
            imageview_delete = itemView.findViewById(R.id.id_delete_image);
            tv_pagenumber = itemView.findViewById(R.id.idpagenumber);
            imgedit = itemView.findViewById(R.id.id_edit_icon);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}
