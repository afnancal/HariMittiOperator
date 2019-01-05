package com.globopex.harimittioperator.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.map.AppUtils;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.PopupMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.joooonho.SelectableRoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Afnan on 03-May-18.
 */
public class AllotmentDetailsActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_allotment;

    private MaterialSearchView search_view_allotment;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_allotment, rl_container_allotment;
    private SelectableRoundedImageView iv_photo_allotment;
    private TextView tv_id_allotment, tv_name_allotment, tv_contact_allotment, tv_email_allotment, tv_address_allotment,
            tv_dateTime_allotment, tv_status_allotment;
    private AppCompatImageView iv_copy_allotment;
    private Button btn_complete_allotment, btn_direction_allotment, btn_reschedule_allotment;

    private GoogleApiClient mGoogleApiClient;
    Context mContext;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private Double latitude = 0.0, longitude = 0.0;
    private Timer myTimer;

    private int allotment_id;
    private String userType, maintainer_id, membership_id, name, address, contact_no, email, img_url, schedule, status;
    private Typeface custom_font_Light, custom_font_Regular;
    private boolean chk1stTime = true;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allotment);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_allotment);
        setSupportActionBar(toolbar);
        mContext = this;

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_allotment = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_allotment);
            fragment_navigation_drawer_allotment.setUpActivity(R.id.fragment_navigation_drawer_allotment, (DrawerLayout) findViewById(R.id.drawer_layout_allotment), toolbar);
            fragment_navigation_drawer_allotment.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_allotment = (MaterialSearchView) findViewById(R.id.search_view_allotment);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotment Details");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_allotment.setVoiceSearch(true);
        search_view_allotment.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_allotment.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(AllotmentDetailsActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                Snackbar.make(findViewById(R.id.container_allotment), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_allotment.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_allotment.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, AllotmentDetailsActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();
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
        // Temporary off LocationDataPost
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
                    LocationDataPost task = new LocationDataPost();
                    task.execute(prefManager.getUrl() + Config.LocationDataUrl);
                }
            }
        }, 0, 600000);      // For every 10 minutes
    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                prefManager = new PrefManager(getApplicationContext());
                networkAvailability = new NetworkAvailability(AllotmentDetailsActivity.this);
                sizeAdjustment = new SizeAdjustment();

                Bundle extras = getIntent().getExtras();
                allotment_id = extras.getInt("allotment_id");
                maintainer_id = extras.getString("maintainer_id");
                membership_id = extras.getString("membership_id");
                name = extras.getString("name");
                address = extras.getString("address");
                contact_no = extras.getString("contact_no");
                email = extras.getString("email");
                img_url = extras.getString("img_url");
                schedule = extras.getString("schedule");
                status = extras.getString("status");

                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                if (userType.equals("Admin")) {

                } else if (userType.equals("Maintainer")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                    Log.e("Afnan", "AllotmentDataGet");
                }
                rl_main_allotment = (RelativeLayout) findViewById(R.id.rl_main_allotment);
                iv_photo_allotment = (SelectableRoundedImageView) findViewById(R.id.iv_photo_allotment);

                tv_id_allotment = (TextView) findViewById(R.id.tv_id_allotment);
                tv_name_allotment = (TextView) findViewById(R.id.tv_name_allotment);
                tv_contact_allotment = (TextView) findViewById(R.id.tv_contact_allotment);
                tv_email_allotment = (TextView) findViewById(R.id.tv_email_allotment);
                tv_address_allotment = (TextView) findViewById(R.id.tv_address_allotment);
                iv_copy_allotment = (AppCompatImageView) findViewById(R.id.iv_copy_allotment);
                tv_dateTime_allotment = (TextView) findViewById(R.id.tv_dateTime_allotment);
                tv_status_allotment = (TextView) findViewById(R.id.tv_status_allotment);

                btn_complete_allotment = (Button) findViewById(R.id.btn_complete_allotment);
                btn_direction_allotment = (Button) findViewById(R.id.btn_direction_allotment);
                btn_reschedule_allotment = (Button) findViewById(R.id.btn_reschedule_allotment);
                rl_container_allotment = (RelativeLayout) findViewById(R.id.rl_container_allotment);

                tv_id_allotment.setTypeface(custom_font_Regular);
                tv_id_allotment.setText("Member Id: " + membership_id);
                tv_name_allotment.setTypeface(custom_font_Regular);
                tv_name_allotment.setText(name);
                tv_contact_allotment.setTypeface(custom_font_Regular);
                tv_contact_allotment.setText(contact_no);
                tv_email_allotment.setTypeface(custom_font_Regular);
                tv_email_allotment.setText(email);
                tv_address_allotment.setTypeface(custom_font_Regular);
                tv_address_allotment.setText(address);
                iv_copy_allotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("address", address);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(AllotmentDetailsActivity.this, "Address copied successfully.", Toast.LENGTH_SHORT).show();
                    }
                });
                tv_dateTime_allotment.setTypeface(custom_font_Regular);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date newDate = null;
                try {
                    newDate = spf.parse(schedule);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                schedule = spf.format(newDate);
                tv_dateTime_allotment.setText(schedule);

                tv_status_allotment.setTypeface(custom_font_Regular);
                if (status.equals("0")) {
                    tv_status_allotment.setText("Pending");
                    tv_status_allotment.setTextColor(Color.RED);

                } else if (status.equals("1")) {
                    tv_status_allotment.setText("Done");
                    tv_status_allotment.setTextColor(Color.GREEN);
                    btn_complete_allotment.setVisibility(View.GONE);
                    btn_direction_allotment.setVisibility(View.GONE);
                    btn_reschedule_allotment.setVisibility(View.GONE);

                } else if (status.equals("2")) {
                    tv_status_allotment.setText("Cancel");
                    tv_status_allotment.setTextColor(Color.YELLOW);
                    btn_complete_allotment.setVisibility(View.GONE);
                    btn_direction_allotment.setVisibility(View.GONE);
                    btn_reschedule_allotment.setVisibility(View.GONE);
                }
                btn_complete_allotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*myTimer.cancel();
                        myTimer.purge();*/
                        Intent i = new Intent(getApplicationContext(), ComplFeedActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putInt("allotment_id", allotment_id);
                        bundle.putString("maintainer_id", maintainer_id);
                        bundle.putString("membership_id", membership_id);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                btn_direction_allotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*myTimer.cancel();
                        myTimer.purge();*/
                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("address", address);
                        bundle.putString("maintainer_id", maintainer_id);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                btn_reschedule_allotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*myTimer.cancel();
                        myTimer.purge();*/
                        Intent i = new Intent(getApplicationContext(), ReScheduleActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putInt("allotment_id", allotment_id);
                        bundle.putString("maintainer_id", maintainer_id);
                        bundle.putString("membership_id", membership_id);
                        bundle.putString("schedule", schedule);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

            }
        });

        getLoaders();
        new DownloadImage().execute(img_url);

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
            ActivityCompat.requestPermissions(AllotmentDetailsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            ActivityCompat.requestPermissions(AllotmentDetailsActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
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
        Log.e("Afnan", "onStart Called");
        try {
            mGoogleApiClient.connect();
            // Temporary off LocationDataPost
            //setTimer();
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

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                    ActivityCompat.requestPermissions(AllotmentDetailsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    ActivityCompat.requestPermissions(AllotmentDetailsActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
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
        /*myTimer.cancel();
        myTimer.purge();
        Log.e("Afnan", "onBackPressed Called Allotment");*/
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(AllotmentDetailsActivity.this, "AllotmentDetailsActivity", "nothing_allotmentDetails");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_allotment.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150,
                getApplicationContext()), sizeAdjustment.getVerticalRatio(140, getApplicationContext()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        noInternetConnection();
        if (chk1stTime == false) {
            new DownloadImage().execute(img_url);
        }
        chk1stTime = false;
    }

    private void noInternetConnection() {
        // get no_internet.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        noInternetView = li.inflate(R.layout.no_internet, null);
        noInternetView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        TextView tv_internetConn = (TextView) noInternetView.findViewById(R.id.tv_internetConn);
        TextView tv_mobData = (TextView) noInternetView.findViewById(R.id.tv_mobData);
        Button btn_internetConn = (Button) noInternetView.findViewById(R.id.btn_internetConn);

        //rl_mainBody_internetConn.setBackgroundResource(R.drawable.bg);
        tv_internetConn.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(16, getApplicationContext()));
        tv_internetConn.setTypeface(custom_font_Regular);

        tv_mobData.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(16, getApplicationContext()));
        tv_mobData.setTypeface(custom_font_Light);

        btn_internetConn.setTypeface(custom_font_Light);
        //btn_submit.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(12, getApplicationContext()));
        btn_internetConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_container_allotment.removeView(noInternetView);
                rl_main_allotment.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_allotment.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_allotment.setVisibility(View.GONE);
                rl_container_allotment.addView(noInternetView);
            }
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            iv_photo_allotment.setImageBitmap(result);
            rl_main_allotment.setVisibility(View.VISIBLE);
            rl_container_allotment.removeView(loaderView);
            //for enable the screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private class LocationDataPost extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public LocationDataPost() {
            try {
                postData = new JSONObject();
                postData.put("maintainer_id", maintainer_id);
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
