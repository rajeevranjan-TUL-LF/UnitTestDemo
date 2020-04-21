package com.unittestdemo.services;

import com.unittestdemo.model.UserModel;
import com.unittestdemo.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpService {
    private static HttpService instance = new HttpService();
    public Retrofit retrofit;
    private DemoServices demoServices;

    private HttpService() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        long TIMEOUT = 60L;
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.addHeader("appplatform", "android");
            requestBuilder.method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);// todo : change it to none

        OfflineMockInterceptor offlineMockInterceptor = new OfflineMockInterceptor();
        OkHttpClient client = httpClientBuilder.addInterceptor(interceptor).build();
        //.addInterceptor(offlineMockInterceptor).build();

        retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        demoServices = retrofit.create(DemoServices.class);

    }

    public static HttpService getInstance() {
        if (instance == null) {
            instance = getSyncedInstance();
        }
        return instance;
    }

    private static synchronized HttpService getSyncedInstance() {
        if (instance == null) {
            instance = new HttpService();
        }
        return instance;
    }

    private String getTagName() {
        return HttpService.class.getSimpleName();
    }


    public Observable<UserModel> getUserListOneByOne() {
        Observable<List<UserModel>> userModelObservable;
        userModelObservable = demoServices.getUserList()
                .flatMap((Function<List<UserModel>, ObservableSource<List<UserModel>>>) users -> Observable.just(users));
        return userModelObservable.flatMapIterable(item -> item)
                .map(userModel -> {
                    userModel.setName("My Name is : " + userModel.getName() + " and my phone No  is  :" + userModel.getPhone());
                    return userModel;
                });
    }

    public Observable<List<UserModel>> getUserList() {
        Observable<List<UserModel>> userModelObservable;
        return userModelObservable = demoServices.getUserList()
                .flatMap((Function<List<UserModel>, ObservableSource<List<UserModel>>>) users -> Observable.just(users));

    }

}
