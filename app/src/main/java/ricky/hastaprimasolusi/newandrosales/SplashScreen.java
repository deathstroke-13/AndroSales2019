package ricky.hastaprimasolusi.newandrosales;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ricky.hastaprimasolusi.newandrosales.app.AppController;

public class SplashScreen extends AppCompatActivity {

    private TextView iv;
    String title, message;
    public String TAG = "mylog";
    String CHANNEL_ID = "my_notification";
    SessionManager session;
    String kodeImei,IPADDR,NMSERVER, URL;
    String tag_json_obj = "json_obj_req";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));
        URL = "http://" + IPADDR + "/" + NMSERVER + "/";

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        String refreshToken = String.valueOf (FirebaseMessaging.getInstance ().getToken ().addOnCompleteListener (new OnCompleteListener<String> () {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TAG : ", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                insertToken(kodeImei, token);
                Log.d("TAG : ", token);
            }
        }));

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(3000) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    //startActivity(i);
                    session.checkLogin();

                    finish();
                }
            }
        };
        timer.start();
    }

    void insertToken(String kodeImei, String token) {
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"newtoken.php";
        StringRequest strReq = new StringRequest (Request.Method.POST, url, response ->{
            Log.d("imei :",kodeImei);
            Log.d("token :", token);
            try{

                JSONObject jObj = new JSONObject (response);
                int success = jObj.getInt (TAG_SUCCESS);
                Log.d("String Request :",response);

                if(success == 1){
                    Log.d("Token : ",jObj.getString ("token"));
                    Log.d(TAG,jObj.getString ("message"));
                }else{
                    Log.d(TAG,jObj.getString ("message"));
                }
            } catch (Exception e) {

                e.printStackTrace ();
                Log.d ("Error :", String.valueOf (e));
            }
        },error -> {
            Log.e(TAG, "Error: " + error.getMessage());

        } ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<> ();
                params.put("imei", kodeImei);
                params.put("token", String.valueOf (token));

                return params;
            }
        };
        AppController.getInstance ().addToRequestQueue (strReq, tag_json_obj);

    }
}
