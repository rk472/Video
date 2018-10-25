package com.swadeshiapps.video.adapter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.swadeshiapps.video.POJO.Videos;
import com.swadeshiapps.video.R;
import com.swadeshiapps.video.viewHolder.VideoViewHolder;

import java.io.File;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private List<Videos> list;
    private AppCompatActivity a;
    private boolean fab;
    public VideoAdapter(List<Videos> list, AppCompatActivity a,boolean fab) {
        this.list = list;
        this.a = a;
        this.fab=fab;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoViewHolder(LayoutInflater.from(a).inflate(R.layout.video_row,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder videoViewHolder, int i) {
        videoViewHolder.setName(new File(list.get(i).getPath()).getName());
        videoViewHolder.setImage(list.get(i).getThumb(),a);
        videoViewHolder.setClick(a,i);
        videoViewHolder.setSize(list.get(i).getPath());
        videoViewHolder.setDuration(list.get(i).getPath(),a);
        videoViewHolder.setLonClick(a,list.get(i).getPath(),list.get(i).getThumb(),fab);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
