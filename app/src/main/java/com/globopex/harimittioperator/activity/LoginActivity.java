package com.globopex.harimittioperator.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globopex.harimittioperator.BuildConfig;
import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.app.Config;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.helper.SaveImage;
import com.globopex.harimittioperator.helper.Validation;
import com.globopex.harimittioperator.network.ConvertInputStream;
import com.globopex.harimittioperator.network.DownloadImage;
import com.globopex.harimittioperator.network.NetworkAvailability;
import com.globopex.harimittioperator.view.AlertDialoge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Afnan on 05-Mar-18.
 */

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout til_username_login, til_pass_login;
    private EditText et_username_login, et_pass_login;
    private Button btn_login;
    private TextView tv_forgetPass_login;
    private RelativeLayout rl_container_login;

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static long back_pressed;

    private String username, password;
    private Typeface custom_font_Light, custom_font_Medium, custom_font_Regular;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private String UrlRemote;

    private PrefManager prefManager;
    private SizeAdjustment sizeAdjustment;
    private NetworkAvailability networkAvailability;
    private AlertDialoge alertDialoge;
    private View loaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get Remote Config instance.
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        prefManager = new PrefManager(getApplicationContext());
        sizeAdjustment = new SizeAdjustment();
        networkAvailability = new NetworkAvailability(this);
        alertDialoge = new AlertDialoge(LoginActivity.this);

        custom_font_Light = Typeface.createFromAsset(getAssets(), "fonts/Pluto Light.ttf");
        custom_font_Medium = Typeface.createFromAsset(getAssets(), "fonts/Pluto Medium.ttf");
        custom_font_Regular = Typeface.createFromAsset(getAssets(), "fonts/Pluto Regular.ttf");

        til_username_login = (TextInputLayout) findViewById(R.id.til_username_login);
        til_pass_login = (TextInputLayout) findViewById(R.id.til_pass_login);
        et_username_login = (EditText) findViewById(R.id.et_username_login);
        et_pass_login = (EditText) findViewById(R.id.et_pass_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_forgetPass_login = (TextView) findViewById(R.id.tv_forgetPass_login);
        rl_container_login = (RelativeLayout) findViewById(R.id.rl_container_login);

        et_username_login.addTextChangedListener(new LoginTextWatcher(et_username_login));
        et_username_login.setTypeface(custom_font_Light);
        et_pass_login.addTextChangedListener(new LoginTextWatcher(et_pass_login));
        et_pass_login.setTypeface(custom_font_Light);

        btn_login.setTypeface(custom_font_Regular);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_username_login.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                submitForm();
            }
        });

        tv_forgetPass_login.setTypeface(custom_font_Light);
        tv_forgetPass_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_username_login.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                Toast.makeText(getApplicationContext(), "This feature is coming soon.", Toast.LENGTH_LONG).show();
                /*Intent i = new Intent(getApplicationContext(), ForgetActivity.class);
                startActivity(i);*/
            }
        });

        // Check Permission in Marshmallow and above version
        chkPermission();

    }

    private void chkPermission() {
        // Check permission for SMS
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(LoginActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])) {
                //Show Information about why you need the permission
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                Toast.makeText(getBaseContext(), "Go to Permissions to Grant Camera, Phone and Storage", Toast.LENGTH_LONG).show();

            } else {
                //just request the permission
                ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            //txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                //proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionsRequired[1])) {
                //txtPermissions.setText("Permissions Required");
                //ActivityCompat.requestPermissions(LoginActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                System.exit(0);

            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //For disable whole screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (back_pressed + 2000 > System.currentTimeMillis()) {

            super.onBackPressed();
            this.finish();

        } else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    private class LoginTextWatcher implements TextWatcher {

        private View view;

        private LoginTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            et_username_login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextNormal));
            et_username_login.setTypeface(custom_font_Light);

            et_pass_login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextNormal));
            et_pass_login.setTypeface(custom_font_Light);
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_username_login:
                    validateUsername();
                    break;
                case R.id.et_pass_login:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validateUsername() {
        username = et_username_login.getText().toString().trim();

        if (username.isEmpty() || !Validation.isValidPhoneNumber(username)) {
            til_username_login.setError(getString(R.string.err_msg_username));
            requestFocus(et_username_login);
            btn_login.setEnabled(false);
            //btn_login.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_disable));
            return false;
        } else {
            til_username_login.setErrorEnabled(false);
            if (til_pass_login.isErrorEnabled() == false && username != "" && password != "" && password != null) {
                btn_login.setEnabled(true);
                //btn_login.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background));
            }
        }

        return true;
    }

    private boolean validatePassword() {
        password = et_pass_login.getText().toString().trim();

        if (password.isEmpty()) {
            til_pass_login.setError("Enter the Password");
            requestFocus(et_pass_login);
            btn_login.setEnabled(false);
            //btn_login.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background_disable));
            return false;
        } else {
            til_pass_login.setErrorEnabled(false);

            if (til_username_login.isErrorEnabled() == false && password != "" && username != "" && username != null) {
                btn_login.setEnabled(true);
                //btn_login.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_background));
            }
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        loaderView = li.inflate(R.layout.loader, null);
        loaderView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl_container_login.addView(loaderView);
        //For disable whole screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ProgressBar progressBar_loader = (ProgressBar) findViewById(R.id.progressBar_loader);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeAdjustment.getHorizontalRatio(150, getApplication()), sizeAdjustment.getVerticalRatio(140, getApplication()));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        progressBar_loader.setLayoutParams(params);

        String GcmRegistration = prefManager.getGcmRegistration();
        if (GcmRegistration == null) {
            if (!networkAvailability.isNetworkAvailable()) {
                rl_container_login.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
                return;
            }
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    Log.e("GCM Token", "sendRegistrationToServer: " + token);
                    prefManager.setGcmRegistration(token);
                    // send it to server
                }
            });
        }

        if (!networkAvailability.isNetworkAvailable()) {
            rl_container_login.removeView(loaderView);
            alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");

        } else {
            getRemoteConfigValue();
        }
    }

    private void getRemoteConfigValue() {
        LoginActivity.this.runOnUiThread(new Thread(new Runnable() {
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
                        // For Calling Asynchronous Task & Sending Data
                        Map<String, String> postDataLogin = new HashMap<>();
                        postDataLogin.put("contact_no", username);
                        postDataLogin.put("password", password);
                        postDataLogin.put("gcm_reg", prefManager.getGcmRegistration());
                        LoginDataGet taskLogin = new LoginDataGet(postDataLogin);
                        taskLogin.execute(prefManager.getUrl() + Config.LoginUrl);
                        //new LoginDataGet().execute(prefManager.getUrl() + Config.LoginUrl);
                    }
                });

            }
        }));
    }

    private class LoginDataGet extends AsyncTask<String, Void, String> {
        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public LoginDataGet(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                rl_container_login.removeView(loaderView);
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
                urlConnection.setRequestMethod("POST");

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
            if (s == null) {
                rl_container_login.removeView(loaderView);
                //for enable the screen
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                alertDialoge.showAlertDialog("single", "Important Message", "Server slow, please try again.");
            } else {
                try {
                    DownloadImage downloadImage = new DownloadImage();
                    SaveImage saveImage = new SaveImage(getApplicationContext());
                    JSONObject jsonObject = new JSONObject(s);
                    boolean statusLogin = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");

                    if (statusLogin == true) {

                        String user_id = jsonObject.getString("user_id");
                        String name = jsonObject.getString("name");
                        String photoUrl = jsonObject.getString("img_url");
                        String usertype = jsonObject.getString("usertype");

                        String imgFileName = "";
                        if (!photoUrl.equals("")) {
                            Bitmap bitmapPhoto = downloadImage.getBitmap(photoUrl);
                            String[] separated = photoUrl.split("/");
                            imgFileName = separated[separated.length - 1];
                            saveImage.ImageSaved(bitmapPhoto, imgFileName);
                        }
                        //for enable the screen
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prefManager.setUserLogin(true);
                        prefManager.setProfileImageName(imgFileName);
                        prefManager.setUserID(user_id);
                        prefManager.setUserName(name);
                        prefManager.setUserType(usertype);

                        Intent intent = null;
                        if (usertype.equals("Maintainer")) {
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        } else if (usertype.equals("Admin")) {
                            intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                        }
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else {
                        rl_container_login.removeView(loaderView);
                        //for enable the screen
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        alertDialoge.showAlertDialog("single", "Important Message", message);
                    }

                } catch (Exception e) {
                    //Log.e("Exception", "" + e);
                }
            }
        }

    }

}
