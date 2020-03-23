package ricky.hastaprimasolusi.newandrosales;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


import butterknife.ButterKnife;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SlipgajiActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    SessionManager session;
    Spinner spin1, spin2;
    Button btn_cetak;
    String selectedMonth, selectedYear;
    int month, year;
    String kodeImei,IPADDR,NMSERVER;
    public String URL;


    private List<Result> results = new ArrayList<>();
    String NIK;
    TextView txt1, txt2, txt3;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slipgaji);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> identifyServer = session.getSettingDetails();
        IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        HashMap<String, String> devId = session.getUserDetails();
        kodeImei = devId.get(SessionManager.KEY_IMEI);
        NIK = devId.get(SessionManager.KEY_NIK);

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);




        ActivityCompat.requestPermissions(SlipgajiActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        spin1 = findViewById(R.id.spinner);
        spin2 = findViewById(R.id.spinner2);
        String[] bulan = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        String[] tahun = {"2019","2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"};

        ArrayAdapter<CharSequence> adapterBulan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, bulan);
        adapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapterBulan);

        ArrayAdapter<CharSequence> adapterTahun = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,tahun);
        adapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapterTahun);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void createPDF(View view){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        selectedMonth = spin1.getSelectedItem().toString();
        selectedYear = spin2.getSelectedItem().toString();
        if(selectedMonth.equals("Januari")){
            month = 1;
        }else if(selectedMonth.equals("Februari")){
            month = 2;
        }else if(selectedMonth.equals("Maret")){
            month = 3;
        }else if(selectedMonth.equals("April")){
            month = 4;
        }else if(selectedMonth.equals("Mei")){
            month = 5;
        }else if(selectedMonth.equals("Juni")){
            month = 6;
        }else if(selectedMonth.equals("Juli")){
            month = 7;
        }else if(selectedMonth.equals("Agustus")){
            month = 8;
        }else if(selectedMonth.equals("September")){
            month = 9;
        }else if(selectedMonth.equals("Oktober")){
            month = 10;
        }else if(selectedMonth.equals("November")){
            month = 11;
        }else{
            month = 12;
        }

        txt1.setText(NIK);
        txt2.setText(String.valueOf(month));
        txt3.setText(selectedYear);
        progress = ProgressDialog.show(SlipgajiActivity.this,"Proses Slip","Please Wait",false,false);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.slipgaji(month, selectedYear, NIK);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                if (value.equals("1")) {
                    results = response.body().getResult();
                    Toast.makeText(SlipgajiActivity.this, value, Toast.LENGTH_LONG).show();
                    addtoPdf();
                    progress.dismiss();

                } else {
                    Toast.makeText(SlipgajiActivity.this, message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                t.getMessage();
                //addtoPdf();
                call.cancel();
                progress.dismiss();
            }
        });



    }

    private void addtoPdf() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PdfDocument pdfDoc = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(800,400,1).create();
            PdfDocument.Page myPage = pdfDoc.startPage(myPageInfo);


            Paint myPaint = new Paint();
            int x = 10, y = 25;
            //Text Komponen
            myPage.getCanvas().drawText("SLIP GAJI",x,y,myPaint);
            myPage.getCanvas().drawText("NIK",x,y+40,myPaint);
            myPage.getCanvas().drawText("Nama",x,y+60,myPaint);
            myPage.getCanvas().drawText("Jabatan",x,y+80,myPaint);
            myPage.getCanvas().drawLine(10,120,790,120,myPaint);
            myPage.getCanvas().drawText("Jumlah Hari Kerja",x,y+120,myPaint);
            myPage.getCanvas().drawText("Jumlah Hari libur",x,y+140,myPaint);
            myPage.getCanvas().drawText("Jumlah Kehadiran",x,y+160,myPaint);
            myPage.getCanvas().drawText("Jumlah Alpha",x,y+180,myPaint);
            myPage.getCanvas().drawText("Jumlah Cuti",x,y+200,myPaint);
            myPage.getCanvas().drawText("Sisa Cuti",x,y+220,myPaint);

            myPage.getCanvas().drawText("Gaji Pokok",x+150,y+120,myPaint);
            myPage.getCanvas().drawText("Premi Hadir", x+150, y+140,myPaint);
            myPage.getCanvas().drawText("Uang Jabatan", x+150, y+160,myPaint);
            myPage.getCanvas().drawText("Uang Makan", x+150, y+180,myPaint);
            myPage.getCanvas().drawText("Uang Transport", x+150, y+200,myPaint);
            myPage.getCanvas().drawText("Kuota", x+150, y+220,myPaint);
            myPage.getCanvas().drawText("Uang Lembur", x+150, y+240,myPaint);
            myPage.getCanvas().drawText("Uang telephone", x+150, y+260,myPaint);
            myPage.getCanvas().drawText("Insentif", x+150, y+280,myPaint);

            myPage.getCanvas().drawText("BPJS",x+350,y+120,myPaint);
            myPage.getCanvas().drawText("Operasional",x+350,y+140,myPaint);
            myPage.getCanvas().drawText("Uang Sewa",x+350,y+160,myPaint);
            myPage.getCanvas().drawText("Uang Kemahalan",x+350,y+180,myPaint);
            myPage.getCanvas().drawText("JKN 4%",x+350,y+200,myPaint);
            myPage.getCanvas().drawText("Gaji Bruto",x+350,y+220,myPaint);
            myPage.getCanvas().drawText("Total Penghasilan",x+350,y+240,myPaint);
            myPage.getCanvas().drawText("Biaya Jabatan",x+350,y+260,myPaint);
            myPage.getCanvas().drawText("BPJK KS",x+350,y+280,myPaint);

            myPage.getCanvas().drawText("Gaji Netto",x+550,y+120,myPaint);
            myPage.getCanvas().drawText("PPH 21",x+550,y+140,myPaint);
            myPage.getCanvas().drawText("BPJS Perusahaan",x+550,y+160,myPaint);
            myPage.getCanvas().drawText("Potongan JHT",x+550,y+180,myPaint);
            myPage.getCanvas().drawText("Potongan Kes",x+550,y+200,myPaint);
            myPage.getCanvas().drawText("Potongan Koperasi",x+550,y+220,myPaint);
            myPage.getCanvas().drawText("Potongan Pinjaman",x+550,y+240,myPaint);

            myPage.getCanvas().drawText("Gaji Diterima",x+550,y+280,myPaint);

            //Isi Komponen
            myPage.getCanvas().drawText(": ",x+110,y+40,myPaint);
            myPage.getCanvas().drawText(": Testing Nama",x+110,y+60,myPaint);
            myPage.getCanvas().drawText(": Programmer",x+110,y+80,myPaint);

            myPage.getCanvas().drawText(": 27",x+110,y+120,myPaint);
            myPage.getCanvas().drawText(": 2",x+110,y+140,myPaint);
            myPage.getCanvas().drawText(": 24",x+110,y+160,myPaint);
            myPage.getCanvas().drawText(": 0",x+110,y+180,myPaint);
            myPage.getCanvas().drawText(": 8",x+110,y+200,myPaint);
            myPage.getCanvas().drawText(": 8",x+110,y+220,myPaint);
            myPage.getCanvas().drawLine(150,130,150,320,myPaint);

            myPage.getCanvas().drawText(": 16.055.000",x+250,y+120,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+140,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+160,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+180,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+200,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+220,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+240,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+260,myPaint);
            myPage.getCanvas().drawText(": 6.055.000",x+250,y+280,myPaint);
            myPage.getCanvas().drawLine(340,130,340,320,myPaint);

            myPage.getCanvas().drawText(": 100.000",x+450,y+120,myPaint);
            myPage.getCanvas().drawText(": 0",x+450,y+140,myPaint);
            myPage.getCanvas().drawText(": 0",x+450,y+160,myPaint);
            myPage.getCanvas().drawText(": 0",x+450,y+180,myPaint);
            myPage.getCanvas().drawText(": 80.000",x+450,y+200,myPaint);
            myPage.getCanvas().drawText(": 9.570.000",x+450,y+220,myPaint);
            myPage.getCanvas().drawText(": 9.500.000",x+450,y+240,myPaint);
            myPage.getCanvas().drawText(": 35.000",x+450,y+260,myPaint);
            myPage.getCanvas().drawText(": 35.0000.",x+450,y+280,myPaint);
            myPage.getCanvas().drawLine(540,130,540,320,myPaint);

            myPage.getCanvas().drawText(": 9.063.000",x+680,y+120,myPaint);
            myPage.getCanvas().drawText(": 0",x+680,y+140,myPaint);
            myPage.getCanvas().drawText(": 67.755",x+680,y+160,myPaint);
            myPage.getCanvas().drawText(": 35.755",x+680,y+180,myPaint);
            myPage.getCanvas().drawText(": 50.000",x+680,y+200,myPaint);
            myPage.getCanvas().drawText(": 0",x+680,y+220,myPaint);
            myPage.getCanvas().drawText(": 0",x+680,y+240,myPaint);
            myPage.getCanvas().drawLine(550,y+260,750,y+260,myPaint);
            myPage.getCanvas().drawText(": 9.277.000",x+680,y+280,myPaint);


            pdfDoc.finishPage(myPage);

            String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/SlipGaji"+selectedMonth+selectedYear+".pdf";
            File myFile  = new File(myFilePath);


            try{
                pdfDoc.writeTo(new FileOutputStream(myFile));
                Toast.makeText(this, "Check the file in your directory path", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Error on getting PDF", Toast.LENGTH_SHORT).show();
            }

            pdfDoc.close();
        }else{
            Toast.makeText(this, "Cant print pdf because lack of API", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

}
