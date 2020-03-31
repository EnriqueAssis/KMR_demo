package com.kuka.kmr_demo;

/* Developed by Enrique Assis
    2020-02-28
 */

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private TextView textView1;
    private TextView textView2;

    /*two boxes on bottom right corner for safety state*/
    private TextView emergencyStop;
    private TextView emergencyFalse;
    private TextView safeBox;
    private TextView acknowledge;
    private TextView protectiveStop;
    private TextView warningField;
    private TextView emergencyState;

    private Integer node = 0;
    private Integer previous_node = 0;

    private String safetyState = "";

    private RequestQueue queue; //for requesting Json
    private RequestQueue requestQueue; //for sending Json

    /* Cells 1 to 6 */
    private ImageButton goto1;
    private ImageButton goto2;
    private ImageButton goto3;
    private ImageButton goto4;
    private ImageButton goto5;
    private ImageButton goto6;

    private Button pause;
    private Button stop;
    private Button run;

    /* Test for KMR position */
    private Button it_is_at1;
    private Button it_is_at2;
    private Button it_is_at3;
    private Button it_is_at4;
    private Button it_is_at5;
    private Button it_is_at6;
    private Button it_is_moving;

    private Button ack ;
    private Button es ;
    private Button protectbutton ;
    private Button safe ;
    private Button warning ;

    /* KMR icon position animation*/
    private ImageView kmr1;
    private ImageView kmr2;
    private ImageView kmr3;
    private ImageView kmr4;
    private ImageView kmr5;
    private ImageView kmr6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goto1 = (ImageButton) findViewById(R.id.imageButton1);
        goto2 = (ImageButton) findViewById(R.id.imageButton2);
        goto3 = (ImageButton) findViewById(R.id.imageButton3);
        goto4 = (ImageButton) findViewById(R.id.imageButton4);
        goto5 = (ImageButton) findViewById(R.id.imageButton5);
        goto6 = (ImageButton) findViewById(R.id.imageButton6);

        pause = (Button) findViewById(R.id.Pause);
        stop = (Button) findViewById(R.id.Stop);
        run = (Button) findViewById(R.id.Run);

//        it_is_at1 = (Button) findViewById(R.id.button11);
//        it_is_at2 = (Button) findViewById(R.id.button12);
//        it_is_at3 = (Button) findViewById(R.id.button13);
//        it_is_at4 = (Button) findViewById(R.id.button14);
//        it_is_at5 = (Button) findViewById(R.id.button15);
//        it_is_at6 = (Button) findViewById(R.id.button16);
//        it_is_moving = (Button) findViewById(R.id.button);

        kmr1 = (ImageView) findViewById(R.id.kmr1);
        kmr2 = (ImageView) findViewById(R.id.kmr2);
        kmr3 = (ImageView) findViewById(R.id.kmr3);
        kmr4 = (ImageView) findViewById(R.id.kmr4);
        kmr5 = (ImageView) findViewById(R.id.kmr5);
        kmr6 = (ImageView) findViewById(R.id.kmr6);

//        ack = (Button) findViewById(R.id.button22);
//        es = (Button) findViewById(R.id.button23);
//        protectbutton = (Button) findViewById(R.id.button24);
//        safe = (Button) findViewById(R.id.button25);
//        warning = (Button) findViewById(R.id.button26);

        textView = findViewById(R.id.text_battery);
        textView1 = findViewById(R.id.text_node);
        textView2 = findViewById(R.id.text_safetyState);
        acknowledge = findViewById(R.id.safetygray);
        emergencyStop = findViewById(R.id.emergencyTrue);
        protectiveStop = findViewById(R.id.safetyblue);
        emergencyFalse = findViewById(R.id.emergencyFalse);
        warningField = findViewById(R.id.safetyyellow);
        safeBox = findViewById(R.id.safeBox);
        emergencyState = findViewById(R.id.emergencyState);

//
//        ack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                safetyState = "ACKNOWLEDGE_REQUIRED";
//            }
//        });
//        es.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                safetyState = "EMERGENCY_STOP";
//            }
//        });
//        protectbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                safetyState = "PROTECTIVE_STOP";
//            }
//        });
//        safe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                safetyState = "SAFE";
//            }
//        });
//        warning.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                safetyState = "WARNING_FIELD";
//            }
//        });

