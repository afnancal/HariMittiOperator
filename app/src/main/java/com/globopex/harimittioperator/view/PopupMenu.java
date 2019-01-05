package com.globopex.harimittioperator.view;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.helper.PrefManager;

/**
 * Created by Afnan on 11-Oct-17.
 */

public class PopupMenu {

    private View view;
    private Activity activity;
    private PrefManager prefManager;

    public PopupMenu(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
        prefManager = new PrefManager(activity.getApplicationContext());
    }

    public void showPopupMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(activity, view);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_notifications:
                        /*if (prefManager.getUserType().equals("A")) {
                            Intent intent = new Intent(activity, PushNotificationActivity.class);
                            activity.startActivity(intent);
                        } else if (prefManager.getUserType().equals("T")) {
                            Intent intent = new Intent(activity, NotificationActivity.class);
                            activity.startActivity(intent);
                        } else if (prefManager.getUserType().equals("P")) {
                            Intent intent = new Intent(activity, NotificationActivity.class);
                            activity.startActivity(intent);
                        }*/
                        Toast.makeText(activity, "This feature is coming soon.", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_chat:
                        Toast.makeText(activity, "This feature is coming soon.", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_logOut:
                        AlertDialoge alertDialoge = new AlertDialoge(activity);
                        alertDialoge.showAlertDialog("double", "Are you sure?", "You want to Sign out.");
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the menu
        popup.show();
    }

}
