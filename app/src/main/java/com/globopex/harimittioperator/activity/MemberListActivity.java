package com.globopex.harimittioperator.activity;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.adapters.Member;
import com.globopex.harimittioperator.adapters.MemberAdapter;
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
 * Created by Afnan on 04-Jun-18.
 */
public class MemberListActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener {

    private Toolbar toolbar_memberList;
    private FragmentDrawerActivity fragment_navigation_drawer_memberList;
    private MaterialSearchView search_view_memberList;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;

    private RelativeLayout rl_main_memberList, rl_container_memberList;
    private RecyclerView rv_member;

    private MemberAdapter memberAdapter;
    private List<Member> memberList;
    private boolean chk1stTime = true;

    private String activity_name, userType;
    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private View noInternetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        // toolbar
        toolbar_memberList = (Toolbar) findViewById(R.id.toolbar_memberList);
        setSupportActionBar(toolbar_memberList);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_memberList = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_memberList);
            fragment_navigation_drawer_memberList.setUpActivity(R.id.fragment_navigation_drawer_memberList, (DrawerLayout) findViewById(R.id.drawer_layout_memberList), toolbar_memberList);
            fragment_navigation_drawer_memberList.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_memberList = (MaterialSearchView) findViewById(R.id.search_view_memberList);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Members");

        // For SearchView
        search_view_memberList.setVoiceSearch(true);
        search_view_memberList.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_memberList.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MemberListActivity.this, SearchListActivity.class);
                intent.putExtra("activity_name", "Member");
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_memberList.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_memberList.setMenuItem(iv_search_toolbar);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, MemberListActivity.this);
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
                networkAvailability = new NetworkAvailability(MemberListActivity.this);

                Bundle extras = getIntent().getExtras();
                activity_name = extras.getString("activity_name");
                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_memberList = (RelativeLayout) findViewById(R.id.rl_main_memberList);
                rv_member = (RecyclerView) findViewById(R.id.rv_member);
                rl_container_memberList = (RelativeLayout) findViewById(R.id.rl_container_memberList);

                memberList = new ArrayList<>();
                memberAdapter = new MemberAdapter(MemberListActivity.this, memberList);
                RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 1);
                rv_member.setLayoutManager(mLayoutManager1);
                rv_member.addItemDecoration(new GridSpacingItemDecoration(0, false));
                rv_member.setItemAnimator(new DefaultItemAnimator());
                rv_member.setAdapter(memberAdapter);
                rv_member.setNestedScrollingEnabled(false);

                if (userType.equals("T")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                } else if (userType.equals("P")) {
                    iv_search_toolbar.setVisibility(View.GONE);
                }
                getLoaders();
                new MemberDataGet().execute(prefManager.getUrl() + Config.MembersDeatailUrl);

            }
        });
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_memberList.addView(loaderView);
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
            new MemberDataGet().execute(prefManager.getUrl() + Config.MembersDeatailUrl);
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
                rl_container_memberList.removeView(noInternetView);
                rl_main_memberList.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(MemberListActivity.this, "MemberListActivity", "nothing_memberList");
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

    private class MemberDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // showing refresh animation before making http call
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_memberList.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_memberList.setVisibility(View.GONE);
                rl_container_memberList.addView(noInternetView);
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
                    memberList.clear();
                    rv_member.setVisibility(View.VISIBLE);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String membership_id = jsonObject.getString("membership_id");
                        String name = jsonObject.getString("name");
                        String address = jsonObject.getString("address");
                        String contact_no = jsonObject.getString("contact_no");
                        String email = jsonObject.getString("email");
                        String password = jsonObject.getString("password");
                        String gcm_reg = jsonObject.getString("gcm_reg");
                        String img_url = jsonObject.getString("img_url");

                        Member member = new Member(membership_id, name, address, contact_no, email, password, gcm_reg, img_url);
                        memberList.add(member);
                        member.setActivity_name(activity_name);
                    }
                    memberAdapter.notifyDataSetChanged();
                    rl_container_memberList.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    rv_member.setVisibility(View.GONE);

                    rl_main_memberList.setVisibility(View.VISIBLE);
                    rl_container_memberList.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            } catch (Exception e) {
                //Log.e("Exception", "" + e);
            }
        }

    }

}
