package com.globopex.harimittioperator.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.helper.Validation;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Afnan on 20-Jul-18.
 */
public class DatabaseBackupActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_databaseBackup;

    private MaterialSearchView search_view_databaseBackup;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_databaseBackup, rl_container_databaseBackup;
    private TextView tv_email_databaseBackup;
    private TextInputLayout til_email_databaseBackup;
    private EditText et_email_databaseBackup;
    private Button btn_submit_databaseBackup;

    private String email_id = "";

    private Typeface custom_font_Light, custom_font_Regular;
    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private View loaderView;
    private AlertDialoge alertDialoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_backup);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_databaseBackup);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_databaseBackup = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_databaseBackup);
            fragment_navigation_drawer_databaseBackup.setUpActivity(R.id.fragment_navigation_drawer_databaseBackup, (DrawerLayout) findViewById(R.id.drawer_layout_databaseBackup), toolbar);
            fragment_navigation_drawer_databaseBackup.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_databaseBackup = (MaterialSearchView) findViewById(R.id.search_view_databaseBackup);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Allotment Details");
        tv_title_toolbar.setSelected(true);

        // For SearchView
        search_view_databaseBackup.setVoiceSearch(true);
        search_view_databaseBackup.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_databaseBackup.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(AllotmentDetailsActivity.this, SearchListActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);*/
                Snackbar.make(findViewById(R.id.container_databaseBackup), "Query: " + query, Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        search_view_databaseBackup.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_databaseBackup.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, DatabaseBackupActivity.this);
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
                networkAvailability = new NetworkAvailability(DatabaseBackupActivity.this);
                alertDialoge = new AlertDialoge(DatabaseBackupActivity.this);

                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_databaseBackup = (RelativeLayout) findViewById(R.id.rl_main_databaseBackup);
                tv_email_databaseBackup = (TextView) findViewById(R.id.tv_email_databaseBackup);
                til_email_databaseBackup = (TextInputLayout) findViewById(R.id.til_email_databaseBackup);
                et_email_databaseBackup = (EditText) findViewById(R.id.et_email_databaseBackup);
                btn_submit_databaseBackup = (Button) findViewById(R.id.btn_submit_databaseBackup);
                rl_container_databaseBackup = (RelativeLayout) findViewById(R.id.rl_container_databaseBackup);

                tv_email_databaseBackup.setTypeface(custom_font_Regular);
                et_email_databaseBackup.addTextChangedListener(new MyTextWatcher(et_email_databaseBackup));

                btn_submit_databaseBackup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!networkAvailability.isNetworkAvailable()) {
                            rl_container_databaseBackup.removeView(loaderView);
                            //for enable the screen
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
                        } else {
                            submitForm();
                        }
                    }
                });
                // Check button enabled or not
                ChkUpdateBtn();

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
        DrawerItemClick drawerItemClick = new DrawerItemClick(DatabaseBackupActivity.this, "DatabaseBackupActivity", "nothing_databaseBackup");
        drawerItemClick.displayViewAdmin(position);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            et_email_databaseBackup.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_email_databaseBackup.setTypeface(custom_font_Light);
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_email_databaseBackup:
                    validateEmail();
                    break;
            }
        }
    }

    private boolean validateEmail() {
        email_id = et_email_databaseBackup.getText().toString().trim();

        if (email_id.isEmpty() || !Validation.isValidEmail(email_id)) {
            til_email_databaseBackup.setError(getString(R.string.err_msg_email));
            requestFocus(et_email_databaseBackup);
            btn_submit_databaseBackup.setEnabled(false);
            return false;
        } else {
            til_email_databaseBackup.setErrorEnabled(false);
            btn_submit_databaseBackup.setEnabled(true);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void ChkUpdateBtn() {
        email_id = et_email_databaseBackup.getText().toString().trim();
        if (!email_id.equals("")) {
            btn_submit_databaseBackup.setEnabled(true);
        }
    }

    private void submitForm() {
        email_id = et_email_databaseBackup.getText().toString().trim();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_databaseBackup.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplication()), sizeAdjustment.getVerticalRatio(140, getApplication()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        // Database Attachment file send to the Email
        new SendEmail().execute(prefManager.getUrl() + Config.DatabaseBackup + email_id + "/");

    }

    private class SendEmail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_databaseBackup.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
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
                JSONObject jsonObject = new JSONObject(s);
                String Message = jsonObject.getString("msg");
                boolean Status = jsonObject.getBoolean("status");

                if (Status == true) {
                    // Remove loader
                    rl_container_databaseBackup.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Mail sent Succesfully", Message);
                    et_email_databaseBackup.setText("");
                    email_id = "";
                    til_email_databaseBackup.setErrorEnabled(false);

                } else {
                    // Remove loader
                    rl_container_databaseBackup.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //Toast.makeText(getApplication(), "" + Message, Toast.LENGTH_LONG).show();
                    alertDialoge.showAlertDialog("single", "Imporatant message", Message);
                }

            } catch (Exception e) {
                Log.e("Exception", "" + e);
            }
        }

    }

}
