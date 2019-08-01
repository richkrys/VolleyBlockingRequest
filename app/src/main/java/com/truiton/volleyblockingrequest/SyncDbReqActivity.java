package com.truiton.volleyblockingrequest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class SyncDbReqActivity extends AppCompatActivity {
    public static final String REQUEST_TAG = "SyncDbReqActivity";
    private Context syncContext;
    private RequestQueue requestQueue;
    private JSONObject receivedJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncContext = this;
        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        makeDbRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("json_resp", receivedJSONObject.toString());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void makeDbRequest() {
        Thread threadA = new Thread() {
            public void run() {
                SyncDbReqActivity.ThreadB threadB = new SyncDbReqActivity.ThreadB(getIntent().getExtras().getString("queryStr"));
                try {
                    receivedJSONObject = threadB.execute().get(10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        };
        threadA.start();
    }

    private class ThreadB extends AsyncTask<Void, Void, JSONObject> {
        private String urlStr;

        public ThreadB(String urlStr) {
            this.urlStr = urlStr;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method
                    .GET, urlStr, new JSONObject(), futureRequest, futureRequest);
            jsonRequest.setTag(REQUEST_TAG);
            requestQueue.add(jsonRequest);
            try {
                return futureRequest.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
