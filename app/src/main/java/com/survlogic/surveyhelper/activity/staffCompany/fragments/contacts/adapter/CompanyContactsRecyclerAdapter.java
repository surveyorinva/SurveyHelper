package com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.activity.CompanyWebActivity;
import com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.activity.CompanyContactView;
import com.survlogic.surveyhelper.activity.staffCompany.model.Contact;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CompanyContactsRecyclerAdapter extends RecyclerView.Adapter<CompanyContactsRecyclerAdapter.ViewHolder> implements SectionIndexer {

    private static final String TAG = "StaggeredRecyclerViewAd";

    private ArrayList<Contact> mContacts = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    private List<String> mDataArray = new ArrayList<>();
    private ArrayList<Integer> mSectionPositions;


        public CompanyContactsRecyclerAdapter(Context context, ArrayList<Contact> contacts) {
            Log.d(TAG, "to_delete: CompanyContactsRecyclerAdapter: contacts Size: " + contacts.size());
            mContacts = contacts;

            mContext = context;
            mActivity = (Activity) context;

            for(int i=0; i<contacts.size(); i++) {
                Contact contact = contacts.get(i);


                Log.d(TAG, "to_delete:CompanyContactsRecyclerAdapter: Contact " + i + " First Name: " + contact.getFirst_name());
                mDataArray.add(contact.getFirst_name());


            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_company_contact_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: called.");
            final Contact contact = mContacts.get(position);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(mContext)
                    .load(contact.getProfile_pic_url())
                    .apply(requestOptions)
                    .into(holder.image);

            String contactName = contact.getFirst_name() + " " + contact.getLast_name();
            holder.name.setText(contactName);

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, CompanyContactView.class);
                    i.putExtra(mActivity.getResources().getString(R.string.KEY_COMPANY_CONTACT_PARCEL),contact);

                    mActivity.startActivity(i);
                    mActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            CardView card;
            CircleImageView image;
            TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                this.card = itemView.findViewById(R.id.cardView_widget);
                this.image = itemView.findViewById(R.id.imageview_widget);
                this.name = itemView.findViewById(R.id.name_widget);
            }
        }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = mDataArray.size(); i < size; i++) {
            String section = String.valueOf(mDataArray.get(i).charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }
}
