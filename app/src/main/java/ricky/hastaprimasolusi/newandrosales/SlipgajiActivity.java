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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;


import butterknife.ButterKnife;
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

    String fuck;

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

        URL = "http://"+IPADDR+"/"+NMSERVER+"/";


        ActivityCompat.requestPermissions(SlipgajiActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        spin1 = findViewById(R.id.spinner);
        spin2 = findViewById(R.id.spinner2);
        String[] bulan = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        String[] tahun = {"2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"};

        ArrayAdapter<CharSequence> adapterBulan = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, bulan);
        adapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapterBulan);

        ArrayAdapter<CharSequence> adapterTahun = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,tahun);
        adapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapterTahun);

        selectedMonth = spin1.getSelectedItem().toString();
        selectedYear = spin2.getSelectedItem().toString();


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void createPDF(View view){
        if(selectedMonth == "Januari"){
            month = 1;
        }else if(selectedMonth =="Februari"){
            month = 2;
        }else if(selectedMonth == "Maret"){
            month = 3;
        }else if(selectedMonth == "April"){
            month = 4;
        }else if(selectedMonth == "Mei"){
            month = 5;
        }else if(selectedMonth == "Juni"){
            month = 6;
        }else if(selectedMonth == "Juli"){
            month = 7;
        }else if(selectedMonth == "Agustus"){
            month = 8;
        }else if(selectedMonth == "September"){
            month = 9;
        }else if(selectedMonth == "Oktober"){
            month = 10;
        }else if(selectedMonth == "November"){
            month = 11;
        }else{
            month = 12;
        }

        progress = ProgressDialog.show(SlipgajiActivity.this,"Proses Slip","Please Wait",false,false);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.slipgaji(month, selectedYear, kodeImei);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                call.cancel();
            }
        });

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
            myPage.getCanvas().drawText(": 5481151545",x+110,y+40,myPaint);
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
}
