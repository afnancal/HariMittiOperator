package com.globopex.harimittioperator.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afnan.datetimelibrary.date.DatePickerDialog;
import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.adapters.AdminAllotment;
import com.globopex.harimittioperator.adapters.AdminAllotmentAdapter;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.GridSpacingItemDecoration;
import com.globopex.harimittioperator.view.PopupMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Afnan on 05-Jun-18.
 */
public class AllotmentListActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar_allotmentList;
    private FragmentDrawerActivity fragment_navigation_drawer_allotmentList;
    private MaterialSearchView search_view_allotmentList;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;

    private RelativeLayout rl_main_allotmentList, rl_container_allotmentList;
    private LinearLayout ll_dateText_allotmentList, ll_date_allotmentList;
    private EditText et_dateFrom_allotmentList, et_dateTo_allotmentList;
    private ImageView iv_go_allotmentList;
    private RecyclerView rv_allotment;
    private TextView tv_avail_allotmentList;

    private AdminAllotmentAdapter adminAllotmentAdapter;
    private List<AdminAllotment> adminAllotmentList;
    private boolean chk1stTime = true;
    private int[] allotmentId;
    private String[] maintainerId, memberId, status, schedule, maintainerName, memberName;
    private int counterMain = 0, counterMemb = 0;

    private String maintainer_id, userType;
    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private AlertDialoge alertDialoge;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allotment_list);

        // toolbar
        toolbar_allotmentList = (Toolbar) findViewById(R.id.toolbar_allotmentList);
        setSupportActionBar(toolbar_allotmentList);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_allotmentList = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_allotmentList);
            fragment_navigation_drawer_allotmentList.setUpActivity(R.id.fragment_navigation_drawer_allotmentList, (DrawerLayout) findViewById(R.id.drawer_layout_allotmentList), toolbar_allotmentList);
            fragment_navigation_drawer_allotmentList.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_allotmentList = (MaterialSearchView) findViewById(R.id.search_view_allotmentList);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotments List");

        // For SearchView
        search_view_allotmentList.setVoiceSearch(true);
        search_view_allotmentList.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_allotmentList.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(AllotmentListActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_allotmentList.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_allotmentList.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, AllotmentListActivity.this);
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
                networkAvailability = new NetworkAvailability(AllotmentListActivity.this);
                alertDialoge = new AlertDialoge(AllotmentListActivity.this);

                Bundle extras = getIntent().getExtras();
                maintainer_id = extras.getString("maintainer_id");
                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_allotmentList = (RelativeLayout) findViewById(R.id.rl_main_allotmentList);
                ll_dateText_allotmentList = (LinearLayout) findViewById(R.id.ll_dateText_allotmentList);
                ll_date_allotmentList = (LinearLayout) findViewById(R.id.ll_date_allotmentList);
                et_dateFrom_allotmentList = (EditText) findViewById(R.id.et_dateFrom_allotmentList);
                et_dateTo_allotmentList = (EditText) findViewById(R.id.et_dateTo_allotmentList);
                iv_go_allotmentList = (ImageView) findViewById(R.id.iv_go_allotmentList);
                rv_allotment = (RecyclerView) findViewById(R.id.rv_allotment);
                tv_avail_allotmentList = (TextView) findViewById(R.id.tv_avail_allotmentList);
                rl_container_allotmentList = (RelativeLayout) findViewById(R.id.rl_container_allotmentList);

                adminAllotmentList = new ArrayList<>();
                adminAllotmentAdapter = new AdminAllotmentAdapter(AllotmentListActivity.this, adminAllotmentList);
                RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
                rv_allotment.setLayoutManager(mLayoutManager1);
                rv_allotment.addItemDecoration(new GridSpacingItemDecoration(0, false));
                rv_allotment.setItemAnimator(new DefaultItemAnimator());
                rv_allotment.setAdapter(adminAllotmentAdapter);
                rv_allotment.setNestedScrollingEnabled(false);

                if (userType.equals("T")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                } else if (userType.equals("P")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                }

                ll_dateText_allotmentList.setVisibility(View.VISIBLE);
                ll_date_allotmentList.setVisibility(View.VISIBLE);
                String currentDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                et_dateFrom_allotmentList.setText(currentDateString);
                et_dateTo_allotmentList.setText(currentDateString);

                et_dateFrom_allotmentList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(AllotmentListActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "DatepickerdialogFrom");
                    }
                });
                et_dateFrom_allotmentList.setTypeface(custom_font_Light);

                et_dateTo_allotmentList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(AllotmentListActivity.this, now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "DatepickerdialogTo");
                    }
                });
                et_dateTo_allotmentList.setTypeface(custom_font_Light);

                iv_go_allotmentList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkAvailability.isNetworkAvailable()) {
                            alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
                        } else {
                            String dateFrom = et_dateFrom_allotmentList.getText().toString().trim();
                            String dateTo = et_dateTo_allotmentList.getText().toString().trim();

                            getLoaders();
                            new AllotmentDataGet().execute(prefManager.getUrl() + Config.AllotmentListUrl + maintainer_id + "/" + dateFrom + "/" + dateTo);
                        }
                    }
                });

                getLoaders();
                new AllotmentDataGet().execute(prefManager.getUrl() + Config.AllotmentListUrl + maintainer_id + "/" + currentDateString + "/" + currentDateString);

            }
        });
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_allotmentList.addView(loaderView);
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
            new AllotmentDataGet().execute(prefManager.getUrl() + Config.AllotmentListUrl + maintainer_id);
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
                rl_container_allotmentList.removeView(noInternetView);
                rl_main_allotmentList.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(AllotmentListActivity.this, "AllotmentListActivity", "nothing_allotmentList");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int monthTemp = ++monthOfYear;
        String month = monthTemp < 10 ? "0" + monthTemp : "" + monthTemp;
        String dayofMonth = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = year + "-" + month + "-" + dayofMonth;
        if (view.getTag().equals("DatepickerdialogFrom")) {
            et_dateFrom_allotmentList.setText(date);
        } else if (view.getTag().equals("DatepickerdialogTo")) {
            et_dateTo_allotmentList.setText(date);
        }
    }

    private class AllotmentDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            //swipe_refresh_layout_home.setRefreshing(true);
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_allotmentList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_allotmentList.setVisibility(View.GONE);
                rl_container_allotmentList.addView(noInternetView);
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
                if (jsonArray.length() != 0) {
                    rv_allotment.setVisibility(View.VISIBLE);
                    tv_avail_allotmentList.setVisibility(View.GONE);

                    allotmentId = new int[jsonArray.length()];
                    maintainerId = new String[jsonArray.length()];
                    memberId = new String[jsonArray.length()];
                    status = new String[jsonArray.length()];
                    schedule = new String[jsonArray.length()];
                    maintainerName = new String[jsonArray.length()];
                    memberName = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int allotment_id = jsonObject.getInt("id");
                        String maintainer_id = jsonObject.getString("maintainer_id");
                        String membership_id = jsonObject.getString("membership_id");
                        String statusTemp = jsonObject.getString("status");
                        String scheduleTemp = jsonObject.getString("schedule");

                        allotmentId[i] = allotment_id;
                        maintainerId[i] = maintainer_id;
                        memberId[i] = membership_id;
                        status[i] = statusTemp;
                        schedule[i] = scheduleTemp;

                        new MaintainerDataGet().execute(prefManager.getUrl() + Config.SearchMaintainerByIdUrl + maintainer_id);
                        new MemberDataGet().execute(prefManager.getUrl() + Config.SearchMemberByIdUrl + membership_id);
                    }
                } else {
                    rv_allotment.setVisibility(View.GONE);
                    tv_avail_allotmentList.setVisibility(View.VISIBLE);

                    rl_main_allotmentList.setVisibility(View.VISIBLE);
                    rl_container_allotmentList.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

    private class MaintainerDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
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
                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    maintainerName[counterMain] = jsonObject.getString("name");
                    /*address[counterMain] = jsonObject.getString("address");
                    contact[counterMain] = jsonObject.getString("contact_no");
                    email[counterMain] = jsonObject.getString("email");
                    imgUrl[counterMain] = jsonObject.getString("img_url");*/

                }
                counterMain++;
                if (counterMain == maintainerName.length) {
                    counterMain = 0;
                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

    private class MemberDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
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
                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    memberName[counterMemb] = jsonObject.getString("name");
                    /*address[counterMemb] = jsonObject.getString("address");
                    contact[counterMemb] = jsonObject.getString("contact_no");
                    email[counterMemb] = jsonObject.getString("email");
                    imgUrl[counterMemb] = jsonObject.getString("img_url");*/

                }
                counterMemb++;
                if (counterMemb == memberName.length) {
                    counterMemb = 0;
                    setTheAllotmentList();
                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

    private void setTheAllotmentList() {
        adminAllotmentList.clear();
        for (int i = 0; i < maintainerId.length; i++) {
            AdminAllotment adminAllotment = new AdminAllotment(allotmentId[i], maintainerId[i], memberId[i], maintainerName[i],
                    memberName[i], schedule[i], status[i]);
            adminAllotmentList.add(adminAllotment);
        }
        adminAllotmentAdapter.notifyDataSetChanged();
        rl_main_allotmentList.setVisibility(View.VISIBLE);
        rl_container_allotmentList.removeView(loaderView);
        //for enable the screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
