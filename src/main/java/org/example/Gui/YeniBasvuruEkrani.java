package org.example.Gui;

import org.example.DataAccessLayer.BasvuruDAO;
import org.example.DataAccessLayer.SirketDAO;
import org.example.Model.Kullanici;
import org.example.Model.Sirket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class YeniBasvuruEkrani extends JFrame {

    private final Kullanici kullanici;
    private JComboBox<Sirket> cmbSirket; // String değil Sirket nesnesi tutacak

    public YeniBasvuruEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        // Pencere Ayarları
        setTitle("Yeni Staj Başvurusu Oluştur");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // 1. Şirket Seçimi (Veritabanından Gelecek)
        formPanel.add(new JLabel("Şirket Seçiniz:"));
        cmbSirket = new JComboBox<>();
        sirketleriYukle(); // Metod aşağıda
        formPanel.add(cmbSirket);

        // 2. Pozisyon
        formPanel.add(new JLabel("Pozisyon:"));
        JTextField txtPozisyon = new JTextField();
        formPanel.add(txtPozisyon);

        // 3. Başlangıç Tarihi
        formPanel.add(new JLabel("Başlangıç (YYYY-AA-GG):"));
        JTextField txtBaslangic = new JTextField("2026-06-15");
        formPanel.add(txtBaslangic);

        // 4. Bitiş Tarihi
        formPanel.add(new JLabel("Bitiş (YYYY-AA-GG):"));
        JTextField txtBitis = new JTextField("2026-08-15");
        formPanel.add(txtBitis);

        // 5. Boşluk
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnKaydet = new JButton("Başvuruyu Kaydet");
        JButton btnIptal = new JButton("İptal");

        buttonPanel.add(btnKaydet);
        buttonPanel.add(btnIptal);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnIptal.addActionListener(e -> this.dispose());

        // KAYDET BUTONU (BACKEND BAĞLANTISI)
        btnKaydet.addActionListener(e -> {
            try {
                // Seçilen şirketi al (Sirket nesnesi olarak)
                Sirket secilenSirket = (Sirket) cmbSirket.getSelectedItem();

                if (secilenSirket == null) {
                    JOptionPane.showMessageDialog(this, "Lütfen bir şirket seçiniz.");
                    return;
                }

                String sirketId = secilenSirket.getId(); // ID'sini alıyoruz (Arka planda lazım)
                String pozisyon = txtPozisyon.getText();
                String baslangic = txtBaslangic.getText();
                String bitis = txtBitis.getText();

                // Backend'e Gönder
                BasvuruDAO dao = new BasvuruDAO();
                boolean sonuc = dao.basvuruEkle(kullanici.getId(), sirketId, pozisyon, baslangic, bitis);

                if (sonuc) {
                    JOptionPane.showMessageDialog(this, "Başvurunuz başarıyla alındı!\n(Durum: Beklemede)", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Pencereyi kapat
                } else {
                    JOptionPane.showMessageDialog(this, "Kaydedilirken hata oluştu!\nTarih formatını kontrol ediniz (YYYY-AA-GG).", "Hata", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Beklenmedik bir hata: " + ex.getMessage());
            }
        });
    }

    // Veritabanındaki şirketleri ComboBox'a dolduran metod
    private void sirketleriYukle() {
        SirketDAO dao = new SirketDAO();
        List<Sirket> sirketListesi = dao.tumSirketleriGetir();

        for (Sirket s : sirketListesi) {
            cmbSirket.addItem(s); // Nesneyi direkt ekliyoruz (toString() sayesinde ismi görünecek)
        }
    }
}