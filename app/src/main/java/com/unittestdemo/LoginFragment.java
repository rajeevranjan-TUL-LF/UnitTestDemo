package com.unittestdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unittestdemo.adapters.UserListAdapter;
import com.unittestdemo.model.UserModel;
import com.unittestdemo.services.HttpService;
import com.unittestdemo.util.EmailValidator;
import com.unittestdemo.util.Utility;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends Fragment {

    UserListAdapter userListAdapter;
    List<UserModel> userModelList;
    RecyclerView rvUsers;
    EditText etEmail, etPassowrd;
    Button btnLogin, btnRetry;
    ProgressBar progressBar;
    CardView cvInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_views(view);
        //getUserList();
    }

    private void init_views(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        etEmail = view.findViewById(R.id.etEmail);
        etPassowrd = view.findViewById(R.id.etPassword);
        cvInput = view.findViewById(R.id.cvInput);
        btnLogin = view.findViewById(R.id.btnLogin);
        rvUsers = view.findViewById(R.id.rvUsers);
        btnRetry = view.findViewById(R.id.btnRetry);
        rvUsers.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    Utility.hideKeyboardFrom(LoginFragment.this.getActivity(), etEmail);
                    getUserList();
                }

            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserList();
            }
        });

    }

    public boolean validateInput() {
        if (!EmailValidator.isValidEmail(etEmail.getText().toString())) {
            etEmail.requestFocus();
            etEmail.setError("Enter valid email");
            return false;
        }
        if (etPassowrd.getText().toString().isEmpty()) {
            etPassowrd.requestFocus();
            etPassowrd.setError("Enter password");
            return false;
        }
        return true;
    }

    public void getUserList() {
        progressBar.setVisibility(View.VISIBLE);
        Observable<List<UserModel>> userModelObservable = HttpService.getInstance().getUserList();
        userModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UserModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<UserModel> userModels) {
                        progressBar.setVisibility(View.GONE);
                        hideShowView(false);
                        userModelList = userModels;
                        bindRecyclerView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        hideShowView(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void hideShowView(boolean isError) {
        if (isError) {
            cvInput.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
            rvUsers.setVisibility(View.GONE);
        } else {
            cvInput.setVisibility(View.GONE);
            rvUsers.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.GONE);
        }
    }

    public void openBottomSheet(List<UserModel> userModels) {

        UPIAppsBottomSheetDialog upiAppsBottomSheetDialog = new UPIAppsBottomSheetDialog(0, userModels, LoginFragment.this.getActivity(), result -> {
            if (result == 1) { //success
                Toast.makeText(LoginFragment.this.getActivity(), "Task Completed Success", Toast.LENGTH_SHORT).show();
            } else { // Failure
                Toast.makeText(LoginFragment.this.getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        upiAppsBottomSheetDialog.show(getFragmentManager(), "Opening UPI bottom sheet");
    }

    public void navigateToFragment() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    private void bindRecyclerView() {
        userListAdapter = new UserListAdapter(this.getActivity(), userModelList, 0);
        rvUsers.setAdapter(userListAdapter);
    }
}
