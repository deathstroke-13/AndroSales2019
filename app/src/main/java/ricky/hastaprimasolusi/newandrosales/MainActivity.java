package ricky.hastaprimasolusi.newandrosales;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    SessionManager session;

    public String URL, URL_CATEGORIES ;
    private static final int REQUEST_LOCATION = 1;

    String kodeImei,IPADDR,NMSERVER;

    private TextView textView;
    Button btPenjualan, btAbsen, btKunjungan, btbiaya_bbm, bt_laporan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        btPenjualan = (Button) findViewById(R.id.bt_penjualan);
        btAbsen = (Button) findViewById(R.id.bt_absen);
        btKunjungan = (Button) findViewById(R.id.bt_kunjungan);
        btbiaya_bbm = (Button) findViewById(R.id.bt_biaya_bbm);
        bt_laporan = (Button) findViewById(R.id.bt_laporan);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";

        //footer text
        textView = findViewById(R.id.langTextView);
        String version = "1.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        textView.setText("By PT. Hasta Prima Solusi - v." + version);

        btAbsen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Open StandardWebView.class
                Intent intent = new Intent(MainActivity.this,
                        AbsensiActivity.class);
                /*
                //Create the bundle
                Bundle bundle = new Bundle();

                //Add your data to bundle
                bundle.putString("imei", kodeImei);
                bundle.putString("ip", IPADDR);
                bundle.putString("path", NMSERVER);
                */
                //Add the bundle to the intent
                startActivity(intent);
            }
        });

        btbiaya_bbm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Open StandardWebView.class
                Intent intent = new Intent(MainActivity.this,
                        BiayaBBMActivity.class);

                //Add the bundle to the intent
                startActivity(intent);
            }
        });

        btKunjungan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Open StandardWebView.class
                Intent intent = new Intent(MainActivity.this,
                        KunjunganActivity.class);

                //Add the bundle to the intent
                startActivity(intent);
            }
        });

        btPenjualan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Open StandardWebView.class
                Intent intent = new Intent(MainActivity.this,
                        PenjualanActivity.class);

                //Add the bundle to the intent
                startActivity(intent);
            }
        });

        bt_laporan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Open StandardWebView.class
                Intent intent = new Intent(MainActivity.this,
                        LaporanListActivity.class);

                //Add the bundle to the intent
                startActivity(intent);
            }
        });


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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), "PT. Hast Prima Solusi", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
