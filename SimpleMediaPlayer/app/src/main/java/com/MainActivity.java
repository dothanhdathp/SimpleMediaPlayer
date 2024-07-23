package com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.humaxdigital.avtovaz.mediaplayer.R;

import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;

public class MainActivity extends AppCompatActivity
    implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
    private static MainActivity mInstance;
    public static MediaPlayer mMediaPlayer;

    private Menu mMenu;
    private final Runnable mRunnablePlaybackTimeUpdateTask = new Runnable() {
        public void run() {
            if (mMediaPlayer.isPlaying()) {
                int playtime;
                int duration;
                TextView textView;
                textView = (TextView)findViewById(R.id.textview_time);
                duration = mMediaPlayer.getDuration();
                playtime = mMediaPlayer.getCurrentPosition();
                textView.setText(String.valueOf(playtime) + " ms / " + String.valueOf(duration + " ms"));
            }
        }
    };

    class PlaybackTimerTask extends TimerTask {
        @Override
        public void run() {
            // Updating TextView must be in UI thread
            runOnUiThread(mRunnablePlaybackTimeUpdateTask);
        }
    }

    private Timer mPlaybackTimer = null;
    private PlaybackTimerTask mPlaybackTimerTask = null;

    // SurfaceView and SurfaceHolder for MediaPlayer video surface
    private SurfaceView mVideoSurfaceView;
    private SurfaceHolder surfaceHolder;

    private BroadCastReceiver listener = null;
    private IntentFilter intentFilter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);

        // Set MediaPlayer video playback surface
        mVideoSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = mVideoSurfaceView.getHolder();
        // MediaPlayer.setDisplay() will be called in :surfaceCreated() callback
        surfaceHolder.addCallback(this);

        this.listener = new BroadCastReceiver();
        this.intentFilter = new IntentFilter("ADB_PLAY_MUSIC");
        registerReceiver(listener, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_audio) {
            AssetFileDescriptor afd;
            afd = getBaseContext().getResources().openRawResourceFd(R.raw.nhung_loi_hua_bo_quen);
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.action_video) {
            AssetFileDescriptor afdv;
            afdv = getBaseContext().getResources().openRawResourceFd(R.raw.yeu_anh_mat_roi);
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(afdv.getFileDescriptor(), afdv.getStartOffset(), afdv.getDeclaredLength());
                mMediaPlayer.prepareAsync();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
//        if (id == R.id.action_video) {
//            String videoPath;
//            videoPath = "http://ie.microsoft.com/TEStdrive/Graphics/VideoFormatSupport/big_buck_bunny_trailer_480p_baseline.mp4";
//            try {
//                mMediaPlayer.reset();
//                mMediaPlayer.setDataSource(videoPath);
//                mMediaPlayer.prepareAsync();
//                TextView t = (TextView)findViewById(R.id.textview_time);
//                t.setText("Buffering...");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    // Implementation of MediaPlayer Listener callbacks
    @Override
    public void onCompletion(MediaPlayer mp) {
        TextView t = (TextView)findViewById(R.id.textview_time);
        t.setText("Complete");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        TextView t = (TextView)findViewById(R.id.textview_time);
        MediaPlayer.TrackInfo info[] = mp.getTrackInfo();
        t.setText(info[0].toString());

        mMediaPlayer.start();

        // Cancel previous Timer
        if (mPlaybackTimer != null) {
            mPlaybackTimer.cancel();
            mPlaybackTimerTask.cancel();
        }
        // Once Timer is canceled, it is required to create a new timer.
        mPlaybackTimer = new Timer();
        mPlaybackTimerTask = new PlaybackTimerTask();
        // Timer starts after 1000 ms with 1000 ms period
        mPlaybackTimer.schedule(mPlaybackTimerTask, 1000, 1000);
    }

    //  Implementation of SurfaceHolder callbacks
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public static MainActivity getInstance() {
        return mInstance;
    }
    public void onHandleBroadcast() {
        AssetFileDescriptor afd;
        afd = getBaseContext().getResources().openRawResourceFd(R.raw.nhung_loi_hua_bo_quen);
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}