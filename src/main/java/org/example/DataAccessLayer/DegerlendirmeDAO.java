package org.example.DataAccessLayer;

import org.example.Model.DegerlendirilecekStaj;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DegerlendirmeDAO {

    // Danışmanın sorumlu olduğu stajları getirir
    public List<DegerlendirilecekStaj> getDanismanStajlari(String danismanId) {
        List<DegerlendirilecekStaj> liste = new ArrayList<>();

        // SQL: Staj, Öğrenci ve Şirket tablolarını birleştir
        // Sadece bu danışmana ait olanları getir
        String sql = "SELECT s.stajid, o.ad, o.soyad, sir.sirketad " +
                "FROM public.staj s " +
                "JOIN public.ogrenciler o ON s.ogrenciid = o.ogrenciid " +
                "JOIN public.sirketler sir ON s.sirketid = sir.sirketid " +
                "WHERE s.danismanid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, danismanId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String adSoyad = rs.getString("ad") + " " + rs.getString("soyad");
                liste.add(new DegerlendirilecekStaj(
                        rs.getString("stajid"),
                        adSoyad,
                        rs.getString("sirketad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Notu Veritabanına Kaydet
    public boolean degerlendirmeEkle(String stajId, String danismanId, int puan, String yorum) {
        String sql = "INSERT INTO public.degerlendirmeler (degerlendirmeid, stajid, danismanid, puan, yorum, tarih) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Rastgele ID (DEG + Sayı)
            String yeniId = "DEG" + (int)(Math.random() * 10000);

            ps.setString(1, yeniId);
            ps.setString(2, stajId);
            ps.setString(3, danismanId);
            ps.setBigDecimal(4, new java.math.BigDecimal(puan)); // Numeric alan
            ps.setString(5, yorum);
            ps.setDate(6, new java.sql.Date(System.currentTimeMillis())); // Bugün

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
