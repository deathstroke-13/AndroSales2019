package ricky.hastaprimasolusi.newandrosales;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LaporanDetailActivity extends AppCompatActivity {

    SessionManager session;

    String kodeImei,IPADDR,NMSERVER,datauser;

    String URL, id_lap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id_lap= null;
            } else {
                id_lap= extras.getString("id_lap");
            }
        } else {
            id_lap= (String) savedInstanceState.getSerializable("id_lap");
        }

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);
        datauser = devId.get(SessionManager.KEY_NAME);
        //kodeImei    = String.valueOf(identifyServer.get(SessionManager.KEY_IMEI));

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";

        final ProgressDialog pd = ProgressDialog.show(LaporanDetailActivity.this, "", "Loading...", true);

        WebView webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(LaporanDetailActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                pd.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();

                String webUrl = webView.getUrl();

            }

        });

        webView.loadUrl(URL+"laporan/index.php?imei="+kodeImei+"&id="+id_lap);


    }
}
