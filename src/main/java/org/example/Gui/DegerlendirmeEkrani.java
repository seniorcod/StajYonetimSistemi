package org.example.Gui;

import org.example.Model.Kullanici;

import javax.swing.*;
import java.awt.*;

public class DegerlendirmeEkrani extends JFrame {

    private final Kullanici kullanici;

    public DegerlendirmeEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        setTitle("Staj Değerlendirme Formu");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("Staj Değerlendirme & Not Girişi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- FORM ---
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // 1. Öğrenci Seçimi (Şimdilik manuel, backend gelince tablodan seçileni alacağız)
        formPanel.add(new JLabel("Değerlendirilecek Öğrenci (Staj ID):"));
        JTextField txtOgrenci = new JTextField("STJ001 - Ali Yılmaz"); // Örnek
        txtOgrenci.setEditable(false); // Danışman bunu değiştiremesin
        formPanel.add(txtOgrenci);

        // 2. Puan Girişi
        formPanel.add(new JLabel("Puan (0-100 Arası):"));
        JSpinner spinnerPuan = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        // (Varsayılan 50, Min 0, Max 100, Artış 1)
        formPanel.add(spinnerPuan);

        // 3. Yorum Alanı
        JPanel yorumPanel = new JPanel(new BorderLayout());
        yorumPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        yorumPanel.add(new JLabel("Danışman Görüşü / Yorum:"), BorderLayout.NORTH);

        JTextArea txtYorum = new JTextArea(5, 20);
        txtYorum.setLineWrap(true);
        JScrollPane scrollYorum = new JScrollPane(txtYorum);
        yorumPanel.add(scrollYorum, BorderLayout.CENTER);

        // Panelleri birleştir
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(formPanel, BorderLayout.NORTH);
        centerContainer.add(yorumPanel, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton btnKaydet = new JButton("Notu Kaydet");
        JButton btnIptal = new JButton("İptal");

        // btnKaydet.setBackground(new Color(70, 130, 180)); // Mavi buton
        // btnKaydet.setForeground(Color.WHITE);

        bottomPanel.add(btnKaydet);
        bottomPanel.add(btnIptal);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnIptal.addActionListener(e -> this.dispose());

        btnKaydet.addActionListener(e -> {
            int puan = (int) spinnerPuan.getValue();
            String yorum = txtYorum.getText();

            if(yorum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen bir yorum giriniz!", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Değerlendirme Kaydedildi!\n" +
                            "Puan: " + puan + "\n" +
                            "Yorum: " + yorum + "\n\n" +
                            "(Veritabanı bağlantısı bekleniyor...)",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            this.dispose();
        });
    }
}