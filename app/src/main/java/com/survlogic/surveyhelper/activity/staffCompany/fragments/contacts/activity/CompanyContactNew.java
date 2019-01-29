package com.survlogic.surveyhelper.activity.staffCompany.fragments.contacts.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appSettings.EditProfileActivity;
import com.survlogic.surveyhelper.activity.staffCompany.model.Contact;
import com.survlogic.surveyhelper.database.Contacts.FirestoreDatabaseContacts;
import com.survlogic.surveyhelper.utils.BaseActivity;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.KeyboardUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class CompanyContactNew extends BaseActivity implements FirestoreDatabaseContacts.addContact{

    private static final String TAG = "CompanyContactNew";

    @Override
    public void addContactSuccess() {
//        DialogUtils.showToast(mContext,"Saved!");
        finish();
    }

    @Override
    public void addContactFail(String error) {
        //Todo
    }

    private Context mContext;

    private TextFieldBoxes tfFirstName, tfLastName, tfTitle, tfEmail, tfPhoneOffice, tfPhoneMobile, tfPhoneExtension, tfProfilePic, tfBirthday;
    private ExtendedEditText etFirstName, etLastName, etTitle, etEmail, etPhoneOffice, etPhoneMobile, etPhoneExtension, etProfilePic, etBirthday;

    private Button btSave, btCalendar;

    final Calendar myCalendar = Calendar.getInstance();
    private Date mDateBirthday = new Date();
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_company_content_contacts_new);

        initView();

    }

    private void initView(){
        tfFirstName = findViewById(R.id.contact_name_first);
        tfLastName = findViewById(R.id.contact_name_last);
        tfTitle = findViewById(R.id.contact_title);
        tfEmail = findViewById(R.id.contact_email);
        tfPhoneOffice = findViewById(R.id.contact_phone_office);
        tfPhoneMobile = findViewById(R.id.contact_phone_mobile);
        tfPhoneExtension = findViewById(R.id.contact_phone_extension);
        tfProfilePic = findViewById(R.id.contact_profile_pic);
        tfBirthday = findViewById(R.id.contact_birthday);

        etFirstName = findViewById(R.id.contact_name_first_value);
        etLastName = findViewById(R.id.contact_name_last_value);
        etTitle = findViewById(R.id.contact_title_value);
        etEmail = findViewById(R.id.contact_email_value);
        etPhoneOffice = findViewById(R.id.contact_phone_office_value);
        etPhoneMobile = findViewById(R.id.contact_phone_mobile_value);
        etPhoneExtension = findViewById(R.id.contact_phone_extension_value);
        etProfilePic = findViewById(R.id.contact_profile_pic_value);
        etBirthday = findViewById(R.id.contact_birthday_value);

        btCalendar = findViewById(R.id.btn_calendar_picker);
        btCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarEvent();
            }
        });

        btSave = findViewById(R.id.btn_contact_action_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                String dateShort =  month + "/" + day;

                etBirthday.setText(dateShort);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try{
                    mDateBirthday = dateFormat.parse(date);

                }catch (ParseException e){
                    e.printStackTrace();
                }

            }
        };

        setIMEEventListeners();

    }

    private void saveContact(){
        Log.d(TAG, "to_delete: saveContact: Started");
        Contact contact = new Contact();

        contact.setFirst_name(etFirstName.getText().toString());
        contact.setLast_name(etLastName.getText().toString());
        contact.setTitle(etTitle.getText().toString());
        contact.setEmail(etEmail.getText().toString());
        contact.setProfile_pic_url(etProfilePic.getText().toString());

        Log.d(TAG, "to_delete: saveContact: Required Fields Filled");

        Log.d(TAG, "to_delete: saveContact: Checking Phone");
        if(!TextUtils.isEmpty(etPhoneOffice.getText().toString())){
            Log.d(TAG, "to_delete: saveContact: Office found, saving as Long");
            String strPhoneOffice = etPhoneOffice.getText().toString();

            HashMap<String,Long> hmTelephone;

            if(contact.getTelephone() == null){
                hmTelephone = new HashMap<>();

            }else{
                hmTelephone = contact.getTelephone();
            }

            hmTelephone.put(getResources().getString(R.string.HASH_KEY_OFFICE),Long.parseLong(strPhoneOffice));

            contact.setTelephone(hmTelephone);
            Log.d(TAG, "to_delete: saveContact: Office Phone saved");
        }

        if(!TextUtils.isEmpty(etPhoneMobile.getText().toString())){
            String strPhoneMobile = etPhoneMobile.getText().toString();

            HashMap<String,Long> hmTelephone;
            if(contact.getTelephone() == null){
                hmTelephone = new HashMap<>();

            }else{
                hmTelephone = contact.getTelephone();
            }

            hmTelephone.put(getResources().getString(R.string.HASH_KEY_MOBILE),Long.parseLong(strPhoneMobile));

            contact.setTelephone(hmTelephone);
        }

        if(!TextUtils.isEmpty(etPhoneExtension.getText().toString())){
            String strPhoneExt = etPhoneExtension.getText().toString();

            HashMap<String,Long> hmTelephone;
            if(contact.getTelephone() == null){
                hmTelephone = new HashMap<>();

            }else{
                hmTelephone = contact.getTelephone();
            }

            hmTelephone.put(getResources().getString(R.string.HASH_KEY_EXT),Long.parseLong(strPhoneExt));

            contact.setTelephone(hmTelephone);
        }

        Log.d(TAG, "to_delete: saveContact: Checking Birthday");
        if(!TextUtils.isEmpty(etBirthday.getText().toString())){
            Log.d(TAG, "to_delete: saveContact: Birthday found, saving as Long");
            long timeInMilliseconds = mDateBirthday.getTime();

            contact.setBirthday(timeInMilliseconds);
            Log.d(TAG, "to_delete: saveContact: Birthday Saved");
        }

        contact.setUser_id(null);

        FirestoreDatabaseContacts db = new FirestoreDatabaseContacts(mContext);
        db.setInterfaceAddContact(this);

        db.addContactToServer(contact);

    }

    private void createCalendarEvent(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                CompanyContactNew.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE);
        dialog.show();
    }

    private void setIMEEventListeners(){
        etProfilePic.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveContact();

                    KeyboardUtils.hideKeyboard(etProfilePic);
                    handled = true;
                }
                return handled;
            }
        });

    }



}
