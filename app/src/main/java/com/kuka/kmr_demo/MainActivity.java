package com.kuka.kmr_demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private RequestQueue queue;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* *** Getting Json *** */
        textView = findViewById(R.id.text_battery);
        Button buttonParse = findViewById(R.id.button_parse);

        queue = Volley.newRequestQueue(this);

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });

        /* *** Sending Json *** */


        Button submitButton = (Button) findViewById(R.id.button_parse2);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String data = "[{}]";
                Submit(data);

            }
        });

    }


    /* Class to request Json */

    private void jsonParse() {

        String url = "http://10.216.70.19:8080/restServices/webapi/services/getAGVStatusList";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //weatherData.setText("Response is :- ");
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("Data Not Received");

            }
        });

        queue.add(request);
        super.onStart();
    }

    /* Class to print on app screen the Json requested beforehand */

    private void parseData(String response) {
        try {
            // Create JSOn Object
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                textView.setText(jsonObject.getString("batteryLevel"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/* Post Json */
private void Submit(String data)
{
    final String savedata= data;
    String URL="http://10.216.70.19:8080/restServices/webapi/services/createNewJob";

    requestQueue = Volley.newRequestQueue(getApplicationContext());
    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject objres=new JSONObject(response);
                Toast.makeText(getApplicationContext(),objres.toString(),Toast.LENGTH_LONG).show();


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

            }
            //Log.i("VOLLEY", response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            //Log.v("VOLLEY", error.toString());
        }
    }) {
        @Override
        public String getBodyContentType() {
            return "application/json; charset=utf-8";
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            try {
                return savedata == null ? null : savedata.getBytes("utf-8");
            } catch (UnsupportedEncodingException uee) {
                //Log.v("Unsupported Encoding while trying to get the bytes", data);
                return null;
            }
        }

    };
    requestQueue.add(stringRequest);
}
}
