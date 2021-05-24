package ricky.hastaprimasolusi.newandrosales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import ricky.hastaprimasolusi.newandrosales.app.AppController;

public class SubmitLeave extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinnerType)
    Spinner spinnerType;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.spinnerCuti) Spinner spinnerCuti;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtCuti)
    TextView txtCuti;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.cvCuti)
    CardView cvCuti;

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.cvImageCircle) CardView cvImageCircle;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.cutiImage)
    ImageView cutiImage;

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtIdPengajuan) TextView txtIdPengajuan;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtJenisCuti) TextView txtJenisCuti;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.txtCode) TextView txtCode;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.editTextMultiLine)
    EditText txtAlasan;

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.etDateAwal) EditText dateAwal;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.etDateAkhir) EditText dateAkhir;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.fabPhotoPengajuan)
    FloatingActionButton fabPhoto;

    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.btnPengajuan)
    Button btnPengajuan;


    String[] pengajuan = {"Cuti","Ijin","Sakit","Lembur","Cuti Khusus"};
    String[] cuti = {"Menikah","Urusan Anak","Meninggal","Lain - Lain"};

    private static final String TAG = SubmitLeave.class.getSimpleName();


    public String URL;
    Bitmap FixBitmap;


    String tag_json_obj = "json_obj_req";
    int success;


    SessionManager session;
    String nik,IPADDR,NMSERVER;

    ByteArrayOutputStream byteArrayOutputStream;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPLKJHGFDSAZCXVBNM";


    String dateAwalan, dateAkhiran, id_pengajuan, alasan, code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_submit_leave);
        ButterKnife.bind (this);
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));
        URL = "http://" + IPADDR + "/" + NMSERVER + "/";



        byteArrayOutputStream = new ByteArrayOutputStream();
        HashMap<String, String> devId = session.getUserDetails();
        nik = String.valueOf (devId.get(SessionManager.KEY_NIK));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this,
                R.array.pengajuan, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter (adapter);
        spinnerType.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener (){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(Objects.equals (pengajuan[position], "Cuti")){
                    txtCuti.setVisibility (View.VISIBLE);
                    cvCuti.setVisibility (View.VISIBLE);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<> (SubmitLeave.this,
                            android.R.layout.simple_spinner_dropdown_item, cuti);
                    adapter2.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
                    spinnerCuti.setAdapter (adapter2);
                    txtIdPengajuan.setText ("1");
                    Log.e ("tag:",txtIdPengajuan.getText ().toString ());
                }else if (Objects.equals (pengajuan[position], "Ijin")){
                    txtCuti.setVisibility (View.GONE);
                    cvCuti.setVisibility (View.GONE);
                    txtIdPengajuan.setText ("2");
                    Log.e ("tag:",txtIdPengajuan.getText ().toString ());
                }else if (Objects.equals (pengajuan[position], "Sakit")){
                    txtCuti.setVisibility (View.GONE);
                    cvCuti.setVisibility (View.GONE);
                    txtIdPengajuan.setText ("3");
                    Log.e ("tag:",txtIdPengajuan.getText ().toString ());
                }else if (Objects.equals (pengajuan[position], "Lembur")){
                    txtCuti.setVisibility (View.GONE);
                    cvCuti.setVisibility (View.GONE);
                    txtIdPengajuan.setText ("4");
                    Log.e ("tag:",txtIdPengajuan.getText ().toString ());
                }else if (Objects.equals (pengajuan[position], "Cuti Khusus")){
                    txtCuti.setVisibility (View.GONE);
                    cvCuti.setVisibility (View.GONE);
                    txtIdPengajuan.setText ("5");
                    Log.e ("tag:",txtIdPengajuan.getText ().toString ());
                }

                id_pengajuan = txtIdPengajuan.getText ().toString ();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateAwal.setOnClickListener ((View v) -> {
            if(hasWindowFocus()){
                DateDialog dialog=new DateDialog(v);
                FragmentTransaction ft =getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");

            }
        });


        dateAkhir.setOnClickListener (view -> {
            if(hasWindowFocus()){
                DateDialog dialog=new DateDialog(view);
                FragmentTransaction ft =getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");

            }
        });



        fabPhoto.setOnClickListener (v -> {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        });

        btnPengajuan.setOnClickListener (v -> {
            //String img_file_name = date_name+"_"+txtIdPengajuan.getText ().toString ()+"_"+nik;
            txtCode.setText (getRandomString ());
            code = txtCode.getText ().toString ();
            alasan = txtAlasan.getText ().toString ();
            dateAwalan = dateAwal.getText ().toString ();
            dateAkhiran = dateAkhir.getText ().toString ();
            SyncronousLeave (nik,dateAwalan, dateAkhiran, id_pengajuan, alasan, code);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            FixBitmap = (Bitmap) Objects.requireNonNull (data.getExtras ()).get ("data");
            cutiImage.setImageBitmap (FixBitmap);
            cvImageCircle.setVisibility (View.VISIBLE);
            //btnSubmit.setVisibility (View.VISIBLE);

        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e (TAG, "Photo Taken.");

                    break;
                case Activity.RESULT_CANCELED:
                    Log.e (TAG, "Photo cancelled.");
                    break;
            }
        }
    }

    public void SyncronousLeave(String nik1, String dateAwal1, String dateAkhir1, String pengajuan, String alasan1, String code1){
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"submitleave.php";
        ProgressDialog progressDialog = new ProgressDialog (SubmitLeave.this);
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
                    sendNotification();
                    //UploadImage ();
                }
                Toast.makeText (getApplicationContext (),jObj.getString ("message"),Toast.LENGTH_LONG).show ();

            }catch (JSONException e){
                progressDialog.dismiss ();
                e.printStackTrace ();
                Log.e(TAG, "Awal: " + dateAwal1);
                Log.e(TAG, "Akhir: " + dateAkhir1);
                Log.e(TAG, "idpengajuan: " + pengajuan);
                Log.e(TAG, "Alasan: " + alasan1);
                Log.e(TAG, "code: " + code1);
                Log.d("Error :",String.valueOf (e));
            }
        }, error -> {
            progressDialog.dismiss ();
            Log.e(TAG, "Error: " + error.getMessage());
            Log.e(TAG, "Awal: " + dateAwal1);
            Log.e(TAG, "Akhir: " + dateAkhir1);
            Log.e(TAG, "idpengajuan: " + pengajuan);
            Log.e(TAG, "Alasan: " + alasan1);
            Log.e(TAG, "code: " + code1);
            Toast.makeText(SubmitLeave.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<> ();
                params.put ("nik",nik1);
                params.put ("dateAwal", String.valueOf (dateAwal1));
                params.put ("dateAkhir", String.valueOf (dateAkhir1));
                params.put ("idpengajuan", String.valueOf(pengajuan));
                params.put ("alasan", String.valueOf (alasan1));
                params.put ("code", String.valueOf (code1));
                return params;
            }
        };
        AppController.getInstance ().addToRequestQueue (strReq, tag_json_obj);
    }

    private void sendNotification() {

    }

    private static String getRandomString()
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(12);
        for(int i = 0; i< 12; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


}