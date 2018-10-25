package com.swadeshiapps.video;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity  {
    private VideoView videoView;
    private TextView videoName,currentTImeText,remainingTimeText;
    private boolean fullScreen=true;
    private RelativeLayout lockScreen,controlScreen,volumeControl;
    private int currentTIme=0;
    private SeekBar progressBar;
    private Timer t;
    private int timeout=3;
    private ImageView playButton;
    private boolean doubleTap=false;
    private LinearLayout forwardControl,backwardControl;
    private TextView forWardText,backwardText;
    private MediaPlayer mp;
    private ProgressBar volumeBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        videoView=findViewById(R.id.video_view);
        videoName=findViewById(R.id.video_title);
        lockScreen=findViewById(R.id.lock_screen);
        currentTImeText=findViewById(R.id.current_time);
        remainingTimeText=findViewById(R.id.remaining_time);
        controlScreen=findViewById(R.id.control_screen);
        progressBar=findViewById(R.id.progress_bar);
        playButton=findViewById(R.id.play_button);
        forwardControl=findViewById(R.id.playback_control_foward);
        forWardText=findViewById(R.id.forward_text);
        backwardControl=findViewById(R.id.playback_control_backward);
        backwardText=findViewById(R.id.backward_text);
        volumeControl=findViewById(R.id.volume_control);
        volumeBar=findViewById(R.id.volume_slider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeout=3;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(timeout>0)
                    timeout--;
                if(timeout==0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            controlScreen.setVisibility(View.GONE);
                        }
                    });
                }
            }
        },0,1000);
        String path=ListActivity.getInstance().list.get(ListActivity.getInstance().position).getPath();
        videoView.setVideoURI(Uri.parse(path));
        videoName.setText(new File(path).getName());
        progressBar.setProgress(0);
        t=new Timer();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Toast.makeText(VideoActivity.this, "done", Toast.LENGTH_SHORT).show();
                VideoActivity.this.mp=mp;
                final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                volumeBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                volumeBar.setProgress(originalVolume);
                videoView.start();
                videoView.seekTo(currentTIme);
                progressBar.setMax(videoView.getDuration());
                t.cancel();
                t=new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(videoView.isPlaying())
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentTIme=videoView.getCurrentPosition();
                                    currentTImeText.setText(getTime(currentTIme));
                                    remainingTimeText.setText(getTime(videoView.getDuration()-videoView.getCurrentPosition()));
                                    progressBar.setProgress(progressBar.getProgress()+100);
                                }
                            });
                    }
                },0,100);
                progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            videoView.seekTo(progress);
                            currentTIme = progress;
                            timeout=3;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ListActivity.getInstance().position++;
                onStart();
            }
        });
        controlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(doubleTap)
                        playPause(playButton);
                    else {
                        doubleTap=true;
                        controlScreen.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleTap=false;
                            }
                        },500);
                    }
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(doubleTap)
                    playPause(playButton);
                else {
                    doubleTap=true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleTap=false;
                        }
                    },500);
                    controlScreen.setVisibility(View.VISIBLE);
                    timeout = 3;
                }
            }
        });
        videoView.setOnTouchListener(new SwipeListener(){
            @Override
            void onSwipeRight() {
                backwardControl.setVisibility(View.GONE);
                forwardControl.setVisibility(View.VISIBLE);
                timeout=3;
                progressBar.setProgress(progressBar.getProgress()+progressBar.getMax()/200);
                videoView.seekTo(progressBar.getProgress());
                forWardText.setText(getTime(progressBar.getProgress())+"/"+getTime(progressBar.getMax()));
                doubleTap=false;
            }

            @Override
            void onSwipeLeft() {
                forwardControl.setVisibility(View.GONE);
                backwardControl.setVisibility(View.VISIBLE);
                timeout=3;
                progressBar.setProgress(progressBar.getProgress()-progressBar.getMax()/200);
                videoView.seekTo(progressBar.getProgress());
                backwardText.setText(getTime(progressBar.getProgress())+"/"+getTime(progressBar.getMax()));
                doubleTap=false;
            }

            @Override
            void close() {
                forwardControl.setVisibility(View.GONE);
                backwardControl.setVisibility(View.GONE);
            }
        });
        controlScreen.setOnTouchListener(new SwipeListener(){
            @Override
            void onSwipeRight() {
                backwardControl.setVisibility(View.GONE);
                forwardControl.setVisibility(View.VISIBLE);
                progressBar.setProgress(progressBar.getProgress()+progressBar.getMax()/200);
                videoView.seekTo(progressBar.getProgress());
                forWardText.setText(getTime(progressBar.getProgress())+"/"+getTime(progressBar.getMax()));
                doubleTap=false;
            }

            @Override
            void onSwipeLeft() {
                doubleTap=false;
                forwardControl.setVisibility(View.GONE);
                backwardControl.setVisibility(View.VISIBLE);
                progressBar.setProgress(progressBar.getProgress()-progressBar.getMax()/200);
                videoView.seekTo(progressBar.getProgress());
                backwardText.setText(getTime(progressBar.getProgress())+"/"+getTime(progressBar.getMax()));
            }

            @Override
            void close() {
                forwardControl.setVisibility(View.GONE);
                backwardControl.setVisibility(View.GONE);
            }
        });
        volumeControl.setOnTouchListener(new SwipeListener(){
            @Override
            void onSwipeDown() {
                volumeBar.setVisibility(View.VISIBLE);
                volumeBar.setProgress(volumeBar.getProgress()-1);
            }

            @Override
            void onSwipeUp() {
                volumeBar.setVisibility(View.VISIBLE);
                volumeBar.setProgress(volumeBar.getProgress()-1);
            }

            @Override
            void close() {
                volumeBar.setVisibility(View.INVISIBLE);
            }
        });
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

    public void back(View view) {
        finish();
    }

    public void playPause(View view) {
        timeout=3;
        ImageView play=(ImageView)view;
        if(videoView.isPlaying()){
            play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            videoView.pause();
        }else{
            play.setImageResource(R.drawable.ic_pause_black_24dp);
            videoView.resume();
            videoView.seekTo(currentTIme+=1000);
        }
    }

    public void fullScreen(View view) {
        if(fullScreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        fullScreen=!fullScreen;
    }

    public void unlock(View view) {
        timeout=3;
        lockScreen.setVisibility(View.GONE);
        controlScreen.setVisibility(View.VISIBLE);
        videoView.setEnabled(true);
    }

    public void lock(View view) {
        lockScreen.setVisibility(View.VISIBLE);
        controlScreen.setVisibility(View.GONE);
        videoView.setEnabled(false);
    }
}
class SwipeListener implements View.OnTouchListener {
    private float x=0,y=0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_MOVE){
            if(event.getX()>x){
                onSwipeRight();
            }else{
                onSwipeLeft();
            }
            if(event.getY()>y){
                onSwipeUp();
            }else{
                onSwipeDown();
            }
            x=event.getX();
            y=event.getY();
        }else if(event.getAction()==MotionEvent.ACTION_DOWN){
            x=event.getX();
            y=event.getY();
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            close();
        }
        return false;
    }

    void onSwipeDown() {
    }

    void onSwipeUp() {
    }

    void close() {
    }

    void onSwipeRight(){

    }
    void onSwipeLeft(){

    }
}
