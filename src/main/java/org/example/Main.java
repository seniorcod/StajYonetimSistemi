package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.Gui.LoginEkrani;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Arayüz işlemlerini "Event Dispatch Thread" içine alıyoruz (En güvenli yöntem)
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. FLATLAF TEMASINI KUR
                FlatLightLaf.setup();

                // 2. GLOBAL STİL AYARLARI (Sihirli Dokunuşlar)
                // Bu ayarlar projendeki her şeyi otomatik güzelleştirir.

                UIManager.put("Button.arc", 999);       // Butonları tam yuvarlak (Hap) yapar
                UIManager.put("Component.arc", 15);     // Input kutularını hafif yuvarlar
                UIManager.put("TextComponent.arc", 15); // Yazı alanlarını yuvarlar
                UIManager.put("ProgressBar.arc", 999);  // Yükleme çubuklarını yuvarlar

                // Odaklanma rengini bizim temaya uydur (Mavi)
                UIManager.put("Component.focusWidth", 1);
                UIManager.put("Component.focusColor", new Color(52, 152, 219));

                // Font ayarlarını işletim sistemine göre iyileştir
                System.setProperty("flatlaf.useWindowDecorations", "true");
                System.setProperty("flatlaf.menuBarEmbedded", "true");

            } catch (Exception e) {
                System.err.println("Tema yüklenirken hata oluştu, varsayılan tema kullanılıyor.");
            }

            // 3. UYGULAMAYI BAŞLAT-
            // Artık konsol testi yok, direkt Login Ekranı açılıyor.
            LoginEkrani loginEkrani = new LoginEkrani();
            loginEkrani.setVisible(true);
        });
    }
}