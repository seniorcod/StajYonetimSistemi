package org.example.Gui;

import org.example.Model.Kullanici;
import javax.swing.*;
import java.awt.*;

public class AnaEkran extends JFrame {

    public AnaEkran(Kullanici kullanici) {
        setTitle("Staj Yönetim Sistemi - " + kullanici.getRol());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Basit bir karşılama mesajı
        JLabel welcomeLabel = new JLabel("Merhaba " + kullanici.getAdSoyad() + " (" + kullanici.getRol() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(welcomeLabel, BorderLayout.CENTER);

        // Eğer kullanıcı ÖĞRENCİ ise butonları ona göre gösterelim
        if(kullanici.getRol().equals("OGRENCI")) {
            // Öğrenciye özel butonlar buraya gelecek
            // örn: add(new JButton("Başvuru Yap"), BorderLayout.SOUTH);
        }
    }
}