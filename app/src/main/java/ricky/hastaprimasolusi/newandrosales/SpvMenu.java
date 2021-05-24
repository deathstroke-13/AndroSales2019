package ricky.hastaprimasolusi.newandrosales;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

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

public class SpvMenu extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerViewSpv)
    RecyclerView rv;
    @SuppressLint("NonConstantResourceId")
    @BindView (R.id.swipeContainerSpv)
    SwipeRefreshLayout swipeRefreshLayout;

    String tag_json_obj = "json_obj_req";
    SessionManager session;
    String IPADDR,NMSERVER,nik;
    int success;


    private static final String TAG = SpvMenu.class.getSimpleName();

    private List<ListDataSpv> listDataSpvList;
    private RecyclerSpv adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_spv_menu);
        ButterKnife.bind (this);
        Objects.requireNonNull (getSupportActionBar ()).hide ();
        session = new SessionManager(getApplicationContext());
        rv.setHasFixedSize (true);
        rv.setLayoutManager (new LinearLayoutManager (this));
        listDataSpvList = new ArrayList<> ();
        adapter = new RecyclerSpv (listDataSpvList);


        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        nik = String.valueOf (devId.get(SessionManager.KEY_NIK));

        getData(nik);

        swipeRefreshLayout.setOnRefreshListener (() ->{
            listDataSpvList.clear ();
            getData(nik);
            adapter.notifyDataSetChanged ();
            new Handler ().postDelayed (() -> swipeRefreshLayout.setRefreshing (false),1000);
        });
    }

    private void getData(String nik) {
        String url = "http://"+IPADDR+"/"+NMSERVER+"/AttendanceAPI/"+"spvmenu.php";
        ProgressDialog pd = new ProgressDialog(SpvMenu.this);
        pd.setMessage("Generating Data . . .");
        pd.show();
        StringRequest strReq = new StringRequest (Request.Method.POST,url, response -> {
            try{
                JSONObject jObj = new JSONObject (response);
                Log.d (TAG, String.valueOf (jObj));
                success = jObj.getInt ("success");
                JSONArray array = jObj.getJSONArray ("array");
                if(jObj.getJSONArray ("array").length () == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(SpvMenu.this).create();
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

                            ListDataSpv listData = new ListDataSpv (ob.getString ("pengajuan"),
                                    ob.getString ("pengaju"),
                                    ob.getString ("dateAwal"),
                                    ob.getString ("dateAkhir"),
                                    ob.getString ("alasan"),
                                    ob.getString ("id_status"),
                                    ob.getString ("ID"));
                            listDataSpvList.add (listData);
                        }
                        rv.setAdapter (adapter);
                    }else{
                        Toast.makeText (getApplicationContext (),"Data cuti/ijin belum ada",Toast.LENGTH_LONG).show ();
                    }
                }
            } catch (JSONException e) {
                pd.dismiss ();
                e.printStackTrace ();
                Log.e(TAG,"Error :"+ nik);
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