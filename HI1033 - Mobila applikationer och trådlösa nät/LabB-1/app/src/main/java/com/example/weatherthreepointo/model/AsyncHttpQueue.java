package com.example.weatherthreepointo.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AsyncHttpQueue {
    private static AsyncHttpQueue instance;
    private RequestQueue requestQueue;

    public static void initialize(Context context) {
        instance = new AsyncHttpQueue();
        instance.requestQueue = Volley.newRequestQueue(context);
    }

    public static AsyncHttpQueue getInstance() {
        return instance;
    }

    public <T> void addRequest(Request<T> request) {
        requestQueue.add(request);
    }
}
