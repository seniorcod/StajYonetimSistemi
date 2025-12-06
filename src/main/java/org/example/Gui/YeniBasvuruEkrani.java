package org.example.Gui;

import org.example.Model.Kullanici;

import javax.swing.*;
import java.awt.*;

public class YeniBasvuruEkrani extends JFrame {

    private final Kullanici kullanici;

    public YeniBasvuruEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        // Pencere Ayarları
        setTitle("Yeni Staj Başvurusu Oluştur");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Sadece bu pencereyi kapatır, ana ekran kalır
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("Staj Başvuru Formu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- FORM ALANI ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 1. Şirket Seçimi
        formPanel.add(new JLabel("Şirket Seçiniz:"));
        // Şimdilik veritabanındaki şirketleri elle yazıyoruz (Test için)
        String[] sirketler = {"Tekno A.Ş.", "Savunma Ltd.", "Oyun Stüdyosu"};
        JComboBox<String> cmbSirket = new JComboBox<>(sirketler);
        formPanel.add(cmbSirket);

        // 2. Pozisyon
        formPanel.add(new JLabel("Pozisyon (Örn: Yazılım Stajyeri):"));
        JTextField txtPozisyon = new JTextField();
        formPanel.add(txtPozisyon);

        // 3. Başlangıç Tarihi
        formPanel.add(new JLabel("Başlangıç Tarihi (YYYY-AA-GG):"));
        JTextField txtBaslangic = new JTextField("2026-06-15"); // Örnek veri
        formPanel.add(txtBaslangic);

        // 4. Bitiş Tarihi
        formPanel.add(new JLabel("Bitiş Tarihi (YYYY-AA-GG):"));
        JTextField txtBitis = new JTextField("2026-08-15"); // Örnek veri
        formPanel.add(txtBitis);

        // 5. Boşluk (Düzen için)
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton btnKaydet = new JButton("Başvuruyu Kaydet");
        JButton btnIptal = new JButton("İptal");

        // Buton Renklendirme (İstersen)
        // btnKaydet.setBackground(new Color(34, 139, 34)); // Yeşil
        // btnKaydet.setForeground(Color.WHITE);

        buttonPanel.add(btnKaydet);
        buttonPanel.add(btnIptal);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnIptal.addActionListener(e -> this.dispose()); // Pencereyi kapat

        btnKaydet.addActionListener(e -> {
            // Şimdilik sadece mesaj göstersin, backend gelince buraya "INSERT" kodu gelecek
            String secilenSirket = (String) cmbSirket.getSelectedItem();
            String pozisyon = txtPozisyon.getText();

            JOptionPane.showMessageDialog(this,
                    "Başvuru Bilgileri Alındı:\n" +
                            "Öğrenci: " + kullanici.getAdSoyad() + "\n" +
                            "Şirket: " + secilenSirket + "\n" +
                            "Pozisyon: " + pozisyon + "\n\n" +
                            "(Veritabanı bağlantısı henüz yapılmadı)",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            this.dispose(); // Kaydettikten sonra pencereyi kapat
        });
    }
}