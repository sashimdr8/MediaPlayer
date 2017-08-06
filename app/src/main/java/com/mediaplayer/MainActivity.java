package com.mediaplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private Cursor c;
    private MediaMetadataRetriever retriever;
    private MediaPlayer mplayer;
    private Handler myhandler=new Handler();
    private String lastplayed;
    private SeekBar seekbar;
    private TextView songnamefooter;

    private void initMediaPlayer(String filename){

        mplayer = new MediaPlayer();
        try {
            mplayer.setDataSource(filename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            mplayer.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Customising actionbar
        String title = getResources().getString(R.string.app_name);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>"+title+"</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CDDC39")));


        //This will be used to display the name of currently playing song
        songnamefooter = (TextView)findViewById(R.id.songnamefooter);

        //It will be used to get song album art
        retriever = new MediaMetadataRetriever();

        seekbar = (SeekBar)findViewById(R.id.progress);
        //disabling seekbar until media player has been initialized
        if(mplayer == null)
            seekbar.setEnabled(false);


        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if(fromUser){
                    if(mplayer != null)
                        mplayer.seekTo(progress);
                }
            }
        });

        //getAllSongs() will return all song data
        c = getAllSongs();

        ListView list = (ListView)findViewById(R.id.listView1);

        list.setAdapter(new CustomAdapter(this, c));

        final ImageView albumart = (ImageView)findViewById(R.id.albumart);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                //whenever a song is selected from the listview, enable the seekbar.
                seekbar.setEnabled(true);
                c.moveToPosition(position);

                //saving last media played
                String filepath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                lastplayed = filepath;

                Bitmap art = getAlbumart(filepath);
                if(art != null)
                    albumart.setImageBitmap(art);
                else
                    albumart.setImageResource(R.drawable.audiofile);

                songnamefooter.setText(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                songnamefooter.setSelected(true);

                //this makes sure that whenever a new song is selected previous one is put to stop.
                //if you don't do this multiple song will play at the same time.

                if(mplayer!=null)
                    mplayer.release();

                initMediaPlayer(filepath);
                seekbar.setMax(mplayer.getDuration());
                mplayer.start();
                updateSeekBar();

            }
        });
    }

    Runnable run=new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    private void updateSeekBar(){
        seekbar.setProgress(mplayer.getCurrentPosition());
        myhandler.postDelayed(run, 100);
    }

    private Bitmap getAlbumart(String filepath){

        retriever.setDataSource(filepath);
        //getEmbeddedPictures() returns null when album art is not available in the media
        byte[] art = retriever.getEmbeddedPicture();
        if(art == null)
            return null;
        Bitmap albumart = BitmapFactory.decodeByteArray(art, 0, art.length);
        //I don't want big image so downscaling it to 100 X 100
        return Bitmap.createScaledBitmap(albumart, 100, 100, false);
    }

    private Cursor getAllSongs(){

        //projection specifies from which coloumns of the database you want. Database is really big and
        //specifying null to projection is inefficient since we don't want everything.
        String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        //Getting music files from external storage
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        return c;
    }


    public void buttonhandler(View v){

        switch(v.getId()){

            case R.id.pause :    //make sure media player is initialized and is currently playing a song
                if(mplayer!=null){
                    if(mplayer.isPlaying())
                        mplayer.pause();
                }
                break;
            case R.id.play  :    // start playing only if mediaplayer is not null
                if(mplayer!=null)mplayer.start();
                break;
            case R.id.stop  :    //make sure media player is initialized and is currently playing a song

                if(mplayer!=null){
                    if(mplayer.isPlaying()){
                        mplayer.stop();
                        initMediaPlayer(lastplayed);
                    }
                }
                break;
            case  R.id.forward : if(mplayer !=null)
                mplayer.seekTo(mplayer.getCurrentPosition()+mplayer.getDuration()/10);
                break;
            case R.id.reverse :  if(mplayer!=null)
                mplayer.seekTo(mplayer.getCurrentPosition()-mplayer.getDuration()/10);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mplayer.release();
        super.onDestroy();
    }

}
