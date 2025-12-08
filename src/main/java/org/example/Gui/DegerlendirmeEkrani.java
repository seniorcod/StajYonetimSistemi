package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import javax.swing.*;
import java.awt.*;

public class DegerlendirmeEkrani extends JFrame {

    private String stajId;
    private String danismanId;
    private String ogrenciAdi;

    // Artık yapıcı metod (Constructor) direkt hedef öğrenciyi alıyor-
    public DegerlendirmeEkrani(String stajId, String ogrenciAdi, String danismanId) {
        this.stajId = stajId;
        this.ogrenciAdi = ogrenciAdi;
        this.danismanId = danismanId;

        setTitle("Not Girişi: " + ogrenciAdi);
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Başlık
        JLabel lblTitle = new JLabel("Staj Değerlendirme", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        formPanel.add(new JLabel("Öğrenci:"));
        JTextField txtOgrenci = new JTextField(ogrenciAdi);
        txtOgrenci.setEditable(false);
        formPanel.add(txtOgrenci);

        formPanel.add(new JLabel("Puan (0-100):"));
        JSpinner spinnerPuan = new JSpinner(new SpinnerNumberModel(85, 0, 100, 1));
        formPanel.add(spinnerPuan);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Yorum
        JPanel yorumPanel = new JPanel(new BorderLayout());
        yorumPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        yorumPanel.add(new JLabel("Yorum / Görüş:"), BorderLayout.NORTH);
        JTextArea txtYorum = new JTextArea(5, 20);
        txtYorum.setLineWrap(true);
        yorumPanel.add(new JScrollPane(txtYorum), BorderLayout.CENTER);

        centerPanel.add(yorumPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Buton
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnKaydet = new JButton("Kaydet ve Stajı Bitir");
        btnKaydet.setBackground(new Color(46, 204, 113));
        btnKaydet.setForeground(Color.WHITE);

        btnPanel.add(btnKaydet);
        add(btnPanel, BorderLayout.SOUTH);

        // Aksiyon
        btnKaydet.addActionListener(e -> {
            int puan = (int) spinnerPuan.getValue();
            String yorum = txtYorum.getText();

            if (yorum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen yorum giriniz.");
                return;
            }

            DegerlendirmeDAO dao = new DegerlendirmeDAO();
            if (dao.degerlendirmeEkle(stajId, danismanId, puan, yorum)) {
                JOptionPane.showMessageDialog(this, "Not kaydedildi. Staj 'Tamamlandı' durumuna geçti.");
                this.dispose(); // Pencereyi kapat
            } else {
                JOptionPane.showMessageDialog(this, "Hata oluştu.");
            }
        });
    }
}