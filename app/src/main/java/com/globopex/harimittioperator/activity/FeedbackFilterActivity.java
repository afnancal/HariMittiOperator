package com.globopex.harimittioperator.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afnan.datetimelibrary.date.DatePickerDialog;
import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.library.DonutLibraryClass.MaterialSpinner;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;

import java.util.Calendar;

/**
 * Created by Afnan on 21-Jun-18.
 */
public class FeedbackFilterActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_feedbackFilter;

    private MaterialSearchView search_view_feedbackFilter;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_feedbackFilter, rl_status_feedbackFilter, rl_container_feedbackFilter;
    private TextView tv_maintainer_feedbackFilter, tv_member_feedbackFilter, tv_dateFrom_feedbackFilter, tv_dateTo_feedbackFilter;
    private EditText et_maintainer_feedbackFilter, et_member_feedbackFilter, et_dateFrom_feedbackFilter, et_dateTo_feedbackFilter;
    private MaterialSpinner spinner_status_feedbackFilter;
    private Button btn_submit_feedbackFilter, btn_clear_feedbackFilter;

    private String maintainer_id = "blank", maintainer_name = "", membership_id = "blank", member_name = "", dateFrom = "",
            dateTo = "", userType;
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

            fragment_navigation_drawer_feedbackFilter = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_createAllotment);
            fragment_navigation_drawer_feedbackFilter.setUpActivity(R.id.fragment_navigation_drawer_createAllotment, (DrawerLayout) findViewById(R.id.drawer_layout_createAllotment), toolbar);
            fragment_navigation_drawer_feedbackFilter.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_feedbackFilter = (MaterialSearchView) findViewById(R.id.search_view_createAllotment);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_search_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Feedback filter");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_feedbackFilter.setVoiceSearch(true);
        search_view_feedbackFilter.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_feedbackFilter.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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

        search_view_feedbackFilter.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_feedbackFilter.setMenuItem(iv_search_toolbar);

        search_view_feedbackFilter.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, FeedbackFilterActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();

    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                prefManager = new PrefManager(getApplicationContext());
                sizeAdjustment = new SizeAdjustment();
                networkAvailability = new NetworkAvailability(FeedbackFilterActivity.this);
                alertDialoge = new AlertDialoge(FeedbackFilterActivity.this);

                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_feedbackFilter = (RelativeLayout) findViewById(R.id.rl_main_createAllotment);
                tv_maintainer_feedbackFilter = (TextView) findViewById(R.id.tv_maintainer_createAllotment);
                et_maintainer_feedbackFilter = (EditText) findViewById(R.id.et_maintainer_createAllotment);
                tv_member_feedbackFilter = (TextView) findViewById(R.id.tv_member_createAllotment);
                et_member_feedbackFilter = (EditText) findViewById(R.id.et_member_createAllotment);
                rl_status_feedbackFilter = (RelativeLayout) findViewById(R.id.rl_status_createAllotment);
                spinner_status_feedbackFilter = (MaterialSpinner) findViewById(R.id.spinner_status_createAllotment);

                tv_dateFrom_feedbackFilter = (TextView) findViewById(R.id.tv_date_createAllotment);
                et_dateFrom_feedbackFilter = (EditText) findViewById(R.id.et_date_createAllotment);
                tv_dateTo_feedbackFilter = (TextView) findViewById(R.id.tv_time_createAllotment);
                et_dateTo_feedbackFilter = (EditText) findViewById(R.id.et_time_createAllotment);
                btn_clear_feedbackFilter = (Button) findViewById(R.id.btn_clear_createAllotment);
                btn_submit_feedbackFilter = (Button) findViewById(R.id.btn_submit_createAllotment);
                rl_container_feedbackFilter = (RelativeLayout) findViewById(R.id.rl_container_createAllotment);

                tv_maintainer_feedbackFilter.setText("Select Maintainer");
                tv_member_feedbackFilter.setText("Select Member");
                rl_status_feedbackFilter.setVisibility(View.GONE);
                tv_dateFrom_feedbackFilter.setText("Select Date From");
                tv_dateTo_feedbackFilter.setText("Select Date To");
                btn_clear_feedbackFilter.setVisibility(View.VISIBLE);
                btn_submit_feedbackFilter.setText("Search");

                et_maintainer_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(FeedbackFilterActivity.this, MaintainerListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "FeedbackFilterActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 1);
                    }
                });
                et_maintainer_feedbackFilter.setTypeface(custom_font_Light);

                et_member_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(FeedbackFilterActivity.this, MemberListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("activity_name", "FeedbackFilterActivity");
                        i.putExtras(bundle);
                        startActivityForResult(i, 2);
                    }
                });
                et_member_feedbackFilter.setTypeface(custom_font_Light);

                et_dateFrom_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(FeedbackFilterActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "DatepickerdialogFrom");
                    }
                });
                et_dateFrom_feedbackFilter.setTypeface(custom_font_Light);

                et_dateTo_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(FeedbackFilterActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "DatepickerdialogTo");
                    }
                });
                et_dateTo_feedbackFilter.setTypeface(custom_font_Light);

                btn_submit_feedbackFilter.setText("Search");
                btn_submit_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkAvailability.isNetworkAvailable()) {
                            rl_container_feedbackFilter.removeView(loaderView);
                            //for enable the screen
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
                        } else {
                            submitForm();
                        }
                    }
                });

                btn_clear_feedbackFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        et_maintainer_feedbackFilter.setText("");
                        et_member_feedbackFilter.setText("");
                        et_dateFrom_feedbackFilter.setText("");
                        et_dateTo_feedbackFilter.setText("");
                        maintainer_id = "blank";
                        maintainer_name = "";
                        membership_id = "blank";
                        member_name = "";
                        dateFrom = "blank";
                        dateTo = "blank";
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
        DrawerItemClick drawerItemClick = new DrawerItemClick(FeedbackFilterActivity.this, "FeedbackFilterActivity", "nothing_feedbackFilter");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int monthTemp = ++monthOfYear;
        String month = monthTemp < 10 ? "0" + monthTemp : "" + monthTemp;
        String dayofMonth = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = year + "-" + month + "-" + dayofMonth;
        if (view.getTag().equals("DatepickerdialogFrom")) {
            et_dateFrom_feedbackFilter.setText(date);
        } else if (view.getTag().equals("DatepickerdialogTo")) {
            et_dateTo_feedbackFilter.setText(date);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("DatepickerdialogFrom");
        if (dpd != null) dpd.setOnDateSetListener(this);

        DatePickerDialog dpd1 = (DatePickerDialog) getFragmentManager().findFragmentByTag("DatepickerdialogTo");
        if (dpd1 != null) dpd1.setOnDateSetListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                maintainer_id = data.getStringExtra("maintainer_id");
                maintainer_name = data.getStringExtra("maintainer_name");
                et_maintainer_feedbackFilter.setText(maintainer_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                membership_id = data.getStringExtra("membership_id");
                member_name = data.getStringExtra("member_name");
                et_member_feedbackFilter.setText(member_name);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void submitForm() {
        dateFrom = et_dateFrom_feedbackFilter.getText().toString().trim();
        if (dateFrom.equals("")) {
            dateFrom = "blank";
        }
        dateTo = et_dateTo_feedbackFilter.getText().toString().trim();
        if (dateTo.equals("")) {
            dateTo = "blank";
        }

        /*if (!maintainer_id.equals("")) {
            tv_dateFrom_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

            if (!membership_id.equals("")) {
                tv_member_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                if (!dateFrom.equals("")) {
                    tv_dateFrom_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                    if (!dateTo.equals("")) {
                        tv_dateTo_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

                        Intent intent = new Intent(FeedbackFilterActivity.this, FeedbackListActivity.class);
                        Bundle bundle = new Bundle();
                        // passing array index
                        bundle.putString("maintainer_id", maintainer_id);
                        bundle.putString("maintainer_name", maintainer_name);
                        bundle.putString("membership_id", membership_id);
                        bundle.putString("member_name", member_name);
                        bundle.putString("dateFrom", dateFrom);
                        bundle.putString("dateTo", dateTo);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else {
                        tv_dateTo_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                        Toast.makeText(this, "Please schedule the Date To.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    tv_dateFrom_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                    Toast.makeText(this, "Please schedule the Date From.", Toast.LENGTH_SHORT).show();
                }

            } else {
                tv_member_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                Toast.makeText(this, "Please select the Member.", Toast.LENGTH_SHORT).show();
            }

        } else {
            tv_maintainer_feedbackFilter.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            Toast.makeText(this, "Please select the Maintainer.", Toast.LENGTH_SHORT).show();
        }*/

        Intent intent = new Intent(FeedbackFilterActivity.this, FeedbackListActivity.class);
        Bundle bundle = new Bundle();
        // passing array index
        bundle.putString("maintainer_id", maintainer_id);
        bundle.putString("maintainer_name", maintainer_name);
        bundle.putString("membership_id", membership_id);
        bundle.putString("member_name", member_name);
        bundle.putString("dateFrom", dateFrom);
        bundle.putString("dateTo", dateTo);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}
