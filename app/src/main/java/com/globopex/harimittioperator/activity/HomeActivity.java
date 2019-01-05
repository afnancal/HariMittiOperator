package com.globopex.harimittioperator.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.BuildConfig;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.adapters.Allotment;
import com.globopex.harimittioperator.adapters.AllotmentAdapter;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawer;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.GridSpacingItemDecoration;
import com.globopex.harimittioperator.view.PopupMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

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

public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private FragmentDrawer fragment_navigation_drawer;
    private MaterialSearchView searchView;
    private ImageView iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private SwipeRefreshLayout swipe_refresh_layout_home;
    private RecyclerView rv_allotment_home;
    private TextView tv_header_home, tv_avail_home;
    private RelativeLayout rl_main_home, rl_container_home;

    private static long back_pressed;
    private AllotmentAdapter allotmentAdapter;
    private List<Allotment> allotmentList;
    private boolean chk1stTime = true;
    private int[] allotmentId;
    private String[] maintainerId, memberId, status, schedule, name, address, contact, email, imgUrl;
    private int counter = 0;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private String UrlRemote;

    private String todaysDate, userType;
    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get Remote Config instance.
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        prefManager = new PrefManager(getApplicationContext());
        sizeAdjustment = new SizeAdjustment();
        networkAvailability = new NetworkAvailability(this);

        userType = prefManager.getUserType();
        custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
        custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

        rl_main_home = (RelativeLayout) findViewById(R.id.rl_main_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        tv_header_home = (TextView) findViewById(R.id.tv_header_home);
        swipe_refresh_layout_home = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_home);
        rv_allotment_home = (RecyclerView) findViewById(R.id.rv_allotment_home);
        tv_avail_home = (TextView) findViewById(R.id.tv_avail_home);
        rl_container_home = (RelativeLayout) findViewById(R.id.rl_container_home);

        //rl_header_home.requestFocus();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fragment_navigation_drawer = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        fragment_navigation_drawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        fragment_navigation_drawer.setDrawerListener(this);

        //tv_title_toolbar.setText(prefManager.getSchoolName());
        tv_title_toolbar.setText("Hari Mitti");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(HomeActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        searchView.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, HomeActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        allotmentList = new ArrayList<>();
        allotmentAdapter = new AllotmentAdapter(this, allotmentList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        rv_allotment_home.setFocusable(false);
        rv_allotment_home.setLayoutManager(mLayoutManager);
        rv_allotment_home.addItemDecoration(new GridSpacingItemDecoration(0, false));
        rv_allotment_home.setItemAnimator(new DefaultItemAnimator());
        rv_allotment_home.setAdapter(allotmentAdapter);
        rv_allotment_home.setNestedScrollingEnabled(false);

        getLoaders();
        swipe_refresh_layout_home.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipe_refresh_layout_home.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               Date c = Calendar.getInstance().getTime();
                                               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                               todaysDate = df.format(c);
                                               //swipe_refresh_layout_home.setRefreshing(true);

                                               if (userType.equals("Maintainer")) {
                                                   iv_search_toolbar.setVisibility(View.GONE);
                                                   if (!networkAvailability.isNetworkAvailable()) {
                                                       rl_container_home.removeView(loaderView);
                                                       //for enable the screen
                                                       getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                       rl_main_home.setVisibility(View.GONE);
                                                       rl_container_home.addView(noInternetView);

                                                   } else {
                                                       getRemoteConfigValue();
                                                   }
                                               }
                                           }
                                       }
        );

    }

    private void getRemoteConfigValue() {
        HomeActivity.this.runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                final String URL = prefManager.getUrl();
                long cacheExpiration = 3600; // 1 hour in seconds.
                firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Afnan", "Fetch Succeeded");
                            // Once the config is successfully fetched it must be activated before newly fetched values are returned.
                            firebaseRemoteConfig.activateFetched();
                            UrlRemote = firebaseRemoteConfig.getString("URL");
                        } else {
                            Log.d("Afnan", "Fetch failed");
                        }

                        if (!UrlRemote.equals(URL)) {
                            prefManager.setUrl(UrlRemote);
                        }
                        new AllotmentDataGet().execute(prefManager.getUrl() + Config.TodayMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);
                    }
                });

            }
        }));
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_home.addView(loaderView);
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
            if (userType.equals("Maintainer")) {
                if (!networkAvailability.isNetworkAvailable()) {
                    rl_container_home.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    rl_main_home.setVisibility(View.GONE);
                    rl_container_home.addView(noInternetView);

                } else {
                    getRemoteConfigValue();
                }

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
                rl_container_home.removeView(noInternetView);
                rl_main_home.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(HomeActivity.this, "HomeActivity", "nothing_home");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipe_refresh_layout_home.setRefreshing(true);
        if (userType.equals("Admin")) {

        } else if (userType.equals("Maintainer")) {
            iv_search_toolbar.setVisibility(View.GONE);
            new AllotmentDataGet().execute(prefManager.getUrl() + Config.TodayMaintainerAllotmentUrl + prefManager.getUserID() + "/" + todaysDate);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            //For disable whole screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (back_pressed + 2000 > System.currentTimeMillis()) {

                super.onBackPressed();
                this.finish();

            } else
                Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private class AllotmentDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            //swipe_refresh_layout_home.setRefreshing(true);
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_home.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_home.setVisibility(View.GONE);
                rl_container_home.addView(noInternetView);
                // stopping swipe refresh
                swipe_refresh_layout_home.setRefreshing(false);
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
                    rv_allotment_home.setVisibility(View.VISIBLE);
                    tv_header_home.setVisibility(View.VISIBLE);
                    tv_avail_home.setVisibility(View.GONE);

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
                        swipe_refresh_layout_home.setRefreshing(false);
                    }
                } else {
                    rv_allotment_home.setVisibility(View.GONE);
                    tv_header_home.setVisibility(View.GONE);
                    tv_avail_home.setVisibility(View.VISIBLE);

                    rl_main_home.setVisibility(View.VISIBLE);
                    rl_container_home.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    // stopping swipe refresh
                    swipe_refresh_layout_home.setRefreshing(false);
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
                rl_container_home.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_home.setVisibility(View.GONE);
                rl_container_home.addView(noInternetView);
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
                Log.e("afnan", "counter size" + counter);
                Log.e("afnan", "name size" + name.length);
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
        rl_main_home.setVisibility(View.VISIBLE);
        rl_container_home.removeView(loaderView);
        //for enable the screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Log.e("Afnan", "complete");
    }

}