/*        if (safetyState=="SAFE"){
            emergencyStop.setVisibility(View.INVISIBLE);
            emergencyFalse.setVisibility(View.INVISIBLE);
            acknowledge.setVisibility(View.INVISIBLE);
            protectiveStop.setVisibility(View.INVISIBLE);
            safeBox.setVisibility(View.INVISIBLE);
            warningField.setVisibility(View.INVISIBLE);
            emergencyState.setVisibility(View.INVISIBLE);
        }*/
        //handler check for KMR feedback every 250 milliseconds

        final Handler handler = new Handler();
        final int delay = 500; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                jsonRequest();
                safetyAnimation();

                // check which node is active to show the solid icon at the position.
                if (node==1) {
                    clearIcons();
                    kmr1.setVisibility(View.VISIBLE);
                    node = 1;
                } else if (node==2) {
                    clearIcons();
                    kmr2.setVisibility(View.VISIBLE);
                    node = 2;

                } else if (node==7) {
                    clearIcons();
                    kmr3.setVisibility(View.VISIBLE);
                    node = 7;

                } else if (node==4) {
                    clearIcons();
                    kmr4.setVisibility(View.VISIBLE);
                    node = 4;

                } else if (node==5) {
                    clearIcons();
                    kmr5.setVisibility(View.VISIBLE);
                    node = 5;

                } else if (node==8) {
                    clearIcons();
                    kmr6.setVisibility(View.VISIBLE);
                    node = 8;

                }
                else if (node==-1) {
                clearIcons();
//                final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                    checkPosition();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


        /* Listener waits a command to move the KMR. After the command, the first
        action is to verify the current position in order to run the proper animation */
        /*It has to be added here the command to send the robot to the node*/

        goto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_1\"\n" +
                        "}";
                Submit(data);
            }
        });

        goto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_2\"\n" +
                        "}";
                Submit(data);
            }
        });

        goto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_3\"\n" +
                        "}";
                Submit(data);
            }
        });

        goto4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_4\"\n" +
                        "}";
                Submit(data);
            }
        });

        goto5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_5\"\n" +
                        "}";
                Submit(data);
            }
        });

        goto6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_6\"\n" +
                        "}";
                Submit(data);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_1\"\n" +
                        "}";
                Submit(data);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"jobName\":\"MOVE_TO_CELL_1\"\n" +
                        "}";
                Submit(data);
            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPosition();

                String data = "{\n" +
                        "\"runRequest\":\"RUN\"\n" +
                        "}";
                Submit(data);
            }
        });

        /* Listener checks if the KMR is at a specific node. If so, all animations
         * are cleared, and a solid icon will be shown at the right node.*/
//        it_is_at1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 1;
//            }
//        });
//
//        it_is_at2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 2;
//            }
//        });

 //       it_is_at3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 7;
//            }
//        });

//        it_is_at4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 4;
//            }
//        });

//        it_is_at5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 5;
//            }
//        });

