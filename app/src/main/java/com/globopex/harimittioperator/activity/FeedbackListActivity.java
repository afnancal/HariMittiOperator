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
import com.globopex.harimittioperator.adapters.Feedback;
import com.globopex.harimittioperator.adapters.FeedbackAdapter;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afnan on 22-Jun-18.
 */
public class FeedbackListActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar_feedbackList;
    private FragmentDrawerActivity fragment_navigation_drawer_feedbackList;
    private MaterialSearchView search_view_feedbackList;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;

    private RelativeLayout rl_main_feedbackList, rl_container_feedbackList;
    private SwipeRefreshLayout swipeRefreshLayout_feedbackList;
    private RecyclerView rv_feedbackList;
    private TextView tv_avail_feedbackList;

    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private boolean chk1stTime = true;
    private String maintainer_id, maintainer_name, membership_id, member_name, dateFrom, dateTo, userType;

    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        // toolbar
        toolbar_feedbackList = (Toolbar) findViewById(R.id.toolbar_feedbackList);
        setSupportActionBar(toolbar_feedbackList);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_feedbackList = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_feedbackList);
            fragment_navigation_drawer_feedbackList.setUpActivity(R.id.fragment_navigation_drawer_feedbackList, (DrawerLayout) findViewById(R.id.drawer_layout_feedbackList), toolbar_feedbackList);
            fragment_navigation_drawer_feedbackList.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_feedbackList = (MaterialSearchView) findViewById(R.id.search_view_feedbackList);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Feedbacks List");

        // For SearchView
        search_view_feedbackList.setVoiceSearch(true);
        search_view_feedbackList.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_feedbackList.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(FeedbackListActivity.this, SearchListActivity.class);
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

        search_view_feedbackList.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_feedbackList.setMenuItem(iv_search_toolbar);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, FeedbackListActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(FeedbackListActivity.this, "FeedbackListActivity", "nothing_feedbackList");
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

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                prefManager = new PrefManager(getApplicationContext());
                sizeAdjustment = new SizeAdjustment();
                networkAvailability = new NetworkAvailability(FeedbackListActivity.this);

                Bundle extras = getIntent().getExtras();
                maintainer_id = extras.getString("maintainer_id");
                maintainer_name = extras.getString("maintainer_name");
                membership_id = extras.getString("membership_id");
                member_name = extras.getString("member_name");
                dateFrom = extras.getString("dateFrom");
                dateTo = extras.getString("dateTo");

                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_feedbackList = (RelativeLayout) findViewById(R.id.rl_main_feedbackList);
                swipeRefreshLayout_feedbackList = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_feedbackList);
                rv_feedbackList = (RecyclerView) findViewById(R.id.rv_feedbackList);
                tv_avail_feedbackList = (TextView) findViewById(R.id.tv_avail_feedbackList);
                rl_container_feedbackList = (RelativeLayout) findViewById(R.id.rl_container_feedbackList);

                feedbackList = new ArrayList<>();
                feedbackAdapter = new FeedbackAdapter(FeedbackListActivity.this, feedbackList);
                RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
                rv_feedbackList.setLayoutManager(mLayoutManager1);
                rv_feedbackList.addItemDecoration(new GridSpacingItemDecoration(0, false));
                rv_feedbackList.setItemAnimator(new DefaultItemAnimator());
                rv_feedbackList.setAdapter(feedbackAdapter);
                rv_feedbackList.setNestedScrollingEnabled(false);

                getLoaders();
                swipeRefreshLayout_feedbackList.setOnRefreshListener(FeedbackListActivity.this);
                /**
                 * Showing Swipe Refresh animation on activity create
                 * As animation won't start on onCreate, post runnable is used
                 */
                swipeRefreshLayout_feedbackList.post(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             //swipe_refresh_layout_home.setRefreshing(true);
                                                             new FeedbackDataGet().execute(prefManager.getUrl() + Config.SearchFeedbackByMainMembIdDate + maintainer_id + "/" + membership_id + "/" + dateFrom + "/" + dateTo);
                                                         }
                                                     }
                );

            }
        });
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipeRefreshLayout_feedbackList.setRefreshing(true);
        new FeedbackDataGet().execute(prefManager.getUrl() + Config.SearchFeedbackByMainMembIdDate + maintainer_id + "/" + membership_id + "/" + dateFrom + "/" + dateTo);
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_feedbackList.addView(loaderView);
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
            new FeedbackDataGet().execute(prefManager.getUrl() + Config.SearchFeedbackByMainMembIdDate + maintainer_id + "/" + membership_id + "/" + dateFrom + "/" + dateTo);
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
                rl_container_feedbackList.removeView(noInternetView);
                rl_main_feedbackList.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    private class FeedbackDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            //swipe_refresh_layout_home.setRefreshing(true);
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_feedbackList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_feedbackList.setVisibility(View.GONE);
                rl_container_feedbackList.addView(noInternetView);
                // stopping swipe refresh
                swipeRefreshLayout_feedbackList.setRefreshing(false);
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
                    feedbackList.clear();
                    rv_feedbackList.setVisibility(View.VISIBLE);
                    tv_avail_feedbackList.setVisibility(View.GONE);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int maintainers_allotment_id = jsonObject.getInt("maintainers_allotment_id");
                        String maintainer_id = jsonObject.getString("maintainer_id");
                        String membership_id = jsonObject.getString("membership_id");
                        String plant_img1 = jsonObject.getString("plant_img1");
                        String plant_img2 = jsonObject.getString("plant_img2");
                        String plant_img3 = jsonObject.getString("plant_img3");
                        String plant_img4 = jsonObject.getString("plant_img4");
                        String plant_img5 = jsonObject.getString("plant_img5");
                        String description = jsonObject.getString("description");
                        String audio_file_url = jsonObject.getString("audio_file_url");
                        int status = jsonObject.getInt("status");
                        String payment_method = jsonObject.getString("payment_method");
                        String chk_img_url = jsonObject.getString("chk_img_url");
                        String amount = jsonObject.getString("amount");
                        String action_on = jsonObject.getString("action_on");

                        Feedback feedback = new Feedback(maintainer_id, maintainer_name, membership_id, plant_img1, plant_img2,
                                plant_img3, plant_img4, plant_img5, description, audio_file_url, status, payment_method,
                                chk_img_url, amount, action_on);
                        feedbackList.add(feedback);

                    }
                    feedbackAdapter.notifyDataSetChanged();

                } else {
                    rv_feedbackList.setVisibility(View.GONE);
                    tv_avail_feedbackList.setVisibility(View.VISIBLE);

                    rl_main_feedbackList.setVisibility(View.VISIBLE);
                }
                rl_container_feedbackList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // stopping swipe refresh
                swipeRefreshLayout_feedbackList.setRefreshing(false);

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

}
