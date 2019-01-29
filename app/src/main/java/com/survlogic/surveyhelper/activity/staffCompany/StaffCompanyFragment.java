package com.survlogic.surveyhelper.activity.staffCompany;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.controller.StaffCompanyController;
import com.survlogic.surveyhelper.activity.staff.inter.StaffActivityListener;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.CompanyContactsFragment;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.news.CompanyNewsFragment;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.services.CompanyServicesFragment;
import com.survlogic.surveyhelper.adapters.ViewPagerAdapter;
import com.survlogic.surveyhelper.model.ThemeSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

import java.util.Calendar;

public class StaffCompanyFragment extends Fragment implements StaffCompanyController.StaffCompanyControllerListener {
    private static final String TAG = "StaffFeedFragment";

    /**
     * StaffCompanyControllerListener
     */

    @Override
    public void refreshCompanyFragmentUI() {

    }

    private Context mContext;

    private View v;

    private ImageLoader mImageLoader;

    private ImageButton ibBackdrop, ibNavigator;
    private TextView tvFragmentName;

    private Toolbar toolbar;
    private ImageView imsHeaderView, imsTabView;
    private AppBarLayout appBar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Uri mImagePage0TopUri;
    private Drawable mImagePage0Top, mImagePage0Bottom;
    private Drawable mImagePage1Top, mImagePage1Bottom;
    private Drawable mImagePage2Top, mImagePage2Bottom;

    private StaffActivityListener mActivityListener;
    private StaffCompanyController mCompanyController;

    private boolean isBitmapsLoadedFromNetwork = false;
    private int mPageToLoad = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.staff_company_fragment, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageLoader = ImageLoader.getInstance();
        initImagePages();

        initView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();

        mActivityListener = (StaffActivityListener) getActivity();
        mCompanyController = new StaffCompanyController(mContext,this);

    }

    private void initView(){
        appBar = v.findViewById(R.id.appbar);

        collapsingToolbar= v.findViewById(R.id.collapsing_toolbar);

        tabLayout = v.findViewById(R.id.detail_tabs);
        viewPager = v.findViewById(R.id.viewpager);

        ibNavigator = v.findViewById(R.id.appBar_top_action_nav_menu);
        ibNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.callNavigationDrawer();
            }
        });


        setToolbar();
        initViewPager();



    }


    private void initViewPager(){
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        CompanyNewsFragment newsFragment = new CompanyNewsFragment();
        viewPagerAdapter.addFragments(newsFragment,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_1));

        CompanyContactsFragment contactsFragment = new CompanyContactsFragment();
        viewPagerAdapter.addFragments(contactsFragment,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_2));

        CompanyServicesFragment policyFragment = new CompanyServicesFragment();
        viewPagerAdapter.addFragments(policyFragment,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_3));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=0){
                    appBar.setExpanded(false);
                }else{
                    appBar.setExpanded(true);
                }
                initImagesPages(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initImagesPages(0);


    }

    private void setToolbar() {
        toolbar= v.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initImagePages(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);

        final ThemeSettings themeSettings = new ThemeSettings(preferenceLoader.getThemeSettings());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    mImagePage0Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage0Top()));
                    mImagePage0Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage0Bottom()));

                    mImagePage1Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage1Top()));
                    mImagePage1Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage1Bottom()));

                    mImagePage2Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage2Top()));
                    mImagePage2Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage2Bottom()));

                    mImagePage0TopUri = Uri.parse(themeSettings.getStaffCompanyPage0Top());

                    isBitmapsLoadedFromNetwork = true;

                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        });
        thread.start();
    }


    private void initImagesPages(int position) {
        imsHeaderView = v.findViewById(R.id.switcherTop);
        imsTabView = v.findViewById(R.id.switcherBottom);

        mPageToLoad = position;

        LoadBackgroundImagesAsyncTask task = new LoadBackgroundImagesAsyncTask();
        task.execute();

    }

    private void setImagePages(int position){

        switch (position){
            case 0:
                imsHeaderView.setImageDrawable(mImagePage0Top);
                imsTabView.setImageDrawable(mImagePage0Bottom);

                break;

            case 1:
                imsHeaderView.setImageDrawable(mImagePage1Top);
                imsTabView.setImageDrawable(mImagePage1Bottom);
                break;

            case 2:
                imsHeaderView.setImageDrawable(mImagePage2Top);
                imsTabView.setImageDrawable(mImagePage2Bottom);
                break;

            default:

        }

    }

    private class LoadBackgroundImagesAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... params) {
            Long t = Calendar.getInstance().getTimeInMillis();
            while(!isBitmapsLoadedFromNetwork && Calendar.getInstance().getTimeInMillis()-t < 10000){
                try {
                    Thread.sleep(1000);

                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }


        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "to_delete: Starting Post Execute ");
            if(isBitmapsLoadedFromNetwork){
                setImagePages(mPageToLoad);

            }else{
                //Todo Show Error Message Here
                Log.d(TAG, "to_delete: Timed Out! ");

            }

        }

    }




}
