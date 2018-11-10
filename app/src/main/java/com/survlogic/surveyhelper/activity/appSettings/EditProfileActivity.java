package com.survlogic.surveyhelper.activity.appSettings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.controller.EditProfileController;
import com.survlogic.surveyhelper.activity.appSettings.inter.EditProfileListener;
import com.survlogic.surveyhelper.model.FirestoreUser;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class EditProfileActivity extends AppCompatActivity implements EditProfileListener, TextView.OnEditorActionListener {

    private static final String TAG = "EditProfileActivity";


    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            etUserNameFirst.requestFocus();
            createCalendarEvent();
            return true;
        }
        return false;
    }

    private Context mContext;

    private EditProfileController mEditProfileController;

    private FirestoreUser mFirestoreUser;

    private ImageButton ibAppBarBack;
    private Button btAppBarSave, btCalendarPicker;
    private RelativeLayout rlCalendarPicker;

    private TextFieldBoxes tfUserNameFirst, tfUserNameLast, tfUserContactPhoneOffice, tfUserContactPhoneMobile, tfUserBirthday;
    private ExtendedEditText etUserNameFirst, etUserNameLast, etUserContactPhoneOffice, etUserContactPhoneMobile, etUserBirthday;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Date mDateBirthday = new Date();
    private long mContactPhoneOffice, mContactPhoneMobile;

    private boolean mIsStartLocked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_edit_profile);

        mContext = EditProfileActivity.this;
        mEditProfileController = new EditProfileController(mContext, this);


        initView();
        setOnClickListeners();
        setOnFocusChangeWatchers();

        getIntentDelivery();

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!mIsStartLocked){
            mEditProfileController.onStart(mFirestoreUser);
            mIsStartLocked = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

    }


    private void initView(){
        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(getResources().getString(R.string.general_menu_settings));
        tvAppBarTitle.setVisibility(View.VISIBLE);

        ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setVisibility(View.VISIBLE);

        btAppBarSave = findViewById(R.id.appBar_top_action_finish);
        btAppBarSave.setText(getResources().getString(R.string.general_save));
        btAppBarSave.setVisibility(View.VISIBLE);

        btCalendarPicker = findViewById(R.id.btn_calendar_picker);
        rlCalendarPicker = findViewById(R.id.rl_calendar_picker);

        tfUserNameFirst = findViewById(R.id.editProfile_user_name_first);
        tfUserNameLast = findViewById(R.id.editProfile_user_name_last);
        tfUserContactPhoneOffice = findViewById(R.id.editProfile_phone_office);

        tfUserContactPhoneMobile = findViewById(R.id.editProfile_phone_mobile);
        tfUserBirthday = findViewById(R.id.editProfile_user_birthday);

        etUserNameFirst = findViewById(R.id.editProfile_user_name_first_value);
        etUserNameLast = findViewById(R.id.editProfile_user_name_last_value);
        etUserContactPhoneOffice = findViewById(R.id.editProfile_phone_office_value);

        etUserContactPhoneMobile = findViewById(R.id.editProfile_phone_mobile_value);
        etUserContactPhoneMobile.setOnEditorActionListener(this);


        etUserBirthday = findViewById(R.id.editProfile_user_birthday_value);
        etUserBirthday.setShowSoftInputOnFocus(false);
        etUserBirthday.setInputType(InputType.TYPE_NULL);
        etUserBirthday.setCursorVisible(false);
        etUserBirthday.setFocusableInTouchMode(false);



        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                etUserBirthday.setText(date);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try{
                    mDateBirthday = dateFormat.parse(date);
                    mEditProfileController.setUserBirthday(mDateBirthday);
                }catch (ParseException e){
                    e.printStackTrace();
                }

            }
        };


    }

    private void setOnClickListeners(){
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btCalendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarEvent();
            }
        });

        btAppBarSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditProfileController.onSaveFirestoreUser();
            }
        });


    }

    private void setOnFocusChangeWatchers(){

        etUserNameFirst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    //todo cannot be null

                    mEditProfileController.setUserDisplayName(etUserNameFirst.getText().toString() + " " + etUserNameLast.getText().toString());
                }
            }
        });

        etUserNameLast.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    mEditProfileController.setUserDisplayName(etUserNameFirst.getText().toString() + " " + etUserNameLast.getText().toString());
                }
            }
        });

        etUserContactPhoneOffice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    String officeNumberString = etUserContactPhoneOffice.getText().toString();

                    if(!TextUtils.isEmpty(officeNumberString)){
                        officeNumberString = officeNumberString.replaceAll("\\D+","");

                        mContactPhoneOffice = Long.valueOf(officeNumberString);

                        String formattedOfficeNumber = PhoneNumberUtils.formatNumber(String.valueOf(etUserContactPhoneOffice.getText()),Locale.getDefault().getCountry());
                        etUserContactPhoneOffice.setText(formattedOfficeNumber);

                        mEditProfileController.setUserContactPhoneOffice(mContactPhoneOffice);
                    }else{
                        mContactPhoneOffice = 0;
                        mEditProfileController.setUserContactPhoneOffice(0);
                    }

                }

            }
        });

        etUserContactPhoneMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String mobileNumberString = etUserContactPhoneMobile.getText().toString();

                    if(!TextUtils.isEmpty(mobileNumberString)){
                        mobileNumberString = mobileNumberString.replaceAll("\\D+","");

                        mContactPhoneMobile = Long.valueOf(mobileNumberString);

                        String formattedMobileNumber = PhoneNumberUtils.formatNumber(String.valueOf(etUserContactPhoneMobile.getText()),Locale.getDefault().getCountry());
                        etUserContactPhoneMobile.setText(formattedMobileNumber);

                        mEditProfileController.setUserContactPhoneMobile(mContactPhoneMobile);
                    }else{
                        mContactPhoneMobile = 0;
                        mEditProfileController.setUserContactPhoneMobile(0);
                    }

                }
            }
        });

    }

    private void createCalendarEvent(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                EditProfileActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getIntentDelivery(){
        mFirestoreUser = getIntent().getParcelableExtra(getResources().getString(R.string.KEY_FIRESTORE_USER));

        populateView();

    }

    private void populateView(){

        String[] displayNameParsed = mFirestoreUser.getDisplay_name().trim().split("\\s+");
        etUserNameFirst.setText(displayNameParsed[0]);
        etUserNameLast.setText(displayNameParsed[1]);

        mContactPhoneOffice = mFirestoreUser.getTelephone_office();
        String formattedOfficeNumber = PhoneNumberUtils.formatNumber(String.valueOf(mContactPhoneOffice),Locale.getDefault().getCountry());
        etUserContactPhoneOffice.setText(formattedOfficeNumber);

        mContactPhoneMobile = mFirestoreUser.getTelephone_mobile();
        String formattedMobileNumber = PhoneNumberUtils.formatNumber(String.valueOf(mContactPhoneMobile),Locale.getDefault().getCountry());
        etUserContactPhoneMobile.setText(formattedMobileNumber);

        if(mFirestoreUser.getProfile_birthday() != null){
            Date birthday = mFirestoreUser.getProfile_birthday();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);

            etUserBirthday.setText(dateFormat.format(birthday));
        }

    }


}
