package org.example.Gui;

import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class RaporEkrani extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;

    public RaporEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        setTitle("Staj Raporlarım");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("Yüklenen Raporlar", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- TABLO (Rapor Listesi) ---
        String[] columnNames = {"Rapor ID", "Staj ID", "Rapor Tipi", "Yükleme Tarihi", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // SAHTE VERİ (Tasarım için)
        tableModel.addRow(new Object[]{"RAP001", "STJ001", "Ara Rapor 1", "2026-07-15", "Onaylandı"});
        tableModel.addRow(new Object[]{"RAP002", "STJ001", "Final Raporu", "2026-08-16", "Bekliyor"});

        table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- ALT PANEL (Dosya Yükleme Butonu) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnYukle = new JButton("Yeni Rapor Yükle");
        JButton btnKapat = new JButton("Kapat");

        bottomPanel.add(btnYukle);
        bottomPanel.add(btnKapat);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnKapat.addActionListener(e -> this.dispose());

        btnYukle.addActionListener(e -> {
            // Dosya Seçme Penceresi (JFileChooser)
            JFileChooser fileChooser = new JFileChooser();
            int secim = fileChooser.showOpenDialog(this);

            if (secim == JFileChooser.APPROVE_OPTION) {
                File secilenDosya = fileChooser.getSelectedFile();
                JOptionPane.showMessageDialog(this,
                        "Seçilen Dosya: " + secilenDosya.getName() + "\n" +
                                "Dosya Yolu: " + secilenDosya.getAbsolutePath() + "\n\n" +
                                "(Veritabanına yükleme işlemi backend bağlanınca yapılacak)",
                        "Dosya Seçildi", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}