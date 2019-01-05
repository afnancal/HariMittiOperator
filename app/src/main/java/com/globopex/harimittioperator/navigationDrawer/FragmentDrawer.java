package com.globopex.harimittioperator.navigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.activity.EditProfileActivity;
import com.globopex.harimittioperator.helper.PrefManager;
import com.globopex.harimittioperator.helper.SaveImage;
import com.joooonho.SelectableRoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afnan on 19-Dec-16.
 */
public class FragmentDrawer extends Fragment {

    private static SelectableRoundedImageView iv_drawer_pic;
    private static ImageView iv_editProfile_drawer;
    private static TextView tv_drawer_name;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private static int[] iconsMaintainer = {R.drawable.ic_home, R.drawable.ic_allocation_previous, R.drawable.ic_allocation_coming,
            R.drawable.ic_feedback_color};
    private static int[] iconsAdmin = {R.drawable.ic_home, R.drawable.ic_allotment_color, R.drawable.ic_list_allotment_color,
            R.drawable.ic_feedback_color, R.drawable.ic_database_backup};
    private FragmentDrawerListener drawerListener;
    private String PACKAGE_NAME;
    private static String userType;
    private PrefManager prefManager;

    public FragmentDrawer() {
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            if (userType.equals("Maintainer")) {
                navItem.setIcon(iconsMaintainer[i]);
            } else if (userType.equals("Admin")) {
                navItem.setIcon(iconsAdmin[i]);
            }
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(getContext());
        userType = prefManager.getUserType();
        if (userType.equals("Maintainer")) {
            // drawer labels
            titles = getActivity().getResources().getStringArray(R.array.nav_drawer_activity_labels_maintainer);
        } else if (userType.equals("Admin")) {
            titles = getActivity().getResources().getStringArray(R.array.nav_drawer_activity_labels_admin);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        iv_editProfile_drawer = (ImageView) layout.findViewById(R.id.iv_editProfile_drawer);
        iv_drawer_pic = (SelectableRoundedImageView) layout.findViewById(R.id.iv_drawer_pic);
        tv_drawer_name = (TextView) layout.findViewById(R.id.tv_drawer_name);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        PACKAGE_NAME = getActivity().getPackageName();
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/" + prefManager.getProfileImageName());
        if (file.exists()) {
            //Do something
            SaveImage saveImage = new SaveImage(getContext());
            Bitmap profilePhoto = saveImage.getSavedImage(prefManager.getProfileImageName());
            //Bitmap roundedProfilePhoto = ImageShape.getRoundedCornerBitmap(profilePhoto, 50);
            if (profilePhoto != null) {
                iv_drawer_pic.setImageBitmap(profilePhoto);
            } else {
                iv_drawer_pic.setImageResource(R.drawable.ic_user);
            }
        }
        tv_drawer_name.setText(prefManager.getUserName());
        iv_editProfile_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        // Add ItemDecoration
        //recyclerView.addItemDecoration(new DividerItemDecoration(getResources()));

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setNestedScrollingEnabled(false);

        return layout;
    }

    // Change name and photo of drawer from anywhere
    public void picNameChange(Context context) {
        prefManager = new PrefManager(context);
        String ownerName = prefManager.getUserName();
        PACKAGE_NAME = context.getPackageName();
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/" +
                prefManager.getProfileImageName());
        if (file.exists()) {
            //Do something
            SaveImage saveImage = new SaveImage(context);
            Bitmap profilePhoto = saveImage.getSavedImage(prefManager.getProfileImageName());
            //Bitmap roundedProfilePhoto = ImageShape.getRoundedCornerBitmap(profilePhoto, 50);
            iv_drawer_pic.setImageBitmap(profilePhoto);
        }
        tv_drawer_name.setText(ownerName);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}
