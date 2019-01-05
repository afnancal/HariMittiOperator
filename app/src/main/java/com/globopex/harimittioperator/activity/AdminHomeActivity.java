package com.globopex.harimittioperator.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.BuildConfig;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawer;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.PopupMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Created by Afnan on 29-May-18.
 */
public class AdminHomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar toolbar;
    private FragmentDrawer fragment_navigation_drawer;
    private MaterialSearchView searchView;
    private ImageView iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_adminHome, rl_container_adminHome;
    private LinearLayout ll_createMainAllot_adminHome, ll_listMainAllot_adminHome, ll_createMain_adminHome, ll_listMain_adminHome,
            ll_createMemb_adminHome, ll_listMemb_adminHome, ll_createAdmin_adminHome, ll_listAdmin_adminHome, ll_listFeedback_adminHome;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private String UrlRemote;

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
        setContentView(R.layout.activity_adminhome);

        // Get Remote Config instance.
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        prefManager = new PrefManager(getApplicationContext());
        sizeAdjustment = new SizeAdjustment();
        networkAvailability = new NetworkAvailability(AdminHomeActivity.this);
        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
        custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

        rl_main_adminHome = (RelativeLayout) findViewById(R.id.rl_main_adminHome);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        ll_createMainAllot_adminHome = (LinearLayout) findViewById(R.id.ll_createMainAllot_adminHome);
        ll_listMainAllot_adminHome = (LinearLayout) findViewById(R.id.ll_listMainAllot_adminHome);
        ll_createMain_adminHome = (LinearLayout) findViewById(R.id.ll_createMain_adminHome);
        ll_listMain_adminHome = (LinearLayout) findViewById(R.id.ll_listMain_adminHome);
        ll_createMemb_adminHome = (LinearLayout) findViewById(R.id.ll_createMemb_adminHome);
        ll_listMemb_adminHome = (LinearLayout) findViewById(R.id.ll_listMemb_adminHome);
        ll_createAdmin_adminHome = (LinearLayout) findViewById(R.id.ll_createAdmin_adminHome);
        ll_listAdmin_adminHome = (LinearLayout) findViewById(R.id.ll_listAdmin_adminHome);
        ll_listFeedback_adminHome = (LinearLayout) findViewById(R.id.ll_listFeedback_adminHome);
        rl_container_adminHome = (RelativeLayout) findViewById(R.id.rl_container_adminHome);

        //rl_header_adminHome.requestFocus();
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
                PopupMenu popupMenu = new PopupMenu(v, AdminHomeActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        ll_createMainAllot_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, CreateAllotmentActivity.class);
                startActivity(intent);
            }
        });
        ll_listMainAllot_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, MaintainerListActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putString("activity_name", "AdminHomeActivity");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ll_createMain_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, CreateMaintainerActivity.class);
                startActivity(intent);
            }
        });
        ll_listMain_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, MaintainerListActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putString("activity_name", "AdminHomeActivityForMaintainer");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ll_createMemb_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, CreateMemberActivity.class);
                startActivity(intent);
            }
        });
        ll_listMemb_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminHomeActivity.this, MemberListActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putString("activity_name", "AdminHomeActivity");
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        ll_createAdmin_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, CreateAdminActivity.class);
                startActivity(intent);
            }
        });
        ll_listAdmin_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminListActivity.class);
                Bundle bundle = new Bundle();
                // passing array index
                bundle.putString("activity_name", "AdminHomeActivity");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ll_listFeedback_adminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, FeedbackFilterActivity.class);
                startActivity(intent);
            }
        });

        getLoaders();
        if (!networkAvailability.isNetworkAvailable()) {
            rl_container_adminHome.removeView(loaderView);
            rl_main_adminHome.setVisibility(View.GONE);
            rl_container_adminHome.addView(noInternetView);

        } else {
            getRemoteConfigValue();
        }

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        DrawerItemClick drawerItemClick = new DrawerItemClick(AdminHomeActivity.this, "AdminHomeActivity", "nothing_adminHome");
        drawerItemClick.displayViewAdmin(position);
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_adminHome.addView(loaderView);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150,
                getApplicationContext()), sizeAdjustment.getVerticalRatio(140, getApplicationContext()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        noInternetConnection();
        if (chk1stTime == false) {
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_adminHome.removeView(loaderView);
                rl_main_adminHome.setVisibility(View.GONE);
                rl_container_adminHome.addView(noInternetView);

            } else {
                getRemoteConfigValue();
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
                rl_container_adminHome.removeView(noInternetView);
                rl_main_adminHome.setVisibility(View.VISIBLE);
                getLoaders();
            }
        });

    }

    private void getRemoteConfigValue() {
        AdminHomeActivity.this.runOnUiThread(new Thread(new Runnable() {
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
                        rl_container_adminHome.removeView(loaderView);

                    }
                });

            }
        }));
    }

}
