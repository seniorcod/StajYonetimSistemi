package org.example.Gui;

import org.example.DataAccessLayer.RaporDAO;
import org.example.Model.Rapor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RaporDenetimEkrani extends JFrame {

    private String stajId;
    private String ogrenciAdi;
    private DefaultTableModel tableModel;
    private JTable table;

    public RaporDenetimEkrani(String stajId, String ogrenciAdi) {
        this.stajId = stajId;
        this.ogrenciAdi = ogrenciAdi;

        setTitle("Rapor Denetimi - " + ogrenciAdi);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel lblBaslik = new JLabel(ogrenciAdi + " - Yüklenen Raporlar", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBaslik.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblBaslik, BorderLayout.NORTH);

        // --- TABLO ---
        String[] columns = {"Rapor ID", "Tür", "Tarih", "Dosya", "Durum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        raporlariYukle(); // Verileri Çek
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton btnOnayla = new JButton("Raporu Onayla");
        JButton btnReddet = new JButton("Reddet / Düzeltme İste");

        btnOnayla.setBackground(new Color(60, 179, 113)); // Yeşil
        btnOnayla.setForeground(Color.WHITE);

        bottomPanel.add(btnOnayla);
        bottomPanel.add(btnReddet);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnOnayla.addActionListener(e -> durumDegistir("Onaylandı"));
        btnReddet.addActionListener(e -> durumDegistir("Reddedildi"));
    }

    private void raporlariYukle() {
        tableModel.setRowCount(0);
        RaporDAO dao = new RaporDAO();
        List<Rapor> raporlar = dao.stajRaporlariniGetir(stajId);

        for (Rapor r : raporlar) {
            tableModel.addRow(new Object[]{
                    r.getRaporId(), r.getRaporTipi(), r.getYuklemeTarihi(), r.getDosyaYolu(), r.getOnayDurumu()
            });
        }
    }

    private void durumDegistir(String yeniDurum) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen listeden bir rapor seçiniz.");
            return;
        }

        String raporId = (String) tableModel.getValueAt(selectedRow, 0);
        RaporDAO dao = new RaporDAO();

        if (dao.raporDurumGuncelle(raporId, yeniDurum)) {
            JOptionPane.showMessageDialog(this, "Rapor durumu güncellendi: " + yeniDurum);
            raporlariYukle(); // Tabloyu yenile
        } else {
            JOptionPane.showMessageDialog(this, "Hata oluştu.");
        }
    }
}