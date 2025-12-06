package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.Model.DegerlendirilecekStaj;
import org.example.Model.Kullanici;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DegerlendirmeEkrani extends JFrame {

    private final Kullanici kullanici;
    private JComboBox<DegerlendirilecekStaj> cmbOgrenci; // Backend'den dolacak

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

        // 1. Öğrenci Seçimi
        formPanel.add(new JLabel("Değerlendirilecek Öğrenci Seçiniz:"));
        cmbOgrenci = new JComboBox<>();
        stajlariYukle(); // Metodu aşağıda çağırıyoruz
        formPanel.add(cmbOgrenci);

        // 2. Puan Girişi
        formPanel.add(new JLabel("Puan (0-100 Arası):"));
        JSpinner spinnerPuan = new JSpinner(new SpinnerNumberModel(80, 0, 100, 1));
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

        bottomPanel.add(btnKaydet);
        bottomPanel.add(btnIptal);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnIptal.addActionListener(e -> this.dispose());

        btnKaydet.addActionListener(e -> {
            DegerlendirilecekStaj secilen = (DegerlendirilecekStaj) cmbOgrenci.getSelectedItem();

            if (secilen == null || secilen.getStajId().equals("0")) {
                JOptionPane.showMessageDialog(this, "Lütfen listeden geçerli bir öğrenci seçiniz.");
                return;
            }

            int puan = (int) spinnerPuan.getValue();
            String yorum = txtYorum.getText();

            if(yorum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen bir yorum giriniz!", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Backend'e Gönder
            DegerlendirmeDAO dao = new DegerlendirmeDAO();
            boolean sonuc = dao.degerlendirmeEkle(secilen.getStajId(), kullanici.getId(), puan, yorum);

            if (sonuc) {
                JOptionPane.showMessageDialog(this,
                        "Değerlendirme Başarıyla Kaydedildi!\nÖğrenci: " + secilen,
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kaydedilirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Veritabanındaki Danışmana ait stajları yükle
    private void stajlariYukle() {
        DegerlendirmeDAO dao = new DegerlendirmeDAO();
        List<DegerlendirilecekStaj> stajlar = dao.getDanismanStajlari(kullanici.getId());

        for (DegerlendirilecekStaj s : stajlar) {
            cmbOgrenci.addItem(s);
        }

        if (stajlar.isEmpty()) {
            cmbOgrenci.addItem(new DegerlendirilecekStaj("0", "Değerlendirilecek Staj Yok", "-"));
            cmbOgrenci.setEnabled(false);
        }
    }
}