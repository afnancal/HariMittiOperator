package com.globopex.harimittioperator.navigationDrawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.globopex.harimittioperator.activity.AdminHomeActivity;
import com.globopex.harimittioperator.activity.CreateAllotmentActivity;
import com.globopex.harimittioperator.activity.DatabaseBackupActivity;
import com.globopex.harimittioperator.activity.FeedbackFilterActivity;
import com.globopex.harimittioperator.activity.HomeActivity;
import com.globopex.harimittioperator.activity.MaintainerAllotListActivity;
import com.globopex.harimittioperator.activity.MaintainerListActivity;

/**
 * Created by Afnan on 04-May-17.
 */

public class DrawerItemClick {

    private Activity activity;
    private String currentActName;
    private String nothingToDo;

    public DrawerItemClick(Activity activity, String currentActName, String nothingToDo) {
        this.activity = activity;
        this.currentActName = currentActName;
        this.nothingToDo = nothingToDo;
    }

    public void displayViewAdmin(int position) {
        switch (position) {
            case 0:
                if (nothingToDo.equals("nothing_adminHome")) {

                } else {
                    Intent intent1 = new Intent(activity, HomeActivity.class);
                    // set the new task and clear flags
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent1);
                }
                break;
            case 1:
                if (nothingToDo.equals("nothing_createAllotment")) {

                } else {
                    Intent intent2 = new Intent(activity, CreateAllotmentActivity.class);
                    activity.startActivity(intent2);
                }
                break;
            case 2:
                if (nothingToDo.equals("nothing_maintainerList")) {

                } else {
                    Intent intent3 = new Intent(activity, MaintainerListActivity.class);
                    Bundle bundle = new Bundle();
                    // passing array index
                    bundle.putString("activity_name", "AdminHomeActivity");
                    intent3.putExtras(bundle);
                    activity.startActivity(intent3);
                }
                break;
            case 3:
                if (nothingToDo.equals("nothing_feedbackFilter")) {

                } else {
                    Intent intent4 = new Intent(activity, FeedbackFilterActivity.class);
                    activity.startActivity(intent4);
                }
                break;
            case 4:
                if (nothingToDo.equals("nothing_databaseBackup")) {

                } else {
                    Intent intent5 = new Intent(activity, DatabaseBackupActivity.class);
                    activity.startActivity(intent5);
                }
                break;
            default:
                break;
        }

    }

    public void displayViewMaintainer(int position) {
        switch (position) {
            case 0:
                if (nothingToDo.equals("nothing_home")) {

                } else {
                    Intent intent0 = new Intent(activity, HomeActivity.class);
                    // set the new task and clear flags
                    intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent0);
                }
                break;
            case 1:
                if (nothingToDo.equals("nothing_previous")) {

                } else {
                    Intent intent1 = new Intent(activity, MaintainerAllotListActivity.class);
                    intent1.putExtra("activity_value", "previous");
                    activity.startActivity(intent1);
                }
                break;
            case 2:
                if (nothingToDo.equals("nothing_coming")) {

                } else {
                    Intent intent2 = new Intent(activity, MaintainerAllotListActivity.class);
                    intent2.putExtra("activity_value", "coming");
                    activity.startActivity(intent2);
                }
                break;
            case 3:
                if (nothingToDo.equals("nothing_feedbackFilter")) {

                } else {
                    Intent intent4 = new Intent(activity, FeedbackFilterActivity.class);
                    activity.startActivity(intent4);
                }
                break;
            default:
                break;
        }
    }

}
