package com.swadeshiapps.video;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.swadeshiapps.video.adapter.VideoAdapter;

public class FavActivity extends AppCompatActivity {
    private RecyclerView list;
    private SwipeRefreshLayout swipe;
    public static FavActivity inst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        list=findViewById(R.id.fav_list);
        swipe=findViewById(R.id.fab_swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                refresh();
            }
        });
        inst=this;
        list.setHasFixedSize(true);
        list.setLayoutManager(new GridLayoutManager(this,2,LinearLayoutManager.VERTICAL,false));
        refresh();
    }
    public void refresh(){
        list.setAdapter(new VideoAdapter(new DBHelper(this).getAll(),this,true));
    }
}
