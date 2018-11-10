package com.survlogic.surveyhelper.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.StaffCompanyFragment;
import com.survlogic.surveyhelper.activity.staffFeed.StaffFeedFragment;
import com.survlogic.surveyhelper.activity.staffProject.StaffProjectFragment;
import com.survlogic.surveyhelper.inter.NavigationHost;

public class BottomNavigationViewLoader {


    private static final String TAG = "BottomNavigationViewLoa";

    public static void setupBottomNavigationStaff (BottomNavigationViewEx view){
        view.enableAnimation(false);
    }

    public static void enableNavgiationStaff(final Context context, final BottomNavigationViewEx view){
        final Menu menu = view.getMenu();
        MenuItem menuItem;

        Activity mActivity = (Activity) context;

        final NavigationHost navigationHost = (NavigationHost) mActivity;

        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_item_1:
                        menuItem = menu.getItem(0);
                        menuItem.setChecked(true);
                        navigationHost.navigateTo(new StaffFeedFragment(),false);
                        break;

                    case R.id.navigation_item_2:
                        menuItem = menu.getItem(1);
                        menuItem.setChecked(true);
                        navigationHost.navigateTo(new StaffCompanyFragment(),false);
                        break;

                    case R.id.navigation_item_3:
                        menuItem = menu.getItem(2);
                        menuItem.setChecked(true);
                        navigationHost.navigateTo(new StaffProjectFragment(), false);
                        break;
                }

                return false;
            }
        });
    }



}
