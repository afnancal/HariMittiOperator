package com.globopex.harimittioperator.navigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.helper.PrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afnan on 03-May-17.
 */

public class FragmentDrawerActivity extends Fragment {

    private RecyclerView recyclerView_activity;

    private static String[] titles = null;
    private static int[] iconsMaintainer = {R.drawable.ic_home, R.drawable.ic_allocation_previous, R.drawable.ic_allocation_coming,
            R.drawable.ic_feedback_color};
    private static int[] iconsAdmin = {R.drawable.ic_home, R.drawable.ic_allotment_color, R.drawable.ic_list_allotment_color,
            R.drawable.ic_feedback_color, R.drawable.ic_database_backup};

    private FragmentDrawerActivityListener drawerListener;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private DrawerLayout mDrawerLayout;
    //private ActionBarDrawerToggle mDrawerToggle;
    private static String userType;
    private PrefManager prefManager;

    public FragmentDrawerActivity() {
    }

    public void setActivityDrawerListener(FragmentDrawerActivityListener listener) {
        this.drawerListener = listener;
    }

    public interface FragmentDrawerActivityListener {
        public void onDrawerItemSelected(View view, int position);
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
        View layout = inflater.inflate(R.layout.fragment_nav_drawer_activity, container, false);
        recyclerView_activity = (RecyclerView) layout.findViewById(R.id.drawerList_activity);

        // Add ItemDecoration
        //recyclerView.addItemDecoration(new DividerItemDecoration(getResources()));

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView_activity.setAdapter(adapter);
        recyclerView_activity.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_activity.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView_activity, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView_activity.setNestedScrollingEnabled(false);

        return layout;
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public void setUpActivity(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        /*mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
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
        });*/

    }

}
