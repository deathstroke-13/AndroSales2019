package ricky.hastaprimasolusi.newandrosales;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private TextView textView;
    private Button btn_save_setting;
    private EditText et_ip,et_folder;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        session = new SessionManager(getApplicationContext());


        textView = findViewById(R.id.text_footer);

        String version = "1.52";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        textView.setText("By PT. Hasta Prima Solusi - v." + version);

        et_ip 	            = findViewById(R.id.et_ip);
        et_folder 	    	= findViewById(R.id.et_folder);
        btn_save_setting    = findViewById(R.id.btn_save_setting);


        HashMap<String, String> settingData = session.getSettingDetails();
        String dataAddr 	= settingData.get(SessionManager.KEY_IP);
        String dataServer 	= settingData.get(SessionManager.KEY_SERVER);
        if(dataAddr != null){
            et_ip.setText(dataAddr);
            et_folder.setText(String.valueOf(dataServer));

            String ipAddress = et_ip.getText().toString();
            String nmServer  = et_folder.getText().toString();
            session.createSettingSession(ipAddress,nmServer);

        }else{
            //10.0.2.2
            String dataIP	  = "36.94.17.163";
            String dataServ   = "androsales_service_dev";
            et_ip.setText(dataIP);
            et_folder.setText(dataServ);
            session.createSettingSession(dataIP,dataServ);

        }

        btn_save_setting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String ipAddress = et_ip.getText().toString();
                String nmServer  = et_folder.getText().toString();
                session.createSettingSession(ipAddress,nmServer);

                Toast.makeText(getApplicationContext(), "Berhasil disimpan.", Toast.LENGTH_SHORT).show();
                Intent inLogin2 = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(inLogin2);
                finish();
            }
        });


    }
}

