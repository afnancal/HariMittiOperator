package com.globopex.harimittioperator.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Afnan on 27-Jun-18.
 */
public class UpdateMaintainerActivity extends AppCompatActivity implements FragmentDrawerActivity.FragmentDrawerActivityListener,
        UtilsForImage.ImageAttachmentListener {

    private Toolbar toolbar;
    private FragmentDrawerActivity fragment_navigation_drawer_updateMaintainer;

    private MaterialSearchView search_view_updateMaintainer;
    private ImageView iv_headerLogo_toolbar, iv_search_toolbar, iv_dotMore;
    private TextView tv_title_toolbar;
    private RelativeLayout rl_main_updateMaintainer, rl_photo_updateMaintainer, rl_container_updateMaintainer;
    private SelectableRoundedImageView iv_photo_updateMaintainer;

    private LinearLayout ll_id_updateMaintainer;
    private TextInputLayout til_name_updateMaintainer, til_email_updateMaintainer, til_mobile_updateMaintainer,
            til_address_updateMaintainer, til_password_updateMaintainer;
    private EditText et_name_updateMaintainer, et_email_updateMaintainer, et_mobile_updateMaintainer,
            et_address_updateMaintainer, et_password_updateMaintainer;
    private Button btn_update_updateMaintainer;
    private TextView tv_contAdmin_updateMaintainer;

    private boolean click1stTime = false;
    private String id, name, address, mobile, email, password, gcm_reg, img_url, userType;
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
    private String file_name = "";
    private String profilePics_url = "http://harimitti.com/uploads/profile_pics/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmember);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_createMember);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            fragment_navigation_drawer_updateMaintainer = (FragmentDrawerActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_createMember);
            fragment_navigation_drawer_updateMaintainer.setUpActivity(R.id.fragment_navigation_drawer_createMember, (DrawerLayout) findViewById(R.id.drawer_layout_createMember), toolbar);
            fragment_navigation_drawer_updateMaintainer.setActivityDrawerListener(this);
        }

        iv_headerLogo_toolbar = (ImageView) findViewById(R.id.iv_headerLogo_toolbar);
        tv_title_toolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        iv_search_toolbar = (ImageView) findViewById(R.id.iv_search_toolbar);
        search_view_updateMaintainer = (MaterialSearchView) findViewById(R.id.search_view_createMember);
        iv_dotMore = (ImageView) findViewById(R.id.iv_dotMore);

        iv_headerLogo_toolbar.setVisibility(View.GONE);
        tv_title_toolbar.setText("Update Maintainer Profile");

        // For SearchView
        search_view_updateMaintainer.setVoiceSearch(true);
        search_view_updateMaintainer.setCursorDrawable(R.drawable.color_cursor_white);
        // For Suggestion aaray
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        search_view_updateMaintainer.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Intent intent = new Intent(UpdateMaintainerActivity.this, SearchListActivity.class);
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

        search_view_updateMaintainer.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        search_view_updateMaintainer.setMenuItem(iv_search_toolbar);
        iv_search_toolbar.setVisibility(View.GONE);
        iv_dotMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v, UpdateMaintainerActivity.this);
                popupMenu.showPopupMenu();
            }
        });

        getView();

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
        DrawerItemClick drawerItemClick = new DrawerItemClick(UpdateMaintainerActivity.this, "UpdateMaintainerActivity", "nothing_updateMaintainer");
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
        iv_photo_updateMaintainer.setImageBitmap(bitmap);
    }

    private void getView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                utilsForImage = new UtilsForImage(UpdateMaintainerActivity.this);
                prefManager = new PrefManager(getApplicationContext());
                networkAvailability = new NetworkAvailability(UpdateMaintainerActivity.this);
                sizeAdjustment = new SizeAdjustment();
                alertDialoge = new AlertDialoge(UpdateMaintainerActivity.this);
                DownloadImage downloadImage = new DownloadImage();

                Bundle extras = getIntent().getExtras();
                id = extras.getString("maintainer_id");
                name = extras.getString("name");
                address = extras.getString("address");
                mobile = extras.getString("contact_no");
                email = extras.getString("email");
                password = extras.getString("password");
                gcm_reg = extras.getString("gcm_reg");
                img_url = extras.getString("img_url");
                userType = prefManager.getUserType();
                custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
                custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

                rl_main_updateMaintainer = (RelativeLayout) findViewById(R.id.rl_main_createMember);
                rl_photo_updateMaintainer = (RelativeLayout) findViewById(R.id.rl_photo_createMember);
                iv_photo_updateMaintainer = (SelectableRoundedImageView) findViewById(R.id.iv_photo_createMember);
                ll_id_updateMaintainer = (LinearLayout) findViewById(R.id.ll_id_createMember);
                til_name_updateMaintainer = (TextInputLayout) findViewById(R.id.til_name_createMember);
                et_name_updateMaintainer = (EditText) findViewById(R.id.et_name_createMember);

                til_email_updateMaintainer = (TextInputLayout) findViewById(R.id.til_email_createMember);
                et_email_updateMaintainer = (EditText) findViewById(R.id.et_email_createMember);
                til_mobile_updateMaintainer = (TextInputLayout) findViewById(R.id.til_mobile_createMember);
                et_mobile_updateMaintainer = (EditText) findViewById(R.id.et_mobile_createMember);

                til_address_updateMaintainer = (TextInputLayout) findViewById(R.id.til_address_createMember);
                et_address_updateMaintainer = (EditText) findViewById(R.id.et_address_createMember);
                til_password_updateMaintainer = (TextInputLayout) findViewById(R.id.til_password_createMember);
                et_password_updateMaintainer = (EditText) findViewById(R.id.et_password_createMember);
                btn_update_updateMaintainer = (Button) findViewById(R.id.btn_submit_createMember);
                rl_container_updateMaintainer = (RelativeLayout) findViewById(R.id.rl_container_createMember);

                if (img_url.equals("")) {
                    iv_photo_updateMaintainer.setImageResource(R.drawable.ic_user);
                } else {
                    downloadImage.setDownloadImage(UpdateMaintainerActivity.this, iv_photo_updateMaintainer, img_url);
                }
                rl_photo_updateMaintainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (click1stTime == true) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_name_updateMaintainer.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                            utilsForImage.imagePickerDialogeBox(1, id);
                        }
                    }
                });

                ll_id_updateMaintainer.setVisibility(View.GONE);
                et_name_updateMaintainer.setText(name);
                et_name_updateMaintainer.setEnabled(false);
                et_email_updateMaintainer.setText(email);
                et_email_updateMaintainer.setEnabled(false);
                et_mobile_updateMaintainer.setText(mobile);
                et_mobile_updateMaintainer.setEnabled(false);
                et_address_updateMaintainer.setText(address);
                et_address_updateMaintainer.setEnabled(false);
                et_password_updateMaintainer.setText(password);
                et_password_updateMaintainer.setEnabled(false);

                et_name_updateMaintainer.addTextChangedListener(new MyTextWatcher(et_name_updateMaintainer));
                //et_name_updateMaintainer.setTypeface(custom_font_Thin);
                et_email_updateMaintainer.addTextChangedListener(new MyTextWatcher(et_email_updateMaintainer));
                et_mobile_updateMaintainer.addTextChangedListener(new MyTextWatcher(et_mobile_updateMaintainer));
                et_address_updateMaintainer.addTextChangedListener(new MyTextWatcher(et_address_updateMaintainer));
                et_password_updateMaintainer.addTextChangedListener(new MyTextWatcher(et_password_updateMaintainer));

                btn_update_updateMaintainer.setText("       Edit Profile       ");
                btn_update_updateMaintainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (click1stTime == false) {
                            click1stTime = true;
                            et_name_updateMaintainer.setEnabled(true);
                            et_email_updateMaintainer.setEnabled(true);
                            et_mobile_updateMaintainer.setEnabled(true);
                            et_address_updateMaintainer.setEnabled(true);
                            et_password_updateMaintainer.setEnabled(true);
                            btn_update_updateMaintainer.setText("       Update Profile       ");

                        } else if (click1stTime == true) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_name_updateMaintainer.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            submitForm();
                        }
                    }
                });

                // Check button enabled or not
                ChkUpdateBtn();

            }
        });

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            et_name_updateMaintainer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_name_updateMaintainer.setTypeface(custom_font_Light);

            et_email_updateMaintainer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_email_updateMaintainer.setTypeface(custom_font_Light);
            et_mobile_updateMaintainer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_mobile_updateMaintainer.setTypeface(custom_font_Light);

            et_address_updateMaintainer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_address_updateMaintainer.setTypeface(custom_font_Light);
            et_password_updateMaintainer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
            et_password_updateMaintainer.setTypeface(custom_font_Light);

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_name_createMember:
                    validateName();
                    break;
                case R.id.et_email_createMember:
                    //validateEmail();
                    break;
                case R.id.et_mobile_createMember:
                    validateMobile();
                    break;
                case R.id.et_address_createMember:
                    validateAddress();
                    break;
                case R.id.et_password_createMember:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validateName() {
        name = et_name_updateMaintainer.getText().toString().trim();

        if (name.isEmpty()) {
            til_name_updateMaintainer.setError(getString(R.string.err_msg_name));
            requestFocus(et_name_updateMaintainer);
            btn_update_updateMaintainer.setEnabled(false);
            return false;
        } else {
            til_name_updateMaintainer.setErrorEnabled(false);
            if (til_mobile_updateMaintainer.isErrorEnabled() == false && til_address_updateMaintainer.isErrorEnabled() == false &&
                    til_password_updateMaintainer.isErrorEnabled() == false && name != "" && mobile != "" && mobile != null &&
                    address != "" && address != null && password != "" && password != null) {
                btn_update_updateMaintainer.setEnabled(true);
            }
        }

        return true;
    }

    private boolean validateMobile() {
        mobile = et_mobile_updateMaintainer.getText().toString().trim();

        if (mobile.isEmpty() || !Validation.isValidPhoneNumber(mobile)) {
            til_mobile_updateMaintainer.setError(getString(R.string.err_msg_mobile));
            requestFocus(et_mobile_updateMaintainer);
            btn_update_updateMaintainer.setEnabled(false);
            return false;
        } else {
            til_mobile_updateMaintainer.setErrorEnabled(false);
            if (til_name_updateMaintainer.isErrorEnabled() == false && til_mobile_updateMaintainer.isErrorEnabled() == false &&
                    til_address_updateMaintainer.isErrorEnabled() == false && mobile != "" && name != "" && name != null &&
                    address != "" && address != null && password != "" && password != null) {
                btn_update_updateMaintainer.setEnabled(true);
            }
        }

        return true;
    }

    private boolean validateAddress() {
        address = et_address_updateMaintainer.getText().toString().trim();

        if (address.isEmpty()) {
            til_address_updateMaintainer.setError(getString(R.string.err_msg_address));
            requestFocus(et_address_updateMaintainer);
            btn_update_updateMaintainer.setEnabled(false);
            return false;
        } else {
            til_address_updateMaintainer.setErrorEnabled(false);
            if (til_name_updateMaintainer.isErrorEnabled() == false && til_mobile_updateMaintainer.isErrorEnabled() == false &&
                    address != "" && name != "" && name != null && mobile != "" && mobile != null && password != "" &&
                    password != null) {
                btn_update_updateMaintainer.setEnabled(true);
                //btn_update_createMember.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background));
            }
        }

        return true;
    }

    private boolean validatePassword() {
        password = et_password_updateMaintainer.getText().toString().trim();

        if (password.isEmpty()) {
            til_password_updateMaintainer.setError(getString(R.string.err_msg_password));
            requestFocus(et_password_updateMaintainer);
            btn_update_updateMaintainer.setEnabled(false);
            return false;
        } else {
            til_password_updateMaintainer.setErrorEnabled(false);
            if (til_name_updateMaintainer.isErrorEnabled() == false && til_mobile_updateMaintainer.isErrorEnabled() == false &&
                    til_address_updateMaintainer.isErrorEnabled() == false && password != "" && name != "" && name != null &&
                    mobile != "" && mobile != null && address != "" && address != null) {
                btn_update_updateMaintainer.setEnabled(true);
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
        name = et_name_updateMaintainer.getText().toString().trim();
        email = et_email_updateMaintainer.getText().toString().trim();
        mobile = et_mobile_updateMaintainer.getText().toString().trim();
        address = et_address_updateMaintainer.getText().toString().trim();
        password = et_password_updateMaintainer.getText().toString().trim();

        if (!name.equals("") && !mobile.equals("") && !address.equals("") && !password.equals("")) {
            btn_update_updateMaintainer.setEnabled(true);
        }

    }

    private void submitForm() {
        name = et_name_updateMaintainer.getText().toString().trim();
        email = et_email_updateMaintainer.getText().toString().trim();
        mobile = et_mobile_updateMaintainer.getText().toString().trim();
        address = et_address_updateMaintainer.getText().toString().trim();
        password = et_password_updateMaintainer.getText().toString().trim();
        if (!img_url.equals("")) {
            String[] separated = img_url.split("/");
            file_name = separated[separated.length - 1];
        }

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_updateMaintainer.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) loaderView.findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplication()), sizeAdjustment.getVerticalRatio(140, getApplication()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        // Data post to server for Update Maintainer profile
        UpdateMaintainerProfile task = new UpdateMaintainerProfile();
        task.execute(prefManager.getUrl() + Config.UpdateMaintainerUrl + id);
    }

    private class UpdateMaintainerProfile extends AsyncTask<String, String, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public UpdateMaintainerProfile() {
            try {
                postData = new JSONObject();
                postData.put("maintainer_id", id);
                postData.put("name", name);
                postData.put("address", address);
                postData.put("contact_no", mobile);
                postData.put("email", email);
                postData.put("password", password);
                postData.put("gcm_reg", gcm_reg);
                if (file_name.equals("")) {
                    postData.put("img_url", "");
                } else {
                    postData.put("img_url", profilePics_url + file_name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_updateMaintainer.removeView(loaderView);
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
                    if (bitmap != null) {
                        // uploading the file to server
                        UploadImageToServer(file_name, bitmap, Config.ProfilePicUploadUrl);
                    }
                    // Remove loader
                    rl_container_updateMaintainer.removeView(loaderView);
                    //for enable the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    alertDialoge.showAlertDialog("single", "Maintainer Profile Update", "The Profile has been updated Succesfully.");
                } else {
                    // Remove loader
                    rl_container_updateMaintainer.removeView(loaderView);
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

}
