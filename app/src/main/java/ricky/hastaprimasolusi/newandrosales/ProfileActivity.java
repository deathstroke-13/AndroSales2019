package ricky.hastaprimasolusi.newandrosales;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.img_profile) ImageView imgProfile;

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.img_plus) ImageView imgPlus;

    //For retriveing NIK
    SessionManager session;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String StoredNIK = "nameKey";

    String IPADDR,NMSERVER;
    public String URL;
    String tag_json_obj = "json_obj_req";
    int success;

    //for photoprofile
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile);
        ButterKnife.bind (this);
        loadProfileDefault();
        //ImagePickerActivity.clearCache(this);

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

        imgPlus.setOnClickListener (v -> updateprofile());
        imgProfile.setOnClickListener (v -> updateprofile ());

    }

    private void updateprofile() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener (new MultiplePermissionsListener (){

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check ();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    
    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        GlideApp.with(this).load(R.drawable.ic_profile)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.transparent_blue));
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