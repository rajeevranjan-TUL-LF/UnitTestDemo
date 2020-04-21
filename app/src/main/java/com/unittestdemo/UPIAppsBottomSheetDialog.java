package com.unittestdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unittestdemo.adapters.UserListAdapter;
import com.unittestdemo.model.UserModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class UPIAppsBottomSheetDialog extends BottomSheetDialogFragment {
   private static final String TAG = UPIAppsBottomSheetDialog.class.getSimpleName();
    TataCliqOnCompletedListener tataCliqOnCompletedListener;
    BottomSheetBehavior bottomSheetBehavior;
    String data;
    Context context;
    TextView tvTitle;
    Button btnPay;
    AppBarLayout appBarLayout;
    RecyclerView rvUpiApp;
    UserListAdapter userListAdapter;
    List<UserModel> userModelList;
    int size;

    public UPIAppsBottomSheetDialog(int size, List<UserModel> userModels, Context context, TataCliqOnCompletedListener tataCliqOnCompletedListener) {
        this.tataCliqOnCompletedListener = tataCliqOnCompletedListener;
        this.userModelList = userModels;
        this.context = context;
        this.size = size;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        //inflating layout
        View view = View.inflate(getContext(), R.layout.layout_upi_apps_bottom_sheet, null);

        init_views(view);
        //setting layout with bottom sheet
        bottomSheet.setContentView(view);

        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));


        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);


        //setting max height of bottom sheet
        //bi.extraSpace.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);

        bindRecyclerView();
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                Log.d("BOTTOM_STATE", "" + i);
                switch (i) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        showView(appBarLayout, getActionBarSize());
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        hideAppBar(appBarLayout);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;

                }
//                if (BottomSheetBehavior.STATE_EXPANDED == i) {
//                    showView(appBarLayout, getActionBarSize());
//                }
//                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
//                    hideAppBar(appBarLayout);
//                    //showView(bi.profileLayout, getActionBarSize());
//                }
//
//                if (BottomSheetBehavior.STATE_HIDDEN == i) {
//                    dismiss();
//                }
//                if (BottomSheetBehavior.STATE_DRAGGING == i) {
//
//                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        hideAppBar(appBarLayout);
        return bottomSheet;
    }

    private void init_views(View view) {
        appBarLayout = view.findViewById(R.id.appBarLayout);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnPay = view.findViewById(R.id.btnPay);
        rvUpiApp = view.findViewById(R.id.rvUpiApp);
        rvUpiApp.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        tataCliqOnCompletedListener.onComplete(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideAppBar(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);

    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) array.getDimension(0, 0);
        return size;
    }

    private void bindRecyclerView() {
        userListAdapter = new UserListAdapter(context, userModelList,size);
        rvUpiApp.setAdapter(userListAdapter);
    }
}