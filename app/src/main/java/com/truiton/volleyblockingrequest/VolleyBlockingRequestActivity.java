/*
 * Copyright (c) 2016. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.truiton.volleyblockingrequest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class VolleyBlockingRequestActivity extends AppCompatActivity {
    public static final String REQUEST_TAG = "VolleyBlockingRequestActivity";
    private TextView textView;
    private Button button;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_blocking_request);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);

        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParsingTask();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }

    public void startParsingTask() {
        Log.i("new thread: ", "NEW");
        Thread threadA = new Thread() {
            public void run() {
                ThreadB threadB = new ThreadB(getApplicationContext(), "http://api.openweathermap.org/data/2.5/weather?q=Detroit&APPID=dc732fc743603e28f0b4fba8ab5ed347");
                JSONObject jsonObject = null;
                try {
                    jsonObject = threadB.execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                final JSONObject receivedJSONObject = jsonObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Response is: " + receivedJSONObject);
                        if (receivedJSONObject != null) {
                            try {
                                textView.setText(textView.getText() + "\n\n" +
                                        receivedJSONObject.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        threadA.start();
    }

    private class ThreadB extends AsyncTask<Void, Void, JSONObject> {
        private Context mContext;
        private String url;

        public ThreadB(Context ctx, String url) {
            mContext = ctx;
            this.url = url;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method
                    .GET, url,
                    new JSONObject(), futureRequest, futureRequest);
            jsonRequest.setTag(REQUEST_TAG);
            requestQueue.add(jsonRequest);
            try {
                return futureRequest.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
