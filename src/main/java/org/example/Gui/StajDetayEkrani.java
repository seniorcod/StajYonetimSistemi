package org.example.Gui;

import org.example.DataAccessLayer.RaporDAO;
import org.example.Model.Kullanici;
import org.example.Model.Rapor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StajDetayEkrani extends JFrame {

    private final Kullanici kullanici;
    private final String stajId;
    private DefaultTableModel tableModel;
    private JTable table;

    public StajDetayEkrani(Kullanici kullanici, String stajId, String stajBaslik) {
        this.kullanici = kullanici;
        this.stajId = stajId;

        setTitle("Staj Raporları - " + stajBaslik);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(new Color(245, 247, 250));
        JLabel lbl = new JLabel("<html><span style='font-size:16px'>Seçilen Staj:</span><br><span style='font-size:20px; color:#4e54c8'><b>" + stajBaslik + "</b></span></html>");
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        // --- TABLO (Raporlar ve Notlar) ---
        String[] columns = {"Rapor Tipi", "Dosya", "Yükleme Tarihi", "Değerlendirme (Durum)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Renklendirme
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String s = (String) value;
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if("Onaylandı".equals(s)) setForeground(new Color(34, 139, 34));
                else if("Reddedildi".equals(s)) setForeground(Color.RED);
                else setForeground(new Color(255, 140, 0));
                return c;
            }
        });

        loadRaporlar(); // Verileri Çek

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        // --- BUTON ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        JButton btnYukle = new JButton("📁 Bu Staja Rapor Yükle");
        btnYukle.setBackground(new Color(0, 123, 255));
        btnYukle.setForeground(Color.WHITE);
        btnYukle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        bottom.add(btnYukle);
        add(bottom, BorderLayout.SOUTH);

        // --- AKSİYON: Rapor Yükleme ----
        btnYukle.addActionListener(e -> {
            String[] raporTipleri = {"Ara Rapor 1", "Ara Rapor 2", "Final Raporu"};
            String secilenTip = (String) JOptionPane.showInputDialog(this, "Rapor Tipi Seçiniz:", "Rapor Yükle",
                    JOptionPane.QUESTION_MESSAGE, null, raporTipleri, raporTipleri[0]);

            if(secilenTip != null) {
                JFileChooser fileChooser = new JFileChooser();
                if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    // KAYIT İŞLEMİ
                    RaporDAO dao = new RaporDAO();
                    if(dao.raporEkle(stajId, secilenTip, file.getName())) {
                        JOptionPane.showMessageDialog(this, "Rapor yüklendi! Danışman onayına düştü.");
                        loadRaporlar(); // Tabloyu yenile
                    } else {
                        JOptionPane.showMessageDialog(this, "Hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void loadRaporlar() {
        tableModel.setRowCount(0);
        RaporDAO dao = new RaporDAO();
        List<Rapor> raporlar = dao.stajRaporlariniGetir(stajId);

        for (Rapor r : raporlar) {
            tableModel.addRow(new Object[]{
                    r.getRaporTipi(),
                    r.getDosyaYolu(),
                    r.getYuklemeTarihi(),
                    r.getOnayDurumu() // Burası Danışmanın notu/onayı olacak
            });
        }
    }
}