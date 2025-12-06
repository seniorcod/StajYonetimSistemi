package org.example.DataAccessLayer;

import org.example.Model.Kullanici;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    // Ana Giriş Metodu
    public Kullanici girisYap(String email, String password) {
        Kullanici kullanici = null;

        // 1. Öğrenci mi? (Tablo ve sütun isimleri senin SQL dosyanla birebir uyumlu)
        kullanici = kontrolEt("ogrenciler", "ogrenciid", "OGRENCI", email, password);
        if (kullanici != null) return kullanici;

        // 2. Danışman mı?
        kullanici = kontrolEt("danismanlar", "danismanid", "DANISMAN", email, password);
        if (kullanici != null) return kullanici;

        // 3. Şirket Yetkilisi mi?
        kullanici = kontrolEt("sirketyetkilileri", "yetkiliid", "SIRKET", email, password);

        // YENİ KISIM: Eğer giren şirket yetkilisiyse, hangi şirkete bağlı olduğunu bulalım
        if (kullanici != null && kullanici.getRol().equals("SIRKET")) {
            String sirketId = getSirketIdByYetkili(kullanici.getId());
            kullanici.setBagliSirketId(sirketId);
        }
        return kullanici;
    }
    // YENİ METOD: Yetkili ID'den Şirket ID bulur
    private String getSirketIdByYetkili(String yetkiliId) {
        String sql = "SELECT sirketid FROM public.sirketyetkilileri WHERE yetkiliid = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yetkiliId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("sirketid");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Yardımcı Metod (Kod tekrarını önlemek için)
    private Kullanici kontrolEt(String tablo, String idKolonu, String rol, String email, String password) {
        // SQL dosyanızda sütun adı "eposta" ve "ad", "soyad" olarak geçiyor.
        String query = "SELECT * FROM public." + tablo + " WHERE eposta = ? AND sifre = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString(idKolonu);
                    String ad = rs.getString("ad");
                    String soyad = rs.getString("soyad");
                    return new Kullanici(id, ad + " " + soyad, rol, email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}