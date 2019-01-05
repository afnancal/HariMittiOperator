package com.globopex.harimittioperator.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.adapters.Allotment;
import com.globopex.harimittioperator.adapters.AllotmentAdapter;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
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
 * Created by Afnan on 02-Jul-18.
 */
public class MaintainerAllotListActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar_maintainerAllotList;
    private FragmentDrawerActivity fragment_navigation_drawer_maintainerAllotList;
    private MaterialSearchView search_view_maintainerAllotList;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;

    private RelativeLayout rl_main_maintainerAllotList, rl_container_maintainerAllotList;
    private SwipeRefreshLayout swipe_refresh_layout_maintainerAllotList;
    private RecyclerView rv_maintainerAllotList;
    private TextView tv_avail_maintainerAllotList;

    private AllotmentAdapter allotmentAdapter;
    private List<Allotment> allotmentList;
    private boolean chk1stTime = true;
    private int[] allotmentId;
    private String[] maintainerId, memberId, status, schedule, name, address, contact, email, imgUrl;
    private int counter = 0;

    private String activity_value, todaysDate, userType;
    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintainer_allotlist);

        // toolbar
        toolbar_maintainerAllotList = (Toolbar) findViewById(R.id.toolbar_maintainerAllotList);
        setSupportActionBar(toolbar_maintainerAllotList);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_maintainerAllotList = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_maintainerAllotList);
            fragment_navigation_drawer_maintainerAllotList.setUpActivity(R.id.fragment_navigation_drawer_maintainerAllotList, (DrawerLayout) findViewById(R.id.drawer_layout_maintainerAllotList), toolbar_maintainerAllotList);
            fragment_navigation_drawer_maintainerAllotList.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_maintainerAllotList = (MaterialSearchView) findViewById(R.id.search_view_maintainerAllotList);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotments List");
        // For SearchView
        search_view_maintainerAllotList.setVoiceSearch(true);
        search_view_maintainerAllotList.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_maintainerAllotList.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(MaintainerAllotListActivity.this, SearchListActivity.class);
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

        search_view_maintainerAllotList.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_maintainerAllotList.setMenuItem(iv_search_toolbar);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, MaintainerAllotListActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        prefManager = new PrefManager(getApplicationContext());
        sizeAdjustment = new SizeAdjustment();
        networkAvailability = new NetworkAvailability(MaintainerAllotListActivity.this);

        Bundle extras = getIntent().getExtras();
        activity_value = extras.getString("activity_value");
        userType = prefManager.getUserType();
        custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
        custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

        rl_main_maintainerAllotList = (RelativeLayout) findViewById(R.id.rl_main_maintainerAllotList);
        swipe_refresh_layout_maintainerAllotList = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_maintainerAllotList);
        rv_maintainerAllotList = (RecyclerView) findViewById(R.id.rv_maintainerAllotList);
        tv_avail_maintainerAllotList = (TextView) findViewById(R.id.tv_avail_maintainerAllotList);
        rl_container_maintainerAllotList = (RelativeLayout) findViewById(R.id.rl_container_maintainerAllotList);

        allotmentList = new ArrayList<>();
        allotmentAdapter = new AllotmentAdapter(MaintainerAllotListActivity.this, allotmentList);
        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
        rv_maintainerAllotList.setLayoutManager(mLayoutManager1);
        rv_maintainerAllotList.addItemDecoration(new GridSpacingItemDecoration(0, false));
        rv_maintainerAllotList.setItemAnimator(new DefaultItemAnimator());
        rv_maintainerAllotList.setAdapter(allotmentAdapter);
        rv_maintainerAllotList.setNestedScrollingEnabled(false);

        iv_search_toolbar.setVisibility(View.GONE);
        getLoaders();
        swipe_refresh_layout_maintainerAllotList.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipe_refresh_layout_maintainerAllotList.post(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              Date c = Calendar.getInstance().getTime();
                                                              SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                                              todaysDate = df.format(c);
                                                              //swipe_refresh_layout_home.setRefreshing(true);
                                                              if (activity_value.equals("previous")) {
                                                                  new AllotmentDataGet().execute(prefManager.getUrl() + Config.PreviousMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);

                                                              } else if (activity_value.equals("coming")) {
                                                                  new AllotmentDataGet().execute(prefManager.getUrl() + Config.ComingMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);
                                                              }
                                                          }
                                                      }
        );

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        if (activity_value.equals("previous")) {
            DrawerItemClick drawerItemClick = new DrawerItemClick(MaintainerAllotListActivity.this, "MaintainerAllotListActivity",
                    "nothing_previous");
            drawerItemClick.displayViewMaintainer(position);

        } else if (activity_value.equals("coming")) {
            DrawerItemClick drawerItemClick = new DrawerItemClick(MaintainerAllotListActivity.this, "MaintainerAllotListActivity",
                    "nothing_coming");
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

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        todaysDate = df.format(c);
        // showing refresh animation before making http call
        swipe_refresh_layout_maintainerAllotList.setRefreshing(true);
        if (activity_value.equals("previous")) {
            new AllotmentDataGet().execute(prefManager.getUrl() + Config.PreviousMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);

        } else if (activity_value.equals("coming")) {
            new AllotmentDataGet().execute(prefManager.getUrl() + Config.ComingMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);
        }
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_maintainerAllotList.addView(loaderView);
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
            if (activity_value.equals("previous")) {
                new AllotmentDataGet().execute(prefManager.getUrl() + Config.PreviousMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);

            } else if (activity_value.equals("coming")) {
                new AllotmentDataGet().execute(prefManager.getUrl() + Config.ComingMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);
            }
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
                rl_container_maintainerAllotList.removeView(noInternetView);
                rl_main_maintainerAllotList.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    private class AllotmentDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            //swipe_refresh_layout_home.setRefreshing(true);
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_maintainerAllotList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_maintainerAllotList.setVisibility(View.GONE);
                rl_container_maintainerAllotList.addView(noInternetView);
                // stopping swipe refresh
                swipe_refresh_layout_maintainerAllotList.setRefreshing(false);
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
                    rv_maintainerAllotList.setVisibility(View.VISIBLE);
                    tv_avail_maintainerAllotList.setVisibility(View.GONE);

                    allotmentId = new int[jsonArray.length()];
                    maintainerId = new String[jsonArray.length()];
                    memberId = new String[jsonArray.length()];
                    status = new String[jsonArray.length()];
                    schedule = new String[jsonArray.length()];

                    name = new String[jsonArray.length()];
                    address = new String[jsonArray.length()];
                    contact = new String[jsonArray.length()];
                    email = new String[jsonArray.length()];
                    imgUrl = new String[jsonArray.length()];

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

                        new MemberDataGet().execute(prefManager.getUrl() + Config.SearchMemberByIdUrl + membership_id);
                        Log.e("afnan", "Member Data Get");
                        // stopping swipe refresh
                        swipe_refresh_layout_maintainerAllotList.setRefreshing(false);
                    }
                } else {
                    rv_maintainerAllotList.setVisibility(View.GONE);
                    tv_avail_maintainerAllotList.setVisibility(View.VISIBLE);

                    rl_main_maintainerAllotList.setVisibility(View.VISIBLE);
                    rl_container_maintainerAllotList.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    // stopping swipe refresh
                    swipe_refresh_layout_maintainerAllotList.setRefreshing(false);
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
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_maintainerAllotList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_maintainerAllotList.setVisibility(View.GONE);
                rl_container_maintainerAllotList.addView(noInternetView);
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
                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    name[counter] = jsonObject.getString("name");
                    address[counter] = jsonObject.getString("address");
                    contact[counter] = jsonObject.getString("contact_no");
                    email[counter] = jsonObject.getString("email");
                    imgUrl[counter] = jsonObject.getString("img_url");

                }
                counter++;
                if (counter == name.length) {
                    counter = 0;
                    setTheAllotmentList();
                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

    private void setTheAllotmentList() {
        allotmentList.clear();
        for (int i = 0; i < maintainerId.length; i++) {
            Allotment allotment = new Allotment(allotmentId[i], maintainerId[i], memberId[i], name[i], address[i], contact[i],
                    email[i], imgUrl[i], schedule[i], status[i]);
            allotmentList.add(allotment);
        }
        allotmentAdapter.notifyDataSetChanged();
        rl_main_maintainerAllotList.setVisibility(View.VISIBLE);
        rl_container_maintainerAllotList.removeView(loaderView);
        //for enable the screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Log.e("Afnan", "complete");
    }

}
