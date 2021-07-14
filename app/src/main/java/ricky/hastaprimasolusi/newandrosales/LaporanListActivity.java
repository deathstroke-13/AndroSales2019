package ricky.hastaprimasolusi.newandrosales;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaporanListActivity extends AppCompatActivity {

    SessionManager session;

    private ProgressDialog progress;

    EditText txtdateAwal, txtdateAkhir;

    public String URL;

    String kodeImei,IPADDR,NMSERVER,datauser,datapass;

    //public static final String URL = "http://10.0.2.2/monitoring_sales_service/";
    private List<Result> results = new ArrayList<>();
    private RecyclerViewAdapterLaporan viewAdapter;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_list);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";


        ButterKnife.bind(this);

        viewAdapter = new RecyclerViewAdapterLaporan(this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText txtDateAwal= findViewById(R.id.txtdateAwal);

        txtDateAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasWindowFocus()){
                    DateDialog dialog=new DateDialog(v);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }
        });

    }

    @OnClick(R.id.btFind) void filter() {
        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        //mengambil data dari edittext
        txtdateAwal = findViewById(R.id.txtdateAwal);

        final String imei = kodeImei;
        final String dateAwal = txtdateAwal.getText().toString();

        //Toast.makeText(getApplicationContext(), ""+URL+"", Toast.LENGTH_SHORT).show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.filter(imei, dateAwal);

        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();
                if (value.equals("1")) {
//                    Toast.makeText(FilterActivity.this, message, Toast.LENGTH_SHORT).show();
//                    finish();
                    Log.d ("URL : ", URL);
                    Log.d ("imei : ", imei);
                    Log.d ("dateAwal :", dateAwal);
                    recyclerView.setVisibility(View.VISIBLE);
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapterLaporan(LaporanListActivity.this, results);
                    recyclerView.setAdapter(viewAdapter);
                } else {
                    Log.d ("URL : ", URL);
                    Log.d ("imei : ", imei);
                    Log.d ("dateAwal :", dateAwal);
                    Toast.makeText(LaporanListActivity.this, message, Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(LaporanListActivity.this, "Tidak terhubung dengan Server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), "PT. Hasta Prima Solusi", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_logout){
            session.logoutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
