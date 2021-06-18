package ricky.hastaprimasolusi.newandrosales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditProfileActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_nama) EditText et_nama;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_norek) EditText et_norek;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_email) EditText et_email;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_nohp) EditText et_nohp;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_npwp) EditText et_npwp;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_kk) EditText et_kk;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_ktp) EditText et_ktp;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.et_address) EditText et_address;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.btn_save_edit) Button btn_save;

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
        setContentView (R.layout.activity_edit_profile);

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
        btn_save.setOnClickListener (v -> {
            if(TextUtils.isEmpty (et_nama.getText ().toString ())||
                    TextUtils.isEmpty (et_norek.getText ().toString ())||
                    TextUtils.isEmpty (et_email.getText ().toString ())||
                    TextUtils.isEmpty (et_nohp.getText ().toString ())||
                    TextUtils.isEmpty (et_npwp.getText ().toString ())||
                    TextUtils.isEmpty (et_kk.getText ().toString ())||
                    TextUtils.isEmpty (et_ktp.getText ().toString ())||
                    TextUtils.isEmpty (et_address.getText ().toString ())){

                Toast.makeText (EditProfileActivity.this,"Silahkan isi semua kolom.",Toast.LENGTH_LONG).show ();

            }else{
                String nik = sharedpreferences.getString(StoredNIK, "");
                String name = et_nama.getText ().toString ().trim ();
                String norek = et_norek.getText ().toString ().trim ();
                String email = et_email.getText ().toString ().trim ();
                String nohp = et_nohp.getText ().toString ().trim ();
                String npwp = et_npwp.getText ().toString ().trim ();
                String kk = et_kk.getText ().toString ().trim ();
                String ktp = et_ktp.getText ().toString ().trim ();
                String alamat = et_address.getText ().toString ().trim ();
                saveEdit(nik,name,norek,email,nohp,npwp,kk,ktp,alamat);
            }
        });

    }

    private void saveEdit(String nik, String name, String norek, String email, String nohp, String npwp, String kk, String ktp, String alamat) {
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"saveeditprofile.php";
        ProgressDialog progressDialog = new ProgressDialog (EditProfileActivity.this);
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
                    finish ();
                }
            }catch (JSONException e){
                progressDialog.dismiss ();
                e.printStackTrace ();
                Log.d("Error :",String.valueOf (e));
            }
        }, error -> {
            progressDialog.dismiss ();
            Log.e(TAG, "Error: " + error.getMessage());
            Toast.makeText(EditProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<> ();
                params.put ("nik",nik);
                params.put ("name",name);
                params.put ("norek",norek);
                params.put ("email",email);
                params.put ("nohp",nohp);
                params.put ("npwp",npwp);
                params.put ("kk",kk);
                params.put ("ktp",ktp);
                params.put ("alamat",alamat);
                return params;
            }
        };
        AppController.getInstance ().addToRequestQueue (strReq, tag_json_obj);
    }

    private void GettingData(String nik) {
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"profile.php";
        ProgressDialog progressDialog = new ProgressDialog (EditProfileActivity.this);
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
                    Log.d ("Masuk :", String.valueOf (jObj));
                    et_nama.setText(jObj.getString ("nama"));
                    et_norek.setText (jObj.getString ("no_rek"));
                    et_email.setText (jObj.getString ("email"));
                    et_nohp.setText (jObj.getString ("nohp"));
                    et_npwp.setText (jObj.getString ("npwp"));
                    et_kk.setText (jObj.getString ("kk"));
                    et_ktp.setText (jObj.getString ("ktp"));
                    et_address.setText (jObj.getString ("alamat"));
                }
            }catch (JSONException e){
                progressDialog.dismiss ();
                e.printStackTrace ();
                Log.d("Error :",String.valueOf (e));
            }
        }, error -> {
            progressDialog.dismiss ();
            Log.e(TAG, "Error: " + error.getMessage());
            Toast.makeText(EditProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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