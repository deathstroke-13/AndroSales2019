package ricky.hastaprimasolusi.newandrosales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AbsensiActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SessionManager session;

    public String URL;

    String kodeImei, IPADDR, NMSERVER, kodeNIK;

    ImageButton imgButton;
    ImageView imageView;
    Intent intent;
    Button bt_absen;

    EditText et_odometer, et_namaoutlet, et_ket, etLocationLat, etLocationLong;
    String odometer, namaoutlet, ket, LocationLat, LocationLong, nilaiAbsen, jen;

    private RadioGroup radioGroupAbsensi;

    private RadioButton radioButtonAbsensi;

    private ProgressDialog progress;

    Bitmap FixBitmap;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    Drawable Draw;

    String ImageTag = "image_tag" ;

    String ImageName = "image_data";
    URL url;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;

    BufferedWriter bufferedWriter;

    CardView imagecircle;

    int RC;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;
    boolean check = true;

    public String file_name     = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
    public String img_new_name  = "img_"+file_name+"_"+System.currentTimeMillis();

    private Handler mHandler;
    private int mInterval = 5000; // 5 seconds by default, can be changed later


    public Double latti;
    public Double longi;
    public FloatingActionButton fab;

    public ImageView imgRed, imgGreen;

    //new

    private static final String TAG = AbsensiActivity.class.getSimpleName();

    // location last updated time
    private String mLastUpdateTime;
    public String fake_status = "0";

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    Spinner spin;
    TextView textJenis;
    String [] jenis = {"Masuk","Pulang"};

    //end new

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR = String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER = String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        URL = "http://" + IPADDR + "/" + NMSERVER + "/";

        imgButton =  findViewById(R.id.searchImageButton);
        imageView =  findViewById(R.id.imageView);
        bt_absen  =  findViewById(R.id.button_simpan_absen);
        byteArrayOutputStream = new ByteArrayOutputStream();
        textJenis = findViewById (R.id.textJenis);


        imagecircle = findViewById (R.id.imagecircle);
        imgGreen =  findViewById(R.id.imageViewGreen);
        imgRed  =  findViewById(R.id.imageViewRed);

        spin = findViewById (R.id.spinnerMasukPulang);
        spin.setOnItemSelectedListener (this);


        ArrayAdapter aa = new ArrayAdapter (this, android.R.layout.simple_spinner_item,jenis);
        aa.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter (aa);

        //radioGroupAbsensi = findViewById(R.id.et_absensi);

        et_odometer     =  findViewById(R.id.et_odometer);
        et_namaoutlet   =  findViewById(R.id.et_namaoutlet);
        et_ket          =  findViewById(R.id.et_ket);
        etLocationLat   =  findViewById(R.id.etLocationLat);
        etLocationLong  =  findViewById(R.id.etLocationLong);

        imgGreen = findViewById(R.id.imageViewGreen);
        imgRed  = findViewById(R.id.imageViewRed);


        //createLocationRequest();
        //getLocation();
        //startRepeatingTask();

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    @OnClick(R.id.button_simpan_absen) void daftar() {
        //membuat progress dialog
        /*
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        */
        progress = ProgressDialog.show(AbsensiActivity.this,"Proses Absensi","Please Wait",false,false);

        // get selected radio button from radioGroup
        //int selectedId = radioGroupAbsensi.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        //radioButtonAbsensi = findViewById(selectedId);

        odometer        = et_odometer.getText().toString();
        namaoutlet      = et_namaoutlet.getText().toString();
        //ket             = et_ket.getText().toString();
        LocationLong    = etLocationLong.getText().toString();
        LocationLat     = etLocationLat.getText().toString();
        jen             = textJenis.getText ().toString ();
        //nilaiAbsen      = radioButtonAbsensi.getText().toString();


        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "Retrofit-Sample-App").build();
                return chain.proceed(newRequest);
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        builder.retryOnConnectionFailure(false);
        builder.followRedirects(false);
        builder.followSslRedirects(false);
        builder.cache(null);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.absensi("0", namaoutlet, jen, LocationLong, LocationLat, nilaiAbsen, kodeImei, img_new_name, fake_status);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();
                if (value.equals("1")) {
                    if(message.equals("timeout")){
                        call.cancel();
                        client.dispatcher().cancelAll();
                    }else {
                        UploadImageToServer();
                        Log.d("Tag: ", img_new_name);
                        Log.d ("URL: ", URL);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Intent inLogin2 = new Intent(AbsensiActivity.this, MainActivity.class);
                        startActivity(inLogin2);
                        finish();
                    }
                } else {
                    Toast.makeText(AbsensiActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                call.cancel();
                client.dispatcher().cancelAll();
                if (call.isCanceled()) {
                    Log.e("TAG", "request was cancelled");
                }
                else {
                    Log.e("TAG", "other larger issue, i.e. no network connection?");
                }
                Toast.makeText(AbsensiActivity.this, "Data gagal di input, silahkan coba lagi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // untuk mengosongi edittext pada form
    private void kosong(){
        et_ket.setText(null);
        et_namaoutlet.setText(null);
        et_odometer.setText(null);
        etLocationLat.setText(null);
        etLocationLong.setText(null);
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            FixBitmap = (Bitmap) data.getExtras ().get ("data");
            imageView.setImageBitmap (FixBitmap);
            imagecircle.setVisibility (View.VISIBLE);
            bt_absen.setVisibility (View.VISIBLE);

        }

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e (TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e (TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                //progress = ProgressDialog.show(AbsensiActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                //progress.dismiss();
                //Toast.makeText(AbsensiActivity.this,string1,Toast.LENGTH_LONG).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<> ();

                HashMapParams.put(ImageTag, img_new_name);

                HashMapParams.put(ImageName, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(URL+"API/upload-image-to-server.php", HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textJenis.setText (jenis[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textJenis.setText ("Masuk");
    }

    public class ImageProcessClass{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null){
                        stringBuilder.append(RC2);
                        Log.d (TAG,"Success?? :"+RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
                Log.d (TAG,"Message?? :"+stringBuilder.toString ());
            }
            return stringBuilder.toString();
        }
    }

    public void logOut(View view)
    {
        session.logoutUser();
        finish();
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), "PT. Hasta Prima Solusi", Toast.LENGTH_SHORT).show();
            return true;

            //Intent i = new Intent(MainActivity.this, AboutActivity.class);
            //startActivity(i);
        }
        else if (id == R.id.action_logout){
            session.logoutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    @SuppressLint("SetTextI18n")
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //for counter fakeGPS using mock provider
                if(mCurrentLocation.isFromMockProvider()){
                    ((EditText) findViewById(R.id.etLocationLat)).setText(Double.toString(mCurrentLocation.getLatitude()));
                    ((EditText) findViewById(R.id.etLocationLong)).setText(Double.toString(mCurrentLocation.getLongitude()));
                    fake_status ="1";
                    imgRed.setVisibility(View.GONE);
                    imgGreen.setVisibility(View.VISIBLE);
                }else {
                    ((EditText) findViewById(R.id.etLocationLat)).setText(Double.toString(mCurrentLocation.getLatitude()));
                    ((EditText) findViewById(R.id.etLocationLong)).setText(Double.toString(mCurrentLocation.getLongitude()));

                    fake_status="0";
                    imgGreen.setVisibility(View.VISIBLE);
                    imgRed.setVisibility(View.GONE);
                }
            }
        }
        else{
            ((EditText)findViewById(R.id.etLocationLat)).setText("");
            ((EditText)findViewById(R.id.etLocationLong)).setText("");
            fake_status="0";
            imgRed.setVisibility(View.VISIBLE);
            imgGreen.setVisibility(View.GONE);
        }

        //toggleButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(AbsensiActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(AbsensiActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });

    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //toggleButtons();
                    }
                });
    }



    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }
}
