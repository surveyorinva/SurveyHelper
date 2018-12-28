package com.survlogic.surveyhelper.activity.staffFeed.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.activity.staffFeed.controller.FeedBottomSheetRecycleController;
import com.survlogic.surveyhelper.activity.staffFeed.model.FeedActions;

import java.util.ArrayList;

public class FeedBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "FeedBottomSheet";

    public interface FeedItemBottomSheetListener {
        void isOpen(boolean isOpen);
        ArrayList<FeedActions> getFeedActions();
    }

    private Context mContext;
    private ArrayList<FeedActions> mFeedActions;
    private FeedItemBottomSheetListener mDialogListener;

    private FeedBottomSheetRecycleController mRecyclerController;

    private RecyclerView recyclerView;

    public static FeedBottomSheet newInstance(Context context, FeedItemBottomSheetListener listener){
        FeedBottomSheet dialog = new FeedBottomSheet();
        dialog.mContext = context;
        dialog.mDialogListener = listener;

        return dialog;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.staff_feed_bottom_sheet_add_new_item,null);
        dialog.setContentView(view);
        createView(view);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    String state = "";

                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING: {
                            state = "DRAGGING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_SETTLING: {
                            state = "SETTLING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            state = "EXPANDED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            state = "COLLAPSED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            state = "HIDDEN";
                            break;
                        }
                    }

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    private void createView(View view){
        Log.d(TAG, "to_delete: Started ");
        recyclerView = view.findViewById(R.id.dialog_recycler_view);
        mRecyclerController = new FeedBottomSheetRecycleController(mContext);

        ArrayList<FeedActions> list = mDialogListener.getFeedActions();
        if(list !=null){
            mRecyclerController.setFeedCompiledList(list);
            mRecyclerController.setRecyclerView(recyclerView);

            if(mRecyclerController.getFeedActionsCompiledList().size() > 0){
                mRecyclerController.updateRecycler();
            }
        }

    }
}
