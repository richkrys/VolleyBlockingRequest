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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VolleyBlockingRequestActivity extends AppCompatActivity {
    public static final String REQUEST_TAG = "VolleyBlockingRequestActivity";
    public static final int SUB_ACTIVITY_DB_REQUEST = 10;

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
                makeRequest();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUB_ACTIVITY_DB_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                try {
                    JSONObject json = new JSONObject(extras.getString("json_resp"));
                    Log.i("result: ", json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeRequest() {
        Log.i("status: ", "make reqeusts");
        Intent i = new Intent(this, SyncDbReqActivity.class);
        i.putExtra("queryStr", "http://api.openweathermap.org/data/2.5/weather?q=Detroit&APPID=dc732fc743603e28f0b4fba8ab5ed347");
        startActivityForResult(i, SUB_ACTIVITY_DB_REQUEST);
        i.putExtra("queryStr", "http://api.openweathermap.org/data/2.5/weather?q=London&APPID=dc732fc743603e28f0b4fba8ab5ed347");
        startActivityForResult(i, SUB_ACTIVITY_DB_REQUEST);
        i.putExtra("queryStr", "http://api.openweathermap.org/data/2.5/weather?q=Paris&APPID=dc732fc743603e28f0b4fba8ab5ed347");
        startActivityForResult(i, SUB_ACTIVITY_DB_REQUEST);
    }
}