//        it_is_at6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = 8;
//            }
//        });
//
//        it_is_moving.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearIcons();
//                node = -1;
//            }
//        });

        queue = Volley.newRequestQueue(this);

    }

    /* ***********END OF ON CREATE ************ */

    // This method clears all animations, but the one where the KMR currently is.
    public void checkPosition(){
        if (previous_node==1){
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr1.startAnimation(animation);

            kmr2.clearAnimation();
            kmr3.clearAnimation();
            kmr4.clearAnimation();
            kmr5.clearAnimation();
            kmr6.clearAnimation();

            kmr2.setVisibility(View.INVISIBLE);
            kmr3.setVisibility(View.INVISIBLE);
            kmr4.setVisibility(View.INVISIBLE);
            kmr5.setVisibility(View.INVISIBLE);
            kmr6.setVisibility(View.INVISIBLE);

//            at_node1 = false;
        }
        else if (previous_node==2){
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr2.startAnimation(animation);

            kmr1.clearAnimation();
            kmr3.clearAnimation();
            kmr4.clearAnimation();
            kmr5.clearAnimation();
            kmr6.clearAnimation();

            kmr1.setVisibility(View.INVISIBLE);
            kmr3.setVisibility(View.INVISIBLE);
            kmr4.setVisibility(View.INVISIBLE);
            kmr5.setVisibility(View.INVISIBLE);
            kmr6.setVisibility(View.INVISIBLE);

            //at_node2 = false;
        }
        else if (previous_node==7) {
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr3.startAnimation(animation);

            kmr1.clearAnimation();
            kmr2.clearAnimation();
            kmr4.clearAnimation();
            kmr5.clearAnimation();
            kmr6.clearAnimation();

            kmr1.setVisibility(View.INVISIBLE);
            kmr2.setVisibility(View.INVISIBLE);
            kmr4.setVisibility(View.INVISIBLE);
            kmr5.setVisibility(View.INVISIBLE);
            kmr6.setVisibility(View.INVISIBLE);

//            at_node3 = false;
//            node=0;

        }
        else if (previous_node==4) {
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr4.startAnimation(animation);

            kmr1.clearAnimation();
            kmr2.clearAnimation();
            kmr3.clearAnimation();
            kmr5.clearAnimation();
            kmr6.clearAnimation();

            kmr1.setVisibility(View.INVISIBLE);
            kmr2.setVisibility(View.INVISIBLE);
            kmr3.setVisibility(View.INVISIBLE);
            kmr5.setVisibility(View.INVISIBLE);
            kmr6.setVisibility(View.INVISIBLE);

//            at_node4 = false;
//            node=0;

        }
        else if (previous_node==5) {
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr5.startAnimation(animation);

            kmr1.clearAnimation();
            kmr2.clearAnimation();
            kmr3.clearAnimation();
            kmr4.clearAnimation();
            kmr6.clearAnimation();

            kmr1.setVisibility(View.INVISIBLE);
            kmr2.setVisibility(View.INVISIBLE);
            kmr3.setVisibility(View.INVISIBLE);
            kmr4.setVisibility(View.INVISIBLE);
            kmr6.setVisibility(View.INVISIBLE);

//            at_node5 = false;
 //           node=0;

        }
        else if (previous_node==8) {
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            kmr6.startAnimation(animation);

            kmr1.clearAnimation();
            kmr2.clearAnimation();
            kmr3.clearAnimation();
            kmr4.clearAnimation();
            kmr5.clearAnimation();

            kmr1.setVisibility(View.INVISIBLE);
            kmr2.setVisibility(View.INVISIBLE);
            kmr3.setVisibility(View.INVISIBLE);
            kmr4.setVisibility(View.INVISIBLE);
            kmr5.setVisibility(View.INVISIBLE);

//            at_node6 = false;
//            node=0;

        }
    }

    public void clearIcons() {

//        at_node1 = false;
//        at_node2 = false;
//        at_node3 = false;
//        at_node4 = false;
//        at_node5 = false;
//        at_node6 = false;
//        node=0;
//
        kmr1.clearAnimation();
        kmr2.clearAnimation();
        kmr3.clearAnimation();
        kmr4.clearAnimation();
        kmr5.clearAnimation();
        kmr6.clearAnimation();

        kmr1.setVisibility(View.INVISIBLE);
        kmr2.setVisibility(View.INVISIBLE);
        kmr3.setVisibility(View.INVISIBLE);
        kmr4.setVisibility(View.INVISIBLE);
        kmr5.setVisibility(View.INVISIBLE);
        kmr6.setVisibility(View.INVISIBLE);
    }

    /* Method to animate the safety state box*/

    private void safetyAnimation(){

        if(safetyState=="ACKNOWLEDGE_REQUIRED"){
            emergencyStop.setVisibility(View.INVISIBLE);
            emergencyFalse.setVisibility(View.VISIBLE);
            acknowledge.setVisibility(View.VISIBLE);
            protectiveStop.setVisibility(View.INVISIBLE);
            safeBox.setVisibility(View.INVISIBLE);
            warningField.setVisibility(View.INVISIBLE);
            emergencyState.setVisibility(View.INVISIBLE);
        }
        else if(safetyState=="EMERGENCY_STOP"){
            emergencyStop.setVisibility(View.VISIBLE);
            emergencyFalse.setVisibility(View.INVISIBLE);
            acknowledge.setVisibility(View.INVISIBLE);
            protectiveStop.setVisibility(View.INVISIBLE);
            safeBox.setVisibility(View.INVISIBLE);
            warningField.setVisibility(View.INVISIBLE);
            emergencyState.setVisibility(View.VISIBLE);
        }
        else if (safetyState=="SAFE"){
            emergencyStop.setVisibility(View.INVISIBLE);
            emergencyFalse.setVisibility(View.VISIBLE);
            acknowledge.setVisibility(View.INVISIBLE);
            protectiveStop.setVisibility(View.INVISIBLE);
            safeBox.setVisibility(View.VISIBLE);
            warningField.setVisibility(View.INVISIBLE);
            emergencyState.setVisibility(View.INVISIBLE);
        }
        else if(safetyState=="PROTECTIVE_STOP"){
            emergencyStop.setVisibility(View.INVISIBLE);
            emergencyFalse.setVisibility(View.VISIBLE);
            acknowledge.setVisibility(View.INVISIBLE);
            protectiveStop.setVisibility(View.VISIBLE);
            safeBox.setVisibility(View.INVISIBLE);
            warningField.setVisibility(View.INVISIBLE);
            emergencyState.setVisibility(View.INVISIBLE);
        }

        else if(safetyState=="WARNING_FIELD"){
            emergencyStop.setVisibility(View.INVISIBLE);
            emergencyFalse.setVisibility(View.VISIBLE);
            acknowledge.setVisibility(View.INVISIBLE);
            protectiveStop.setVisibility(View.INVISIBLE);
            safeBox.setVisibility(View.INVISIBLE);
            warningField.setVisibility(View.VISIBLE);
            emergencyState.setVisibility(View.INVISIBLE);
        }

    }

    /* Class to request Json */

    private void jsonRequest() {

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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                textView.setText(jsonObject.getString("batteryLevel"));

                node = jsonObject.getInt("currentNodeID");  //activate when testing on real KMR
                if (node!=-1){
                    previous_node=node;
                }
                safetyState = jsonObject.getString("safetyState");
                safetyAnimation();

                textView2.setText(String.valueOf( safetyState));
                textView1.setText(Integer.toString(node));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Post Json */
    private void Submit(String data) {
        final String savedata = data;
        String URL1 = "http://10.216.70.19:8080/restServices/webapi/services/createNewJob";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), objres.toString(), Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();

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

            // post data to server
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
