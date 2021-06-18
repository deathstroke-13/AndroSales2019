package ricky.hastaprimasolusi.newandrosales;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class LaporanActivity extends AppCompatActivity {

    SessionManager session;

    public static String URL, URL_IMG;

    public static String kodeImei, IPADDR, NMSERVER;

    public static String TGL;

    private ProgressDialog progress;

    public String noTrans;

    ListView listView;

    Button bt_cari_lap;
    Intent intent;

    EditText txtdateAwal;

    class Spacecraft {
        /*
        INSTANCE FIELDS
        */
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("km")
        private String km;
        @SerializedName("propellant")
        private String propellant;
        @SerializedName("imageurl")
        private String imageURL;
        @SerializedName("technologyexists")
        private int technologyExists;
        public Spacecraft(int id, String name, String propellant, String imageURL, String km, int technologyExists) {
            this.id = id;
            this.name = name;
            this.propellant = propellant;
            this.imageURL = imageURL;
            this.km = km;
            this.technologyExists = technologyExists;
        }
        /*
         *GETTERS AND SETTERS
         */
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPropellant() {
            return propellant;
        }
        public String getKm() {
            return km;
        }
        public String getImageURL() {
            return URL_IMG+imageURL;
        }
        public int getTechnologyExists() {
            return technologyExists;
        }
        /*
        TOSTRING
        */
        @Override
        public String toString() {
            return name;
        }
    }

    static class RetrofitClientInstance {

        public String newString;

        private static Retrofit retrofit;
        private static final String BASE_URL = URL+"API/list_laporan.php/"+kodeImei+"/"+TGL+"/";
        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d (LaporanActivity.class.getSimpleName (),"Base url : "+ BASE_URL);
            }
            return retrofit;
        }
    }
    class ListViewAdapter extends BaseAdapter {
        private List<Spacecraft> spacecrafts;
        private Context context;
        public ListViewAdapter(Context context,List<Spacecraft> spacecrafts){
            this.context = context;
            this.spacecrafts = spacecrafts;
        }
        @Override
        public int getCount() {
            return spacecrafts.size();
        }
        @Override
        public Object getItem(int pos) {
            return spacecrafts.get(pos);
        }
        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if(view==null)
            {
                view= LayoutInflater.from(context).inflate(R.layout.list_view_laporan,viewGroup,false);
            }
            TextView nameTxt = view.findViewById(R.id.nameTextView);
            TextView txtPropellant = view.findViewById(R.id.propellantTextView);
            TextView kmTextView = view.findViewById(R.id.kmTextView);
            ImageView spacecraftImageView = view.findViewById(R.id.spacecraftImageView);
            final Spacecraft thisSpacecraft= spacecrafts.get(position);
            nameTxt.setText(thisSpacecraft.getName());
            txtPropellant.setText(thisSpacecraft.getPropellant());
            kmTextView.setText(thisSpacecraft.getKm());
            if(thisSpacecraft.getImageURL() != null && thisSpacecraft.getImageURL().length()>0)
            {
                Picasso.get().load(thisSpacecraft.getImageURL()).placeholder(R.drawable.ic_photo_camera_black).into(spacecraftImageView);
            }else {
                Toast.makeText(context, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.ic_photo_camera_black).into(spacecraftImageView);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, thisSpacecraft.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }
    }
    private ListViewAdapter adapter;
    private ListView mListView;
    ProgressBar myProgressBar;
    private void populateListView(List<Spacecraft> spacecraftList) {
        mListView = findViewById(R.id.mListViewS);
        adapter = new ListViewAdapter(this,spacecraftList);
        mListView.setAdapter(adapter);
    }

    public interface MyAPIService {
        String TGLas = "ada";

        //getActivity().getIntent().getExtras().getString("image")
        //Intent intent = getExtras();

//        String npm = intent.getStringExtra("npm");
//        String kelas = intent.getStringExtra("kelas");

        //EditText mEditText = (EditText) new Activity();
//        final String TGLs = txtdateAwals.getText().toString();
        //                txtdateAwal = findViewById(R.id.txtdateAwal);
        @GET("RIKOYKASEP")  Call<List<Spacecraft>> getSpacecrafts();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        getSupportActionBar().setElevation(0);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR = String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER = String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        URL = "http://" + IPADDR + "/" + NMSERVER + "/";

        URL_IMG = "http://"+IPADDR+"/androsales2015/service/photos/";
        final String URL_IMGs = "http://"+IPADDR+"/androsales2015/service/photos/";

        EditText txtDateAwal= findViewById(R.id.txtdateAwal);

        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);

        bt_cari_lap = findViewById(R.id.button_cari_lap);


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



        bt_cari_lap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                txtdateAwal = findViewById(R.id.txtdateAwal);
                final String TGLs = txtdateAwal.getText().toString();

                TGL = TGLs;
                //Toast.makeText(LaporanActivity.this, TGL, Toast.LENGTH_LONG).show();

                myProgressBar.setIndeterminate(true);
                myProgressBar.setVisibility(View.VISIBLE);
                /*Create handle for the RetrofitInstance interface*/



                MyAPIService myAPIService = RetrofitClientInstance.getRetrofitInstance().create(MyAPIService.class);
                Call<List<Spacecraft>> call = myAPIService.getSpacecrafts();
                call.enqueue(new Callback<List<Spacecraft>>() {
                    @Override
                    public void onResponse(Call<List<Spacecraft>> call, Response<List<Spacecraft>> response) {
                        myProgressBar.setVisibility(View.GONE);
                        populateListView(response.body());
                    }
                    @Override
                    public void onFailure(Call<List<Spacecraft>> call, Throwable throwable) {
                        myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(LaporanActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });



    }


    //menu di action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public String senTgl(String tgl){
        tgl = TGL;
        return tgl;
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
    //end menu di action bar
}
