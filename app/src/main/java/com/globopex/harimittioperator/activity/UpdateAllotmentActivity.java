package com.globopex.harimittioperator.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
 * Created by Afnan on 05-Jun-18.
 */
public class UpdateAllotmentActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_updateAllotment;

    private MaterialSearchView search_view_updateAllotment;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_updateAllotment, rl_container_updateAllotment;
    private TextView tv_maintainer_updateAllotment, tv_member_updateAllotment, tv_date_updateAllotment, tv_time_updateAllotment;
    private EditText et_maintainer_updateAllotment, et_member_updateAllotment, et_date_updateAllotment, et_time_updateAllotment;
    private MaterialSpinner spinner_status_updateAllotment;
    private Button btn_submit_updateAllotment;

    private ArrayAdapter<String> spinnerAdapter;
    private static final String[] SPINNER_ITEMS = {"Pending", "Cancel"};
    private int allotment_id;
    private String maintainer_id, membership_id, maintainerName, memberName, schedule, status;

    private Typeface custom_font_Light, custom_font_Regular;
    private boolean chk1stTime = true;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;
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

            fragment_navigation_drawer_updateAllotment = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_createAllotment);
            fragment_navigation_drawer_updateAllotment.setUpActivity(R.id.fragment_navigation_drawer_createAllotment, (DrawerLayout) findViewById(R.id.drawer_layout_createAllotment), toolbar);
            fragment_navigation_drawer_updateAllotment.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_updateAllotment = (MaterialSearchView) findViewById(R.id.search_view_createAllotment);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotment Details");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_updateAllotment.setVoiceSearch(true);
        search_view_updateAllotment.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_updateAllotment.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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

        search_view_updateAllotment.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_updateAllotment.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, UpdateAllotmentActivity.this);
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
                networkAvailability = new NetworkAvailability(UpdateAllotmentActivity.this);
                alertDialoge = new AlertDialoge(UpdateAllotmentActivity.this);

                Bundle extras = getIntent().getExtras();
                allotment_id = extras.getInt("allotment_id");
                maintainer_id = extras.getString("maintainer_id");
                membership_id = extras.getString("membership_id");
                maintainerName = extras.getString("maintainerName");
                memberName = extras.getString("memberName");
                schedule = extras.getString("schedule");
                status = extras.getString("status");
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_updateAllotment = (RelativeLayout) findViewById(R.id.rl_main_createAllotment);
                tv_maintainer_updateAllotment = (TextView) findViewById(R.id.tv_maintainer_createAllotment);
                et_maintainer_updateAllotment = (EditText) findViewById(R.id.et_maintainer_createAllotment);
                tv_member_updateAllotment = (TextView) findViewById(R.id.tv_member_createAllotment);
                et_member_updateAllotment = (EditText) findViewById(R.id.et_member_createAllotment);
                spinner_status_updateAllotment = (MaterialSpinner) findViewById(R.id.spinner_status_createAllotment);

                tv_date_updateAllotment = (TextView) findViewById(R.id.tv_date_createAllotment);
                et_date_updateAllotment = (EditText) findViewById(R.id.et_date_createAllotment);
                tv_time_updateAllotment = (TextView) findViewById(R.id.tv_time_createAllotment);
                et_time_updateAllotment = (EditText) findViewById(R.id.et_time_createAllotment);
                btn_submit_updateAllotment = (Button) findViewById(R.id.btn_submit_createAllotment);
                rl_container_updateAllotment = (RelativeLayout) findViewById(R.id.rl_container_createAllotment);

                et_maintainer_updateAllotment.setText(maintainerName);
                et_maintainer_updateAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(UpdateAllotmentActivity.this, MaintainerListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "UpdateAllotmentActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 1);
                    }
                });
                et_maintainer_updateAllotment.setTypeface(custom_font_Light);

                et_member_updateAllotment.setText(memberName);
                et_member_updateAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(UpdateAllotmentActivity.this, MemberListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "UpdateAllotmentActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 2);
                    }
                });
                et_member_updateAllotment.setTypeface(custom_font_Light);

                spinner_status_updateAllotment.setAdapter(spinnerAdapter);
                if (status.equals("0")) {
                    spinner_status_updateAllotment.setSelection(0);
                } else if (status.equals("2")) {
                    //spinner_status_updateAllotment.setSelection(spinnerAdapter.getPosition("Cancel"));
                    spinner_status_updateAllotment.setSelection(1);
                }
                spinner_status_updateAllotment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        status = spinner_status_updateAllotment.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                String[] separated = schedule.split(" ");
                String dateNdMonth = separated[0];
                String time = separated[1];
                et_date_updateAllotment.setText(dateNdMonth);
                et_date_updateAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(UpdateAllotmentActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMinDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });
                et_date_updateAllotment.setTypeface(custom_font_Light);

                et_time_updateAllotment.setText(time);
                et_time_updateAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance(UpdateAllotmentActivity.this, now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE), false);
                        //tpd.setMaxTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
                        tpd.show(getFragmentManager(), "Timepickerdialog");
                    }
                });
                et_time_updateAllotment.setTypeface(custom_font_Light);

                btn_submit_updateAllotment.setText("Update");
                btn_submit_updateAllotment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkAvailability.isNetworkAvailable()) {
                            rl_container_updateAllotment.removeView(loaderView);
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
        DrawerItemClick drawerItemClick = new DrawerItemClick(UpdateAllotmentActivity.this, "UpdateAllotmentActivity", "nothing_updateAllotment");
        drawerItemClick.displayViewAdmin(position);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int monthTemp = ++monthOfYear;
        String month = monthTemp < 10 ? "0" + monthTemp : "" + monthTemp;
        String dayofMonth = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = year + "-" + month + "-" + dayofMonth;
        et_date_updateAllotment.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = hourString + ":" + minuteString + ":" + secondString;
        et_time_updateAllotment.setText(time);

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
                et_maintainer_updateAllotment.setText(maintainer_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                membership_id = data.getStringExtra("membership_id");
                String member_name = data.getStringExtra("member_name");
                et_member_updateAllotment.setText(member_name);
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
        rl_container_updateAllotment.addView(loaderView);
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
        status = spinner_status_updateAllotment.getSelectedItem().toString();
        String date = et_date_updateAllotment.getText().toString().trim();
        String time = et_time_updateAllotment.getText().toString().trim();
        schedule = date + " " + time;

        getLoaders();
        // Data post to server for Update Maintainer Allotment
        UpdateMaintainersAllotment task = new UpdateMaintainersAllotment();
        task.execute(prefManager.getUrl() + Config.UpdateMaintainersAllotmentUrl + allotment_id);
    }

    private class UpdateMaintainersAllotment extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public UpdateMaintainersAllotment() {
            try {
                postData = new JSONObject();
                postData.put("maintainer_id", maintainer_id);
                postData.put("membership_id", membership_id);
                if (status.equals("Cancel")) {
                    postData.put("status", 2);
                }
                postData.put("schedule", schedule);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_updateAllotment.removeView(loaderView);
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
                urlConnection.setRequestMethod("PUT");

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
                    rl_container_updateAllotment.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Update Succesfully", "The Schedule updated succesfully.");
                } else {
                    // Remove loader
                    rl_container_updateAllotment.removeView(loaderView);
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
