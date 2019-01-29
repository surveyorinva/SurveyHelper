package com.survlogic.surveyhelper.activity.staffCompany.fragments.news.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.activity.CompanyWebActivity;

import java.util.ArrayList;

public class CompanyNewsStaggeredAdapter extends RecyclerView.Adapter<CompanyNewsStaggeredAdapter.ViewHolder> {

    private static final String TAG = "StaggeredRecyclerViewAd";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mWebUrls = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    public CompanyNewsStaggeredAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls, ArrayList<String> webUrls) {
        mNames = names;
        mImageUrls = imageUrls;
        mWebUrls = webUrls;
        mContext = context;
        mActivity = (Activity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_company_news_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Glide.with(mContext)
                .load(mImageUrls.get(position))
                .apply(requestOptions)
                .into(holder.image);

        holder.name.setText(mNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, CompanyWebActivity.class);
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_WEB_URL),mWebUrls.get(position));
                i.putExtra(mActivity.getResources().getString(R.string.KEY_EVENT_HEADER_TITLE),mActivity.getResources().getString(R.string.staff_feed_event_web_view_event_all_about_event));
                mActivity.startActivity(i);
                mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageview_widget);
            this.name = itemView.findViewById(R.id.name_widget);
        }
    }
}
