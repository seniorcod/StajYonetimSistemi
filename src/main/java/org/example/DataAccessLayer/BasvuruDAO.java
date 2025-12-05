package org.example.DataAccessLayer;

import org.example.Model.Basvuru;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasvuruDAO {

    // Tüm Başvuruları Listele (Danışman Ekranı İçin)
    public List<Basvuru> tumBasvurulariGetir() {
        List<Basvuru> liste = new ArrayList<>();

        // SQL JOIN SORGUSU:
        // Basvuru tablosunu, Ogrenciler ve Sirketler tablosuyla birleştiriyoruz.
        String sql = "SELECT " +
                "b.basvuruid, " +
                "o.ad || ' ' || o.soyad as ogrenci_tam_ad, " + // Ad ve Soyadı birleştir
                "s.sirketad, " +
                "b.pozisyon, " +
                "b.durum, " +
                "b.planlananbaslangic " +
                "FROM public.basvuru b " +
                "JOIN public.ogrenciler o ON b.ogrenciid = o.ogrenciid " +
                "JOIN public.sirketler s ON b.sirketid = s.sirketid";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Basvuru(
                        rs.getString("basvuruid"),
                        rs.getString("ogrenci_tam_ad"),
                        rs.getString("sirketad"),
                        rs.getString("pozisyon"),
                        rs.getString("durum"),
                        rs.getDate("planlananbaslangic")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}