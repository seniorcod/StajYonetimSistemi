package org.example.Gui;

import org.example.DataAccessLayer.BasvuruDAO;
import org.example.Model.Basvuru;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AnaEkran extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;

    // Veritabanından çektiğimiz gerçek veriler burada duracak
    private List<Basvuru> tumBasvurular;

    public AnaEkran(Kullanici kullanici) {
        this.kullanici = kullanici;

        // Pencere Temel Ayarları
        setTitle("Staj Yönetim Sistemi - Ana Ekran");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BACKEND BAĞLANTISI ---
        // Ekran açılırken senin yazdığın DAO'ya gidip verileri çekiyoruz
        BasvuruDAO dao = new BasvuruDAO();
        tumBasvurular = dao.tumBasvurulariGetir();

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
        // --- 1. ÜST PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Merhaba, " + kullanici.getAdSoyad() + " (Öğrenci Paneli)");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(welcomeLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo) ---
        String[] columnNames = {"Başvuru ID", "Şirket", "Pozisyon", "Başlangıç", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // --- VERİTABANI VERİLERİNİ YÜKLEME ---
        // Sadece giriş yapan öğrencinin kendi başvurularını listeliyoruz
        if (tumBasvurular != null) {
            for (Basvuru b : tumBasvurular) {
                // İsim eşleşmesiyle filtreliyoruz
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    tableModel.addRow(new Object[]{
                            b.getBasvuruId(),
                            b.getSirketAd(),
                            b.getPozisyon(),
                            b.getBaslangicTarihi(),
                            b.getDurum()
                    });
                }
            }
        }

        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Başvurularım"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- 3. ALT PANEL (GÜNCELLENMİŞ BUTONLAR) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnRaporlar = new JButton("Rapor İşlemleri"); // Yeni Buton
        JButton btnYeniBasvuru = new JButton("Yeni Başvuru Yap");
        JButton btnCikis = new JButton("Çıkış Yap");

        bottomPanel.add(btnRaporlar);
        bottomPanel.add(btnYeniBasvuru);
        bottomPanel.add(btnCikis);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnCikis.addActionListener(e -> {
            this.dispose();
            new LoginEkrani().setVisible(true);
        });

         btnYeniBasvuru.addActionListener(e -> {
             YeniBasvuruEkrani ekran = new YeniBasvuruEkrani(kullanici);

             // Pencere kapandığını dinleyen "Dinleyici" (Listener) ekliyoruz
             ekran.addWindowListener(new java.awt.event.WindowAdapter() {
                 @Override
                 public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                     refreshData(); // Pencere kapanınca tabloyu güncelle!
                 }
             });

             ekran.setVisible(true);
         });

        // Rapor Ekranını Aç
        btnRaporlar.addActionListener(e -> new RaporEkrani(kullanici).setVisible(true));
    }

    private void initDanismanEkrani() {
        // --- 1. ÜST PANEL ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Sayın Danışman: " + kullanici.getAdSoyad());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Filtreleme
        String[] filtreler = {"Hepsi", "Beklemede", "Onaylandı", "Reddedildi", "Stajda"};
        JComboBox<String> cmbFiltre = new JComboBox<>(filtreler);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Durum Filtresi:"));
        filterPanel.add(cmbFiltre);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. ORTA PANEL ---
        String[] columnNames = {"ID", "Öğrenci", "Şirket", "Pozisyon", "Tarih", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Verileri doldur
        tabloyuDoldur(tumBasvurular, "Hepsi");

        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Öğrenci Başvuru Listesi (Canlı Veri)"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnIstatistik = new JButton("📊 İstatistikler"); // <--- YENİ EKLENEN
        JButton btnDegerlendir = new JButton("Staj Değerlendir");
        JButton btnDetay = new JButton("İşlem Yap (Onay/Red)");
        JButton btnCikis = new JButton("Çıkış Yap");

        // Renklendirme (Opsiyonel)
        btnIstatistik.setBackground(new Color(255, 69, 0)); // Turuncu
        btnIstatistik.setForeground(Color.WHITE);

        bottomPanel.add(btnIstatistik); // Panele ekle
        bottomPanel.add(btnDegerlendir);
        bottomPanel.add(btnDetay);
        bottomPanel.add(btnCikis);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        cmbFiltre.addActionListener(e -> {
            String secilen = (String) cmbFiltre.getSelectedItem();
            tabloyuDoldur(tumBasvurular, secilen);
        });

        btnIstatistik.addActionListener(e -> {
            new IstatistikEkrani().setVisible(true);
        });

        btnCikis.addActionListener(e -> {
            this.dispose();
            new LoginEkrani().setVisible(true);
        });

        // Değerlendirme Ekranını Aç
        btnDegerlendir.addActionListener(e -> {
            new DegerlendirmeEkrani(kullanici).setVisible(true);
        });

        btnDetay.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String basvuruId = (String) tableModel.getValueAt(selectedRow, 0);
                String ogrenci = (String) tableModel.getValueAt(selectedRow, 1);
                String durum = (String) tableModel.getValueAt(selectedRow, 5);

                if(durum.equals("Beklemede")) {
                    int secim = JOptionPane.showConfirmDialog(this,
                            ogrenci + " başvurusunu onaylıyor musun?\n(Hayır dersen Reddedilir)",
                            "Başvuru İşlemi", JOptionPane.YES_NO_CANCEL_OPTION);

                    BasvuruDAO dao = new BasvuruDAO();
                    if(secim == JOptionPane.YES_OPTION) {
                        // Şirket Yetkilisi ataması için basit bir input box açıyoruz
                        String yetkiliId = JOptionPane.showInputDialog(this, "Şirket Yetkili ID giriniz (Örn: YET001):", "YET001");
                        if(yetkiliId != null && !yetkiliId.isEmpty()){
                            // Stajı Başlat (Transaction)
                            org.example.DataAccessLayer.StajDAO stajDao = new org.example.DataAccessLayer.StajDAO();
                            if(stajDao.stajBaslat(basvuruId, yetkiliId)){
                                JOptionPane.showMessageDialog(this, "Staj Başlatıldı!");
                                refreshData();
                            } else {
                                JOptionPane.showMessageDialog(this, "Hata! Yetkili ID kontrol ediniz.");
                            }
                        }
                    } else if (secim == JOptionPane.NO_OPTION) {
                        if(dao.basvuruDurumGuncelle(basvuruId, "Reddedildi")) {
                            JOptionPane.showMessageDialog(this, "Başvuru Reddedildi.");
                            refreshData();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Sadece 'Beklemede' olan başvurulara işlem yapabilirsiniz.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir satır seçiniz.");
            }
        });
    }

    // --- YARDIMCI METODLAR ---

    // Filtreye göre tabloyu doldurur
    private void tabloyuDoldur(List<Basvuru> veriListesi, String filtre) {
        tableModel.setRowCount(0); // Tabloyu temizle

        for (Basvuru b : veriListesi) {
            if (filtre.equals("Hepsi") || b.getDurum().equalsIgnoreCase(filtre)) {
                tableModel.addRow(new Object[]{
                        b.getBasvuruId(),
                        b.getOgrenciAdSoyad(),
                        b.getSirketAd(),
                        b.getPozisyon(),
                        b.getBaslangicTarihi(),
                        b.getDurum()
                });
            }
        }
    }

    // İşlem yapıldıktan sonra verileri veritabanından tekrar çeker
    // Verileri veritabanından tekrar çekip tabloyu yenileyen metod
    private void refreshData() {
        // 1. Veritabanından en güncel listeyi çek
        BasvuruDAO dao = new BasvuruDAO();
        tumBasvurular = dao.tumBasvurulariGetir();

        // 2. Tabloyu temizle
        tableModel.setRowCount(0);

        // 3. Role göre tabloyu tekrar doldur
        if (kullanici.getRol().equalsIgnoreCase("OGRENCI")) {
            // -- ÖĞRENCİ İSE SADECE KENDİ KAYITLARI --
            for (Basvuru b : tumBasvurular) {
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    tableModel.addRow(new Object[]{
                            b.getBasvuruId(),
                            b.getSirketAd(),
                            b.getPozisyon(),
                            b.getBaslangicTarihi(),
                            b.getDurum()
                    });
                }
            }
        } else {
            // -- DANIŞMAN İSE HEPSİ (veya filtreye göre) --
            // Varsayılan olarak "Hepsi" modunda yeniliyoruz
            tabloyuDoldur(tumBasvurular, "Hepsi");
        }
    }

    private void initSirketEkrani() {
        // --- 1. ÜST PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(new JLabel("Şirket Yetkili Paneli (" + kullanici.getAdSoyad() + ")"));
        add(topPanel, BorderLayout.NORTH);

        // --- 2. ORTA PANEL (Tablo) ---
        String[] columnNames = {"ID", "Öğrenci", "Pozisyon", "Tarih", "Durum"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Verileri Çek (Sadece bu şirkete ait olanlar)
        BasvuruDAO dao = new BasvuruDAO();
        // Login olurken set ettiğimiz şirket ID'sini kullanıyoruz
        List<Basvuru> sirketBasvurulari = dao.sirketBasvurulariniGetir(kullanici.getBagliSirketId());

        for (Basvuru b : sirketBasvurulari) {
            tableModel.addRow(new Object[]{
                    b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
            });
        }

        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. ALT PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnIslem = new JButton("Başvuruyu Değerlendir");
        JButton btnCikis = new JButton("Çıkış Yap");
        bottomPanel.add(btnIslem);
        bottomPanel.add(btnCikis);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnCikis.addActionListener(e -> { this.dispose(); new LoginEkrani().setVisible(true); });

        btnIslem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String basvuruId = (String) tableModel.getValueAt(selectedRow, 0);
                String durum = (String) tableModel.getValueAt(selectedRow, 4);

                if (durum.equals("Beklemede")) {
                    Object[] options = {"Kabul Et", "Reddet", "İptal"};
                    int secim = JOptionPane.showOptionDialog(this, "Bu staj başvurusuna şirket olarak yanıtınız nedir?",
                            "Şirket Onayı", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);

                    if (secim == 0) { // Kabul Et
                        // Durumu 'Şirket Onayladı' yapıyoruz (Danışman sonra 'Onaylandı' yapacak)
                        if (dao.basvuruDurumGuncelle(basvuruId, "Şirket Onayladı")) {
                            JOptionPane.showMessageDialog(this, "Başvuru kabul edildi. Danışman onayı bekleniyor.");
                            // Tabloyu yenileme kodu eklenebilir
                        }
                    } else if (secim == 1) { // Reddet
                        dao.basvuruDurumGuncelle(basvuruId, "Reddedildi");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Bu başvuru zaten işlem görmüş.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seçim yapınız.");
            }
        });
    }
}