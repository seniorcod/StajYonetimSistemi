package org.example.Model;

import java.sql.Date; // Tarih için gerekli

public class Basvuru {
    private String basvuruId;
    private String ogrenciAdSoyad; // ID yerine artık ismini tutacağız
    private String sirketAd;       // ID yerine şirket ismini tutacağız
    private String pozisyon;
    private String durum;          // "Onaylandı", "Beklemede" vb.
    private Date baslangicTarihi;

    public Basvuru(String basvuruId, String ogrenciAdSoyad, String sirketAd, String pozisyon, String durum, Date baslangicTarihi) {
        this.basvuruId = basvuruId;
        this.ogrenciAdSoyad = ogrenciAdSoyad;
        this.sirketAd = sirketAd;
        this.pozisyon = pozisyon;
        this.durum = durum;
        this.baslangicTarihi = baslangicTarihi;
    }

    // Getter Metodları (Sadece okuma yapacağız)
    public String getBasvuruId() { return basvuruId; }
    public String getOgrenciAdSoyad() { return ogrenciAdSoyad; }
    public String getSirketAd() { return sirketAd; }
    public String getPozisyon() { return pozisyon; }
    public String getDurum() { return durum; }
    public Date getBaslangicTarihi() { return baslangicTarihi; }

    @Override
    public String toString() {
        return basvuruId + " | " + ogrenciAdSoyad + " -> " + sirketAd + " (" + durum + ")";
    }
}