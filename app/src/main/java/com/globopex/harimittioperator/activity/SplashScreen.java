package com.globopex.harimittioperator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.helper.PrefManager;

/**
 * Bismillah
 * Created by Afnan on 16/01/2018.
 */

public class SplashScreen extends Activity {

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefManager = new PrefManager(getApplicationContext());

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (prefManager.getUserLogin() == false) {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    } else {
                        Intent intent = null;
                        if (prefManager.getUserType().equals("Maintainer")) {
                            intent = new Intent(SplashScreen.this, HomeActivity.class);
                        } else if (prefManager.getUserType().equals("Admin")) {
                            intent = new Intent(SplashScreen.this, AdminHomeActivity.class);
                        }
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
