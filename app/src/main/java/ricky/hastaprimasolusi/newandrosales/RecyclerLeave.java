package ricky.hastaprimasolusi.newandrosales;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerLeave extends RecyclerView.Adapter<RecyclerLeave.ViewHolder> {

    final private List<ListDataLeave> listDataLeave;

    public RecyclerLeave(List<ListDataLeave> listDataLeave){
        this.listDataLeave = listDataLeave;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext ()).inflate (R.layout.rv_list_leave,parent,false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListDataLeave listData = listDataLeave.get (position);
        holder.txtpengajuan.setText (listData.getPengajuan ());
        holder.txtatasan.setText (listData.getAtasan ());
        holder.txttanggalawal.setText (listData.getDateAwal ());
        holder.txttanggalakhir.setText (listData.getDateAkhir ());
        holder.txtalasan.setText (listData.getAlasan ());
        holder.txtstatus.setText (listData.getStatus ());
        if(listData.getStatus ().equals ("Approved")){
            holder.txtstatus.setTextColor (Color.BLUE);
        }else if (listData.getStatus ().equals ("Open")){
            holder.txtstatus.setTextColor (Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return listDataLeave.size ();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final private TextView txtpengajuan, txtatasan, txttanggalawal, txttanggalakhir, txtalasan, txtstatus;
        public ViewHolder(View itemView){
            super(itemView);
            txtpengajuan = itemView.findViewById (R.id.txtPengajuan);
            txtatasan = itemView.findViewById (R.id.txtAtasan);
            txttanggalawal = itemView.findViewById (R.id.txtTanggalawal);
            txttanggalakhir = itemView.findViewById (R.id.txtTanggalakhir);
            txtalasan = itemView.findViewById (R.id.txtAlasan);
            txtstatus = itemView.findViewById (R.id.txtStatus);
        }
    }
}