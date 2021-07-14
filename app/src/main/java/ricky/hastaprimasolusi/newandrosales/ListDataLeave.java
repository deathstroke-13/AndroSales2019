package ricky.hastaprimasolusi.newandrosales;

public class ListDataLeave {

    private final String pengajuan;
    private final String atasan;
    private final String dateAwal;
    private final String dateAkhir;
    private final String status;
    private final String alasan;

    public ListDataLeave(String pengajuan, String atasan, String dateAwal,String dateAkhir, String alasan, String status){

        this.pengajuan = pengajuan;
        this.atasan = atasan;
        this.dateAwal = dateAwal;
        this.dateAkhir = dateAkhir;
        this.alasan = alasan;
        this.status = status;
    }

    public String getPengajuan() {
        String jenis = null;
        switch (pengajuan) {
            case "1":
                jenis = "Cuti";
                break;
            case "2":
                jenis = "Ijin";
                break;
            case "3":
                jenis = "Sakit";
                break;
            case "4":
                jenis = "Lembur";
                break;
            case "5":
                jenis = "Cuti Khusus";
                break;
            case "6":
                jenis = "Perjalanan Dinas";
                break;
        }

        return jenis;
    }

    public String getAtasan(){
        return atasan;
    }

    public String getDateAwal(){
        return dateAwal;
    }

    public String getDateAkhir() {
        return dateAkhir;
    }

    public String getAlasan(){
        return alasan;
    }

    public String getStatus(){
        String value;
        switch (status) {
            case "1":
                value = "Open";
                break;
            case "2":
                value = "Declined";
                break;
            case "3":
                value = "Approved";
                break;
            default:
                value = "nowhere";
                break;
        }

        return value;
    }
}