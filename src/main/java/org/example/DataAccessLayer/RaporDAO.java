package org.example.DataAccessLayer;

import org.example.Model.Rapor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaporDAO {

    // Belirli bir staja ait raporları getir
    public List<Rapor> stajRaporlariniGetir(String stajId) {
        List<Rapor> liste = new ArrayList<>();
        String sql = "SELECT * FROM public.raporlar WHERE stajid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stajId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                liste.add(new Rapor(
                        rs.getString("raporid"),
                        rs.getString("raportipi"),
                        rs.getString("dosyayolu"),
                        rs.getString("onaydurumu"),
                        rs.getDate("yuklemetarih")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Yeni Rapor Ekle
    public boolean raporEkle(String stajId, String raporTipi, String dosyaYolu) {
        String sql = "INSERT INTO public.raporlar (raporid, stajid, raportipi, dosyayolu, yuklemetarih, onaydurumu) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String yeniId = "RAP" + (int)(Math.random() * 10000);

            ps.setString(1, yeniId);
            ps.setString(2, stajId);
            ps.setString(3, raporTipi);
            ps.setString(4, dosyaYolu);
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis())); // Bugün
            ps.setString(6, "Bekliyor");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Danışman raporu onaylar veya reddeder
    public boolean raporDurumGuncelle(String raporId, String yeniDurum) {
        String sql = "UPDATE public.raporlar SET onaydurumu = ? WHERE raporid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, yeniDurum); // Örn: "Onaylandı" veya "Düzeltme İstendi"
            ps.setString(2, raporId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
