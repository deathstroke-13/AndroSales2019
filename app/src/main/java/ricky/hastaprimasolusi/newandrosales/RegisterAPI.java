package ricky.hastaprimasolusi.newandrosales;

import android.renderscript.Sampler;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RegisterAPI {
    //odometer, namaoutlet, ket, LocationLong, LocationLat, nilaiAbsen

    @FormUrlEncoded
    @POST("API/absensi.php")
    Call<Value> absensi(@Field("odometer") String odometer,
                       @Field("namaoutlet") String namaoutlet,
                       @Field("ket") String ket,
                       @Field("LocationLong") String LocationLong,
                       @Field("LocationLat") String LocationLat,
                       @Field("nilaiAbsen") String nilaiAbsen,
                       @Field("kodeImei") String kodeImei,
                       @Field("image_data") String img_new_name,
                        @Field("fake_status") String fake_status);


    @FormUrlEncoded
    @POST("API/kunjungan.php")
    Call<Value> kunjungan(@Field("namaoutlet") String namaoutlet,
                        @Field("ket") String ket,
                        @Field("LocationLong") String LocationLong,
                        @Field("LocationLat") String LocationLat,
                        @Field("kodeImei") String kodeImei,
                        @Field("image_data") String img_new_name,
                        @Field("fake_status")String fake_status);

    @FormUrlEncoded
    @POST("API/odometer.php")
    Call<Value> odometer(@Field("namaoutlet") String namaoutlet,
                          @Field("ket") String ket,
                          @Field("LocationLong") String LocationLong,
                          @Field("LocationLat") String LocationLat,
                          @Field("kodeImei") String kodeImei,
                          @Field("image_data") String img_new_name,
                          @Field("fake_status")String fake_status,
                          @Field ("saran")String saran);

    @FormUrlEncoded
    @POST("API/penjualan.php")
    Call<Value> penjualan(@Field("namaoutlet") String namaoutlet,
                          @Field("ket") String ket,
                          @Field("LocationLong") String LocationLong,
                          @Field("LocationLat") String LocationLat,
                          @Field("kodeImei") String kodeImei,
                          @Field("image_data") String img_new_name,
                          @Field("NoTransaksi") String NoTransaksi,
                          @Field("fake_status") String fake_status);

    @FormUrlEncoded
    @POST("API/tambah_penjualan.php")
    Call<Value> tambah_penjualan(@Field("qty_produk") String qty_produk,
                          @Field("id_produk") String id_produk,
                          @Field("harga_prd_satuan") String harga_prd_satuan,
                          @Field("etNoTransaksi") String etNoTransaksi);

    @FormUrlEncoded
    @POST("API/hapus_penjualan.php")
    Call<Value> batal_transaksi(@Field("etNoTransaksi") String etNoTransaksi);

    @FormUrlEncoded
    @POST("API/biayaBBM.php")
    Call<Value> biayaBBM(@Field("kodeImei") String kodeImei);

    @GET("API/produk.php")
    Call<Value> produk();

    @GET("API/list_laporan.php")
    Call<Value> filter_laporan_coba();

    @FormUrlEncoded
    @POST("API/search_produk.php")
    Call<Value> search(@Field("search") String search);

    @FormUrlEncoded
    @POST("API/list_laporan.php")
    Call<Value> filter_laporan(@Field("imei") String imei,
                       @Field("dateAwal") String dateAwal);

    @FormUrlEncoded
    @POST("API/filter_laporan.php")
    Call<Value> filter(@Field("imei") String imei,
                       @Field("dateAwal") String dateAwal);

    @FormUrlEncoded
    @POST("API/slipgaji.php")
    Call<Value> slipgaji(@Field("month") int month,
                         @Field("selectedYear") String selectedYear,
                         @Field("NIK") String NIK);
/*
    @GET("view.php")
    Call<Value> view();

    @FormUrlEncoded
    @POST("update.php")
    Call<Value> ubah(@Field("npm") String npm,
                     @Field("nama") String nama,
                     @Field("kelas") String kelas,
                     @Field("sesi") String sesi);


    @FormUrlEncoded
    @POST("delete.php")
    Call<Value> hapus(@Field("npm") String npm);

    @FormUrlEncoded
    @POST("search.php")
    Call<Value> search(@Field("search") String search);
*/
}
