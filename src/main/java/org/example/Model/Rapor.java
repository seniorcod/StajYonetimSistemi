package org.example.Model;

import java.sql.Date;

public class Rapor {
    private String raporId;
    private String raporTipi; // Ara Rapor, Final Raporu
    private String dosyaYolu;
    private String onayDurumu;
    private Date yuklemeTarihi;

    public Rapor(String raporId, String raporTipi, String dosyaYolu, String onayDurumu, Date yuklemeTarihi) {
        this.raporId = raporId;
        this.raporTipi = raporTipi;
        this.dosyaYolu = dosyaYolu;
        this.onayDurumu = onayDurumu;
        this.yuklemeTarihi = yuklemeTarihi;
    }

    // Getterlar
    public String getRaporId() { return raporId; }
    public String getRaporTipi() { return raporTipi; }
    public String getDosyaYolu() { return dosyaYolu; }
    public String getOnayDurumu() { return onayDurumu; }
    public Date getYuklemeTarihi() { return yuklemeTarihi; }
}
