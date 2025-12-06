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

    // Notu Kaydet ve Stajı "Tamamlandı" Yap
    // Notu Kaydet ve Stajı "Tamamlandı" Yap
    public boolean degerlendirmeEkle(String stajId, String danismanId, int puan, String yorum) {
        Connection conn = null;
        try {
            conn = DbHelper.getConnection();
            conn.setAutoCommit(false); // Transaction Başlat

            // 1. Notu Ekle
            String sqlNot = "INSERT INTO public.degerlendirmeler (degerlendirmeid, stajid, danismanid, puan, yorum, tarih) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psNot = conn.prepareStatement(sqlNot);

            // ... (Parametre set etme kısımları aynı) ...
            psNot.setString(1, "DEG" + (int)(Math.random() * 10000));
            psNot.setString(2, stajId);
            psNot.setString(3, danismanId);
            psNot.setBigDecimal(4, new java.math.BigDecimal(puan));
            psNot.setString(5, yorum);
            psNot.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            psNot.executeUpdate();

            // 2. Staj Durumunu 'Tamamlandı' Yap
            String sqlStaj = "UPDATE public.staj SET durum = 'Tamamlandı' WHERE stajid = ?";
            PreparedStatement psStaj = conn.prepareStatement(sqlStaj);
            psStaj.setString(1, stajId);
            psStaj.executeUpdate();

            // 3. Başvuru Durumunu da 'Tamamlandı' Yap (LİSTEDEN GİTMESİ İÇİN BU ŞART)
            String sqlBasvuru = "UPDATE public.basvuru SET durum = 'Tamamlandı' WHERE basvuruid = (SELECT basvuruid FROM public.staj WHERE stajid = ?)";
            PreparedStatement psBasvuru = conn.prepareStatement(sqlBasvuru);
            psBasvuru.setString(1, stajId);
            psBasvuru.executeUpdate();

            conn.commit(); // Hepsini onayla
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    // Staj ID'sine göre not ve yorumu getirir
    public String[] getDegerlendirmeSonucu(String stajId) {
        String sql = "SELECT puan, yorum FROM public.degerlendirmeler WHERE stajid = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stajId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("puan")),
                        rs.getString("yorum")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
