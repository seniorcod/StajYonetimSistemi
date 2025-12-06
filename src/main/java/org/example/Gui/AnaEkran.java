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
        // Ana Panel (Tüm ekranı kaplayacak)
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 247, 250)); // Kurumsal Gri Arkaplan

        // --- 1. ÜST KISIM (BAŞLIK + KARTLAR) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(new Color(245, 247, 250));

        // Hoşgeldin Başlığı
        JLabel welcomeLabel = new JLabel("👋 Merhaba, " + kullanici.getAdSoyad());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topContainer.add(welcomeLabel);
        topContainer.add(Box.createVerticalStrut(15)); // Boşluk

        // İstatistikleri Hesapla
        int toplam = 0, bekleyen = 0, onayli = 0;
        if (tumBasvurular != null) {
            for (Basvuru b : tumBasvurular) {
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    toplam++;
                    // Bekleyen veya Şirket Onaylamışsa -> İşlem Bekliyor sayılır
                    if (b.getDurum().equals("Beklemede") || b.getDurum().equals("Şirket Onayladı")) bekleyen++;
                    // Danışman onaylamışsa veya staj başlamışsa -> Onaylı sayılır
                    if (b.getDurum().equals("Onaylandı") || b.getDurum().equals("Stajda") || b.getDurum().equals("Tamamlandı")) onayli++;
                }
            }
        }

        // İstatistik Kartları Paneli
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(new Color(245, 247, 250));
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardsPanel.add(createStatCard("Toplam Başvuru", String.valueOf(toplam), new Color(100, 149, 237), "📂"));
        cardsPanel.add(createStatCard("Onaylanan / Stajda", String.valueOf(onayli), new Color(46, 204, 113), "✅"));
        cardsPanel.add(createStatCard("İşlem Bekleyen", String.valueOf(bekleyen), new Color(243, 156, 18), "⏳"));

        // Kart boyutunu sınırla
        cardsPanel.setPreferredSize(new Dimension(800, 100));
        cardsPanel.setMaximumSize(new Dimension(2000, 100));

        topContainer.add(cardsPanel);
        mainPanel.add(topContainer, BorderLayout.NORTH);

        // --- 2. ORTA KISIM (BAŞVURU LİSTESİ TABLOSU) ---
        String[] columnNames = {"Başvuru ID", "Şirket", "Pozisyon", "Başlangıç", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Verileri Yükle (Sadece bu öğrencinin başvuruları)
        if (tumBasvurular != null) {
            for (Basvuru b : tumBasvurular) {
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    tableModel.addRow(new Object[]{
                            b.getBasvuruId(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
                    });
                }
            }
        }

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Renklendiriciyi Bağla (Tablodaki yazıları boyar)
        try {
            table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        } catch (Exception e) { e.printStackTrace(); }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. ALT KISIM (GÜNCELLENMİŞ BUTONLAR) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        // Yeni Kurguya Uygun Butonlar
        JButton btnStajlarim = new JButton("🎓 Stajlarım (Bitir/Görüntüle)");
        JButton btnYeniBasvuru = new JButton("➕ Yeni Başvuru");
        JButton btnCikis = new JButton("🚪 Çıkış");

        styleButton(btnStajlarim, new Color(155, 89, 182)); // Mor
        styleButton(btnYeniBasvuru, new Color(52, 152, 219)); // Mavi
        styleButton(btnCikis, new Color(231, 76, 60)); // Kırmızı

        bottomPanel.add(btnStajlarim);
        bottomPanel.add(btnYeniBasvuru);
        bottomPanel.add(btnCikis);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- EKRANI GÜNCELLE ---
        this.setContentPane(mainPanel);
        this.revalidate();
        this.repaint();

        // --- AKSİYONLAR ---

        // Çıkış
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });

        // Yeni Başvuru (Kapanınca tabloyu yeniler)
        btnYeniBasvuru.addActionListener(e -> {
            YeniBasvuruEkrani ekran = new YeniBasvuruEkrani(kullanici);
            ekran.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) { refreshData(); }
            });
            ekran.setVisible(true);
        });

        // Stajlarım Ekranını Aç (Yeni Ekran)
        btnStajlarim.addActionListener(e -> new StajlarimEkrani(kullanici).setVisible(true));
    }

    // --- YARDIMCI METOD: KART OLUŞTURMA ---
    private JPanel createStatCard(String title, String count, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color), // Sol tarafa renkli çizgi
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblCount = new JLabel(count);
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCount.setForeground(new Color(50, 50, 50));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(lblTitle);
        textPanel.add(lblCount);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    // --- YARDIMCI METOD: BUTON STİLİ ---
    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void initDanismanEkrani() {
        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        // --- 1. ÜST KISIM ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.add(new JLabel("👨‍🏫 Danışman Paneli: " + kullanici.getAdSoyad()), BorderLayout.NORTH);

        // Bilgi Notu
        JLabel infoLabel = new JLabel("Listede sadece onayınızı veya notlandırmanızı bekleyen aktif işler görünür.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. ORTA KISIM (TABLO) ---
        String[] columnNames = {"ID", "Öğrenci", "Şirket", "Pozisyon", "Tarih", "Durum"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // --- FİLTRELEME (SADECE İŞİ OLANLARI GÖSTER) ---
        BasvuruDAO dao = new BasvuruDAO();
        // Giriş yapan kullanıcının ID'sini (örn: DAN001) gönderiyoruz
        java.util.List<Basvuru> benimBasvurularim = dao.getBasvurularByDanisman(kullanici.getId());

        if (benimBasvurularim != null) {
            for (Basvuru b : benimBasvurularim) {
                // DÜZELTME BURADA YAPILDI: "Tamamlandı" SİLİNDİ.
                // Sadece "Şirket Onayladı" (Başlatma Bekleyen) ve "Danışmana Gönderildi" (Not Bekleyen)
                if (b.getDurum().equals("Şirket Onayladı") || b.getDurum().equals("Danışmana Gönderildi")) {
                    tableModel.addRow(new Object[]{
                            b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
                    });
                }
            }
        }

        table = new JTable(tableModel);
        table.setRowHeight(35);
        try { table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer()); } catch (Exception e) {}

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. ALT KISIM (BUTONLAR) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton btnBasvuruIslem = new JButton("✅ Başvuruyu Onayla/Reddet");
        JButton btnStajDegerlendir = new JButton("⚖️ Stajı Değerlendir (Rapor)");
        JButton btnIstatistik = new JButton("📊");
        JButton btnCikis = new JButton("Çıkış");

        styleButton(btnBasvuruIslem, new Color(52, 152, 219)); // Mavi
        styleButton(btnStajDegerlendir, new Color(155, 89, 182)); // Mor
        styleButton(btnIstatistik, new Color(243, 156, 18)); // Turuncu
        styleButton(btnCikis, new Color(231, 76, 60));

        bottomPanel.add(btnIstatistik);
        bottomPanel.add(btnBasvuruIslem);
        bottomPanel.add(btnStajDegerlendir);
        bottomPanel.add(btnCikis);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.revalidate();
        this.repaint();

        // --- AKSİYONLAR ---
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });
        btnIstatistik.addActionListener(e -> new IstatistikEkrani().setVisible(true));

        // 1. BAŞVURU ONAYLA/REDDET
        btnBasvuruIslem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız."); return; }

            String durum = (String) tableModel.getValueAt(row, 5);
            String basvuruId = (String) tableModel.getValueAt(row, 0);

            if (durum.equals("Şirket Onayladı")) {
                int secim = JOptionPane.showConfirmDialog(this, "Bu başvuruyu onaylayıp stajı başlatıyor musunuz?", "Onay", JOptionPane.YES_NO_OPTION);
                if (secim == JOptionPane.YES_OPTION) {
                    org.example.DataAccessLayer.StajDAO stajDao = new org.example.DataAccessLayer.StajDAO();
                    String yetkiliId = stajDao.otomatikYetkiliBul(basvuruId);
                    if (yetkiliId == null) yetkiliId = JOptionPane.showInputDialog("Yetkili ID:");

                    if (stajDao.stajBaslat(basvuruId, yetkiliId, kullanici.getId())) {
                        JOptionPane.showMessageDialog(this, "Staj Başlatıldı!");
                        // Ekranı yenilemek için en temiz yol: Mevcut ekranı kapatıp yenisini açmak
                        new AnaEkran(kullanici).setVisible(true);
                        dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bu buton sadece 'Şirket Onayladı' durumundakiler içindir.");
            }
        });

        // 2. STAJI DEĞERLENDİR
        btnStajDegerlendir.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seçim yapınız.");
                return;
            }

            String durum = (String) tableModel.getValueAt(row, 5);
            String basvuruId = (String) tableModel.getValueAt(row, 0);
            String ogrenci = (String) tableModel.getValueAt(row, 1);

            if (durum.equals("Danışmana Gönderildi")) {
                org.example.DataAccessLayer.StajDAO stajDao = new org.example.DataAccessLayer.StajDAO();
                String stajId = stajDao.getStajIdByBasvuru(basvuruId);

                StajDegerlendirmeEkrani degerlendirmeEkrani = new StajDegerlendirmeEkrani(stajId, ogrenci, kullanici.getId());

                // Pencere kapandığında Ana Ekranı yenile
                degerlendirmeEkrani.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        tableModel.setRowCount(0);
                        BasvuruDAO dao = new BasvuruDAO();
                        // DÜZELTME: Sadece bu danışmana ait olanları çekiyoruz
                        java.util.List<Basvuru> guncelListe = dao.getBasvurularByDanisman(kullanici.getId());

                        if (guncelListe != null) {
                            for (Basvuru b : guncelListe) {
                                // FİLTRE BURADA DA GEÇERLİ: Tamamlananları gösterme
                                if (b.getDurum().equals("Şirket Onayladı") || b.getDurum().equals("Danışmana Gönderildi")) {
                                    tableModel.addRow(new Object[]{
                                            b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
                                    });
                                }
                            }
                        }
                    }
                });

                degerlendirmeEkrani.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Bu buton sadece 'Danışmana Gönderildi' durumundaki stajlar içindir.");
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
        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 247, 250)); // Kurumsal Gri

        // --- 1. ÜST KISIM (BAŞLIK + TEK ODAKLI KART) ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(245, 247, 250));

        // Başlık
        JLabel welcomeLabel = new JLabel("🏢 Şirket Paneli: " + kullanici.getAdSoyad());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(44, 62, 80));

        // Alt Başlık
        JLabel subLabel = new JLabel("Aşağıda onayınızı bekleyen staj başvuruları listelenmektedir.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(Color.GRAY);
        subLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(new Color(245, 247, 250));
        titlePanel.add(welcomeLabel);
        titlePanel.add(subLabel);

        topContainer.add(titlePanel, BorderLayout.WEST);

        // Verileri Çek
        BasvuruDAO dao = new BasvuruDAO();
        java.util.List<Basvuru> sirketBasvurulari = dao.sirketBasvurulariniGetir(kullanici.getBagliSirketId());

        // --- 2. ORTA KISIM (TABLO) ---
        String[] columnNames = {"ID", "Öğrenci", "Pozisyon", "Tarih", "Durum"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // --- SADECE BEKLEYENLERİ FİLTRELE ---
        int bekleyenSayisi = 0;
        if (sirketBasvurulari != null) {
            for (Basvuru b : sirketBasvurulari) {
                // KRİTİK NOKTA: Sadece "Beklemede" olanları tabloya ekle
                if (b.getDurum().equals("Beklemede")) {
                    tableModel.addRow(new Object[]{
                            b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
                    });
                    bekleyenSayisi++;
                }
            }
        }

        // İstatistik Kartı (Sağ Tarafa)
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cardPanel.setBackground(new Color(245, 247, 250));
        cardPanel.add(createStatCard("Bekleyen İşlem", String.valueOf(bekleyenSayisi), new Color(243, 156, 18), "⏳")); // Turuncu

        topContainer.add(cardPanel, BorderLayout.EAST);
        mainPanel.add(topContainer, BorderLayout.NORTH);

        // Tablo Ayarları
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Renklendirici (Varsa kullanır)
        try { table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer()); } catch (Exception e) {}

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Tablo boşsa "İşlem Yok" yazısı gösterilebilir ama şimdilik boş tablo yeterli.
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. ALT KISIM (BUTONLAR) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton btnIslem = new JButton("✅ Başvuruyu Onayla / Reddet");
        JButton btnCikis = new JButton("🚪 Çıkış");

        styleButton(btnIslem, new Color(39, 174, 96)); // Yeşil
        styleButton(btnCikis, new Color(231, 76, 60)); // Kırmızı

        bottomPanel.add(btnIslem);
        bottomPanel.add(btnCikis);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- EKRANI GÜNCELLE ---
        this.setContentPane(mainPanel);
        this.revalidate();
        this.repaint();

        // --- AKSİYONLAR ---
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });

        // Değerlendirme Aksiyonu
        btnIslem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String basvuruId = (String) tableModel.getValueAt(selectedRow, 0);
                String ogrenciAdi = (String) tableModel.getValueAt(selectedRow, 1);

                // Durumu zaten 'Beklemede' olduğu için kontrole gerek yok (Listede sadece onlar var)

                Object[] options = {"Kabul Et", "Reddet", "İptal"};
                int secim = JOptionPane.showOptionDialog(this,
                        "Öğrenci: " + ogrenciAdi + "\nBu staj başvurusuna yanıtınız nedir?",
                        "Şirket Onayı", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                if (secim == 0) { // Kabul Et
                    if (dao.basvuruDurumGuncelle(basvuruId, "Şirket Onayladı")) {
                        JOptionPane.showMessageDialog(this, "✅ Başvuru onaylandı. Listeden kaldırılıyor.");
                        // Ekranı Yenile (Yeniden çizerek listeyi temizler)
                        new AnaEkran(kullanici).setVisible(true); dispose();
                    }
                } else if (secim == 1) { // Reddet
                    if(dao.basvuruDurumGuncelle(basvuruId, "Reddedildi")) {
                        JOptionPane.showMessageDialog(this, "Başvuru reddedildi. Listeden kaldırılıyor.");
                        // Ekranı Yenile
                        new AnaEkran(kullanici).setVisible(true); dispose();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen listeden işlem yapmak istediğiniz başvuruyu seçiniz.");
            }
        });
    }

    // --- YARDIMCI METOD (KOD TEKRARINI ÖNLER) ---
    private void sirketTablosunuYenile() {
        tableModel.setRowCount(0); // Tabloyu temizle
        BasvuruDAO dao = new BasvuruDAO();
        // Şirket ID'sine göre güncel listeyi çek
        java.util.List<org.example.Model.Basvuru> sirketBasvurulari = dao.sirketBasvurulariniGetir(kullanici.getBagliSirketId());

        for (org.example.Model.Basvuru b : sirketBasvurulari) {
            tableModel.addRow(new Object[]{
                    b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()
            });
        }
    }
    // Tablo Durum Sütunu Renklendirici
    private static class StatusRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setHorizontalAlignment(CENTER);

            if (status != null) {
                if (status.contains("Onay") || status.contains("Stajda") || status.contains("Tamam")) {
                    setForeground(new Color(46, 204, 113)); // Yeşil
                    setBackground(new Color(235, 250, 240)); // Açık Yeşil Arkaplan
                } else if (status.contains("Red")) {
                    setForeground(new Color(231, 76, 60)); // Kırmızı
                    setBackground(new Color(253, 237, 236)); // Açık Kırmızı
                } else if (status.contains("Bekle")) {
                    setForeground(new Color(243, 156, 18)); // Turuncu
                    setBackground(new Color(254, 249, 231)); // Açık Turuncu
                } else {
                    setForeground(Color.BLACK);
                    setBackground(Color.WHITE);
                }
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }

            // Yuvarlak kenar efekti için border (Opsiyonel)
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            return this;
        }
    }


}