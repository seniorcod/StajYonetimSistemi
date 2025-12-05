package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.DataAccessLayer.AuthDAO;
import org.example.Model.Kullanici;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup(); // Tema

        AuthDAO auth = new AuthDAO();

        // Veritabanındaki gerçek veri: Ali Yılmaz
        System.out.println("Giriş deneniyor...");

        // Şifre olarak SQL'de belirlediğimiz varsayılan '12345'i kullanıyoruz
        Kullanici girisYapan = auth.girisYap("ali.yilmaz@ogr.edu.tr", "12345");

        if (girisYapan != null) {
            System.out.println("✅ GİRİŞ BAŞARILI!");
            System.out.println("Hoşgeldin: " + girisYapan.getAdSoyad()); // Çıktı: Ali Yılmaz
            System.out.println("Rolü: " + girisYapan.getRol());           // Çıktı: OGRENCI
        } else {
            System.out.println("❌ GİRİŞ BAŞARISIZ! Şifre sütunlarını eklediğinden emin misin?");
        }
    }
}