package com.survlogic.surveyhelper.activity.staffCompany;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.survlogic.surveyhelper.activity.template.SampleFragment;
import com.survlogic.surveyhelper.adapters.ViewPagerAdapter;
import com.survlogic.surveyhelper.model.ThemeSettings;
import com.survlogic.surveyhelper.utils.PreferenceLoader;

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

        SampleFragment sampleFragment = new SampleFragment();
        viewPagerAdapter.addFragments(sampleFragment,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_1));

        SampleFragment sampleFragment2 = new SampleFragment();
        viewPagerAdapter.addFragments(sampleFragment2,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_2));

        SampleFragment sampleFragment3 = new SampleFragment();
        viewPagerAdapter.addFragments(sampleFragment3,getActivity().getResources().getString(R.string.staff_company_fragment_tab_item_3));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setImagesPages(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setImagesPages(0);


    }

    private void setToolbar() {
        toolbar= v.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initImagePages(){
        PreferenceLoader preferenceLoader = new PreferenceLoader(mContext);

        ThemeSettings themeSettings = new ThemeSettings(preferenceLoader.getThemeSettings());

        mImagePage0Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage0Top()));
        mImagePage0Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage0Bottom()));

        mImagePage1Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage1Top()));
        mImagePage1Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage1Bottom()));

        mImagePage2Top = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage2Top()));
        mImagePage2Bottom = new BitmapDrawable(getResources(),mImageLoader.loadImageSync(themeSettings.getStaffCompanyPage2Bottom()));


        mImagePage0TopUri = Uri.parse(themeSettings.getStaffCompanyPage0Top());
    }


    private void setImagesPages(int position){
        imsHeaderView = v.findViewById(R.id.switcherTop);
        imsTabView = v.findViewById(R.id.switcherBottom);

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



}
