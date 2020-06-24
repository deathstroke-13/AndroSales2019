package ricky.hastaprimasolusi.newandrosales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewAdapterLaporan extends RecyclerView.Adapter<RecyclerViewAdapterLaporan.ViewHolder>{
    private Context context;
    private List<Result> results;
    RequestOptions option;

    SessionManager session;


    public RecyclerViewAdapterLaporan(Context context, List<Result> results) {
        this.context = context;
        this.results = results;

        // Request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.ic_photo_camera_black).error(R.drawable.ic_photo_camera_black);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_laporan_new, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        session = new SessionManager(context);

        HashMap<String, String> identifyServer = session.getSettingDetails();
        String IPADDR 		= String.valueOf(identifyServer.get(SessionManager.KEY_IP));
        String NMSERVER 	= String.valueOf(identifyServer.get(SessionManager.KEY_SERVER));

        String IMG_URL = "http://"+IPADDR+"/";

        Result result = results.get(position);
        holder.namaOutlet.setText(result.getLoket_name());
        holder.kunjungan.setText(result.getSaran());
        holder.tanggal.setText(result.getTanggal());
        holder.jam.setText(result.getJam());
        holder.km.setText(result.getKm());
        holder.lat.setText(result.getLatKunjungan());
        holder.longi.setText(result.getLonKunjungan());
        holder.gambar.setText(IMG_URL+result.getGambar());
        holder.idKujungan.setText(result.getIdKunjungan());

        Glide.with(context).load(IMG_URL+result.getGambar()).apply(option).into(holder.img_thumbnail);

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.txtNamaOutlet) TextView namaOutlet;
        @BindView(R.id.txtReportKUnjungan) TextView kunjungan;
        @BindView(R.id.txtTanggal) TextView tanggal;
        @BindView(R.id.txtJam) TextView jam;
        @BindView(R.id.txtKm) TextView km;
        @BindView(R.id.txtLat) TextView lat;
        @BindView(R.id.txtLongi) TextView longi;
        @BindView(R.id.txtGambar) TextView gambar;
        @BindView(R.id.thumbnail) ImageView img_thumbnail;
        @BindView(R.id.txtIdKujungan) TextView idKujungan;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String longs = longi.getText().toString();
            String lati = lat.getText().toString();
            String nm_ot = namaOutlet.getText().toString();
            String id = idKujungan.getText().toString();

            //Toast.makeText(((Activity) context), id, Toast.LENGTH_SHORT).show();


            /*
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://maps.google.com/?q="+lati+","+longs));
            */

            Intent i = new Intent(context, LaporanDetailActivity.class);

            //Create the bundle
            Bundle bundle = new Bundle();

            //Add your data to bundle
            bundle.putString("id_lap", id);

            //Add the bundle to the intent
            i.putExtras(bundle);

            //Fire that second activity
            context.startActivity(i);


            /*
            //String npm = txtLatitude.getText().toString();
            Intent i = new Intent(context, FilterActivity.class);
            context.startActivity(i);
            */
            //https://maps.google.com/?q=<lat>,<lng>

        }
    }
}
