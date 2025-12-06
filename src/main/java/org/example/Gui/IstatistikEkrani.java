package org.example.Gui;

import org.example.DataAccessLayer.IstatistikDAO;
import javax.swing.*;
import java.awt.*;

public class IstatistikEkrani extends JFrame {

    public IstatistikEkrani() {
        setTitle("Sistem İstatistikleri");
        setSize(600, 250); // Pencereyi küçülttük çünkü alt yazı kalktı
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Arka plan
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Verileri Çek
        IstatistikDAO dao = new IstatistikDAO();
        int toplamBasvuru = dao.getToplamBasvuruSayisi();
        int onayliStaj = dao.getOnayliStajSayisi();
        int aktifStaj = dao.getAktifStajSayisi();
        String populerSirket = dao.getEnPopulerSirket();

        // --- KARTLAR (Sadece burası kaldı) ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setBackground(new Color(245, 247, 250));

        cardsPanel.add(createCard("Toplam", String.valueOf(toplamBasvuru), new Color(100, 149, 237)));
        cardsPanel.add(createCard("Tamamlanan", String.valueOf(onayliStaj), new Color(60, 179, 113)));
        cardsPanel.add(createCard("Aktif Staj", String.valueOf(aktifStaj), new Color(255, 165, 0)));
        cardsPanel.add(createCard("Popüler", populerSirket, new Color(147, 112, 219)));

        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Altına küçük bir bilgi notu (Zorunlu değil ama şık durur)
        JLabel lblFooter = new JLabel("Veriler anlık olarak sistemden çekilmektedir.", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(Color.GRAY);
        mainPanel.add(lblFooter, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Kart Oluşturucu Metod
    private JPanel createCard(String baslik, String deger, Color renk) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, renk), // Üstte renkli çizgi
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));

        JLabel lblDeger = new JLabel(deger, SwingConstants.CENTER);
        lblDeger.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDeger.setForeground(new Color(50, 50, 50));
        lblDeger.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblBaslik = new JLabel(baslik, SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBaslik.setForeground(Color.GRAY);
        lblBaslik.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        card.add(lblDeger, BorderLayout.CENTER);
        card.add(lblBaslik, BorderLayout.SOUTH);
        return card;
    }
}