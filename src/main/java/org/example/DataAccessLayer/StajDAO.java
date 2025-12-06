package org.example.DataAccessLayer;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date; // Tarih kullanıyoruz
import java.util.UUID; // Rastgele ID üretmek için

public class StajDAO {

    // GÜNCELLENMİŞ METOD: Artık danismanId parametresi alıyor
    public boolean stajBaslat(String basvuruId, String yetkiliId, String danismanId) {
        Connection conn = null;
        PreparedStatement psOku = null;
        PreparedStatement psEkle = null;
        PreparedStatement psGuncelle = null;
        ResultSet rs = null;

        try {
            conn = DbHelper.getConnection();
            conn.setAutoCommit(false); // Transaction Başlat

            // A. Başvuru Bilgilerini Çek
            String sqlOku = "SELECT * FROM public.basvuru WHERE basvuruid = ?";
            psOku = conn.prepareStatement(sqlOku);
            psOku.setString(1, basvuruId);
            rs = psOku.executeQuery();

            if (!rs.next()) {
                return false;
            }

            String ogrenciId = rs.getString("ogrenciid");
            String sirketId = rs.getString("sirketid");
            // Not: rs.getString("danismanid") NULL gelebilir, o yüzden parametre olarak gelen 'danismanId'yi kullanacağız.
            Date planlananBaslangic = rs.getDate("planlananbaslangic");

            // B. Staj Tablosuna Ekle
            String yeniStajId = "STJ" + (int)(Math.random() * 100000);

            String sqlEkle = "INSERT INTO public.staj (stajid, basvuruid, ogrenciid, sirketid, danismanid, yetkiliid, gercekbaslangic, durum) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            psEkle = conn.prepareStatement(sqlEkle);
            psEkle.setString(1, yeniStajId);
            psEkle.setString(2, basvuruId);
            psEkle.setString(3, ogrenciId);
            psEkle.setString(4, sirketId);
            psEkle.setString(5, danismanId); // <-- DÜZELTME BURADA: Giriş yapan hocanın ID'sini yazıyoruz
            psEkle.setString(6, yetkiliId);
            psEkle.setDate(7, planlananBaslangic);
            psEkle.setString(8, "Aktif");

            psEkle.executeUpdate();

            // C. Başvuru Durumunu Güncelle (Ve Danışmanı da işle)
            // Başvuru tablosundaki danışman ID'sini de güncelleyelim ki kayıt tutarlı olsun
            String sqlGuncelle = "UPDATE public.basvuru SET durum = 'Stajda', danismanid = ? WHERE basvuruid = ?";
            psGuncelle = conn.prepareStatement(sqlGuncelle);
            psGuncelle.setString(1, danismanId); // Başvuruya da imzamızı atıyoruz
            psGuncelle.setString(2, basvuruId);
            psGuncelle.executeUpdate();

            conn.commit(); // Onayla
            System.out.println("✅ Transaction Başarılı: Staj kaydı oluşturuldu (" + yeniStajId + ")");
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
                System.out.println("⛔ HATA OLUŞTU! İşlemler geri alındı (Rollback).");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
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
    // Başvuru ID'sinden Staj ID'sini bulur
    public String getStajIdByBasvuru(String basvuruId) {
        String sql = "SELECT stajid FROM public.staj WHERE basvuruid = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, basvuruId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("stajid");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    // Başvurunun yapıldığı şirketten rastgele bir yetkili ID'si bulur
    public String otomatikYetkiliBul(String basvuruId) {
        // Bu SQL sorgusu: Başvuru -> Şirket -> Şirket Yetkilisi zincirini kurar ve 1 kişi getirir.
        String sql = "SELECT y.yetkiliid FROM public.sirketyetkilileri y " +
                "JOIN public.basvuru b ON y.sirketid = b.sirketid " +
                "WHERE b.basvuruid = ? LIMIT 1";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, basvuruId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("yetkiliid"); // Örn: YET001
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Eğer şirketin hiç yetkilisi yoksa
    }
    // Öğrencinin stajlarını listeler
    public List<org.example.Model.Basvuru> getOgrenciStajlari(String ogrenciId) {
        List<org.example.Model.Basvuru> liste = new ArrayList<>();
        // Staj tablosundan verileri çekiyoruz (Başvuru modelini kullanabiliriz verileri taşımak için)
        String sql = "SELECT s.stajid, sir.sirketad, b.pozisyon, s.gercekbaslangic, s.durum " +
                "FROM public.staj s " +
                "JOIN public.sirketler sir ON s.sirketid = sir.sirketid " +
                "JOIN public.basvuru b ON s.basvuruid = b.basvuruid " +
                "WHERE s.ogrenciid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ogrenciId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Verileri Basvuru objesine paketleyip gönderiyoruz (Modeli yeniden kullanıyoruz)
                // BasvuruId yerine StajId koyuyoruz ki arayüzde onu kullanalım
                String stajId = rs.getString("stajid");
                String sirket = rs.getString("sirketad");
                String pozisyon = rs.getString("pozisyon");
                Date tarih = rs.getDate("gercekbaslangic");
                String durum = rs.getString("durum");

                // Basvuru constructor: (id, ogrenciAd, sirketAd, pozisyon, durum, tarih)
                // Burada öğrenci adı yerine boş string geçiyoruz, çünkü zaten öğrenci kendi ekranında.
                liste.add(new org.example.Model.Basvuru(stajId, "", sirket, pozisyon, durum, tarih));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return liste;
    }

    // KRİTİK METOD: Stajı Bitir ve Rapor Yükle (Transaction)
    public boolean stajiBitirVeRaporla(String stajId, String dosyaYolu) {
        Connection conn = null;
        try {
            conn = DbHelper.getConnection();
            conn.setAutoCommit(false); // Transaction Başlat

            // 1. Raporu Ekle
            String sqlRapor = "INSERT INTO public.raporlar (raporid, stajid, raportipi, dosyayolu, yuklemetarih, onaydurumu) " +
                    "VALUES (?, ?, 'Final Raporu', ?, ?, 'Bekliyor')";
            PreparedStatement psRapor = conn.prepareStatement(sqlRapor);
            psRapor.setString(1, "RAP" + (int)(Math.random()*10000));
            psRapor.setString(2, stajId);
            psRapor.setString(3, dosyaYolu);
            psRapor.setDate(4, new Date(System.currentTimeMillis()));
            psRapor.executeUpdate();

            // 2. Staj Durumunu Güncelle
            String sqlStaj = "UPDATE public.staj SET durum = 'Danışmana Gönderildi' WHERE stajid = ?";
            PreparedStatement psStaj = conn.prepareStatement(sqlStaj);
            psStaj.setString(1, stajId);
            psStaj.executeUpdate();

            // 3. Başvuru Durumunu da Güncelle (Senkronizasyon için)
            String sqlBasvuru = "UPDATE public.basvuru SET durum = 'Danışmana Gönderildi' WHERE basvuruid = (SELECT basvuruid FROM public.staj WHERE stajid = ?)";
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
    // Stajın durumunu doğrudan güncelleyen metod
    public boolean stajDurumGuncelle(String stajId, String yeniDurum) {
        String sql = "UPDATE public.staj SET durum = ? WHERE stajid = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, yeniDurum);
            ps.setString(2, stajId);

            // Başvuru tablosunu da senkronize edelim
            String sqlBasvuru = "UPDATE public.basvuru SET durum = ? WHERE basvuruid = (SELECT basvuruid FROM public.staj WHERE stajid = ?)";
            try(PreparedStatement psB = conn.prepareStatement(sqlBasvuru)) {
                psB.setString(1, yeniDurum);
                psB.setString(2, stajId);
                psB.executeUpdate();
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 1. Düzeltme İste (Notu Kaydeder ve Durumu Günceller)
    public boolean stajDuzeltmeIste(String stajId, String mesaj) {
        String sql = "UPDATE public.staj SET durum = 'Düzeltme Gerekli', duzeltmenotu = ? WHERE stajid = ?";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mesaj);
            ps.setString(2, stajId);

            // Başvuru tablosunu da senkronize et (Durum bilgisi için)
            String sqlBasvuru = "UPDATE public.basvuru SET durum = 'Düzeltme Gerekli' WHERE basvuruid = (SELECT basvuruid FROM public.staj WHERE stajid = ?)";
            try(PreparedStatement psB = conn.prepareStatement(sqlBasvuru)) {
                psB.setString(1, stajId);
                psB.executeUpdate();
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Düzeltme Notunu Getir (Öğrenci Okusun Diye)
    public String getDuzeltmeNotu(String stajId) {
        String sql = "SELECT duzeltmenotu FROM public.staj WHERE stajid = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stajId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("duzeltmenotu");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Not bulunamadı.";
    }
}
