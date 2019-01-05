package com.globopex.harimittioperator.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.UiAdjustment.SizeAdjustment;
import com.globopex.harimittioperator.activity.AdminHomeActivity;
import com.globopex.harimittioperator.activity.AdminListActivity;
import com.globopex.harimittioperator.activity.HomeActivity;
import com.globopex.harimittioperator.activity.LoginActivity;
import com.globopex.harimittioperator.activity.MaintainerListActivity;
import com.globopex.harimittioperator.activity.MemberListActivity;
import com.globopex.harimittioperator.helper.PrefManager;

import java.io.File;

/**
 * Created by Afnan on 20-Jan-17.
 */

public class AlertDialoge {

    private Activity activity;
    private SizeAdjustment sizeAdjustment;
    private PrefManager prefManager;
    //private DatabaseHandler databaseHandler;

    // Save GCM Reg. for some time to before cleaning the preference data
    private String GcmRegistration;

    public AlertDialoge(Activity activity) {
        this.activity = activity;
    }

    public void showAlertDialog(String singleOrDouble, String title, String message) {
        sizeAdjustment = new SizeAdjustment();
        prefManager = new PrefManager(activity.getApplicationContext());
        //databaseHandler = new DatabaseHandler(activity.getApplicationContext());

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(activity.getApplicationContext());
        View promptsView = li.inflate(R.layout.prompts, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        ImageView iv_forgotPass_alertBox = (ImageView) promptsView.findViewById(R.id.iv_forgotPass_alertBox);
        TextView tv_forgetPass_alertBox = (TextView) promptsView.findViewById(R.id.tv_forgetPass_alertBox);
        TextView tv_emailAddress_alertBox = (TextView) promptsView.findViewById(R.id.tv_emailAddress_alertBox);

        iv_forgotPass_alertBox.setVisibility(View.GONE);
        tv_forgetPass_alertBox.setText(title);
        tv_forgetPass_alertBox.setTextColor(Color.BLACK);
        tv_emailAddress_alertBox.setText(message);
        tv_emailAddress_alertBox.setTextColor(Color.BLACK);
        tv_emailAddress_alertBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeAdjustment.getTextRatio(16, activity.getApplicationContext()));
        tv_emailAddress_alertBox.setPadding(0, sizeAdjustment.getVerticalRatio(7, activity.getApplication()), 0, sizeAdjustment.getVerticalRatio(7, activity.getApplication()));

        if (singleOrDouble.equals("double")) {
            // set dialog message
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GcmRegistration = prefManager.getGcmRegistration();
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    activity.startActivity(intent);
                                    String path = Environment.getExternalStorageDirectory() + "/Android/data/" +
                                            activity.getApplicationContext().getPackageName();
                                    // temp file delete
                                    File file = new File(path, prefManager.getProfileImageName());
                                    if (file.exists()) {
                                        file.delete();
                                        //Log.i("file", "deleted succesfully");
                                    }
                                    prefManager.clearSession();
                                    // Then now again set the Gcm Reg. after deleting the all preference data
                                    prefManager.setGcmRegistration(GcmRegistration);
                                    //databaseHandler.deleteAll();
                                    activity.finish();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
        } else {
            if (title.equals("Submitted Succesfully")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Submit Succesfully")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), AdminHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Update Succesfully")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        /*Intent intent = new Intent(activity.getApplicationContext(), AllotmentListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.startActivity(intent);*/

                                        Intent intent = new Intent(activity.getApplicationContext(), MaintainerListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Bundle bundle = new Bundle();
                                        // passing array index
                                        bundle.putString("activity_name", "AdminHomeActivity");
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);

                                    }
                                });
            } else if (title.equals("Update Password")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        activity.finish();
                                    }
                                });
            } else if (title.equals("Update Profile Succesful")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), AdminHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Member Profile Update")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), MemberListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Bundle bundle = new Bundle();
                                        // passing array index
                                        bundle.putString("activity_name", "AdminHomeActivity");
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Maintainer Profile Update")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), MaintainerListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Bundle bundle = new Bundle();
                                        // passing array index
                                        bundle.putString("activity_name", "AdminHomeActivityForMaintainer");
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Admin Profile Update")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(activity.getApplicationContext(), AdminListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Bundle bundle = new Bundle();
                                        // passing array index
                                        bundle.putString("activity_name", "AdminHomeActivity");
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    }
                                });
            } else if (title.equals("Homework Submit")) {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent returnIntent = new Intent();
                                        //returnIntent.putExtra("result", result);
                                        activity.setResult(Activity.RESULT_OK, returnIntent);
                                        activity.finish();
                                    }
                                });
            } else {
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
            }
        }

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}
