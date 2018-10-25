package com.swadeshiapps.video;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.swadeshiapps.video.POJO.Videos;

import java.io.File;
import java.io.IOException;

public class PopupCard extends RelativePopupWindow {

    public PopupCard(final Context context, final String path,final String thumb) {
        View v=LayoutInflater.from(context).inflate(R.layout.popup, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        TextView fabText=v.findViewById(R.id.popup_add);
        TextView shareText=v.findViewById(R.id.popup_share);
        TextView deleteText=v.findViewById(R.id.popup_delete);
        final DBHelper db=new DBHelper(context);
        if(db.isFab(path))
            fabText.setText("Remove from Favorites");
        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(db.isFab(path)) {
                    db.removeFav(path);
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.addFav(new Videos(path, thumb));
                    Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(context, db.getAll().size()+"", Toast.LENGTH_SHORT).show();
            }
        });
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(shareIntent, "Share Video..."));
            }
        });
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new AlertDialog.Builder(context)
                        .setTitle("Exit")
                        .setMessage("Do You really want to delete ?")
                        .setPositiveButton("Yes, Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File file=new File(path);
                                file.delete();
                                if(file.exists()){
                                    try {
                                        file.getCanonicalFile().delete();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(file.exists()){
                                        context.deleteFile(file.getName());
                                    }
                                }
                                if(!file.exists()) {
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    ListActivity.getInstance().fn_video();
                                }else
                                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No, Don't",null).show();
            }
        });

        // Disable default animation for circular reveal
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         //   setAnimationStyle(0);
        //}
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y, boolean fitInScreen) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularReveal(@NonNull final View anchor) {
        final View contentView = getContentView();
        contentView.post(new Runnable() {
            @Override
            public void run() {
                final int[] myLocation = new int[2];
                final int[] anchorLocation = new int[2];
                contentView.getLocationOnScreen(myLocation);
                anchor.getLocationOnScreen(anchorLocation);
                final int cx = anchorLocation[0] - myLocation[0] + anchor.getWidth()/2;
                final int cy = anchorLocation[1] - myLocation[1] + anchor.getHeight()/2;

                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                final float finalRadius = (float) Math.hypot(dx, dy);
                Animator animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                animator.setDuration(500);
                animator.start();
            }
        });
    }
}
