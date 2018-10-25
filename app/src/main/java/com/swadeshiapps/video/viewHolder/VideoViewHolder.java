package com.swadeshiapps.video.viewHolder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.swadeshiapps.video.FabPopupCard;
import com.swadeshiapps.video.ListActivity;
import com.swadeshiapps.video.PopupCard;
import com.swadeshiapps.video.R;
import com.swadeshiapps.video.VideoActivity;

import java.io.File;

public class VideoViewHolder extends RecyclerView.ViewHolder {
    private ImageView thumb;
    private TextView nameText,durationText,sizeText;
    private View v;
    private FirebaseAnalytics f;
    public VideoViewHolder(@NonNull View itemView ) {
        super(itemView);
        thumb=itemView.findViewById(R.id.video_thumb);
        v=itemView;
        nameText=v.findViewById(R.id.video_name);
        durationText=v.findViewById(R.id.video_time);
        sizeText=v.findViewById(R.id.video_size);
        f=FirebaseAnalytics.getInstance(v.getContext());
    }
    public void setLonClick(final AppCompatActivity a, final String path, final String thumb, final boolean fav){
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(fav)
                    new FabPopupCard(v.getContext(),path,thumb).showOnAnchor(v,RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT, false);
                else
                    new PopupCard(v.getContext(),path,thumb).showOnAnchor(v,RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.ALIGN_LEFT, false);

                return true;
            }
        });
    }
    public void setSize(String path){
        File f=new File(path);
        int fileSize = Integer.parseInt(String.valueOf(f.length()/1024));
        int mb=fileSize/1024;
        int gb=mb/1024;
        sizeText.setText(getSize(fileSize,mb,gb));
    }
    String getSize(int kb,int mb,int gb){
        String size;
        mb=mb%1024;
        kb=kb%1024;
        if(gb!=0){
            size=Float.parseFloat(gb+"."+mb/10)+" gb";
        }else if(mb!=0){
            size=Float.parseFloat(mb+"."+kb/10)+" mb";
        }else{
            size=kb+" kb";
        }

        return size;
    }
    private String getTime(int currentTIme) {
        int hour=currentTIme/3600000;
        String h=(hour>0)?((hour<10)?"0"+hour:"")+":":"";
        int minute=(currentTIme/60000)%60;
        String m=(minute>9)?minute+"":"0"+minute;
        int second=(currentTIme/1000)%60;
        String s=(second>9)?second+"":"0"+second;
        return h+m+":"+s ;
    }
    public void setDuration(String path,AppCompatActivity a){
        MediaPlayer mp = MediaPlayer.create(a, Uri.parse(path));
        int duration=0;
        if(mp!=null)
        duration = mp.getDuration();
        durationText.setText(getTime(duration));
    }
    public void setName(String name){
        nameText.setText(name);
    }
    public void setImage(String path, AppCompatActivity a){
        Glide.with(a).load( path)
                .skipMemoryCache(false)
                .into(thumb);
    }
    public void setClick(final AppCompatActivity a,final int position){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(position));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, new File(ListActivity.getInstance().list.get(position).getPath()).getName());
                f.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Intent i=new Intent(a, VideoActivity.class);
                ListActivity.getInstance().position=position;
                a.startActivity(i);
            }
        });

    }
}
