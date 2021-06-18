package ricky.hastaprimasolusi.newandrosales;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SessionManager session;

    public String URL, URL_CATEGORIES ;
    private static final int REQUEST_LOCATION = 1;

    String kodeImei,IPADDR,NMSERVER;

    Button btPenjualan, btAbsen, btKunjungan, btbiaya_bbm, bt_laporan, bt_slipgaji, bt_cuti, bt_approval, bt_bbm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull (getSupportActionBar ()).setElevation(0);

        myAlarmF();
        myAlarmL();

        btPenjualan = findViewById(R.id.bt_penjualan);
        btAbsen = findViewById(R.id.bt_absen);
        btKunjungan = findViewById(R.id.bt_kunjungan);
        btbiaya_bbm = findViewById(R.id.bt_biaya_bbm);
        bt_laporan = findViewById(R.id.bt_laporan);
        bt_slipgaji = findViewById(R.id.bt_slipGaji);
        bt_cuti = findViewById (R.id.bt_takeLeave);
        bt_approval = findViewById (R.id.bt_Approval);
        bt_bbm = findViewById (R.id.bt_BBM);


        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";

        //footer text
        TextView textView = findViewById (R.id.langTextView);
        String version = "1.51";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        textView.setText("By PT. Hasta Prima Solusi - v." + version);
        bt_slipgaji.setOnClickListener(arg0 -> {
            Intent intent = new Intent(MainActivity.this, SlipgajiActivity.class);
            startActivity(intent);
        });

        btAbsen.setOnClickListener(arg0 -> {
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
        });

        btbiaya_bbm.setOnClickListener(arg0 -> {
            // Open StandardWebView.class
            Intent intent = new Intent(MainActivity.this,
                    BiayaBBMActivity.class);

            //Add the bundle to the intent
            startActivity(intent);
        });

        bt_bbm.setOnClickListener (v -> {
            Intent intent = new Intent (MainActivity.this, BBMActivity.class);
            startActivity (intent);
        });

        btKunjungan.setOnClickListener(arg0 -> {
            // Open StandardWebView.class
            Intent intent = new Intent(MainActivity.this,
                    KunjunganActivity.class);

            //Add the bundle to the intent
            startActivity(intent);
        });

        btPenjualan.setOnClickListener(arg0 -> {
            // Open StandardWebView.class
            Intent intent = new Intent(MainActivity.this,
                    PenjualanActivity.class);

            //Add the bundle to the intent
            startActivity(intent);
        });

        bt_laporan.setOnClickListener(arg0 -> {
            // Open StandardWebView.class
            Intent intent = new Intent(MainActivity.this,
                    LaporanListActivity.class);

            //Add the bundle to the intent
            startActivity(intent);
        });

        bt_cuti.setOnClickListener (v -> {
            Intent intent = new Intent (MainActivity.this, LeaveActivity.class);
            startActivity (intent);
        });

        bt_approval.setOnClickListener (v -> {
            Intent intent = new Intent (MainActivity.this, SpvMenu.class);
            startActivity (intent);
        });



    }

    public void myAlarmF() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date ()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), AbsensiActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

    }

    public void myAlarmL(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date ()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), AbsensiActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_profile){

            Intent intent = new Intent (MainActivity.this, ProfileActivity.class);
            startActivity (intent);

        } else if(id == R.id.action_changePassword){
            Intent changePassword = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity (changePassword);
        }
        else if (id == R.id.action_about) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
