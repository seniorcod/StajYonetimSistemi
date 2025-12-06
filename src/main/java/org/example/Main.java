package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.DataAccessLayer.StajDAO;

public class Main {
    public static void main(String[] args) {
        // Tema kurulumu (Hata vermemesi için try-catch içinde)
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StajDAO stajDAO = new StajDAO();

        System.out.println("--- STAJ BAŞLATMA TESTİ ---");

        // SENARYO:
        // Danışman (DAN001), BAS002 nolu başvuruyu onaylıyor.

        String basvuruId = "BAS002";
        String yetkiliId = "YET002";
        String danismanId = "DAN001"; // <-- YENİ EKLENEN (Murat Şahin)

        // ARTIK 3 PARAMETRE GÖNDERİYORUZ:
        boolean sonuc = stajDAO.stajBaslat(basvuruId, yetkiliId, danismanId);

        if (sonuc) {
            System.out.println("🎉 Tebrikler! Veli Kaya'nın stajı resmen başladı.");
        } else {
            System.out.println("❌ İşlem başarısız oldu (Belki zaten stajdadır veya ID yanlıştır).");
        }
    }
}