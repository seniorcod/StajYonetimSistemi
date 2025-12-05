package org.example.Gui;

import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnaEkran extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;

    public AnaEkran(Kullanici kullanici) {
        this.kullanici = kullanici;

        // Pencere Temel Ayarları
        setTitle("Staj Yönetim Sistemi - Ana Ekran");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ortada aç
        setLayout(new BorderLayout());

        // Kullanıcı Rolüne Göre Ekranı Hazırla
        if (kullanici.getRol().equalsIgnoreCase("OGRENCI")) {
            initOgrenciEkrani();
        } else if (kullanici.getRol().equalsIgnoreCase("DANISMAN")) {
            initDanismanEkrani();
        } else {
            initSirketEkrani();
        }
    }

    private void initOgrenciEkrani() {
        // --- 1. ÜST PANEL (Hoşgeldin Yazısı) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Merhaba, " + kullanici.getAdSoyad());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(welcomeLabel);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo - Başvurularım) ---
        // Tablo Sütun Başlıkları
        String[] columnNames = {"Başvuru ID", "Şirket", "Pozisyon", "Başlangıç", "Bitiş", "Durum"};

        // Tablo Modeli (Verileri tutan kısım)
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hücrelere çift tıklayıp değiştiremesinler
            }
        };

        // SAHTE VERİ (Tasarımı görmek için) - Sonra burayı sileceğiz!
        tableModel.addRow(new Object[]{"BAS001", "Tekno A.Ş.", "Yazılım Stajyeri", "2026-07-01", "2026-08-15", "Onaylandı"});
        tableModel.addRow(new Object[]{"BAS002", "Savunma Ltd.", "Sistem Mühendisi", "2026-06-15", "2026-07-30", "Beklemede"});
        tableModel.addRow(new Object[]{"BAS003", "Oyun Stüdyosu", "Game Developer", "2026-08-01", "2026-09-01", "Reddedildi"});

        table = new JTable(tableModel);
        table.setRowHeight(25); // Satır yüksekliği
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Tabloyu kaydırılabilir panelin içine koy
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Başvurularım"));

        // Kenarlardan boşluk bırakalım
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. ALT PANEL (Butonlar) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnYeniBasvuru = new JButton("Yeni Başvuru Yap");
        JButton btnRaporlar = new JButton("Raporlarım");
        JButton btnCikis = new JButton("Çıkış Yap");

        // Butonlara ikon veya renk verebilirsin (İsteğe bağlı)
        // btnYeniBasvuru.setBackground(new Color(60, 179, 113)); // Yeşilimsi

        bottomPanel.add(btnRaporlar);
        bottomPanel.add(btnYeniBasvuru);
        bottomPanel.add(btnCikis);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTON AKSİYONLARI ---
        btnCikis.addActionListener(e -> {
            this.dispose(); // Ekranı kapat
            new LoginEkrani().setVisible(true); // Giriş ekranına dön
        });

        btnYeniBasvuru.addActionListener(e -> {
            // Yeni pencereyi aç ve kullanıcı bilgisini gönder
            new YeniBasvuruEkrani(kullanici).setVisible(true);
        });
        btnRaporlar.addActionListener(e -> {
            new RaporEkrani(kullanici).setVisible(true);
        });
    }

    private void initDanismanEkrani() {
        // --- 1. ÜST PANEL (Hoşgeldin ve Filtre) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Sayın Danışman: " + kullanici.getAdSoyad());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Filtreleme Seçeneği
        String[] filtreler = {"Hepsi", "Beklemede", "Onaylandı", "Reddedildi"};
        JComboBox<String> cmbFiltre = new JComboBox<>(filtreler);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Durum Filtresi:"));
        filterPanel.add(cmbFiltre);
        topPanel.add(filterPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo) ---
        String[] columnNames = {"Başvuru ID", "Öğrenci Adı", "Şirket", "Pozisyon", "Tarih", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Öğrenci Başvuru Listesi"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. ALT PANEL (Butonlar) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnDetay = new JButton("Başvuru Detayı / İşlem");
        JButton btnNotlandir = new JButton("Staj Değerlendir");
        JButton btnCikis = new JButton("Çıkış Yap");

        btnDetay.setBackground(new Color(70, 130, 180));
        btnDetay.setForeground(Color.WHITE);

        bottomPanel.add(btnDetay);
        bottomPanel.add(btnNotlandir);
        bottomPanel.add(btnCikis);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. SAHTE VERİLER VE FİLTRE MANTIĞI ---
        // Tüm verileri bir havuzda tutalım (Backend gelene kadar)
        Object[][] tumVeriler = {
                {"BAS001", "Veli Kaya", "Savunma Ltd.", "Sistem Mühendisi", "2026-06-15", "Beklemede"},
                {"BAS002", "Ahmet Yılmaz", "Tekno A.Ş.", "Yazılım Stajyeri", "2026-07-01", "Beklemede"},
                {"BAS003", "Ali Yılmaz", "Tekno A.Ş.", "Yazılım Stajyeri", "2026-07-01", "Onaylandı"},
                {"BAS004", "Ayşe Demir", "Oyun Stüdyosu", "Game Dev", "2026-08-01", "Reddedildi"}
        };

        // İlk açılışta hepsini yükle
        tabloyuDoldur(tumVeriler, "Hepsi");

        // FİLTRE AKSİYONU (İşte çalışmayan kısım burasıydı)
        cmbFiltre.addActionListener(e -> {
            String secilen = (String) cmbFiltre.getSelectedItem();
            tabloyuDoldur(tumVeriler, secilen);
        });

        // --- DİĞER BUTON AKSİYONLARI ---
        btnCikis.addActionListener(e -> {
            this.dispose();
            new LoginEkrani().setVisible(true);
        });

        btnNotlandir.addActionListener(e -> {
            new DegerlendirmeEkrani(kullanici).setVisible(true);
        });

        btnDetay.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String durum = (String) tableModel.getValueAt(selectedRow, 5);
                if(durum.equals("Beklemede")) {
                    String ogrenci = (String) tableModel.getValueAt(selectedRow, 1);
                    int secim = JOptionPane.showConfirmDialog(this, ogrenci + " başvurusunu onaylıyor musun?", "Onay", JOptionPane.YES_NO_OPTION);
                    if(secim == JOptionPane.YES_OPTION) tableModel.setValueAt("Onaylandı", selectedRow, 5);
                    else tableModel.setValueAt("Reddedildi", selectedRow, 5);
                } else {
                    JOptionPane.showMessageDialog(this, "Bu başvuru zaten sonuçlanmış.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seçim yapınız.");
            }
        });
    }

    // Tabloyu filtreye göre dolduran yardımcı metod
    private void tabloyuDoldur(Object[][] veriHavuzu, String filtre) {
        tableModel.setRowCount(0); // Tabloyu temizle

        for (Object[] satir : veriHavuzu) {
            String durum = (String) satir[5]; // Durum sütunu

            if (filtre.equals("Hepsi") || durum.equals(filtre)) {
                tableModel.addRow(satir);
            }
        }
    }

    private void initSirketEkrani() {
        // Şirket paneli sırası gelince burayı dolduracağız
        JLabel label = new JLabel("Şirket Paneli - Yapım Aşamasında", SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
    }
}