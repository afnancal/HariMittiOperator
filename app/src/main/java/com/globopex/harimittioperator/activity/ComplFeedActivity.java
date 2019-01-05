package com.globopex.harimittioperator.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afnan.audiorecorderlibrary.AndroidAudioRecorder;
import com.afnan.audiorecorderlibrary.model.AudioChannel;
import com.afnan.audiorecorderlibrary.model.AudioSampleRate;
import com.afnan.audiorecorderlibrary.model.AudioSource;
import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.library.DonutLibraryClass.MaterialSpinner;
import com.globopex.harimittioperator.map.AppUtils;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.network.VolleyMultipartRequest;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;
import com.globopex.harimittioperator.view.Util;
import com.globopex.harimittioperator.view.UtilsForImage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Afnan on 08-May-18.
 */
public class ComplFeedActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        UtilsForImage.ImageAttachmentListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_complFeed;

    private MaterialSearchView search_view_complFeed;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_complFeed, rl_audio_complFeed, rl_cheque_complFeed, rl_container_complFeed;

    private RelativeLayout rl_photo1_complFeed;
    private ImageView iv_deleteAudio_complFeed, iv_cheque_complFeed;
    private ImageView iv_photo1_complFeed, iv_photo2_complFeed, iv_photo3_complFeed, iv_photo4_complFeed, iv_photo5_complFeed;
    private ImageView iv_delete1_complFeed, iv_delete2_complFeed, iv_delete3_complFeed, iv_delete4_complFeed, iv_delete5_complFeed;
    private ImageView iv_addPhoto2_complFeed, iv_addPhoto3_complFeed, iv_addPhoto4_complFeed, iv_addPhoto5_complFeed;
    private EditText et_feed_complFeed, et_amount_complFeed;
    private Button btn_audio_complFeed, btn_submit_complFeed;
    private TextView tv_upload_compFeed, tv_feed_complFeed, tv_audio_complFeed, tv_cheque_complFeed, tv_amount_complFeed;
    private MaterialSpinner spinner_status_complFeed, spinner_payment_complFeed;

    private boolean firstTimeAddPhoto2 = true, firstTimeAddPhoto3 = true, firstTimeAddPhoto4 = true, firstTimeAddPhoto5 = true;
    private ArrayAdapter<String> spinnerAdapter, spinnerAdapterPayment;
    private static final String[] SPINNER_ITEMS = {"Select Option", "Done", "Pending", "Cancel"};
    private static final String[] SPINNER_ITEMS_PAYMENT = {"Select Option", "Cheque", "Cash"};
    private static final int REQUEST_RECORD_AUDIO = 33;
    private static String AUDIO_FILE_PATH, AUDIO_FILE_NAME = "";

    private Bitmap bitmapProduct1, bitmapProduct2, bitmapProduct3, bitmapProduct4, bitmapProduct5, bitmapCheque;
    private String fileNameProduct1 = "", fileNameProduct2 = "", fileNameProduct3 = "", fileNameProduct4 = "",
            fileNameProduct5 = "", fileNameCheque = "";
    private String PACKAGE_NAME;
    private String path;
    private String plantImg_url = "http://harimitti.com/uploads/plants_img/";
    private String audioFiles_url = "http://harimitti.com/uploads/audio_files/";
    private String chequeImg_url = "http://harimitti.com/uploads/cheques_img/";

    private GoogleApiClient mGoogleApiClient;
    Context mContext;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private Double latitude = 0.0, longitude = 0.0;
    private Timer myTimer;

    private int allotment_id;
    private String userType, maintainer_id, membership_id, description = "", status, paymentMethod, amount = "";
    private Typeface custom_font_Light, custom_font_Regular;
    private UtilsForImage utilsForImage;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private AlertDialoge alertDialoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complfeed);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_complFeed);
        setSupportActionBar(toolbar);
        mContext = this;

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_complFeed = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_complFeed);
            fragment_navigation_drawer_complFeed.setUpActivity(R.id.fragment_navigation_drawer_complFeed, (DrawerLayout) findViewById(R.id.drawer_layout_complFeed), toolbar);
            fragment_navigation_drawer_complFeed.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_complFeed = (MaterialSearchView) findViewById(R.id.search_view_complFeed);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Feedback Details");
        tv_title_toolbar.setSelected(true);

        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, ComplFeedActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPINNER_ITEMS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapterPayment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPINNER_ITEMS_PAYMENT);
        spinnerAdapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        getView();
        Util.chkPermission(this);
        // For getting Lat Lng
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        // Temporary off setTimer, now i post the location data when image click
        //setTimer();

    }

    private void setTimer() {
        // For Sending Lat Lng to the Server
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (latitude != 0.0 && longitude != 0.0) {
                    Log.e("Afnan", "Run in every 10 minutes");
                    Log.e("Afnan", "" + latitude);
                    Log.e("Afnan", "" + longitude);
                    // Data post to server for submit location data
                    //new LocationDataPost().execute(prefManager.getUrl() + Config.LocationDataUrl);
                }
            }
        }, 0, 600000);      // For every 10 minutes
    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                utilsForImage = new UtilsForImage(ComplFeedActivity.this);
                prefManager = new PrefManager(getApplicationContext());
                networkAvailability = new NetworkAvailability(ComplFeedActivity.this);
                sizeAdjustment = new SizeAdjustment();
                alertDialoge = new AlertDialoge(ComplFeedActivity.this);

                Bundle extras = getIntent().getExtras();
                allotment_id = extras.getInt("allotment_id");
                maintainer_id = extras.getString("maintainer_id");
                membership_id = extras.getString("membership_id");

                PACKAGE_NAME = getApplicationContext().getPackageName();
                //path = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/" + prefManager.getProfileImageName();
                path = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/temp/";
                File pathFolder = new File(path);
                if (!pathFolder.exists()) {
                    pathFolder.mkdirs();
                }
                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                if (userType.equals("Admin")) {

                } else if (userType.equals("Maintainer")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                }
                rl_main_complFeed = (RelativeLayout) findViewById(R.id.rl_main_complFeed);
                rl_photo1_complFeed = (RelativeLayout) findViewById(R.id.rl_photo1_complFeed);
                iv_photo1_complFeed = (ImageView) findViewById(R.id.iv_photo1_complFeed);
                iv_delete1_complFeed = (ImageView) findViewById(R.id.iv_delete1_complFeed);

                iv_addPhoto2_complFeed = (ImageView) findViewById(R.id.iv_addPhoto2_complFeed);
                iv_photo2_complFeed = (ImageView) findViewById(R.id.iv_photo2_complFeed);
                iv_delete2_complFeed = (ImageView) findViewById(R.id.iv_delete2_complFeed);
                iv_addPhoto3_complFeed = (ImageView) findViewById(R.id.iv_addPhoto3_complFeed);
                iv_photo3_complFeed = (ImageView) findViewById(R.id.iv_photo3_complFeed);
                iv_delete3_complFeed = (ImageView) findViewById(R.id.iv_delete3_complFeed);

                iv_addPhoto4_complFeed = (ImageView) findViewById(R.id.iv_addPhoto4_complFeed);
                iv_photo4_complFeed = (ImageView) findViewById(R.id.iv_photo4_complFeed);
                iv_delete4_complFeed = (ImageView) findViewById(R.id.iv_delete4_complFeed);
                iv_addPhoto5_complFeed = (ImageView) findViewById(R.id.iv_addPhoto5_complFeed);
                iv_photo5_complFeed = (ImageView) findViewById(R.id.iv_photo5_complFeed);
                iv_delete5_complFeed = (ImageView) findViewById(R.id.iv_delete5_complFeed);

                tv_upload_compFeed = (TextView) findViewById(R.id.tv_upload_compFeed);
                tv_feed_complFeed = (TextView) findViewById(R.id.tv_feed_complFeed);
                et_feed_complFeed = (EditText) findViewById(R.id.et_feed_complFeed);
                btn_audio_complFeed = (Button) findViewById(R.id.btn_audio_complFeed);
                rl_audio_complFeed = (RelativeLayout) findViewById(R.id.rl_audio_complFeed);
                tv_audio_complFeed = (TextView) findViewById(R.id.tv_audio_complFeed);
                iv_deleteAudio_complFeed = (ImageView) findViewById(R.id.iv_deleteAudio_complFeed);
                spinner_status_complFeed = (MaterialSpinner) findViewById(R.id.spinner_status_complFeed);
                spinner_payment_complFeed = (MaterialSpinner) findViewById(R.id.spinner_payment_complFeed);
                rl_cheque_complFeed = (RelativeLayout) findViewById(R.id.rl_cheque_complFeed);
                iv_cheque_complFeed = (ImageView) findViewById(R.id.iv_cheque_complFeed);
                tv_cheque_complFeed = (TextView) findViewById(R.id.tv_cheque_complFeed);
                tv_amount_complFeed = (TextView) findViewById(R.id.tv_amount_complFeed);
                et_amount_complFeed = (EditText) findViewById(R.id.et_amount_complFeed);
                btn_submit_complFeed = (Button) findViewById(R.id.btn_submit_complFeed);
                rl_container_complFeed = (RelativeLayout) findViewById(R.id.rl_container_complFeed);

                iv_photo1_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_feed_complFeed.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(1, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_delete1_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileNameProduct1 = "";
                        iv_delete1_complFeed.setImageResource(R.drawable.ic_add);
                        iv_photo1_complFeed.setImageResource(R.drawable.ic_plant);
                    }
                });

                iv_addPhoto2_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(2, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_photo2_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(2, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_delete2_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileNameProduct2 = "";
                        iv_delete2_complFeed.setImageResource(R.drawable.ic_add);
                        iv_photo2_complFeed.setImageResource(R.drawable.ic_plant);
                    }
                });

                iv_addPhoto3_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(3, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_photo3_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(3, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_delete3_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileNameProduct3 = "";
                        iv_delete3_complFeed.setImageResource(R.drawable.ic_add);
                        iv_photo3_complFeed.setImageResource(R.drawable.ic_plant);
                    }
                });

                iv_addPhoto4_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(4, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_photo4_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(4, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_delete4_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileNameProduct4 = "";
                        iv_delete4_complFeed.setImageResource(R.drawable.ic_add);
                        iv_photo4_complFeed.setImageResource(R.drawable.ic_plant);
                    }
                });

                iv_addPhoto5_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(5, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_photo5_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(5, membership_id + "_P" + currentDateTimeString);
                    }
                });
                iv_delete5_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileNameProduct5 = "";
                        iv_delete5_complFeed.setImageResource(R.drawable.ic_add);
                        iv_photo5_complFeed.setImageResource(R.drawable.ic_plant);
                    }
                });

                btn_audio_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        AUDIO_FILE_NAME = membership_id + "_AR" + currentDateTimeString + ".wav";
                        AUDIO_FILE_PATH = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/temp/" + AUDIO_FILE_NAME;

                        AndroidAudioRecorder.with(ComplFeedActivity.this)
                                // Required
                                .setFilePath(AUDIO_FILE_PATH)
                                .setColor(ContextCompat.getColor(ComplFeedActivity.this, R.color.colorTextDark))
                                .setRequestCode(REQUEST_RECORD_AUDIO)

                                // Optional
                                .setSource(AudioSource.MIC)
                                .setChannel(AudioChannel.MONO)
                                .setSampleRate(AudioSampleRate.HZ_8000)
                                .setAutoStart(false)
                                .setKeepDisplayOn(true)

                                // Start recording
                                .record();
                    }
                });


                iv_deleteAudio_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AUDIO_FILE_NAME = "";
                        rl_audio_complFeed.setVisibility(View.GONE);
                    }
                });

                spinner_status_complFeed.setAdapter(spinnerAdapter);
                spinner_status_complFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        status = spinner_status_complFeed.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                spinner_payment_complFeed.setAdapter(spinnerAdapterPayment);
                spinner_payment_complFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        paymentMethod = spinner_payment_complFeed.getItemAtPosition(i).toString();
                        if (paymentMethod.equals("Cheque")) {
                            rl_cheque_complFeed.setVisibility(View.VISIBLE);
                            tv_cheque_complFeed.setVisibility(View.VISIBLE);

                        } else {
                            fileNameCheque = "";
                            rl_cheque_complFeed.setVisibility(View.GONE);
                            tv_cheque_complFeed.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                rl_cheque_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_feed_complFeed.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                        String currentDateTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        utilsForImage.imagePickerDialogeBox(6, membership_id + "_C" + currentDateTimeString);
                    }
                });

                btn_submit_complFeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_feed_complFeed.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                        if (!networkAvailability.isNetworkAvailable()) {
                            rl_container_complFeed.removeView(loaderView);
                            //for enable the screen
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
                        } else {
                            submitForm();
                        }
                    }
                });

            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(ComplFeedActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            ActivityCompat.requestPermissions(ComplFeedActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("Afnan", "onBackPressed Called Complete");
        /*myTimer.cancel();
        myTimer.purge();*/
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(ComplFeedActivity.this, "ComplFeedActivity", "nothing_complFeed");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        utilsForImage.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                rl_audio_complFeed.setVisibility(View.VISIBLE);
                tv_audio_complFeed.setText(AUDIO_FILE_NAME);
                tv_audio_complFeed.setSelected(true);
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }

        // Google Lat Lng
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter
                LatLng latLng;
                latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(ComplFeedActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    ActivityCompat.requestPermissions(ComplFeedActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                    return;
                }

            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
            //Log.e(TAG, "" + status);

        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        utilsForImage.request_permission_result(requestCode, permissions, grantResults);

    }

    @Override
    public void image_attachment(int from, String filename, Bitmap bitmap, Uri uri) {

        String currentDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (from == 1) {
            // Data post to server for submit location data
            LocationDataPost task = new LocationDataPost(from);
            task.execute(prefManager.getUrl() + Config.UpdateCreateLocationDataUrl + currentDateString);

            this.bitmapProduct1 = bitmap;
            this.fileNameProduct1 = filename;
            iv_photo1_complFeed.setImageBitmap(bitmap);
            rl_photo1_complFeed.setVisibility(View.VISIBLE);
            iv_delete1_complFeed.setImageResource(R.drawable.ic_delete_button);
            if (firstTimeAddPhoto2 == true) {
                iv_addPhoto2_complFeed.setVisibility(View.VISIBLE);
                firstTimeAddPhoto2 = false;
            }

        } else if (from == 2) {
            // Data post to server for submit location data
            LocationDataPost task = new LocationDataPost(from);
            task.execute(prefManager.getUrl() + Config.UpdateCreateLocationDataUrl + currentDateString);

            this.bitmapProduct2 = bitmap;
            this.fileNameProduct2 = filename;
            iv_photo2_complFeed.setVisibility(View.VISIBLE);
            iv_delete2_complFeed.setVisibility(View.VISIBLE);
            iv_delete2_complFeed.setImageResource(R.drawable.ic_delete_button);
            iv_photo2_complFeed.setImageBitmap(bitmap);
            iv_addPhoto2_complFeed.setVisibility(View.GONE);
            if (firstTimeAddPhoto3 == true) {
                iv_addPhoto3_complFeed.setVisibility(View.VISIBLE);
                firstTimeAddPhoto3 = false;
            }

        } else if (from == 3) {
            // Data post to server for submit location data
            LocationDataPost task = new LocationDataPost(from);
            task.execute(prefManager.getUrl() + Config.UpdateCreateLocationDataUrl + currentDateString);

            this.bitmapProduct3 = bitmap;
            this.fileNameProduct3 = filename;
            iv_photo3_complFeed.setVisibility(View.VISIBLE);
            iv_delete3_complFeed.setVisibility(View.VISIBLE);
            iv_delete3_complFeed.setImageResource(R.drawable.ic_delete_button);
            iv_photo3_complFeed.setImageBitmap(bitmap);
            iv_addPhoto3_complFeed.setVisibility(View.GONE);
            if (firstTimeAddPhoto4 == true) {
                iv_addPhoto4_complFeed.setVisibility(View.VISIBLE);
                firstTimeAddPhoto4 = false;
            }

        } else if (from == 4) {
            // Data post to server for submit location data
            LocationDataPost task = new LocationDataPost(from);
            task.execute(prefManager.getUrl() + Config.UpdateCreateLocationDataUrl + currentDateString);

            this.bitmapProduct4 = bitmap;
            this.fileNameProduct4 = filename;
            iv_photo4_complFeed.setVisibility(View.VISIBLE);
            iv_delete4_complFeed.setVisibility(View.VISIBLE);
            iv_delete4_complFeed.setImageResource(R.drawable.ic_delete_button);
            iv_photo4_complFeed.setImageBitmap(bitmap);
            iv_addPhoto4_complFeed.setVisibility(View.GONE);
            if (firstTimeAddPhoto5 == true) {
                iv_addPhoto5_complFeed.setVisibility(View.VISIBLE);
                firstTimeAddPhoto5 = false;
            }

        } else if (from == 5) {
            // Data post to server for submit location data
            LocationDataPost task = new LocationDataPost(from);
            task.execute(prefManager.getUrl() + Config.UpdateCreateLocationDataUrl + currentDateString);

            this.bitmapProduct5 = bitmap;
            this.fileNameProduct5 = filename;
            iv_photo5_complFeed.setVisibility(View.VISIBLE);
            iv_delete5_complFeed.setVisibility(View.VISIBLE);
            iv_delete5_complFeed.setImageResource(R.drawable.ic_delete_button);
            iv_photo5_complFeed.setImageBitmap(bitmap);
            iv_addPhoto5_complFeed.setVisibility(View.GONE);

        } else if (from == 6) {
            this.bitmapCheque = bitmap;
            this.fileNameCheque = filename;
            iv_cheque_complFeed.setImageBitmap(bitmap);
        }
        utilsForImage.createImage(bitmap, filename, path, true);
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_complFeed.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplicationContext()),
                sizeAdjustment.getVerticalRatio(140, getApplicationContext()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

    }

    private void submitForm() {
        description = et_feed_complFeed.getText().toString().trim();
        amount = et_amount_complFeed.getText().toString().trim();
        //status = spinner_status_complFeed.getSelectedItem().toString();
        paymentMethod = spinner_payment_complFeed.getSelectedItem().toString();
        if (paymentMethod.equals("Select Option")) {
            paymentMethod = "";
        }

        if (!fileNameProduct1.equals("") || !fileNameProduct2.equals("") || !fileNameProduct3.equals("") || !fileNameProduct4.equals("") || !fileNameProduct5.equals("")) {
            tv_upload_compFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

            if (!description.equals("") || !AUDIO_FILE_NAME.equals("")) {
                tv_feed_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
                //tv_amount_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                //if (!status.equals("Select Option") && !paymentMethod.equals("Select Option")) {

                if (paymentMethod.equals("") || paymentMethod.equals("Cash") || paymentMethod.equals("Cheque") && !fileNameCheque.equals("")) {
                    tv_cheque_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                    getLoaders();

                    if (bitmapProduct1 != null) {
                        // uploading the file to server
                        UploadImageToServer(fileNameProduct1, bitmapProduct1, Config.PlantPicUploadUrl);
                    }
                    if (bitmapProduct2 != null) {
                        UploadImageToServer(fileNameProduct2, bitmapProduct2, Config.PlantPicUploadUrl);
                    }
                    if (bitmapProduct3 != null) {
                        UploadImageToServer(fileNameProduct3, bitmapProduct3, Config.PlantPicUploadUrl);
                    }
                    if (bitmapProduct4 != null) {
                        UploadImageToServer(fileNameProduct4, bitmapProduct4, Config.PlantPicUploadUrl);
                    }
                    if (bitmapProduct5 != null) {
                        UploadImageToServer(fileNameProduct5, bitmapProduct5, Config.PlantPicUploadUrl);
                    }

                    if (!AUDIO_FILE_NAME.equals("")) {
                        // uploading the file to server
                        new UploadAudioToServer().execute(AUDIO_FILE_PATH, Config.AudioFileUploadUrl);
                    }
                    if (bitmapCheque != null) {
                        UploadImageToServer(fileNameCheque, bitmapCheque, Config.ChequePicUploadUrl);
                    }
                    // Data post to server for submit feedback data
                    FeedbackDataPost task = new FeedbackDataPost();
                    task.execute(prefManager.getUrl() + Config.FeedbackDataUrl);

                } else {
                    tv_cheque_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    Toast.makeText(this, "Please upload Cheque photo", Toast.LENGTH_SHORT).show();
                }

                /*} else {
                    Toast.makeText(this, "Please set Status & Payment method", Toast.LENGTH_SHORT).show();
                }*/

            } else {
                tv_feed_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                //tv_amount_complFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                Toast.makeText(this, "Please write feedback or upload Audio feedback", Toast.LENGTH_SHORT).show();
            }

        } else {
            tv_upload_compFeed.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            Toast.makeText(this, "Please upload atleast 1 pic of plant", Toast.LENGTH_SHORT).show();
        }

    }

    private void UploadImageToServer(final String imageName, final Bitmap bitmap, String uploadUrl) {

        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("tags", tags);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(imageName, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private class UploadAudioToServer extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            /*if (!networkAvailability.isNetworkAvailable()) {
                rl_container_editProfile.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_editProfile.setVisibility(View.GONE);
                rl_container_editProfile.addView(noInternetView);
            }*/
        }

        @Override
        protected String doInBackground(String... params) {

            String responseFromServer = "";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            HttpURLConnection conn = null;

            try {
                //------------------ CLIENT REQUEST
                FileInputStream fileInputStream = new FileInputStream(new File(params[0]));
                // open a URL connection to the Servlet
                URL url = new URL(params[1]);
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"audioFiles\";filename=\"" + AUDIO_FILE_NAME + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams
                Log.e("Debug", "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }

            //------------------ read the SERVER RESPONSE
            try {
                InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(inStream);
                String str;
                StringBuffer response = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    Log.e("Debug", "Server Response " + str);
                    response.append(str);
                }
                inStream.close();
                responseFromServer = response.toString();

            } catch (IOException ioex) {
                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
            }

            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rl_main_complFeed.setVisibility(View.VISIBLE);
            rl_container_complFeed.removeView(loaderView);
            //for enable the screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    private class FeedbackDataPost extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public FeedbackDataPost() {
            try {
                postData = new JSONObject();
                postData.put("maintainers_allotment_id", allotment_id);
                postData.put("membership_id", membership_id);
                postData.put("maintainer_id", maintainer_id);
                if (fileNameProduct1.equals("")) {
                    postData.put("plant_img1", fileNameProduct1);
                } else {
                    postData.put("plant_img1", plantImg_url + fileNameProduct1);
                }
                if (fileNameProduct2.equals("")) {
                    postData.put("plant_img2", fileNameProduct2);
                } else {
                    postData.put("plant_img2", plantImg_url + fileNameProduct2);
                }
                if (fileNameProduct3.equals("")) {
                    postData.put("plant_img3", fileNameProduct3);
                } else {
                    postData.put("plant_img3", plantImg_url + fileNameProduct3);
                }
                if (fileNameProduct4.equals("")) {
                    postData.put("plant_img4", fileNameProduct4);
                } else {
                    postData.put("plant_img4", plantImg_url + fileNameProduct4);
                }
                if (fileNameProduct5.equals("")) {
                    postData.put("plant_img5", fileNameProduct5);
                } else {
                    postData.put("plant_img5", plantImg_url + fileNameProduct5);
                }
                postData.put("description", description);
                if (AUDIO_FILE_NAME.equals("")) {
                    postData.put("audio_file_url", AUDIO_FILE_NAME);
                } else {
                    postData.put("audio_file_url", audioFiles_url + AUDIO_FILE_NAME);
                }
                /*if (status.equals("Pending")) {
                    json.put("status", 0);
                } else if (status.equals("Done")) {
                    json.put("status", 1);
                } else if (status.equals("Cancel")) {
                    json.put("status", 2);
                }*/
                postData.put("status", 1);
                postData.put("payment_method", paymentMethod);
                if (fileNameCheque.equals("")) {
                    postData.put("chk_img_url", fileNameCheque);
                } else {
                    postData.put("chk_img_url", chequeImg_url + fileNameCheque);
                }
                postData.put("amount", amount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_complFeed.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String response = null;
            try {
                // This is getting the url from the string we passed in
                URL url = new URL(params[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                // OPTIONAL - Sets an authorization header
                //urlConnection.setRequestProperty("Authorization", "someAuthString");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = ConvertInputStream.convertInputStreamToString(inputStream);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use
                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                }

            } catch (Exception e) {
                Log.d("Afnan", e.getLocalizedMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String Message = jsonObject.getString("msg");
                boolean Status = jsonObject.getBoolean("status");

                if (Status == true) {
                    // Remove loader
                    rl_container_complFeed.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Submitted Succesfully", "The Feedback submitted Succesfully.");
                    // Stop the timer
                    /*myTimer.cancel();
                    myTimer.purge();*/
                } else {
                    // Remove loader
                    rl_container_complFeed.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //Toast.makeText(getApplication(), "" + Message, Toast.LENGTH_LONG).show();
                    alertDialoge.showAlertDialog("single", "Imporatant message", Message);
                }


            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

    private class LocationDataPost extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public LocationDataPost(int image_number) {
            try {
                postData = new JSONObject();
                postData.put("maintainer_id", maintainer_id);
                postData.put("membership_id", membership_id);
                postData.put("image_number", image_number);
                postData.put("latitude", latitude);
                postData.put("longitude", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
        }

        @Override
        protected String doInBackground(String... params) {

            String response = null;
            try {
                // This is getting the url from the string we passed in
                URL url = new URL(params[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                // OPTIONAL - Sets an authorization header
                //urlConnection.setRequestProperty("Authorization", "someAuthString");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = ConvertInputStream.convertInputStreamToString(inputStream);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use
                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                }

            } catch (Exception e) {
                Log.d("Afnan", e.getLocalizedMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String Message = jsonObject.getString("msg");
                boolean Status = jsonObject.getBoolean("status");

                if (Status == true) {

                } else {

                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

}
