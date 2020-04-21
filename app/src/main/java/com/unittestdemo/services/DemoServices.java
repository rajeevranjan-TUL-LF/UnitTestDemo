package com.unittestdemo.services;


import com.unittestdemo.model.UserModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface DemoServices {

    @GET("users")
    Observable<List<UserModel>> getUserList();
}
