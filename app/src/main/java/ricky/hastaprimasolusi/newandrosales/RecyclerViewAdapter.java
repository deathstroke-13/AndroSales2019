package ricky.hastaprimasolusi.newandrosales;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

/**
 * Created by sulistiyanto on 07/12/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Result> results;

    SessionManager session;

    //
    EditText txt_id, txt_qty, txt_no_transaksi, txt_harga;
    String id_produk, qty_produk, etNoTransaksi, etHarga;
    TextView nama_prd;

    Button bt_save, bt_cancel;

    private Activity activity;

    private ProgressDialog progress;


    public RecyclerViewAdapter(Context context, List<Result> results) {
        this.context = context;
        this.results = results;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Result result = results.get(position);
        holder.textViewNamaProduk.setText(result.get_nama_item()+" - "+result.get_nama_produk());
        holder.textViewSatuanProduk.setText(result.get_satuan_produk());
        holder.textViewHargaProduk.setText(result.get_harga_produk());
        holder.textViewIdProduk.setText(result.get_id_produk());
        holder.textViewHargaSatuan.setText(result.get_harga_produk_satuan());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.text_nama_produk) TextView textViewNamaProduk;
        @BindView(R.id.text_satuan_produk) TextView textViewSatuanProduk;
        @BindView(R.id.text_harga_produk) TextView textViewHargaProduk;
        @BindView(R.id.text_id_produk) TextView textViewIdProduk;
        @BindView(R.id.text_harga_satuan) TextView textViewHargaSatuan;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String nama_produk = textViewNamaProduk.getText().toString();
            String satuan_produk = textViewSatuanProduk.getText().toString();
            String harga_produk = textViewHargaProduk.getText().toString();
            String id_produk = textViewIdProduk.getText().toString();
            String harga_prd_satuan = textViewHargaSatuan.getText().toString();

            //String npm = i.getStringExtra("npm");

            //masuk ke halaman update ketika di klik
            /*
            Intent i = new Intent(context, FilterActivity.class);
            i.putExtra("npm", npm);
            i.putExtra("nama", nama);
            i.putExtra("kelas", kelas);
            i.putExtra("sesi", sesi);
            context.startActivity(i);
            */

            //pass the 'context' here

            session = new SessionManager(context);

            HashMap<String, String> identifyServer = session.getSettingDetails();
            String IPADDR = String.valueOf(identifyServer.get(SessionManager.KEY_IP));
            String NMSERVER = String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

            HashMap<String, String> devId = session.getUserDetails();
            String kodeImei = devId.get(SessionManager.KEY_IMEI);

            String URL = "http://" + IPADDR + "/" + NMSERVER + "/";

            Intent intent = ((Activity) context).getIntent();
            String NoTransaksi = intent.getStringExtra("NoTransaksi");

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.form_input_qty_prd);

            txt_id  = dialog.findViewById(R.id.txt_id);
            txt_qty = dialog.findViewById(R.id.txt_qty);
            txt_no_transaksi = dialog.findViewById(R.id.txt_no_transaksi);
            bt_save     = dialog.findViewById(R.id.btn_save);
            nama_prd    = dialog.findViewById(R.id.label_nama_produk);

            txt_id.setText(id_produk);

            txt_no_transaksi.setText(NoTransaksi);
            nama_prd.setText(nama_produk);
            txt_qty.setFocusable(true);


            dialog.setTitle("Form Qty");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            //setDataToView(name,address,icon,position);
            dialog.show();

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);

            bt_save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //progress = ProgressDialog.show(this,"Proses Penjualan","Please Wait",false,false);

                    qty_produk      = txt_qty.getText().toString();
                    etNoTransaksi   = txt_no_transaksi.getText().toString();


                    progress = new ProgressDialog(context);
                    progress.setCancelable(false);
                    progress.setMessage("Loading ...");
                    progress.show();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<Value> call = api.tambah_penjualan(qty_produk, id_produk, harga_prd_satuan, etNoTransaksi);
                    call.enqueue(new Callback<Value>() {
                        @Override
                        public void onResponse(Call<Value> call, Response<Value> response) {
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            progress.dismiss();
                            if (value.equals("1")) {
                                dialog.cancel();
                                progress.dismiss();
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            } else {
                                dialog.cancel();
                                progress.dismiss();
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Value> call, Throwable t) {
                            t.printStackTrace();
                            progress.dismiss();
                            dialog.cancel();
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            });

        }


    }
}
