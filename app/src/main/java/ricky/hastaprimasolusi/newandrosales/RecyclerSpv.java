package ricky.hastaprimasolusi.newandrosales;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ricky.hastaprimasolusi.newandrosales.app.AppController;

public class RecyclerSpv extends RecyclerView.Adapter<RecyclerSpv.ViewHolder> {

    final private List<ListDataSpv> listDataSpv;

    public  RecyclerSpv(List<ListDataSpv> listDataSpv){ this.listDataSpv = listDataSpv;}



    @NonNull
    @Override
    public RecyclerSpv.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext ()).inflate (R.layout.rv_spv_menu,parent,false);
        return new RecyclerSpv.ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerSpv.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext ();
        ListDataSpv listData = listDataSpv.get (position);
        holder.txtpengajuan.setText (listData.getPengajuan ());
        switch (listData.getPengajuan ()) {
            case "1":
                holder.txtpengajuan.setText ("Cuti");
                break;
            case "2":
                holder.txtpengajuan.setText ("Ijin");
                break;
            case "3":
                holder.txtpengajuan.setText ("Sakit");
                break;
            case "4":
                holder.txtpengajuan.setText ("Lembur");
                break;
            case "5":
                holder.txtpengajuan.setText ("Cuti Khusus");
                break;
            default:
                holder.txtpengajuan.setText ("Perjalanan Dinas");
                break;
        }
        holder.txtpengaju.setText (listData.getPengaju ());
        holder.txttglawal.setText (listData.getDateAwal ());
        holder.txttglakhir.setText (listData.getDateAkhir ());
        holder.txtalasan.setText (listData.getAlasan ());
        holder.txtstatus.setText (listData.getStatus ());
        holder.txtID.setText (listData.getID ());
        switch (listData.getStatus ()) {
            case "1":
                holder.txtstatus.setText ("Open");
                holder.txtstatus.setTextColor (Color.BLACK);
                break;
            case "2":
                holder.txtstatus.setText ("Decline");
                break;
            case "3":
                holder.txtstatus.setText ("Approved");
                holder.txtstatus.setTextColor (Color.BLUE);
                break;
        }
        holder.btnAccept.setOnClickListener (v -> {

            String idRv = listDataSpv.get (position).getID ();
            Log.d (String.valueOf (context),"Accepted clicked!");
            Log.d (String.valueOf (context),"Accepted clicked!:"+idRv);

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder (context);
            materialAlertDialogBuilder.setTitle ("Approval Confirmation");
            materialAlertDialogBuilder.setMessage ("Approve this leave?");
            materialAlertDialogBuilder.setNegativeButton ("No", (dialog, which) -> dialog.cancel ());
            materialAlertDialogBuilder.setPositiveButton ("Yes", (dialog, which) -> {
                String url = "http://36.94.17.163/androsales_service/AttendanceAPI/"+"acceptdeclinespv.php";
                ProgressDialog progressDialog = new ProgressDialog (context);
                progressDialog.setTitle ("Processing");
                progressDialog.setMessage ("Please Wait. . .");
                progressDialog.setCancelable (true);
                progressDialog.show ();

                StringRequest strReq = new StringRequest (Request.Method.POST, url, response ->{
                    Log.d("id_leave :",idRv);
                    try{

                        JSONObject jObj = new JSONObject (response);
                        int success = jObj.getInt ("success");
                        Log.d("String Request :",response);

                        progressDialog.dismiss ();
                        if(success == 1){
                            Log.d("Success",jObj.toString ());
                        }
                        Toast.makeText (context, jObj.getString("message"),Toast.LENGTH_LONG).show ();
                    } catch (Exception e) {
                        progressDialog.dismiss ();
                        e.printStackTrace ();
                        Log.d ("Error :", String.valueOf (e));
                    }
                },error -> {
                    progressDialog.dismiss ();
                    Log.e(String.valueOf (context), "Error: " + error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                } ){
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<> ();
                        params.put ("id", idRv);
                        params.put ("type","1");
                        return params;
                    }
                };
                AppController.getInstance ().addToRequestQueue (strReq, "json_obj_req");

                notifyDataSetChanged ();
            }).show ();
        });

        holder.btnDecline.setOnClickListener (v -> {
            String idRv = listDataSpv.get (position).getID ();
            Log.d (String.valueOf (context),"Accepted clicked!");
            Log.d (String.valueOf (context),"Accepted clicked!:"+idRv);

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder (context);
            materialAlertDialogBuilder.setTitle ("Decline Confirmation");
            materialAlertDialogBuilder.setMessage ("Decline this leave?");
            materialAlertDialogBuilder.setNegativeButton ("No", (dialog, which) -> dialog.cancel ());
            materialAlertDialogBuilder.setPositiveButton ("Yes", (dialog, which) -> {
                String url = "http://36.94.17.163/androsales_service/AttendanceAPI/"+"acceptdeclinespv.php";
                ProgressDialog progressDialog = new ProgressDialog (context);
                progressDialog.setTitle ("Processing");
                progressDialog.setMessage ("Please Wait. . .");
                progressDialog.setCancelable (true);
                progressDialog.show ();

                StringRequest strReq = new StringRequest (Request.Method.POST, url, response ->{
                    Log.d("id_leave :",idRv);
                    try{

                        JSONObject jObj = new JSONObject (response);
                        int success = jObj.getInt ("success");
                        Log.d("String Request :",response);

                        progressDialog.dismiss ();
                        if(success == 1){
                            Log.d("Success",jObj.toString ());
                        }
                        Toast.makeText (context, jObj.getString("message"),Toast.LENGTH_LONG).show ();
                    } catch (Exception e) {
                        progressDialog.dismiss ();
                        e.printStackTrace ();
                        Log.d ("Error :", String.valueOf (e));
                    }
                },error -> {
                    progressDialog.dismiss ();
                    Log.e(String.valueOf (context), "Error: " + error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                } ){
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<> ();
                        params.put ("id", idRv);
                        params.put ("type","2");
                        return params;
                    }
                };
                AppController.getInstance ().addToRequestQueue (strReq, "json_obj_req");

                notifyDataSetChanged ();
            }).show ();
        });
    }

    @Override
    public int getItemCount() {
        return listDataSpv.size ();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final private TextView txtpengajuan, txtpengaju, txttglawal, txttglakhir, txtalasan, txtstatus,txtID;
        final private Button btnAccept, btnDecline;

        public ViewHolder(View itemView){
            super(itemView);
            txtpengajuan = itemView.findViewById (R.id.txtPengajuanSpv);
            txtpengaju = itemView.findViewById (R.id.txtPengajuSpv);
            txttglawal = itemView.findViewById (R.id.txtTanggalawalSpv);
            txttglakhir = itemView.findViewById (R.id.txtTanggalakhirSpv);
            txtalasan = itemView.findViewById (R.id.txtAlasanSpv);
            txtstatus = itemView.findViewById (R.id.txtStatusSpv);
            txtID = itemView.findViewById (R.id.txtID);
            btnAccept = itemView.findViewById (R.id.btnAccept);
            btnDecline= itemView.findViewById (R.id.btnDecline);

        }
    }

}
