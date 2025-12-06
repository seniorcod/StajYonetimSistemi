package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.DataAccessLayer.StajDAO;
import org.example.Model.Basvuru;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StajlarimEkrani extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;

    public StajlarimEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        setTitle("Stajlarım ve Rapor İşlemleri");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel lblTitle = new JLabel("Stajlarım", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- TABLO ---
        String[] columns = {"Staj ID", "Şirket", "Pozisyon", "Başlangıç", "Durum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        verileriYukle();

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton btnBitir = new JButton("Stajı Bitir & Rapor Yükle");
        JButton btnSonuc = new JButton("Değerlendirmeyi Gör");
        JButton btnNotOku = new JButton("⚠️ Düzeltme Notunu Oku"); // <-- YENİ BUTON

        btnBitir.setBackground(new Color(255, 159, 67));
        btnBitir.setForeground(Color.WHITE);

        btnSonuc.setBackground(new Color(46, 204, 113));
        btnSonuc.setForeground(Color.WHITE);

        btnNotOku.setBackground(new Color(231, 76, 60)); // Kırmızımsı
        btnNotOku.setForeground(Color.WHITE);

        bottomPanel.add(btnBitir);
        bottomPanel.add(btnNotOku); // <-- EKLENDİ
        bottomPanel.add(btnSonuc);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---

        // 1. STAJI BİTİRME
        btnBitir.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen listeden bir staj seçiniz.");
                return;
            }

            String stajId = (String) tableModel.getValueAt(row, 0);
            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Aktif") || durum.equals("Stajda") || durum.equals("Düzeltme Gerekli")) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Staj Raporunu Seçiniz (PDF)");
                int userSelection = fileChooser.showOpenDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToUpload = fileChooser.getSelectedFile();
                    String dosyaYolu = fileToUpload.getAbsolutePath();

                    StajDAO dao = new StajDAO();
                    // Dosyayı yükle ve durumu tekrar 'Danışmana Gönderildi' yap
                    if (dao.stajiBitirVeRaporla(stajId, dosyaYolu)) {
                        JOptionPane.showMessageDialog(this, "Rapor yüklendi! Durum 'Danışmana Gönderildi' oldu.");
                        verileriYukle();
                    }
                }
            } else if (durum.equals("Danışmana Gönderildi")) {
                JOptionPane.showMessageDialog(this, "Raporunuz şu an incelemede, tekrar yükleme yapamazsınız.");
            }
        });
        btnNotOku.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız."); return; }

            String stajId = (String) tableModel.getValueAt(row, 0); // Bu aslında BasvuruID olabilir, kontrol etmemiz lazım.
            // DİKKAT: StajDAO.getOgrenciStajlari metodunda 1. sıraya STAJ ID koymuştuk. Yani row,0 StajID'dir. Doğru.

            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Düzeltme Gerekli")) {
                StajDAO dao = new StajDAO();
                String hocaNotu = dao.getDuzeltmeNotu(stajId);

                JTextArea textArea = new JTextArea(hocaNotu);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                textArea.setSize(300, 150);

                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Danışman Düzeltme Notu", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Bu staj için düzeltme notu bulunmuyor.");
            }
        });

        // 2. SONUCU GÖRME
        btnSonuc.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            String stajId = (String) tableModel.getValueAt(row, 0);
            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Tamamlandı")) {
                DegerlendirmeDAO dao = new DegerlendirmeDAO();
                String[] sonuc = dao.getDegerlendirmeSonucu(stajId);

                if (sonuc != null) {
                    String mesaj = "🎓 STAJ SONUCU\n\n" +
                            "Puan: " + sonuc[0] + " / 100\n" +
                            "Yorum: " + sonuc[1];
                    JOptionPane.showMessageDialog(this, mesaj, "Değerlendirme", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Henüz not girilmemiş.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Staj henüz tamamlanmamış.");
            }
        });
    }

    private void verileriYukle() {
        tableModel.setRowCount(0);
        StajDAO dao = new StajDAO();
        List<Basvuru> stajlar = dao.getOgrenciStajlari(kullanici.getId());

        for (Basvuru b : stajlar) {
            tableModel.addRow(new Object[]{
                    b.getBasvuruId(), // Aslında StajID tutuyoruz burada
                    b.getSirketAd(),
                    b.getPozisyon(),
                    b.getBaslangicTarihi(),
                    b.getDurum()
            });
        }
    }
}
