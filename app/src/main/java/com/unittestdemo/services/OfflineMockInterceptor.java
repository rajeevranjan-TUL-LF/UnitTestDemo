package com.unittestdemo.services;

import com.unittestdemo.model.UserModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OfflineMockInterceptor implements Interceptor {

    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");

    @Override
    public Response intercept(Chain chain) throws IOException {
        /** Will be called for every request you make.
         chain will include the request data, and you could
         just call chain.proceed() to continue with the HTTP request.

         We will return "hello", though. */
        String responseString = getStringResponse();
        return chain.proceed(chain.request())
                .newBuilder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .message(responseString)
                .body(ResponseBody.create(MediaType.parse("application/json"),
                        responseString))
                .addHeader("content-type", "application/json")
                .build();
    /*Response response = new Response.newBU()


            .newBuilder()
            .code(SUCCESS_CODE)
            .protocol(Protocol.HTTP_2)
            .message(responseString)
            .body(ResponseBody.create(MediaType.parse("application/json"),
                    responseString.toByteArray()))
            .addHeader("content-type", "application/json")

        *//* Return "hello" to the api call *//*
        .body(ResponseBody.create(MEDIA_JSON, "\"hello\""))
        *//* Additional methods to satisfy OkHttp *//*
        .request(chain.request())
        .protocol(Protocol.HTTP_2)
        .code(200)
        .build();

    return response;*/
    }

    public String getStringResponse() {
        List<UserModel> userModels = new ArrayList<>();
        userModels.add(new UserModel(1));
        //userModels.add(new UserModel(2));
        //userModels.add(new UserModel(3));
        return new Gson().toJson(userModels.get(0));
    }
}