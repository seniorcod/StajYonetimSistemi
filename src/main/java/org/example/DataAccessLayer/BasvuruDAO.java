package org.example.DataAccessLayer;

import org.example.Model.Basvuru;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasvuruDAO {

    // Tüm Başvuruları Listele
    public List<Basvuru> tumBasvurulariGetir() {
        List<Basvuru> liste = new ArrayList<>();

        // SQL JOIN SORGUSU
        String sql = "SELECT " +
                "b.basvuruid, " +
                "o.ad || ' ' || o.soyad as ogrenci_tam_ad, " +
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
                // DÜZELTME BURADA: .trim() ekledik
                // Veritabanından "BAS001      " gelirse "BAS001" yapacak.
                String id = rs.getString("basvuruid") != null ? rs.getString("basvuruid").trim() : "";
                String ogrenciAd = rs.getString("ogrenci_tam_ad");
                String sirketAd = rs.getString("sirketad");
                String pozisyon = rs.getString("pozisyon");

                // Durum sütunu CHAR ise onda da boşluk olabilir, trimleyelim
                String durum = rs.getString("durum") != null ? rs.getString("durum").trim() : "";

                Date tarih = rs.getDate("planlananbaslangic");

                liste.add(new Basvuru(id, ogrenciAd, sirketAd, pozisyon, durum, tarih));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Başvuru Durumunu Güncelle
    public boolean basvuruDurumGuncelle(String basvuruId, String yeniDurum) {
        String sql = "UPDATE public.basvuru SET durum = ? WHERE basvuruid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, yeniDurum);

            // DÜZELTME: Gelen ID'de boşluk varsa bile temizleyip gönderelim
            ps.setString(2, basvuruId.trim());

            int etkilenenSatir = ps.executeUpdate();
            return etkilenenSatir > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Yeni Başvuru Ekleme Metodu
    // 1. GÜNCELLENMİŞ KAYIT METODU (Danışman ID parametresi eklendi)
    public boolean basvuruEkle(String ogrenciId, String sirketId, String danismanId, String pozisyon, String baslangic, String bitis) {
        // SQL'e 'danismanid' eklendi
        String sql = "INSERT INTO public.basvuru (basvuruid, ogrenciid, sirketid, danismanid, pozisyon, planlananbaslangic, planlananbitis, durum, basvurutarihi) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String yeniId = "BAS" + (int)(Math.random() * 100000);

            ps.setString(1, yeniId);
            ps.setString(2, ogrenciId);
            ps.setString(3, sirketId);
            ps.setString(4, danismanId); // <-- YENİ EKLENEN KISIM
            ps.setString(5, pozisyon);
            ps.setDate(6, java.sql.Date.valueOf(baslangic));
            ps.setDate(7, java.sql.Date.valueOf(bitis));
            ps.setString(8, "Beklemede");
            ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Şirket Paneli İçin: Sadece o şirkete gelen başvuruları getir
    public List<Basvuru> sirketBasvurulariniGetir(String sirketId) {
        List<Basvuru> liste = new ArrayList<>();
        String sql = "SELECT b.basvuruid, o.ad || ' ' || o.soyad as ogrenci_tam_ad, s.sirketad, b.pozisyon, b.durum, b.planlananbaslangic " +
                "FROM public.basvuru b " +
                "JOIN public.ogrenciler o ON b.ogrenciid = o.ogrenciid " +
                "JOIN public.sirketler s ON b.sirketid = s.sirketid " +
                "WHERE b.sirketid = ?"; // <-- KRİTİK FİLTRE

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sirketId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // (Buradaki kod tumBasvurulariGetir ile aynı, kopyala-yapıştır yapabilirsin)
                String id = rs.getString("basvuruid") != null ? rs.getString("basvuruid").trim() : "";
                String ogrenciAd = rs.getString("ogrenci_tam_ad");
                String sirketAd = rs.getString("sirketad");
                String pozisyon = rs.getString("pozisyon");
                String durum = rs.getString("durum") != null ? rs.getString("durum").trim() : "";
                Date tarih = rs.getDate("planlananbaslangic");
                liste.add(new Basvuru(id, ogrenciAd, sirketAd, pozisyon, durum, tarih));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return liste;
    }
    // 2. YENİ METOD: Sadece belirli bir danışmana ait başvuruları getir
    public List<org.example.Model.Basvuru> getBasvurularByDanisman(String danismanId) {
        List<org.example.Model.Basvuru> liste = new ArrayList<>();
        // WHERE şartına dikkat: b.danismanid = ?
        String sql = "SELECT b.basvuruid, o.ad || ' ' || o.soyad as ogrenci_tam_ad, s.sirketad, b.pozisyon, b.durum, b.planlananbaslangic " +
                "FROM public.basvuru b " +
                "JOIN public.ogrenciler o ON b.ogrenciid = o.ogrenciid " +
                "JOIN public.sirketler s ON b.sirketid = s.sirketid " +
                "WHERE b.danismanid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, danismanId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Verileri listeye ekleme (Standart kod)
                String id = rs.getString("basvuruid");
                String ogrAd = rs.getString("ogrenci_tam_ad");
                String sirket = rs.getString("sirketad");
                String poz = rs.getString("pozisyon");
                String dur = rs.getString("durum");
                Date tar = rs.getDate("planlananbaslangic");
                liste.add(new org.example.Model.Basvuru(id, ogrAd, sirket, poz, dur, tar));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return liste;
    }
}