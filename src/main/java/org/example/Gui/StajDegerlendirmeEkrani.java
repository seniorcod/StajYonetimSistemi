package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.DataAccessLayer.RaporDAO;
import org.example.DataAccessLayer.StajDAO;
import org.example.Model.Rapor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StajDegerlendirmeEkrani extends JFrame {

    private String stajId;
    private String ogrenciAdi;
    private String danismanId;
    private DefaultTableModel tableModel;
    private JTable table;

    public StajDegerlendirmeEkrani(String stajId, String ogrenciAdi, String danismanId) {
        this.stajId = stajId;
        this.ogrenciAdi = ogrenciAdi;
        this.danismanId = danismanId;

        setTitle("Staj Değerlendirme: " + ogrenciAdi);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel lblInfo = new JLabel("<html><center>Öğrencinin yüklediği raporları inceleyin.<br>Dosyayı açmak için listedeki satıra <b>ÇİFT TIKLAYIN</b>.</center></html>", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblInfo, BorderLayout.NORTH);

        // --- TABLO ---
        String[] columns = {"Rapor ID", "Tür", "Tarih", "Dosya Yolu"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        raporlariYukle();

        // PDF AÇMA ÖZELLİĞİ (Çift Tıklama)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Çift Tık
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String dosyaYolu = (String) tableModel.getValueAt(row, 3);
                        dosyayiAc(dosyaYolu);
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnDuzeltme = new JButton("⚠️ Düzeltme İste");
        JButton btnOnayla = new JButton("✅ Onayla ve Not Ver");

        btnDuzeltme.setBackground(new Color(230, 126, 34)); // Turuncu
        btnDuzeltme.setForeground(Color.WHITE);

        btnOnayla.setBackground(new Color(46, 204, 113)); // Yeşil
        btnOnayla.setForeground(Color.WHITE);

        bottomPanel.add(btnDuzeltme);
        bottomPanel.add(btnOnayla);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---

        // 1. DÜZELTME İSTEME (GÜNCELLENMİŞ)
        btnDuzeltme.addActionListener(e -> {
            String mesaj = JOptionPane.showInputDialog(this, "Öğrenciye iletilecek düzeltme notunu giriniz:");

            if (mesaj != null && !mesaj.trim().isEmpty()) {
                StajDAO dao = new StajDAO();

                // ESKİ KOD: dao.stajDurumGuncelle(...) idi.
                // YENİ KOD: Mesajı da gönderiyoruz
                if (dao.stajDuzeltmeIste(stajId, mesaj)) {
                    JOptionPane.showMessageDialog(this, "Düzeltme talebi ve notunuz kaydedildi.\nÖğrenci bilgilendirilecek.");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Hata oluştu.");
                }
            }
        });

        // 2. ONAYLAMA VE NOT VERME
        btnOnayla.addActionListener(e -> acNotEkrani());
    }

    private void raporlariYukle() {
        tableModel.setRowCount(0);
        RaporDAO dao = new RaporDAO();
        List<Rapor> raporlar = dao.stajRaporlariniGetir(stajId);
        for (Rapor r : raporlar) {
            tableModel.addRow(new Object[]{r.getRaporId(), r.getRaporTipi(), r.getYuklemeTarihi(), r.getDosyaYolu()});
        }
    }

    // Bilgisayardaki PDF'i açan metod
    private void dosyayiAc(String dosyaYolu) {
        try {
            File file = new File(dosyaYolu);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "Bu işletim sisteminde otomatik dosya açma desteklenmiyor.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Dosya bulunamadı!\nYol: " + dosyaYolu, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dosya açılırken hata oluştu: " + ex.getMessage());
        }
    }

    // Küçük bir iç pencere (Dialog) ile not verdiriyoruz
    private void acNotEkrani() {
        JDialog dialog = new JDialog(this, "Not Girişi", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(4, 1, 10, 10));

        dialog.add(new JLabel("   Puan (0-100):"));
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(85, 0, 100, 1));
        dialog.add(spinner);

        dialog.add(new JLabel("   Yorum:"));
        JTextField txtYorum = new JTextField();
        dialog.add(txtYorum);

        JButton btnKaydet = new JButton("Kaydet ve Bitir");
        dialog.add(btnKaydet);

        btnKaydet.addActionListener(ev -> {
            int puan = (int) spinner.getValue();
            String yorum = txtYorum.getText();

            DegerlendirmeDAO dao = new DegerlendirmeDAO();
            // Notu kaydet ve durumu 'Tamamlandı' yap
            if (dao.degerlendirmeEkle(stajId, danismanId, puan, yorum)) {
                JOptionPane.showMessageDialog(dialog, "Staj başarıyla tamamlandı!");
                dialog.dispose();
                this.dispose(); // Ana pencereyi de kapat
            }
        });

        dialog.setVisible(true);
    }
}
