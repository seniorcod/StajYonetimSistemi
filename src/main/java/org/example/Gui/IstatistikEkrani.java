package org.example.Gui;

import org.example.DataAccessLayer.IstatistikDAO;
import javax.swing.*;
import java.awt.*;

public class IstatistikEkrani extends JFrame {

    public IstatistikEkrani() {
        setTitle("Sistem İstatistikleri ve Özet Rapor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1)); // Üstte sayılar, altta grafiksel bilgi

        // Verileri Çek
        IstatistikDAO dao = new IstatistikDAO();
        int toplamBasvuru = dao.getToplamBasvuruSayisi();
        int onayliStaj = dao.getOnayliStajSayisi();
        int aktifStaj = dao.getAktifStajSayisi();
        String populerSirket = dao.getEnPopulerSirket();

        // --- ÜST KISIM: KARTLAR ---
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(new Color(240, 248, 255)); // Açık mavi

        cardsPanel.add(createCard("Toplam Başvuru", String.valueOf(toplamBasvuru), new Color(100, 149, 237)));
        cardsPanel.add(createCard("Onaylanan / Biten", String.valueOf(onayliStaj), new Color(60, 179, 113)));
        cardsPanel.add(createCard("Şu An Stajda", String.valueOf(aktifStaj), new Color(255, 165, 0)));
        cardsPanel.add(createCard("Popüler Şirket", populerSirket, new Color(147, 112, 219)));

        add(cardsPanel);

        // --- ALT KISIM: BİLGİLENDİRME ---
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea txtInfo = new JTextArea();
        txtInfo.setText("SİSTEM ÖZETİ:\n\n" +
                "- Sistemde toplam " + toplamBasvuru + " adet başvuru kaydı bulunmaktadır.\n" +
                "- Bunlardan " + aktifStaj + " tanesi şu anda aktif olarak staj yapmaktadır.\n" +
                "- Öğrencilerin en çok tercih ettiği şirket: " + populerSirket + "\n\n" +
                "Not: Raporlar bölümünden detaylı çıktılar alınabilir.");
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));

        infoPanel.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        add(infoPanel);
    }

    // Şık kutucuklar oluşturmak için yardımcı metod
    private JPanel createCard(String baslik, String deger, Color renk) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(renk);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel lblDeger = new JLabel(deger, SwingConstants.CENTER);
        lblDeger.setFont(new Font("Arial", Font.BOLD, 24));
        lblDeger.setForeground(Color.WHITE);

        JLabel lblBaslik = new JLabel(baslik, SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBaslik.setForeground(Color.WHITE);
        lblBaslik.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        card.add(lblDeger, BorderLayout.CENTER);
        card.add(lblBaslik, BorderLayout.SOUTH);
        return card;
    }
}
