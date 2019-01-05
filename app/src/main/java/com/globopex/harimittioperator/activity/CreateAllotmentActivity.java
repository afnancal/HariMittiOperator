package com.globopex.harimittioperator.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afnan.datetimelibrary.date.DatePickerDialog;
import com.afnan.datetimelibrary.time.TimePickerDialog;
import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.library.DonutLibraryClass.MaterialSpinner;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Afnan on 31-May-18.
 */
public class CreateAllotmentActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_createAllotment;

    private MaterialSearchView search_view_createAllotment;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_createAllotment, rl_container_createAllotment;
    private TextView tv_maintainer_createAllotment, tv_member_createAllotment, tv_date_createAllotment, tv_time_createAllotment;
    private EditText et_maintainer_createAllotment, et_member_createAllotment, et_date_createAllotment, et_time_createAllotment;
    private MaterialSpinner spinner_status_createAllotment;
    private Button btn_submit_createAllotment;

    private ArrayAdapter<String> spinnerAdapter;
    private static final String[] SPINNER_ITEMS = {"Pending"};
    private String maintainer_id = "", membership_id = "", status, schedulePost = "";

    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private AlertDialoge alertDialoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_allotment);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_createAllotment);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_createAllotment = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_createAllotment);
            fragment_navigation_drawer_createAllotment.setUpActivity(R.id.fragment_navigation_drawer_createAllotment, (DrawerLayout) findViewById(R.id.drawer_layout_createAllotment), toolbar);
            fragment_navigation_drawer_createAllotment.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_createAllotment = (MaterialSearchView) findViewById(R.id.search_view_createAllotment);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotment Details");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_createAllotment.setVoiceSearch(true);
        search_view_createAllotment.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_createAllotment.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(AllotmentDetailsActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                Snackbar.make(findViewById(R.id.container_createAllotment), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_createAllotment.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_createAllotment.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, CreateAllotmentActivity.this);
                popupMenu.showPopupMenu();
            }
        });
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SPINNER_ITEMS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        getView();

    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                prefManager = new PrefManager(getApplicationContext());
                sizeAdjustment = new SizeAdjustment();
                networkAvailability = new NetworkAvailability(CreateAllotmentActivity.this);
                alertDialoge = new AlertDialoge(CreateAllotmentActivity.this);

                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_createAllotment = (RelativeLayout) findViewById(R.id.rl_main_createAllotment);
                tv_maintainer_createAllotment = (TextView) findViewById(R.id.tv_maintainer_createAllotment);
                et_maintainer_createAllotment = (EditText) findViewById(R.id.et_maintainer_createAllotment);
                tv_member_createAllotment = (TextView) findViewById(R.id.tv_member_createAllotment);
                et_member_createAllotment = (EditText) findViewById(R.id.et_member_createAllotment);
                spinner_status_createAllotment = (MaterialSpinner) findViewById(R.id.spinner_status_createAllotment);

                tv_date_createAllotment = (TextView) findViewById(R.id.tv_date_createAllotment);
                et_date_createAllotment = (EditText) findViewById(R.id.et_date_createAllotment);
                tv_time_createAllotment = (TextView) findViewById(R.id.tv_time_createAllotment);
                et_time_createAllotment = (EditText) findViewById(R.id.et_time_createAllotment);
                btn_submit_createAllotment = (Button) findViewById(R.id.btn_submit_createAllotment);
                rl_container_createAllotment = (RelativeLayout) findViewById(R.id.rl_container_createAllotment);

                et_maintainer_createAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CreateAllotmentActivity.this, MaintainerListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "CreateAllotmentActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 1);
                    }
                });
                et_maintainer_createAllotment.setTypeface(custom_font_Light);

                et_member_createAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CreateAllotmentActivity.this, MemberListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "CreateAllotmentActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 2);
                    }
                });
                et_member_createAllotment.setTypeface(custom_font_Light);

                spinner_status_createAllotment.setAdapter(spinnerAdapter);
                spinner_status_createAllotment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        status = spinner_status_createAllotment.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                et_date_createAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(CreateAllotmentActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMinDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });
                et_date_createAllotment.setTypeface(custom_font_Light);

                et_time_createAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance(CreateAllotmentActivity.this, now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE), false);
                        //tpd.setMaxTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    }
                });
                et_time_createAllotment.setTypeface(custom_font_Light);

                btn_submit_createAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkAvailability.isNetworkAvailable()) {
                            rl_container_createAllotment.removeView(loaderView);
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
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(CreateAllotmentActivity.this, "CreateAllotmentActivity", "nothing_createAllotment");
        drawerItemClick.displayViewAdmin(position);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int monthTemp = ++monthOfYear;
        String month = monthTemp < 10 ? "0" + monthTemp : "" + monthTemp;
        String dayofMonth = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = year + "-" + month + "-" + dayofMonth;
        et_date_createAllotment.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = hourString + ":" + minuteString + ":" + secondString;
        et_time_createAllotment.setText(time);

    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);

        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                maintainer_id = data.getStringExtra("maintainer_id");
                String maintainer_name = data.getStringExtra("maintainer_name");
                et_maintainer_createAllotment.setText(maintainer_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                membership_id = data.getStringExtra("membership_id");
                String member_name = data.getStringExtra("member_name");
                et_member_createAllotment.setText(member_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_createAllotment.addView(loaderView);
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
        status = spinner_status_createAllotment.getSelectedItem().toString();
        String date = et_date_createAllotment.getText().toString().trim();
        String time = et_time_createAllotment.getText().toString().trim();
        schedulePost = date + " " + time;

        if (!maintainer_id.equals("")) {
            tv_date_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

            if (!membership_id.equals("")) {
                tv_member_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                if (!date.equals("")) {
                    tv_date_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                    if (!time.equals("")) {
                        tv_time_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                        getLoaders();
                        // Data post to server for Maintainer Allotment
                        MaintainerAllotmentDataPost task = new MaintainerAllotmentDataPost();
                        task.execute(prefManager.getUrl() + Config.CreateMaintainerAllotmentUrl);

                    } else {
                        tv_time_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                        Toast.makeText(this, "Please schedule the Time.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    tv_date_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    Toast.makeText(this, "Please schedule the Date.", Toast.LENGTH_SHORT).show();
                }

            } else {
                tv_member_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                Toast.makeText(this, "Please select the Member.", Toast.LENGTH_SHORT).show();
            }

        } else {
            tv_maintainer_createAllotment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            Toast.makeText(this, "Please select the Maintainer.", Toast.LENGTH_SHORT).show();
        }

    }

    private class MaintainerAllotmentDataPost extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public MaintainerAllotmentDataPost() {
            try {
                postData = new JSONObject();
                postData.put("maintainer_id", maintainer_id);
                postData.put("membership_id", membership_id);
                if (status.equals("Pending")) {
                    postData.put("status", 0);
                }
                postData.put("schedule", schedulePost);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_createAllotment.removeView(loaderView);
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
                    rl_container_createAllotment.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Submit Succesfully", "Allotment has been created succesfully.");
                } else {
                    // Remove loader
                    rl_container_createAllotment.removeView(loaderView);
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

}
