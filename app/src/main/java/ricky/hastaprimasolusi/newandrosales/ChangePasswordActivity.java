package ricky.hastaprimasolusi.newandrosales;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ricky.hastaprimasolusi.newandrosales.app.AppController;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldpw, newpw, confirmnewpw;
    Button btnSubmit;
    int success;
    SessionManager session;
    String IPADDR,NMSERVER,kodeImei;

    String tag_json_obj = "json_obj_req";
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_change_password);
        getSupportActionBar().setElevation(0);

        btnSubmit = findViewById (R.id.buttonSubmit);
        oldpw = findViewById (R.id.txt_password);
        newpw = findViewById (R.id.txt_newPassword);
        confirmnewpw = findViewById (R.id.txt_confirmPassword);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        btnSubmit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                getData ();
                String oldpassword, newpassword, confirmpassword;
                oldpassword = oldpw.getText ().toString ();
                newpassword = newpw.getText ().toString ();
                confirmpassword = confirmnewpw.getText ().toString ();

                if(!newpassword.equals (confirmpassword)||newpassword==null){
                    Toast.makeText (ChangePasswordActivity.this, "Password baru dengan confirm password tidak sama", Toast.LENGTH_SHORT).show ();
                }else{
                    changePw (kodeImei,oldpassword,newpassword);
                }

            }
        });
    }

    public void getData(){
        btnSubmit = findViewById (R.id.buttonSubmit);
        oldpw = findViewById (R.id.txt_password);
        newpw = findViewById (R.id.txt_newPassword);
        confirmnewpw = findViewById (R.id.txt_confirmPassword);
    };

    public void changePw(String imei, String passold, String passnew){
        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));
        String url = "http://"+IPADDR+"/"+NMSERVER+"/API/"+"changepassword.php";
        getData ();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            Log.d(TAG, "Response: " + response);

            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);

                // Cek error node pada json
                if (success == 1) {
                    Log.d("Add/update", jObj.toString());

                    Toast.makeText(ChangePasswordActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    Intent intenthome = new Intent (ChangePasswordActivity.this, MainActivity.class);
                    startActivity (intenthome);
                    finish ();

                } else {
                    Toast.makeText(ChangePasswordActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

        }, error -> {
            Log.e(TAG, "Error: " + error.getMessage());
            Toast.makeText(ChangePasswordActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String> ();
                    params.put("imei", String.valueOf (imei));
                    params.put("oldpassword", String.valueOf (passold));
                    params.put("newpassword", String.valueOf (passnew));

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}


