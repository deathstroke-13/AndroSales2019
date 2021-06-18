package ricky.hastaprimasolusi.newandrosales.SendNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ricky.hastaprimasolusi.newandrosales.MainActivity;
import ricky.hastaprimasolusi.newandrosales.R;
import ricky.hastaprimasolusi.newandrosales.SessionManager;
import ricky.hastaprimasolusi.newandrosales.SplashScreen;
import ricky.hastaprimasolusi.newandrosales.app.AppController;


class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title, message;
    public String TAG = "mylog";
    String CHANNEL_ID = "my_notification";
    SessionManager session;
    String kodeImei,IPADDR,NMSERVER, URL;
    String tag_json_obj = "json_obj_req";
    private static final String TAG_SUCCESS = "success";



    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        session = new SessionManager(getApplicationContext());
        sendRegistrationToServer(token);

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));
        URL = "http://" + IPADDR + "/" + NMSERVER + "/";

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        insertToken(kodeImei, token);



        /*APIService apiService1;
        apiService1 = Client_wamp.getClient("http://192.168.0.7/").create(APIService.class);

        apiService1.insertKey(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });*/
    }

    private void sendRegistrationToServer(String token) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/IDs");
        // then store your token ID
        ref.push().setValue (token);
    }

    void insertToken(String kodeImei, String token) {

        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"newtoken.php";
        ProgressDialog progressDialog = new ProgressDialog (getApplicationContext ());
        progressDialog.setTitle ("Processing");
        progressDialog.setMessage ("Please Wait. . .");
        progressDialog.setCancelable (true);
        progressDialog.show ();

        StringRequest strReq = new StringRequest (Request.Method.POST, url, response ->{
            Log.d("imei :",kodeImei);
            Log.d("token :", token);

            try{

                JSONObject jObj = new JSONObject (response);
                int success = jObj.getInt (TAG_SUCCESS);
                Log.d("String Request :",response);

                progressDialog.dismiss ();
                if(success == 1){
                    Log.d("Token : ",jObj.getString ("token"));
                    Log.d(TAG,jObj.getString ("message"));
                }else{
                    Log.d(TAG,jObj.getString ("message"));
                }
            } catch (Exception e) {
                progressDialog.dismiss ();
                e.printStackTrace ();
                Log.d ("Error :", String.valueOf (e));
            }
        },error -> {
            progressDialog.dismiss ();
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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        notification();
    }

    void notification() {
        createNotificationChannel();

        Intent intent = new Intent (this, SplashScreen.class);
        Log.e ("Title : ", title);
        Log.e ("Message : ", message);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(404, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my notification";
            String description = "general notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}