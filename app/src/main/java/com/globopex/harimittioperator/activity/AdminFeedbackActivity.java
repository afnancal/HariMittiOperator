package com.globopex.harimittioperator.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.DownloadImage;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;
import com.globopex.harimittioperator.view.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Afnan on 07-Jun-18.
 */
public class AdminFeedbackActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        View.OnTouchListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_adminFeed;

    private MaterialSearchView search_view_adminFeed;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_adminFeed, rl_audio_adminFeed, rl_cheque_adminFeed, rl_container_adminFeed;
    private ImageView iv_photo1_adminFeed, iv_photo2_adminFeed, iv_photo3_adminFeed, iv_photo4_adminFeed, iv_photo5_adminFeed,
            iv_cheque_adminFeed;
    private TextView tv_latLng1_adminFeed, tv_latLng2_adminFeed, tv_latLng3_adminFeed, tv_latLng4_adminFeed, tv_latLng5_adminFeed;
    private TextView tv_feedback_adminFeed, tv_currentDur, tv_totalTime, tv_status_adminFeed, tv_payment_adminFeed,
            tv_amount_adminFeed, tv_actionOn_adminFeed;
    private SeekBar seekBar;
    private ImageButton ib_backward, ib_play, ib_pause, ib_forward;

    public MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds;
    //CounterClass timer;
    private Handler handler = new Handler();
    Boolean timer1stTimeChk = true;
    private String audioLength;

    private int seekForwardTime = 5000; // 5000 milliseconds or 5 secs
    private int seekBackwardTime = 5000; // 5000 milliseconds or 5 secs
    private int plantCounter = 0;
    boolean firstTime = true;

    private String[] plantImgs = new String[5], multiPlantImgsFilter, checkImg = new String[1];
    private int status;
    private String maintainer_id, membership_id, description, audio_file_url, payment_method, chk_img_url, amount, action_on,
            userType;
    private Typeface custom_font_Light, custom_font_Regular;
    Utilities utils = new Utilities();
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;
    private AlertDialoge alertDialoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminfeed);

        prefManager = new PrefManager(getApplicationContext());
        sizeAdjustment = new SizeAdjustment();
        networkAvailability = new NetworkAvailability(AdminFeedbackActivity.this);
        alertDialoge = new AlertDialoge(AdminFeedbackActivity.this);

        Bundle extras = getIntent().getExtras();
        maintainer_id = extras.getString("maintainer_id");
        membership_id = extras.getString("membership_id");
        plantImgs[0] = extras.getString("plant_img1");
        plantImgs[1] = extras.getString("plant_img2");
        plantImgs[2] = extras.getString("plant_img3");
        plantImgs[3] = extras.getString("plant_img4");
        plantImgs[4] = extras.getString("plant_img5");
        description = extras.getString("description");
        audio_file_url = extras.getString("audio_file_url");
        status = extras.getInt("status");
        payment_method = extras.getString("payment_method");
        chk_img_url = extras.getString("chk_img_url");
        amount = extras.getString("amount");
        action_on = extras.getString("action_on");

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_adminFeed);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_adminFeed = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_adminFeed);
            fragment_navigation_drawer_adminFeed.setUpActivity(R.id.fragment_navigation_drawer_adminFeed, (DrawerLayout) findViewById(R.id.drawer_layout_adminFeed), toolbar);
            fragment_navigation_drawer_adminFeed.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_adminFeed = (MaterialSearchView) findViewById(R.id.search_view_adminFeed);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Feedback");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_adminFeed.setVoiceSearch(true);
        search_view_adminFeed.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_adminFeed.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(AdminFeedbackActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                Snackbar.make(findViewById(R.id.container_adminFeed), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_adminFeed.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_adminFeed.setMenuItem(iv_search_toolbar);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, AdminFeedbackActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();
        mediaPlayer = new MediaPlayer();
        // Listeners
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // Getting the Maintainer Location data
        String[] dateTime = action_on.split(" ");
        String date = dateTime[0];
        SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("yyyy-MM-dd");
        date = spf.format(newDate);
        new MaintainerLocationDataGet().execute(prefManager.getUrl() + Config.SearchMaintainerLocationUrl + maintainer_id + "/" + membership_id + "/" + date);

    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                iv_search_toolbar.setVisibility(View.GONE);
                rl_main_adminFeed = (RelativeLayout) findViewById(R.id.rl_main_adminFeed);
                iv_photo1_adminFeed = (ImageView) findViewById(R.id.iv_photo1_adminFeed);
                tv_latLng1_adminFeed = (TextView) findViewById(R.id.tv_latLng1_adminFeed);
                iv_photo2_adminFeed = (ImageView) findViewById(R.id.iv_photo2_adminFeed);
                tv_latLng2_adminFeed = (TextView) findViewById(R.id.tv_latLng2_adminFeed);
                iv_photo3_adminFeed = (ImageView) findViewById(R.id.iv_photo3_adminFeed);
                tv_latLng3_adminFeed = (TextView) findViewById(R.id.tv_latLng3_adminFeed);

                iv_photo4_adminFeed = (ImageView) findViewById(R.id.iv_photo4_adminFeed);
                tv_latLng4_adminFeed = (TextView) findViewById(R.id.tv_latLng4_adminFeed);
                iv_photo5_adminFeed = (ImageView) findViewById(R.id.iv_photo5_adminFeed);
                tv_latLng5_adminFeed = (TextView) findViewById(R.id.tv_latLng5_adminFeed);

                tv_feedback_adminFeed = (TextView) findViewById(R.id.tv_feedback_adminFeed);
                rl_audio_adminFeed = (RelativeLayout) findViewById(R.id.rl_audio_adminFeed);
                tv_currentDur = (TextView) findViewById(R.id.tv_currentDur);
                tv_totalTime = (TextView) findViewById(R.id.tv_totalTime);
                seekBar = (SeekBar) findViewById(R.id.seekBar);
                ib_backward = (ImageButton) findViewById(R.id.ib_backward);
                ib_play = (ImageButton) findViewById(R.id.ib_play);
                ib_pause = (ImageButton) findViewById(R.id.ib_pause);
                ib_forward = (ImageButton) findViewById(R.id.ib_forward);

                tv_status_adminFeed = (TextView) findViewById(R.id.tv_status_adminFeed);
                tv_payment_adminFeed = (TextView) findViewById(R.id.tv_payment_adminFeed);
                iv_cheque_adminFeed = (ImageView) findViewById(R.id.iv_cheque_adminFeed);
                tv_amount_adminFeed = (TextView) findViewById(R.id.tv_amount_adminFeed);
                tv_actionOn_adminFeed = (TextView) findViewById(R.id.tv_actionOn_adminFeed);
                rl_container_adminFeed = (RelativeLayout) findViewById(R.id.rl_container_adminFeed);

                iv_photo1_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", multiPlantImgsFilter);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                iv_photo2_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", multiPlantImgsFilter);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                iv_photo3_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", multiPlantImgsFilter);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                iv_photo4_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", multiPlantImgsFilter);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                iv_photo5_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", multiPlantImgsFilter);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

                tv_latLng1_adminFeed.setPaintFlags(tv_latLng1_adminFeed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_latLng1_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latLng = tv_latLng1_adminFeed.getText().toString().trim();
                        String[] parts = latLng.split("\n");
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        String uriBegin = "geo:" + lat + "," + lng;
                        String query = lat + "," + lng;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                tv_latLng2_adminFeed.setPaintFlags(tv_latLng2_adminFeed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_latLng2_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latLng = tv_latLng2_adminFeed.getText().toString().trim();
                        String[] parts = latLng.split("\n");
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        String uriBegin = "geo:" + lat + "," + lng;
                        String query = lat + "," + lng;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                tv_latLng3_adminFeed.setPaintFlags(tv_latLng3_adminFeed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_latLng3_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latLng = tv_latLng3_adminFeed.getText().toString().trim();
                        String[] parts = latLng.split("\n");
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        String uriBegin = "geo:" + lat + "," + lng;
                        String query = lat + "," + lng;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                tv_latLng4_adminFeed.setPaintFlags(tv_latLng4_adminFeed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_latLng4_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latLng = tv_latLng4_adminFeed.getText().toString().trim();
                        String[] parts = latLng.split("\n");
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        String uriBegin = "geo:" + lat + "," + lng;
                        String query = lat + "," + lng;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                tv_latLng5_adminFeed.setPaintFlags(tv_latLng5_adminFeed.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv_latLng5_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latLng = tv_latLng5_adminFeed.getText().toString().trim();
                        String[] parts = latLng.split("\n");
                        double lat = Double.parseDouble(parts[0]);
                        double lng = Double.parseDouble(parts[1]);

                        String uriBegin = "geo:" + lat + "," + lng;
                        String query = lat + "," + lng;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                ib_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("AFNAN", "Play button pressed");
                        if (firstTime == true) {
                            playAudioFile(audio_file_url);
                            firstTime = false;
                        } else {
                            playControl();
                        }
                    }
                });

                ib_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("AFNAN", "Pause button pressed");
                        pauseControl();
                        //ib_play.setBackgroundResource(R.drawable.play);
                        ib_play.setVisibility(View.VISIBLE);
                        ib_pause.setVisibility(View.GONE);
                    }
                });

                // Forward button click event Forwards song specified seconds
                ib_forward.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        forwardControl();
                    }
                });

                // Backward button click event Backward song to specified seconds
                ib_backward.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        rewindControl();
                    }
                });

                //seekBar.getThumb().mutate().setAlpha(0);
                seekBar.setThumb(null);
                tv_currentDur.setText("0.00");
                // Off here bcoz of i can't get data here
                //tv_totalTime.setText(audioLength);

                iv_cheque_adminFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putStringArray("multiPlantImgs", checkImg);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

                //getLoaders();
                tv_feedback_adminFeed.setText(description);
                MediaPlayer mp = MediaPlayer.create(AdminFeedbackActivity.this, Uri.parse(audio_file_url));
                int millisLength = mp.getDuration();
                long minutes = (millisLength / 1000) / 60;
                int seconds = (millisLength / 1000) % 60;
                String minTemp = String.valueOf(minutes);
                String secTemp = String.valueOf(seconds);

                audioLength = minTemp + "." + secTemp;
                tv_totalTime.setText(audioLength);

                if (status == 0) {
                    tv_status_adminFeed.setText("Pending");
                } else if (status == 1) {
                    tv_status_adminFeed.setText("Done");
                    tv_status_adminFeed.setTextColor(Color.GREEN);
                } else if (status == 2) {
                    tv_status_adminFeed.setText("Cancel");
                    tv_status_adminFeed.setTextColor(Color.YELLOW);
                }
                tv_payment_adminFeed.setText(payment_method);
                if (!chk_img_url.equals("")) {
                    DownloadImage downloadImage = new DownloadImage();
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_cheque_adminFeed, chk_img_url);
                    checkImg[0] = chk_img_url;
                }
                tv_amount_adminFeed.setText("Rs. " + amount);

                SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                Date newDate = null;
                try {
                    newDate = spf.parse(action_on);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                action_on = spf.format(newDate);
                tv_actionOn_adminFeed.setText(action_on);
                // For seting the Plant Images
                setPlantImages();
                // Filter the blank images
                filterBlankImages();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        pauseControl();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                pauseControl();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(AdminFeedbackActivity.this, "AdminFeedbackActivity", "nothing_adminFeed");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    private void setPlantImages() {
        for (int z = 0; z < plantImgs.length; z++) {
            DownloadImage downloadImage = new DownloadImage();
            if (!plantImgs[z].equals("")) {
                if (plantCounter == 0) {
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_photo1_adminFeed, plantImgs[z]);
                    plantCounter++;

                } else if (plantCounter == 1) {
                    iv_photo2_adminFeed.setVisibility(View.VISIBLE);
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_photo2_adminFeed, plantImgs[z]);
                    plantCounter++;

                } else if (plantCounter == 2) {
                    iv_photo3_adminFeed.setVisibility(View.VISIBLE);
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_photo3_adminFeed, plantImgs[z]);
                    plantCounter++;

                } else if (plantCounter == 3) {
                    iv_photo4_adminFeed.setVisibility(View.VISIBLE);
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_photo4_adminFeed, plantImgs[z]);
                    plantCounter++;

                } else if (plantCounter == 4) {
                    iv_photo5_adminFeed.setVisibility(View.VISIBLE);
                    downloadImage.setDownloadImage(AdminFeedbackActivity.this, iv_photo5_adminFeed, plantImgs[z]);
                    plantCounter++;
                }
            }
        }
    }

    private void filterBlankImages() {
        int counter = 0;
        for (int z = 0; z < plantImgs.length; z++) {
            if (!plantImgs[z].equals("")) {
                counter++;
            }
        }
        multiPlantImgsFilter = new String[counter];
        counter = 0;    // Reset the counter for inserting value into multiPlantImgsFilter array
        for (int p = 0; p < plantImgs.length; p++) {
            if (!plantImgs[p].equals("")) {
                multiPlantImgsFilter[counter] = plantImgs[p];
                counter++;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //MediaPlayer onCompletion event handler. Method which calls then song playing is complete
        ib_play.setVisibility(View.VISIBLE);
        ib_pause.setVisibility(View.GONE);
        tv_currentDur.setText(audioLength);
        firstTime = true;

        mediaPlayer.stop();
        mediaPlayer.reset();
        //mediaPlayer.release();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        //handler.removeCallbacks(mUpdateTimeTask);
        primarySeekBarProgressUpdater();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
        // update timer progress again
        primarySeekBarProgressUpdater();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.seekBar) {
            //Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position
            SeekBar sb = (SeekBar) v;
            int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
            mediaPlayer.seekTo(playPositionInMillisecconds);
            //}
        }
        return false;
    }

    public void playControl() {
        //ImageButton onClick event handler. Method which start/pause mediaplayer playing
        try {
            mediaPlayer.setDataSource(audio_file_url);
            // you must call this method after setup the datasource in setDataSource method. After calling prepare()
            // the instance of MediaPlayer starts load data from URL to internal buffer.
            mediaPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("AFNAN", e + "");
        }
        // gets the song length in milliseconds from URL
        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
        if (!mediaPlayer.isPlaying()) {
            if (timer1stTimeChk == true) {
                mediaPlayer.start();
            } else {
                mediaPlayer.start();
            }
        }
        ib_play.setVisibility(View.GONE);
        ib_pause.setVisibility(View.VISIBLE);
        //for enable the screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //rl_progressBar.setVisibility(View.GONE);
        // Updating progress bar
        primarySeekBarProgressUpdater();
    }

    public void playAudioFile(String urlstring2) {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(urlstring2);
            //rl_progressBar.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mediaPlayer.prepareAsync();
            //player.start();

        } catch (Throwable thr) {
            Log.e("AFNAN", "could not play audio", thr);
            //rl_progressBar.setVisibility(View.INVISIBLE);
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                //mediaPlayer.seekTo(length);
                mediaPlayer.start();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //rl_progressBar.setVisibility(View.GONE);
                ib_play.setVisibility(View.GONE);
                ib_pause.setVisibility(View.VISIBLE);
            }
        });
        firstTime = false;
        // Updating progress bar
        primarySeekBarProgressUpdater();

        //Update total no. of play songs
        //updatePlaySong();
    }

    public void pauseControl() {
        mediaPlayer.pause();
        timer1stTimeChk = false;
    }

    public void forwardControl() {
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void rewindControl() {
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }

    // Update timer on seekbar
    private void primarySeekBarProgressUpdater() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    // Background Runnable thread
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Remaining time
            long remainingDuration = totalDuration - currentDuration;
            //tv_displayTime.setText("-" + utils.milliSecondsToTimer(remainingDuration));
            tv_currentDur.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
            if (progress == 100) {
                tv_currentDur.setText("" + audioLength);
                ib_play.setVisibility(View.VISIBLE);
                ib_pause.setVisibility(View.GONE);
                firstTime = true;
            }
        }
    };

    private class MaintainerLocationDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            //swipe_refresh_layout_home.setRefreshing(true);
            if (!networkAvailability.isNetworkAvailable()) {
                alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String responseString;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(params[0]);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                //Set our result equal to our stringBuilder
                responseString = ConvertInputStream.convertInputStreamReaderToString(streamReader);
            } catch (IOException e) {
                e.printStackTrace();
                responseString = null;
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double latitude = jsonObject.getDouble("latitude");
                    double longitude = jsonObject.getDouble("longitude");
                    if (i == 0) {
                        tv_latLng1_adminFeed.setText(latitude + "\n" + longitude);

                    } else if (i == 1) {
                        tv_latLng2_adminFeed.setVisibility(View.VISIBLE);
                        tv_latLng2_adminFeed.setText(latitude + "\n" + longitude);

                    } else if (i == 2) {
                        tv_latLng3_adminFeed.setVisibility(View.VISIBLE);
                        tv_latLng3_adminFeed.setText(latitude + "\n" + longitude);

                    } else if (i == 3) {
                        tv_latLng4_adminFeed.setVisibility(View.VISIBLE);
                        tv_latLng4_adminFeed.setText(latitude + "\n" + longitude);

                    } else if (i == 4) {
                        tv_latLng5_adminFeed.setVisibility(View.VISIBLE);
                        tv_latLng5_adminFeed.setText(latitude + "\n" + longitude);
                    }

                }


            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

}
