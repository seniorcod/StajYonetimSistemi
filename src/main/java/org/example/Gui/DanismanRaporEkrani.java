package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.DataAccessLayer.RaporDAO;
import org.example.Model.Kullanici;
import org.example.Model.Rapor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DanismanRaporEkrani extends JFrame {

    private final Kullanici danisman;
    private final String stajId;
    private final String ogrenciAdi;
    private DefaultTableModel tableModel;

    public DanismanRaporEkrani(Kullanici danisman, String stajId, String ogrenciAdi) {
        this.danisman = danisman;
        this.stajId = stajId;
        this.ogrenciAdi = ogrenciAdi;

        setTitle("Rapor İnceleme ve Değerlendirme");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(new Color(78, 84, 200));
        JLabel lbl = new JLabel("<html><span style='color:white; font-size:14px'>Öğrenci:</span> <span style='color:white; font-size:18px'><b>" + ogrenciAdi + "</b></span></html>");
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        // --- ORTA: RAPOR LİSTESİ ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Color.WHITE);

        String[] cols = {"Rapor Tipi", "Dosya Adı", "Tarih", "Durum"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);

        loadRaporlar(); // Verileri Çek

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Yüklenen Raporlar"));
        centerPanel.add(scroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- ALT: NOT VERME ALANI ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JLabel lblNot = new JLabel("STAJ DEĞERLENDİRMESİ");
        lblNot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNot.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        inputPanel.setOpaque(false);

        JSpinner spinnerPuan = new JSpinner(new SpinnerNumberModel(85, 0, 100, 1));
        spinnerPuan.setPreferredSize(new Dimension(80, 30));

        JTextField txtYorum = new JTextField("Başarılı bir staj dönemi geçirdi.");
        txtYorum.setPreferredSize(new Dimension(300, 30));

        inputPanel.add(new JLabel("Puan:"));
        inputPanel.add(spinnerPuan);
        inputPanel.add(new JLabel("Yorum:"));
        inputPanel.add(txtYorum);

        JButton btnKaydet = new JButton("💾 DEĞERLENDİRMEYİ KAYDET");
        btnKaydet.setBackground(new Color(34, 139, 34)); // Yeşil
        btnKaydet.setForeground(Color.WHITE);
        btnKaydet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKaydet.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(lblNot);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(inputPanel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(btnKaydet);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYON: NOT KAYDETME ---
        btnKaydet.addActionListener(e -> {
            int puan = (int) spinnerPuan.getValue();
            String yorum = txtYorum.getText();

            DegerlendirmeDAO dao = new DegerlendirmeDAO();
            if(dao.degerlendirmeEkle(stajId, danisman.getId(), puan, yorum)) {
                JOptionPane.showMessageDialog(this, "✅ Değerlendirme kaydedildi! Öğrenci ekranına yansıdı.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hata! Daha önce not verilmiş olabilir.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadRaporlar() {
        RaporDAO dao = new RaporDAO();
        List<Rapor> raporlar = dao.stajRaporlariniGetir(stajId);
        tableModel.setRowCount(0);
        for(Rapor r : raporlar) {
            tableModel.addRow(new Object[]{r.getRaporTipi(), r.getDosyaYolu(), r.getYuklemeTarihi(), r.getOnayDurumu()});
        }
    }
}