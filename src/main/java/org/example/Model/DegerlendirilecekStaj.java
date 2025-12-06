package org.example.Model;

public class DegerlendirilecekStaj {
    private String stajId;
    private String ogrenciAdSoyad;
    private String sirketAd;

    public DegerlendirilecekStaj(String stajId, String ogrenciAdSoyad, String sirketAd) {
        this.stajId = stajId;
        this.ogrenciAdSoyad = ogrenciAdSoyad;
        this.sirketAd = sirketAd;
    }

    public String getStajId() { return stajId; }

    // ComboBox ekranda bunu gösterecek
    @Override
    public String toString() {
        return ogrenciAdSoyad + " (" + sirketAd + ")";
    }
}