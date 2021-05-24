package ricky.hastaprimasolusi.newandrosales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ricky.hastaprimasolusi.newandrosales.app.AppController;

public class LeaveActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerViewLeave)
    RecyclerView rv;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.fabAdd)
    FloatingActionButton fabAdd;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    String tag_json_obj = "json_obj_req";
    SessionManager session;
    String IPADDR,NMSERVER,nik;
    int success;

    private static final String TAG = LeaveActivity.class.getSimpleName();

    private List<ListDataLeave> listDataLeave;
    private RecyclerLeave adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_leave);
        Objects.requireNonNull (getSupportActionBar ()).hide ();
        ButterKnife.bind (this);
        session = new SessionManager(getApplicationContext());
        rv.setHasFixedSize (true);
        rv.setLayoutManager (new LinearLayoutManager (this));
        listDataLeave = new ArrayList<> ();
        adapter = new RecyclerLeave (listDataLeave);

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        nik = String.valueOf (devId.get(SessionManager.KEY_NIK));


        getData (nik);

        swipeRefreshLayout.setOnRefreshListener (() -> {
            listDataLeave.clear ();
            getData (nik);
            adapter.notifyDataSetChanged ();

            //Toast.makeText (getApplicationContext (),"Works!",Toast.LENGTH_LONG).show ();
            new Handler ().postDelayed (() -> swipeRefreshLayout.setRefreshing (false),1000);
        });

        fabAdd.setOnClickListener (v -> {
            Intent intent = new Intent (LeaveActivity.this, SubmitLeave.class);
            startActivity (intent);
        });
    }

    private void getData(String nik){
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"leave.php";
        ProgressDialog pd = new ProgressDialog(LeaveActivity.this);
        pd.setMessage("Generating Data . . .");
        pd.show();
        StringRequest strReq = new StringRequest (Request.Method.POST,url, response -> {
            try{
                JSONObject jObj = new JSONObject (response);
                success = jObj.getInt ("success");
                JSONArray array = jObj.getJSONArray ("array");

                if(array.length () == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(LeaveActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("You dont have a paid leave/leave yet.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss ();
                                finish();
                            });
                    alertDialog.show();
                }else{
                    if(success ==1){
                        pd.dismiss ();
                        for(int i = 0; i<array.length ();i++){
                            JSONObject ob = array.getJSONObject (i);

                            ListDataLeave listData = new ListDataLeave (ob.getString ("pengajuan"),
                                    ob.getString ("atasan"),
                                    ob.getString ("dateAwal"),
                                    ob.getString ("dateAkhir"),
                                    ob.getString ("alasan"),
                                    ob.getString ("id_status"));
                            listDataLeave.add (listData);
                        }
                        rv.setAdapter (adapter);
                    }else{
                        Toast.makeText (getApplicationContext (),"Data cuti/ijin belum ada",Toast.LENGTH_LONG).show ();
                    }
                }


            } catch (JSONException e) {
                pd.dismiss ();
                e.printStackTrace ();
                Log.d ("Error :", String.valueOf (e));
            }
        },error -> {
            pd.dismiss ();
            Log.e(TAG, "Error: " + error.getMessage());
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<> ();
                params.put ("nik", nik);
                return params;
            }
        };
        AppController.getInstance ().addToRequestQueue (strReq, tag_json_obj);
    }
}