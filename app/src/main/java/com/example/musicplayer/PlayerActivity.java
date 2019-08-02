package com.example.musicplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    String sname;
    static MediaPlayer myMediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateseekerBar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next = findViewById(R.id.next);
        btn_previous = findViewById(R.id.previous);
        btn_pause = findViewById(R.id.pause);
        songTextLabel = findViewById(R.id.songLabel);
        songSeekbar = findViewById(R.id.seekbar);

        updateseekerBar = new Thread() {
            @Override
            public void run() {

                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            };

                if(myMediaPlayer!=null) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
            }

            Intent i = getIntent();
            Bundle bundle = i.getExtras();

            mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
            sname=mySongs.get(position).getName().toString();

            String songName = i.getStringExtra("songname");
            songTextLabel.setText(songName);
            songTextLabel.setSelected(true);

            position=bundle.getInt("pos",0);
            Uri u = Uri.parse(mySongs.get(position).toString());

            myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
            myMediaPlayer.start();
            songSeekbar.setMax(myMediaPlayer.getDuration());

            updateseekerBar.start();
            songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

           songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged (SeekBar seekBar,int i, boolean b){
            }
                @Override
                public void onStartTrackingTouch (SeekBar seekBar){
            }
                @Override
                public void onStopTrackingTouch (SeekBar seekBar){
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
            });

          btn_pause.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View view){
                songSeekbar.setMax(myMediaPlayer.getDuration());
                if (myMediaPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.ic_play);
                    myMediaPlayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.ic_music_pause);
                    myMediaPlayer.start();
                }
                }
            });
          btn_next.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View view){
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);
                myMediaPlayer.start();
            }
            });
        btn_previous.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View view){
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position - 1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);
                myMediaPlayer.start();
            }
        });
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

