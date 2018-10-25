package com.swadeshiapps.video;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.swadeshiapps.video.POJO.Videos;
import com.swadeshiapps.video.adapter.VideoAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    public List<Videos>  list;
    public int position;
    private static ListActivity instance;
    public static ListActivity getInstance(){
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setElevation(20f);
        instance=this;
        fn_video();
    }
    public void fn_video() {
        GetVideoTask task=new GetVideoTask();
        task.execute();

    }
    class GetVideoTask extends AsyncTask<String,Void,List<Videos>>{

        @Override
        protected List<Videos> doInBackground(String... strings) {
            Uri uri;
            Cursor cursor;
            int column_index_data,thum;

            String absolutePathOfImage ;
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            list=new ArrayList<>();
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("thum", cursor.getString(thum));

                Videos obj_model = new Videos();
                obj_model.setPath(absolutePathOfImage);
                obj_model.setThumb(cursor.getString(thum));
                //MediaPlayer mp = MediaPlayer.create(this, Uri.parse(absolutePathOfImage));
                //if(mp!=null)
                if(new File(absolutePathOfImage).length()!=0)
                    list.add(obj_model);
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Videos> videos) {
            super.onPostExecute(videos);
            RecyclerView videoList=findViewById(R.id.video_list);
            VideoAdapter adapter=new VideoAdapter(list,instance,false);
            videoList.setLayoutManager(new GridLayoutManager(instance,2,LinearLayoutManager.VERTICAL,false));
            videoList.setHasFixedSize(true);
            videoList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_fav:
                startActivity(new Intent(this,FavActivity.class));
                break;
            case R.id.action_info:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey....Checkout the awesome video player at http://play.google.com");
                startActivity(Intent.createChooser(shareIntent, "Share App using"));
                break;
        }

        return true;
    }
}
