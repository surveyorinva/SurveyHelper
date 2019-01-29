package com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffCompany.model.Contact;
import com.survlogic.surveyhelper.utils.BaseActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyContactView  extends BaseActivity {

    private static final String TAG = "CompanyContactView";
    private Contact mContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_company_content_contacts_view);

        getIntentDelivery();

    }


    private void getIntentDelivery(){
        mContact = getIntent().getParcelableExtra(getString(R.string.KEY_COMPANY_CONTACT_PARCEL));

        buildContact();

    }

    private void buildContact(){
        ImageButton ivFinish = findViewById(R.id.appBar_top_action_nav_back);
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CircleImageView cvProfilePic = findViewById(R.id.contact_details_picture_url);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mContact.getProfile_pic_url(), cvProfilePic);

        TextView tvProfileName = findViewById(R.id.contact_details_name_full);
        String strContactNameFull = mContact.getFirst_name() + " " + mContact.getLast_name();
        tvProfileName.setText(strContactNameFull);

        TextView tvJobDescription = findViewById(R.id.contact_details_job_title);
        tvJobDescription.setText(mContact.getTitle());

        TextView tvEmail = findViewById(R.id.contact_details_email_address);
        String emailAddress = mContact.getEmail();
        SpannableString content = new SpannableString(emailAddress);
        content.setSpan(new UnderlineSpan(), 0, emailAddress.length(), 0);
        tvEmail.setText(content);


        if(mContact.getTelephone() !=null){
            HashMap<String,Long> hmTelephoneList = mContact.getTelephone();

            Long office = hmTelephoneList.get(getResources().getString(R.string.HASH_KEY_OFFICE));
            if(office !=null){
                TextView tvOfficeTelephone = findViewById(R.id.contact_details_phone_office);
                String formattedOfficeNumber = PhoneNumberUtils.formatNumber(String.valueOf(office), Locale.getDefault().getCountry());
                tvOfficeTelephone.setText(formattedOfficeNumber);
            }

            Long mobile = hmTelephoneList.get(getResources().getString(R.string.HASH_KEY_MOBILE));
            if(mobile !=null){
                TextView tvMobileTelephone = findViewById(R.id.contact_details_phone_mobile);
                String formattedMobileNumber = PhoneNumberUtils.formatNumber(String.valueOf(mobile), Locale.getDefault().getCountry());
                tvMobileTelephone.setText(formattedMobileNumber);
            }

        }

        TextView tvBirthday = findViewById(R.id.contact_details_birthday);
        long birthday_long = mContact.getBirthday();
        Date birthday = new Date(birthday_long);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        tvBirthday.setText(dateFormat.format(birthday));
    }
}
