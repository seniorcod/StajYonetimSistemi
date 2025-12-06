package org.example.DataAccessLayer;

import java.sql.*;
import java.util.UUID; // Rastgele ID üretmek için

public class StajDAO {

    public boolean stajBaslat(String basvuruId, String yetkiliId) {
        Connection conn = null;
        PreparedStatement psOku = null;
        PreparedStatement psEkle = null;
        PreparedStatement psGuncelle = null;
        ResultSet rs = null;

        try {
            conn = DbHelper.getConnection();

            // 1. KRİTİK ADIM: Otomatik kaydetmeyi kapat (Transaction Başlangıcı)
            conn.setAutoCommit(false);

            // A. Başvuru Bilgilerini Çek
            String sqlOku = "SELECT * FROM public.basvuru WHERE basvuruid = ?";
            psOku = conn.prepareStatement(sqlOku);
            psOku.setString(1, basvuruId);
            rs = psOku.executeQuery();

            if (!rs.next()) {
                System.out.println("Hata: Başvuru bulunamadı!");
                return false;
            }

            // Bilgileri alıyoruz
            String ogrenciId = rs.getString("ogrenciid");
            String sirketId = rs.getString("sirketid");
            String danismanId = rs.getString("danismanid");
            Date planlananBaslangic = rs.getDate("planlananbaslangic");

            // B. Staj Tablosuna Ekle (Insert)
            // StajID'yi otomatik üretiyoruz (Örn: STJ + Rastgele Sayılar)
            String yeniStajId = "STJ" + (int)(Math.random() * 100000);

            String sqlEkle = "INSERT INTO public.staj (stajid, basvuruid, ogrenciid, sirketid, danismanid, yetkiliid, gercekbaslangic, durum) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            psEkle = conn.prepareStatement(sqlEkle);
            psEkle.setString(1, yeniStajId);
            psEkle.setString(2, basvuruId);
            psEkle.setString(3, ogrenciId);
            psEkle.setString(4, sirketId);
            psEkle.setString(5, danismanId);
            psEkle.setString(6, yetkiliId); // Bunu dışarıdan aldık
            psEkle.setDate(7, planlananBaslangic); // Başvuru tarihini baz aldık
            psEkle.setString(8, "Aktif");

            psEkle.executeUpdate();

            // C. Başvuru Durumunu Güncelle (Update)
            String sqlGuncelle = "UPDATE public.basvuru SET durum = 'Stajda' WHERE basvuruid = ?";
            psGuncelle = conn.prepareStatement(sqlGuncelle);
            psGuncelle.setString(1, basvuruId);
            psGuncelle.executeUpdate();

            // 2. KRİTİK ADIM: Her şey yolunda gittiyse onayla (Commit)
            conn.commit();
            System.out.println("✅ Transaction Başarılı: Staj kaydı oluşturuldu (" + yeniStajId + ")");
            return true;

        } catch (SQLException e) {
            // HATA VARSA: Her şeyi geri al (Rollback)
            try {
                if (conn != null) conn.rollback();
                System.out.println("⛔ HATA OLUŞTU! İşlemler geri alındı (Rollback).");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Kaynakları kapat (Standart temizlik)
            try {
                if (rs != null) rs.close();
                if (psOku != null) psOku.close();
                if (psEkle != null) psEkle.close();
                if (psGuncelle != null) psGuncelle.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // Öğrencinin aktif stajının ID'sini getirir (Rapor yüklemek için lazım)
    public String getAktifStajId(String ogrenciId) {
        String sql = "SELECT stajid FROM public.staj WHERE ogrenciid = ? AND durum = 'Aktif'"; // veya 'Devam Ediyor'

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ogrenciId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("stajid"); // Örn: STJ12345
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Aktif stajı yoksa null döner
    }
}
