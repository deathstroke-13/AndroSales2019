package ricky.hastaprimasolusi.newandrosales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ricky.hastaprimasolusi.newandrosales.app.AppController;

import static ricky.hastaprimasolusi.newandrosales.app.AppController.TAG;

public class ProfileActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtNamaProfile) TextView txtNamaProfile;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtStatusProfile) TextView txtStatus;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtBank) TextView txtBank;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtNoRekening) TextView txtNoRekening;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtEmail) TextView txtEmail;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtNoHp) TextView txtNoHp;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtNpwp) TextView txtNpwp;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtKK) TextView txtKK;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtKTP) TextView txtKTP;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtAlamat) TextView txtAlamat;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.bt_edit_profile) Button bt_edit;

    //For retriveing NIK
    SessionManager session;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String StoredNIK = "nameKey";

    String IPADDR,NMSERVER;
    public String URL;
    String tag_json_obj = "json_obj_req";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile);
        ButterKnife.bind (this);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));
        URL = "http://" + IPADDR + "/" + NMSERVER + "/";

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(StoredNIK)) {
            GettingData(sharedpreferences.getString(StoredNIK, ""));
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setMessage ("Data tidak ditemukan, silahkan re-login").setTitle ("Error in getting NIK")
                    .setCancelable (false).setPositiveButton ("Ok", (dialog, which) -> finish());
            AlertDialog alert = builder.create ();
            alert.setTitle ("Alert");
            alert.show ();
        }

        bt_edit.setOnClickListener (v -> {
            Intent intent = new Intent (ProfileActivity.this, EditProfileActivity.class);
            startActivity (intent);
        });

    }

    private void GettingData(String nik) {
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"profile.php";
        ProgressDialog progressDialog = new ProgressDialog (ProfileActivity.this);
        progressDialog.setTitle ("Processing");
        progressDialog.setMessage ("Please Wait. . .");
        progressDialog.setCancelable (true);
        progressDialog.show ();

        StringRequest strReq = new StringRequest (Request.Method.POST, url, response -> {
            try{
                JSONObject jObj = new JSONObject (response);
                success = jObj.getInt ("success");
                if(success == 1){
                    progressDialog.dismiss ();
                    Log.d("NIK : ", nik);
                    Log.d ("Query :", String.valueOf (jObj));
                    txtNamaProfile.setText(jObj.getString ("nama"));
                    txtStatus.setText (jObj.getString("status"));
                    txtBank.setText (jObj.getString ("bank"));
                    txtNoRekening.setText (jObj.getString ("no_rek"));
                    txtEmail.setText (jObj.getString ("email"));
                    txtNoHp.setText (jObj.getString ("nohp"));
                    txtNpwp.setText (jObj.getString ("npwp"));
                    txtKK.setText (jObj.getString ("kk"));
                    txtKTP.setText (jObj.getString ("ktp"));
                    txtAlamat.setText (jObj.getString ("alamat"));
                }


            }catch (JSONException e){
                progressDialog.dismiss ();
                e.printStackTrace ();
                Log.d("Error :",String.valueOf (e));
            }
        }, error -> {
            progressDialog.dismiss ();
            Log.e(TAG, "Error: " + error.getMessage());
            Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<> ();
                params.put ("nik",nik);
                return params;
            }
        };
        AppController.getInstance ().addToRequestQueue (strReq, tag_json_obj);
    }
}