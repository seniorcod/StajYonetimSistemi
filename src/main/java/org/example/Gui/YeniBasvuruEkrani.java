package org.example.Gui;

import org.example.DataAccessLayer.BasvuruDAO;
import org.example.DataAccessLayer.DanismanDAO;
import org.example.DataAccessLayer.SirketDAO;
import org.example.Model.Danisman; // <-- ARTIK BUNU TANIYOR
import org.example.Model.Kullanici;
import org.example.Model.Sirket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class YeniBasvuruEkrani extends JFrame {

    private final Kullanici kullanici;

    // ARTIK SORU İŞARETİ (?) YOK, NET TÜR VAR
    private JComboBox<Sirket> cmbSirket;
    private JComboBox<Danisman> cmbDanisman;

    public YeniBasvuruEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        setTitle("Yeni Staj Başvurusu");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Başlık
        JLabel titleLabel = new JLabel("Staj Başvuru Formu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 1. Şirket
        formPanel.add(new JLabel("Şirket Seçiniz:"));
        cmbSirket = new JComboBox<>();
        sirketleriYukle();
        formPanel.add(cmbSirket);

        // 2. Danışman
        formPanel.add(new JLabel("Danışman Seçiniz:"));
        cmbDanisman = new JComboBox<>();
        danismanlariYukle(); // <-- ARTIK HATA VERMEYECEK
        formPanel.add(cmbDanisman);

        // 3. Pozisyon
        formPanel.add(new JLabel("Pozisyon:"));
        JTextField txtPozisyon = new JTextField();
        formPanel.add(txtPozisyon);

        // 4. Tarihler
        formPanel.add(new JLabel("Başlangıç (YYYY-AA-GG):"));
        JTextField txtBaslangic = new JTextField("2026-06-15");
        formPanel.add(txtBaslangic);

        formPanel.add(new JLabel("Bitiş (YYYY-AA-GG):"));
        JTextField txtBitis = new JTextField("2026-08-15");
        formPanel.add(txtBitis);

        add(formPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnKaydet = new JButton("Başvuruyu Kaydet");
        JButton btnIptal = new JButton("İptal");

        btnKaydet.setBackground(new Color(46, 204, 113));
        btnKaydet.setForeground(Color.WHITE);

        buttonPanel.add(btnKaydet);
        buttonPanel.add(btnIptal);
        add(buttonPanel, BorderLayout.SOUTH);

        // Aksiyonlar
        btnIptal.addActionListener(e -> this.dispose());

        btnKaydet.addActionListener(e -> {
            try {
                Sirket secilenSirket = (Sirket) cmbSirket.getSelectedItem();
                Danisman secilenDanisman = (Danisman) cmbDanisman.getSelectedItem(); // <-- ARTIK CAST ETMEK GÜVENLİ

                if (secilenSirket == null || secilenDanisman == null) {
                    JOptionPane.showMessageDialog(this, "Lütfen Şirket ve Danışman seçiniz.");
                    return;
                }

                String sirketId = secilenSirket.getId();
                String danismanId = secilenDanisman.getId(); // <-- ID'yi RAHATÇA ALIYORUZ

                String pozisyon = txtPozisyon.getText();
                String baslangic = txtBaslangic.getText();
                String bitis = txtBitis.getText();

                BasvuruDAO dao = new BasvuruDAO();
                boolean sonuc = dao.basvuruEkle(kullanici.getId(), sirketId, danismanId, pozisyon, baslangic, bitis);

                if (sonuc) {
                    JOptionPane.showMessageDialog(this, "Başvurunuz alındı! Seçtiğiniz danışman onayına sunuldu.");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });
    }

    private void sirketleriYukle() {
        SirketDAO dao = new SirketDAO();
        List<Sirket> list = dao.tumSirketleriGetir();
        for (Sirket s : list) cmbSirket.addItem(s);
    }

    // --- BURASI DÜZELDİ ---
    private void danismanlariYukle() {
        DanismanDAO dao = new DanismanDAO();
        // DAO artık public Model döndürüyor, JComboBox<Danisman> da bunu kabul ediyor
        List<Danisman> list = dao.tumDanismanlariGetir();
        for (Danisman d : list) {
            cmbDanisman.addItem(d);
        }
    }
}