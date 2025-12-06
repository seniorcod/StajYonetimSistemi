package org.example.DataAccessLayer;

import java.sql.*;

public class IstatistikDAO {

    // Toplam Başvuru Sayısı
    public int getToplamBasvuruSayisi() {
        return getCount("SELECT COUNT(*) FROM public.basvuru");
    }

    // Onaylanmış (Stajda veya Biten) Sayısı
    public int getOnayliStajSayisi() {
        return getCount("SELECT COUNT(*) FROM public.basvuru WHERE durum IN ('Onaylandı', 'Stajda', 'Tamamlandı')");
    }

    // En Çok Başvuru Alan Şirket
    public String getEnPopulerSirket() {
        String sql = "SELECT s.sirketad FROM public.basvuru b " +
                "JOIN public.sirketler s ON b.sirketid = s.sirketid " +
                "GROUP BY s.sirketad " +
                "ORDER BY COUNT(*) DESC LIMIT 1";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "Veri Yok";
    }

    // Aktif Devam Eden Staj Sayısı
    public int getAktifStajSayisi() {
        return getCount("SELECT COUNT(*) FROM public.staj WHERE durum = 'Aktif'");
    }

    // Yardımcı Metod (Sürekli try-catch yazmamak için)
    private int getCount(String sql) {
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}