package org.example.DataAccessLayer;

import org.example.Model.Danisman; // <-- YENİ MODELİMİZİ IMPORT ETTİK
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DanismanDAO {

    // Tüm Danışmanları Getir (Öğrenci Seçsin Diye)
    public List<Danisman> tumDanismanlariGetir() {
        List<Danisman> liste = new ArrayList<>();
        String sql = "SELECT danismanid, ad, soyad FROM public.danismanlar";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Danisman(
                        rs.getString("danismanid"),
                        rs.getString("ad") + " " + rs.getString("soyad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}