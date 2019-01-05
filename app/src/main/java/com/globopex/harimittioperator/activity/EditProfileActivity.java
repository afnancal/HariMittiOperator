package com.globopex.harimittioperator.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afnan.materialsearchlibrary.MaterialSearchView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.helper.SaveImage;
import com.globopex.harimittioperator.helper.Validation;
import com.globopex.harimittioperator.navigationDrawer.DrawerItemClick;
import com.globopex.harimittioperator.navigationDrawer.FragmentDrawerActivity;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.DownloadImage;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.network.VolleyMultipartRequest;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.globopex.harimittioperator.view.PopupMenu;
import com.globopex.harimittioperator.view.UtilsForImage;
import com.joooonho.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Afnan on 16-May-18.
 */
public class EditProfileActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        UtilsForImage.ImageAttachmentListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_editProfile;

    private MaterialSearchView search_view_editProfile;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_editProfile, rl_photo_editProfile, rl_container_editProfile;
    private SelectableRoundedImageView iv_photo_editProfile;

    private TextInputLayout til_name_editProfile, til_email_editProfile, til_mobile_editProfile, til_address_editProfile,
            til_password_editProfile;
    private EditText et_name_editProfile, et_email_editProfile, et_mobile_editProfile, et_address_editProfile,
            et_password_editProfile;
    private Button btn_update_editProfile;
    private TextView tv_contAdmin_editProfile;

    private String userType;
    private Typeface custom_font_Light, custom_font_Regular;
    private UtilsForImage utilsForImage;
    private PrefManager prefManager;
    private NetworkAvailability networkAvailability;
    private SizeAdjustment sizeAdjustment;
    private View loaderView;
    private View noInternetView;
    private boolean chk1stTime = true;
    private AlertDialoge alertDialoge;

    private Bitmap bitmap;
    private String file_name;
    private String PACKAGE_NAME;
    private String path;
    private String name, email, mobile, address, password;
    private String profilePics_url = "http://harimitti.com/uploads/profile_pics/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_editProfile);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_editProfile = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_editProfile);
            fragment_navigation_drawer_editProfile.setUpActivity(R.id.fragment_navigation_drawer_editProfile, (DrawerLayout) findViewById(R.id.drawer_layout_editProfile), toolbar);
            fragment_navigation_drawer_editProfile.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_editProfile = (MaterialSearchView) findViewById(R.id.search_view_editProfile);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Edit Profile");

        // For SearchView
        search_view_editProfile.setVoiceSearch(true);
        search_view_editProfile.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_editProfile.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(EditProfileActivity.this, SearchListActivity.class);
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

        search_view_editProfile.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_editProfile.setMenuItem(iv_search_toolbar);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, EditProfileActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();

    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                PACKAGE_NAME = getApplicationContext().getPackageName();
                utilsForImage = new UtilsForImage(EditProfileActivity.this);
                prefManager = new PrefManager(getApplicationContext());
                networkAvailability = new NetworkAvailability(EditProfileActivity.this);
                sizeAdjustment = new SizeAdjustment();
                alertDialoge = new AlertDialoge(EditProfileActivity.this);

                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_editProfile = (RelativeLayout) findViewById(R.id.rl_main_editProfile);
                rl_photo_editProfile = (RelativeLayout) findViewById(R.id.rl_photo_editProfile);
                iv_photo_editProfile = (SelectableRoundedImageView) findViewById(R.id.iv_photo_editProfile);
                til_name_editProfile = (TextInputLayout) findViewById(R.id.til_name_editProfile);
                et_name_editProfile = (EditText) findViewById(R.id.et_name_editProfile);

                til_email_editProfile = (TextInputLayout) findViewById(R.id.til_email_editProfile);
                et_email_editProfile = (EditText) findViewById(R.id.et_email_editProfile);
                til_mobile_editProfile = (TextInputLayout) findViewById(R.id.til_mobile_editProfile);
                et_mobile_editProfile = (EditText) findViewById(R.id.et_mobile_editProfile);

                til_address_editProfile = (TextInputLayout) findViewById(R.id.til_address_editProfile);
                et_address_editProfile = (EditText) findViewById(R.id.et_address_editProfile);
                til_password_editProfile = (TextInputLayout) findViewById(R.id.til_password_editProfile);
                et_password_editProfile = (EditText) findViewById(R.id.et_password_editProfile);

                btn_update_editProfile = (Button) findViewById(R.id.btn_update_editProfile);
                tv_contAdmin_editProfile = (TextView) findViewById(R.id.tv_contAdmin_editProfile);
                rl_container_editProfile = (RelativeLayout) findViewById(R.id.rl_container_editProfile);

                if (userType.equals("Admin")) {
                    rl_photo_editProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_name_editProfile.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                            utilsForImage.imagePickerDialogeBox(1, prefManager.getProfileImageName());
                        }
                    });
                }

                et_name_editProfile.addTextChangedListener(new MyTextWatcher(et_name_editProfile));
                //et_name_editProfile.setTypeface(custom_font_Thin);
                et_email_editProfile.addTextChangedListener(new MyTextWatcher(et_email_editProfile));
                et_mobile_editProfile.addTextChangedListener(new MyTextWatcher(et_mobile_editProfile));
                et_address_editProfile.addTextChangedListener(new MyTextWatcher(et_address_editProfile));
                et_password_editProfile.addTextChangedListener(new MyTextWatcher(et_password_editProfile));

                btn_update_editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_name_editProfile.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        submitForm();
                    }
                });

                // Check button enabled or not
                ChkUpdateBtn();

            }
        });

        getLoaders();
        if (userType.equals("Admin")) {
            new AdminProfileDataGet().execute(prefManager.getUrl() + Config.SearchAdminByIdUrl + prefManager.getUserID());

        } else if (userType.equals("Maintainer")) {
            iv_search_toolbar.setVisibility(View.GONE);
            new ProfileDataGet().execute(prefManager.getUrl() + Config.SearchMaintainerByIdUrl + prefManager.getUserID());
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            et_name_editProfile.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_name_editProfile.setTypeface(custom_font_Light);

            et_email_editProfile.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_email_editProfile.setTypeface(custom_font_Light);
            et_mobile_editProfile.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_mobile_editProfile.setTypeface(custom_font_Light);

            et_address_editProfile.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_address_editProfile.setTypeface(custom_font_Light);
            et_password_editProfile.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_password_editProfile.setTypeface(custom_font_Light);

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_name_editProfile:
                    validateName();
                    break;
                case R.id.et_email_editProfile:
                    validateEmail();
                    break;
                case R.id.et_mobile_editProfile:
                    validateMobile();
                    break;
                case R.id.et_address_editProfile:
                    validateAddress();
                    break;
                case R.id.et_password_editProfile:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validateName() {
        name = et_name_editProfile.getText().toString().trim();

        if (name.isEmpty()) {
            til_name_editProfile.setError(getString(R.string.err_msg_name));
            requestFocus(et_name_editProfile);
            btn_update_editProfile.setEnabled(false);
            return false;
        } else {
            til_name_editProfile.setErrorEnabled(false);
            if (til_mobile_editProfile.isErrorEnabled() == false && til_address_editProfile.isErrorEnabled() == false &&
                    til_password_editProfile.isErrorEnabled() == false && name != "" && mobile != "" && mobile != null &&
                    address != "" && address != null && password != "" && password != null) {
                btn_update_editProfile.setEnabled(true);
            }
        }

        return true;
    }

    private boolean validateEmail() {
        email = et_email_editProfile.getText().toString().trim();

        if (email.isEmpty() || !Validation.isValidEmail(email)) {
            til_email_editProfile.setError(getString(R.string.err_msg_email));
            requestFocus(et_email_editProfile);
            btn_update_editProfile.setEnabled(false);
            return false;
        } else {
            til_email_editProfile.setErrorEnabled(false);
            if (til_name_editProfile.isErrorEnabled() == false && til_mobile_editProfile.isErrorEnabled() == false &&
                    til_address_editProfile.isErrorEnabled() == false && til_password_editProfile.isErrorEnabled() == false &&
                    email != "" && name != "" && name != null && mobile != "" && mobile != null && address != "" &&
                    address != null && password != "" && password != null) {
                btn_update_editProfile.setEnabled(true);
            }
        }

        return true;
    }

    private boolean validateMobile() {
        mobile = et_mobile_editProfile.getText().toString().trim();

        if (mobile.isEmpty() || !Validation.isValidPhoneNumber(mobile)) {
            til_mobile_editProfile.setError(getString(R.string.err_msg_mobile));
            requestFocus(et_mobile_editProfile);
            btn_update_editProfile.setEnabled(false);
            return false;
        } else {
            til_mobile_editProfile.setErrorEnabled(false);
            if (til_name_editProfile.isErrorEnabled() == false && til_mobile_editProfile.isErrorEnabled() == false &&
                    til_address_editProfile.isErrorEnabled() == false && mobile != "" && name != "" && name != null &&
                    address != "" && address != null && password != "" && password != null) {
                btn_update_editProfile.setEnabled(true);
            }
        }

        return true;
    }

    private boolean validateAddress() {
        address = et_address_editProfile.getText().toString().trim();

        if (address.isEmpty()) {
            til_address_editProfile.setError(getString(R.string.err_msg_address));
            requestFocus(et_address_editProfile);
            btn_update_editProfile.setEnabled(false);
            return false;
        } else {
            til_address_editProfile.setErrorEnabled(false);
            if (til_name_editProfile.isErrorEnabled() == false && til_mobile_editProfile.isErrorEnabled() == false && address != ""
                    && name != "" && name != null && mobile != "" && mobile != null && password != "" && password != null) {
                btn_update_editProfile.setEnabled(true);
                btn_update_editProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background));
            }
        }

        return true;
    }

    private boolean validatePassword() {
        password = et_password_editProfile.getText().toString().trim();

        if (password.isEmpty()) {
            til_password_editProfile.setError(getString(R.string.err_msg_password));
            requestFocus(et_password_editProfile);
            btn_update_editProfile.setEnabled(false);
            return false;
        } else {
            til_password_editProfile.setErrorEnabled(false);
            if (til_mobile_editProfile.isErrorEnabled() == false && til_address_editProfile.isErrorEnabled() == false &&
                    til_name_editProfile.isErrorEnabled() == false && password != "" && mobile != "" && mobile != null &&
                    address != "" && address != null && name != "" && name != null) {
                btn_update_editProfile.setEnabled(true);
            }
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void ChkUpdateBtn() {
        if (userType.equals("Admin")) {
            btn_update_editProfile.setVisibility(View.VISIBLE);
            name = et_name_editProfile.getText().toString().trim();
            email = et_email_editProfile.getText().toString().trim();
            mobile = et_mobile_editProfile.getText().toString().trim();
            address = et_address_editProfile.getText().toString().trim();
            password = et_password_editProfile.getText().toString().trim();

            if (!name.equals("") && !email.equals("") && !mobile.equals("") && !address.equals("") && !password.equals("")) {
                btn_update_editProfile.setEnabled(true);
            }

        } else if (userType.equals("Maintainer")) {
            et_name_editProfile.setEnabled(false);
            et_email_editProfile.setEnabled(false);
            et_mobile_editProfile.setEnabled(false);
            et_address_editProfile.setEnabled(false);
            et_password_editProfile.setEnabled(false);
            btn_update_editProfile.setVisibility(View.GONE);
            tv_contAdmin_editProfile.setVisibility(View.VISIBLE);
        }

    }

    private void submitForm() {
        name = et_name_editProfile.getText().toString().trim();
        email = et_email_editProfile.getText().toString().trim();
        mobile = et_mobile_editProfile.getText().toString().trim();
        address = et_address_editProfile.getText().toString().trim();
        password = et_password_editProfile.getText().toString().trim();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_editProfile.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplication()), sizeAdjustment.getVerticalRatio(140, getApplication()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        if (bitmap != null) {
            // uploading the file to server
            UploadImageToServer(file_name, bitmap, Config.ProfilePicUploadUrl);
        }
        // Data post to server for update Admin profile
        Map<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("address", address);
        postData.put("contact_no", mobile);
        postData.put("email", email);
        postData.put("password", password);
        postData.put("gcm_reg", prefManager.getGcmRegistration());
        postData.put("img_url", profilePics_url + file_name);
        UpdateAdminProfile task = new UpdateAdminProfile(postData);
        task.execute(prefManager.getUrl() + Config.UpdateAdminUrl + prefManager.getUserID());
    }

    private void getLoaders() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_editProfile.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplicationContext()),
                sizeAdjustment.getVerticalRatio(140, getApplicationContext()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        noInternetConnection();
        if (chk1stTime == false) {
            if (userType.equals("Admin")) {
                new AdminProfileDataGet().execute(prefManager.getUrl() + Config.SearchAdminByIdUrl + prefManager.getUserID());

            } else if (userType.equals("Maintainer")) {
                new ProfileDataGet().execute(prefManager.getUrl() + Config.SearchMaintainerByIdUrl + prefManager.getUserID());
            }
        }
        chk1stTime = false;
    }

    private void noInternetConnection() {
        // get no_internet.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        noInternetView = li.inflate(R.layout.no_internet, null);
        noInternetView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        ImageView iv_internetConn = (ImageView) noInternetView.findViewById(R.id.iv_internetConn);
        TextView tv_internetConn = (TextView) noInternetView.findViewById(R.id.tv_internetConn);
        TextView tv_mobData = (TextView) noInternetView.findViewById(R.id.tv_mobData);
        Button btn_internetConn = (Button) noInternetView.findViewById(R.id.btn_internetConn);

        //rl_mainBody_internetConn.setBackgroundResource(R.drawable.bg);
        iv_internetConn.setImageResource(R.drawable.ic_wifi);
        tv_internetConn.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(16, getApplicationContext()));
        tv_internetConn.setTypeface(custom_font_Regular);

        tv_mobData.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(16, getApplicationContext()));
        tv_mobData.setTypeface(custom_font_Light);

        btn_internetConn.setTypeface(custom_font_Light);
        //btn_submit.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(12, getApplicationContext()));
        btn_internetConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_container_editProfile.removeView(noInternetView);
                rl_main_editProfile.setVisibility(View.VISIBLE);
                getLoaders();
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
        DrawerItemClick drawerItemClick = new DrawerItemClick(EditProfileActivity.this, "EditProfileActivity", "nothing_editProfile");
        if (userType.equals("Admin")) {
            drawerItemClick.displayViewAdmin(position);
        } else if (userType.equals("Maintainer")) {
            drawerItemClick.displayViewMaintainer(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        utilsForImage.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        utilsForImage.request_permission_result(requestCode, permissions, grantResults);

    }

    @Override
    public void image_attachment(int from, String filename, Bitmap bitmap, Uri uri) {
        this.bitmap = bitmap;
        this.file_name = filename;
        iv_photo_editProfile.setImageBitmap(bitmap);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        //path = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/" + prefManager.getProfileImageName();
        path = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/temp/";
        utilsForImage.createImage(bitmap, filename, path, true);
    }

    private class ProfileDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_editProfile.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_editProfile.setVisibility(View.GONE);
                rl_container_editProfile.addView(noInternetView);
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
                DownloadImage downloadImage = new DownloadImage();
                //SaveImage saveImage = new SaveImage(getApplicationContext());

                JSONArray jsonArray = new JSONArray(s);
                String maintainer_id = "";
                String name = "";
                String email = "";
                String phone = "";
                String address = "";
                String photoUrl = "";
                String password = "";

                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject infoObject = jsonArray.getJSONObject(j);

                    maintainer_id = infoObject.getString("maintainer_id");
                    name = infoObject.getString("name");
                    email = infoObject.getString("email");
                    phone = infoObject.getString("contact_no");
                    address = infoObject.getString("address");
                    photoUrl = infoObject.getString("img_url");
                    password = infoObject.getString("password");
                }
                String imgFileName = "";
                if (!photoUrl.equals("")) {
                    String[] separated = photoUrl.split("/");
                    imgFileName = separated[separated.length - 1];
                    //saveImage.ImageSaved(bitmapPhoto, imgFileName);
                    downloadImage.setDownloadImage(EditProfileActivity.this, iv_photo_editProfile, photoUrl);
                } else {
                    iv_photo_editProfile.setImageResource(R.drawable.ic_user);
                }

                et_name_editProfile.setText(name);
                et_email_editProfile.setText(email);
                et_mobile_editProfile.setText(phone);
                et_address_editProfile.setText(address);
                et_password_editProfile.setText(password);

                rl_main_editProfile.setVisibility(View.VISIBLE);
                rl_container_editProfile.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            } catch (Exception e) {
                Log.e("Exception", "" + e);
            }
        }

    }

    private class AdminProfileDataGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_editProfile.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                rl_main_editProfile.setVisibility(View.GONE);
                rl_container_editProfile.addView(noInternetView);
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
                DownloadImage downloadImage = new DownloadImage();
                //SaveImage saveImage = new SaveImage(getApplicationContext());

                JSONArray jsonArray = new JSONArray(s);
                String admin_id = "";
                String name = "";
                String email = "";
                String phone = "";
                String address = "";
                String photoUrl = "";
                String password = "";

                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject infoObject = jsonArray.getJSONObject(j);

                    admin_id = infoObject.getString("admin_id");
                    name = infoObject.getString("name");
                    email = infoObject.getString("email");
                    phone = infoObject.getString("contact_no");
                    address = infoObject.getString("address");
                    photoUrl = infoObject.getString("img_url");
                    password = infoObject.getString("password");
                }
                String imgFileName = "";
                if (!photoUrl.equals("")) {
                    String[] separated = photoUrl.split("/");
                    imgFileName = separated[separated.length - 1];
                    //saveImage.ImageSaved(bitmapPhoto, imgFileName);
                    downloadImage.setDownloadImage(EditProfileActivity.this, iv_photo_editProfile, photoUrl);
                } else {
                    iv_photo_editProfile.setImageResource(R.drawable.ic_user);
                }

                et_name_editProfile.setText(name);
                et_email_editProfile.setText(email);
                et_mobile_editProfile.setText(phone);
                et_address_editProfile.setText(address);
                et_password_editProfile.setText(password);

                rl_main_editProfile.setVisibility(View.VISIBLE);
                rl_container_editProfile.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            } catch (Exception e) {
                Log.e("Exception", "" + e);
            }
        }

    }

    private void UploadImageToServer(final String imageName, final Bitmap bitmap, String uploadUrl) {

        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("tags", tags);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(imageName, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private class UpdateAdminProfile extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public UpdateAdminProfile(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_editProfile.removeView(loaderView);
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
                SaveImage saveImage = new SaveImage(getApplicationContext());
                JSONObject jsonObject = new JSONObject(s);
                String Message = jsonObject.getString("msg");
                boolean Status = jsonObject.getBoolean("status");

                if (Status == true) {
                    saveImage.ImageSaved(bitmap, file_name);
                    // Remove loader
                    rl_container_editProfile.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Update Profile Succesful", "The Profile has been updated Succesfully.");
                } else {
                    // Remove loader
                    rl_container_editProfile.removeView(loaderView);
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
