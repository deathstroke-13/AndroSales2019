package ricky.hastaprimasolusi.newandrosales;

public class ListDataSpv {
    private final String pengajuan;
    private final String dateAwal;
    private final String pengaju;
    private final String dateAkhir;
    private final String status;
    private final String alasan;
    private final String ID;

    public ListDataSpv(String pengajuan,  String pengaju, String dateAwal,String dateAkhir,String alasan, String status, String ID) {
        this.pengajuan = pengajuan;
        this.dateAwal = dateAwal;
        this.pengaju = pengaju;
        this.dateAkhir = dateAkhir;
        this.alasan = alasan;
        this.status = status;
        this.ID = ID;

    }

    public String getPengajuan() {
        return pengajuan;
    }

    public String getDateAwal() {
        return dateAwal;
    }

    public String getPengaju() {
        return pengaju;
    }

    public String getDateAkhir() {
        return dateAkhir;
    }

    public String getAlasan() {
        return alasan;
    }

    public String getStatus() {
        return status;
    }

    public String getID(){ return ID; }
}
