package ricky.hastaprimasolusi.newandrosales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.os.Build.VERSION_CODES.KITKAT;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE = 121;

    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 3000;

    private TextView textUUID;
    private EditText etIMEI,etNIK, etPassword;

    //shared preferences for retrieve NIK after logout
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String StoredNIK = "nameKey";

    public String result1,imeigiven;


    //session
    String kodeImei, IPADDR , IPADDR2, NMSERVER, info, strPhoneType, test1;
    SessionManager session;

    @RequiresApi(api = KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull (getSupportActionBar ()).setElevation (0);

        etIMEI = findViewById(R.id.field_IMEI);
        etIMEI.setEnabled(true);
        etNIK = findViewById(R.id.field_NIK);
        etPassword = findViewById(R.id.field_Password);
        textUUID = findViewById(R.id.txt_uuid);

        EditText et_ip = findViewById (R.id.et_ip);
        EditText et_folder = findViewById (R.id.et_folder);

        uuid();
        session = new SessionManager(getApplicationContext());
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(StoredNIK)) {
            etNIK.setText(sharedpreferences.getString(StoredNIK, ""));
        }

        HashMap<String, String> identifyServer = session.getSettingDetails();

        String dataAddr 	= identifyServer.get(SessionManager.KEY_IP);
        String dataServer 	= identifyServer.get(SessionManager.KEY_SERVER);


        if(dataAddr != null){
            et_ip.setText(dataAddr);
            et_folder.setText(String.valueOf(dataServer));


            String ipAddress = et_ip.getText().toString();
            String nmServer  = et_folder.getText().toString();
            session.createSettingSession(ipAddress,nmServer);

            IPADDR 		= ipAddress;
            IPADDR2     = "103.83.179.211";
            NMSERVER 	= nmServer;
        }else{
            String dataIP	  = "36.94.17.163";
            String dataServ   = "androsales_service_dev";
            String uuid = textUUID.getText().toString();
            et_ip.setText(dataIP);
            et_folder.setText(dataServ);

            session.createSettingSession(dataIP,dataServ);

            IPADDR 		= dataIP;
            IPADDR2     = "103.83.179.211";
            NMSERVER 	= dataServ;

        }

        //footer text
        TextView textView = findViewById (R.id.text_footer);
        String version = "1.52";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        textView.setText("By PT. Hasta Prima Solusi - v." + version);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            String[] perms = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CAMERA
            };


            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
            }
        }

        // show imei
        /*TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final String imei = manager.getDeviceId();*/

        //etIMEI.setText(String.valueOf(imei));
        //etIMEI.setText("865300048276503");
        /*String androidId = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID androidId_UUID = null;
        try {
            androidId_UUID = UUID
                    .nameUUIDFromBytes(androidId.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String unique_id = androidId_UUID.toString();
        etIMEI.setText(unique_id);*/



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.requestPermissions(this, "All permissions are required to run this application", requestCode, permissions);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // if permissions have been granted, or has been passed, take an action here,
        // perform your action here
        // this method will be called as soon as permissions have been granted
        // you can add a toast message here, to see what happens

        // show imei
        /*TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final String imei = manager.getDeviceId();*/

        //etIMEI.setText(String.valueOf(imei));
        //"865300048276503"
        //etIMEI.setText("865300048276503");
        /*String androidId = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID androidId_UUID = null;
        try {
            androidId_UUID = UUID
                    .nameUUIDFromBytes(androidId.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String unique_id = androidId_UUID.toString();
        etIMEI.setText(unique_id);*/
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // if some permissions has been denied, show a settings dialog
            // that will take user to the application permissions setting secreen
            new AppSettingsDialog.Builder(this).build().show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.button_ab_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_setting_ip:
                openSetting();
                break;
            default:
                break;
        }

        return true;
    }

    public void openSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    //login imei

    public void cekImei(View view) {

        String nik          = etNIK.getText().toString().trim();
        String uuid         = textUUID.getText().toString();
        String password     = etPassword.getText().toString();
        new AsyncLoginIMEI().execute(nik,uuid,password);
    }

    private class AsyncLoginIMEI extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;
        URL url2 = null;

        String URL = "http://"+IPADDR+"/"+NMSERVER+"/loginNIKUUID.inc.php";
        String URL2 = "http://"+IPADDR2+"/"+NMSERVER+"/loginNIKUUID.inc.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @RequiresApi(api = KITKAT)
        @Override
        protected String doInBackground(String... params) {
           return connect(URL, params);
        }

        private String connect(String url, String[] params) {
            try {

                // Enter URL address where your php file resides
                url2 = new URL(url);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception.111";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url2.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder;
                builder = new Uri.Builder();
                //builder.appendQueryParameter("imei", params[0]);
                builder.appendQueryParameter("nik", params[0]);
                builder.appendQueryParameter("uuid", params[1]);
                builder.appendQueryParameter("password", params[2]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (SocketTimeoutException e){
                e.printStackTrace ();
                if(url.equals (URL)){
                    return connect(URL2, params);
                }else {
                    return "exception";
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{
                    return("unsuccessful.123");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception.123";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {

            //String imeiid 	    = etIMEI.getText().toString().trim();
            String nikID        = etNIK.getText().toString().trim();


            Pattern regex = Pattern.compile(".");
            if(regex.matcher (result).find ()){
                Log.e("Character :",result);
                String[] separated = result.split("\\.");
                result1 = separated[0].trim();
                    imeigiven = separated[1].trim();

                Log.e("Character 1:",result1);
                Log.e("Character 2:",imeigiven);

            }else{
                result1 = result;
            }



            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result1.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Log.e("Is in true statement",result1);
                session.createLoginSession(imeigiven,nikID);


                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(StoredNIK, nikID);
                editor.apply ();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            }else if (result1.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Log.e("Is in false statement",result1);
                Toast.makeText(LoginActivity.this, "Invalid IMEI/UUID not found", Toast.LENGTH_LONG).show();

            } else if (result1.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Log.e("Is in exception state",result1);
                Toast.makeText(LoginActivity.this, "Tidak terhubung dengan Server!", Toast.LENGTH_LONG).show();

            } else if (result1.equalsIgnoreCase("mismatch")) {

                Log.e("Is in mismatch state",result1);
                Toast.makeText(LoginActivity.this, "NIK/Password salah!", Toast.LENGTH_LONG).show();

            } else{
                Log.e("Is in else statement",result1);
                Toast.makeText(LoginActivity.this, "Terjadi Kesalahan, Silahkan Coba lagi", Toast.LENGTH_LONG).show();
            }
        }

    }
    //end login imei

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @RequiresApi(api = KITKAT)
    public void uuid(){
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID androidId_UUID;
        androidId_UUID = UUID
                .nameUUIDFromBytes(androidId.getBytes(StandardCharsets.UTF_8));

        assert androidId_UUID != null;
        String unique_id = androidId_UUID.toString();
        textUUID.setText(unique_id);
    }
}
