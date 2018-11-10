package com.survlogic.surveyhelper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments= new ArrayList<>();
        private ArrayList<String> tabTitles = new ArrayList<>();

        public void addFragments(Fragment fragments, String title){
            this.fragments.add(fragments);
            this.tabTitles.add(title);

        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);

        }
    }


