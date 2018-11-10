package com.survlogic.surveyhelper.activity.appSettings.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.appEntry.EntryActivity;
import com.survlogic.surveyhelper.activity.appSettings.EditProfileActivity;
import com.survlogic.surveyhelper.activity.appSettings.controller.ProfileSettingsController;
import com.survlogic.surveyhelper.activity.appSettings.inter.ProfileControllerListener;
import com.survlogic.surveyhelper.activity.appSettings.inter.SettingsListener;
import com.survlogic.surveyhelper.dialog.SelectPhotoDialog;
import com.survlogic.surveyhelper.model.FirestoreAppAccessKeys;
import com.survlogic.surveyhelper.model.FirestoreUser;
import com.survlogic.surveyhelper.utils.DialogUtils;
import com.survlogic.surveyhelper.utils.GraphicUtils;
import com.survlogic.surveyhelper.utils.HapticFeedbackUtils;
import com.survlogic.surveyhelper.utils.UniversalImageLoader;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsFragment extends Fragment implements ProfileControllerListener, SelectPhotoDialog.OnPhotoSelectedListener {

    private static final String TAG = "ProfileSettingsFragment";
    private View v;

    private String parentClass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mCurrentUser;
    private FirestoreUser mFirestoreUser;

    private SettingsListener mSettingsListener;
    private ProfileSettingsController mControllerListener;

    private CircleImageView ivUserProfilePicture;
    private ImageButton ibUserProfilePicture;
    private Button btUserEdit, btUserLogOut;
    private TextView tvUserDisplayName, tvUserEmail, tvUserPhoneOffice, tvUserPhoneMobile, tvUserAccessLevel,
                        tvUserBirthday, tvUserFeedPosts, tvUserRewardCurrent, tvUserRewardTotal;

    private ProgressBar pbUserProfilePictureProgress;

    private boolean isLocked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.settings_fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mSettingsListener = (SettingsListener) getActivity();
        mControllerListener = new ProfileSettingsController(getActivity(),mAuth,this);

        initView();
        setOnClickListeners();

        getIntentDelivery();
        setupFirebaseListener();

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        if(!isLocked){
            mCurrentUser = mAuth.getCurrentUser();
            mControllerListener.onStart(mCurrentUser);

            FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mAuthStateListener !=null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }

    }

    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    DialogUtils.showToast(getActivity(),getResources().getString(R.string.general_signedOut));

                    //Todo remove username/password for preferenceloader

                    Intent i = new Intent(getActivity(),EntryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            }
        };
    }

    private void getIntentDelivery(){
        parentClass = getActivity().getIntent().getStringExtra(getString(R.string.KEY_PARENT_ACTIVITY));

    }

    private void initView(){
        ivUserProfilePicture = v.findViewById(R.id.profile_current_user_image);
        ibUserProfilePicture = v.findViewById(R.id.profile_user_image);
        pbUserProfilePictureProgress = v.findViewById(R.id.profile_current_user_image_progress);

        btUserEdit = v.findViewById(R.id.action_profile_current_user_edit_user);
        btUserLogOut = v.findViewById(R.id.btn_action_log_out);

        tvUserDisplayName = v.findViewById(R.id.profile_current_user_display_name);
        tvUserEmail = v.findViewById(R.id.profile_contact_email_address);
        tvUserPhoneOffice = v.findViewById(R.id.profile_contact_phone_office);
        tvUserPhoneMobile = v.findViewById(R.id.profile_contact_phone_mobile);
        tvUserAccessLevel = v.findViewById(R.id.profile_contact_access_level);
        tvUserBirthday = v.findViewById(R.id.profile_contact_birthday);

        tvUserFeedPosts = v.findViewById(R.id.profile_feed_posts_value);
        tvUserRewardCurrent = v.findViewById(R.id.profile_rewards_current_value);
        tvUserRewardTotal = v.findViewById(R.id.profile_rewards_total_value);

    }

    private void setOnClickListeners(){
        ibUserProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocked = true;
                HapticFeedbackUtils.init(getActivity());
                HapticFeedbackUtils.once(50);

                SelectPhotoDialog selectPhotoDialog = new SelectPhotoDialog();
                selectPhotoDialog.show(getFragmentManager(),getString(R.string.app_dialog_name_select_photo));
                selectPhotoDialog.setTargetFragment(ProfileSettingsFragment.this,1);

            }
        });

        btUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocked = false;
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                i.putExtra(getResources().getString(R.string.KEY_FIRESTORE_USER),mFirestoreUser);
                i.putExtra(getResources().getString(R.string.KEY_PARENT_ACTIVITY),R.string.CLASS_FRAGMENT_PROFILE);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        btUserLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classSetup = getActivity().getResources().getString(R.string.CLASS_SETUP);

                if(parentClass.equals(classSetup)){
                    DialogUtils.showAlertDialog(getActivity(),getActivity().getResources().getString(R.string.appSettings_profile_logout_error_due_to_being_in_setup_title), getActivity().getResources().getString(R.string.appSettings_profile_logout_error_due_to_being_in_setup_summary));
                }else{
                    mAuth.signOut();

                }
            }
        });


    }

    @Override
    public void returnCurrentUser(FirestoreUser firestoreUser) {
        mFirestoreUser = firestoreUser;

        tvUserDisplayName.setText(firestoreUser.getDisplay_name());

        String emailAddress = firestoreUser.getEmail();
        SpannableString content = new SpannableString(emailAddress);
        content.setSpan(new UnderlineSpan(), 0, emailAddress.length(), 0);
        tvUserEmail.setText(content);

        long officeNumber = firestoreUser.getTelephone_office();
        String formattedOfficeNumber = PhoneNumberUtils.formatNumber(String.valueOf(officeNumber),Locale.getDefault().getCountry());
        tvUserPhoneOffice.setText(formattedOfficeNumber);

        long mobileNumber = firestoreUser.getTelephone_mobile();
        String formattedMobileNumber = PhoneNumberUtils.formatNumber(String.valueOf(mobileNumber),Locale.getDefault().getCountry());
        tvUserPhoneMobile.setText(formattedMobileNumber);

        if(firestoreUser.getProfile_birthday() != null){
            Date birthday = firestoreUser.getProfile_birthday();
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
            tvUserBirthday.setText(dateFormat.format(birthday));
        }

        tvUserFeedPosts.setText(String.valueOf(firestoreUser.getFeed_posts()));
        tvUserRewardCurrent.setText(String.valueOf(firestoreUser.getRewards_current()));
        tvUserRewardTotal.setText(String.valueOf(firestoreUser.getRewards_total()));


        String profilePicURL = firestoreUser.getProfile_pic_url();

        if(profilePicURL == null){
            Bitmap bitmap = GraphicUtils.drawableToBitmap(getResources().getDrawable(R.drawable.ic_person_outline_dark_24dp));
            ivUserProfilePicture.setImageBitmap(bitmap);
            pbUserProfilePictureProgress.setVisibility(View.INVISIBLE);

        }else{
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(profilePicURL, ivUserProfilePicture, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if(pbUserProfilePictureProgress !=null){
                        pbUserProfilePictureProgress.setVisibility(View.GONE);
                    }
                }
            });
        }


    }

    @Override
    public void returnErrorNoUserLoggedIn() {
        //todo Create Error View
        DialogUtils.showToast(getActivity(),"Error, User Not logged In");

    }

    @Override
    public void returnCurrentUserAccess(FirestoreAppAccessKeys currentUserAccess) {
        tvUserAccessLevel.setText(currentUserAccess.getAccess_level());

    }

    @Override
    public void returnErrorGettingAccessKeys() {
        //todo Create Error View
        DialogUtils.showToast(getActivity(),"Error, User Access Key Not Found");
    }

    @Override
    public void returnImagePath(Uri imagePath) {
        pbUserProfilePictureProgress.setVisibility(View.VISIBLE);

        UniversalImageLoader.setImage(imagePath.toString(),ivUserProfilePicture);
        Log.d(TAG, "returnImagePath: Should be loaded: " + imagePath.toString());

        mControllerListener.setUserProfilePictureUri(imagePath);
        mControllerListener.setUserProfilePictureBitmap(null);

        mControllerListener.startProfilePictureUploadToCloud();
    }

    @Override
    public void returnImageBitmap(Bitmap bitmap) {
        pbUserProfilePictureProgress.setVisibility(View.VISIBLE);

        ivUserProfilePicture.setImageBitmap(bitmap);

        mControllerListener.setUserProfilePictureUri(null);
        mControllerListener.setUserProfilePictureBitmap(bitmap);

        mControllerListener.startProfilePictureUploadToCloud();

    }
}
