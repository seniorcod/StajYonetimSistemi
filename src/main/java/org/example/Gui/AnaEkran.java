package org.example.Gui;

import org.example.DataAccessLayer.BasvuruDAO;
import org.example.Model.Basvuru;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.List;

public class AnaEkran extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Basvuru> tumBasvurular;

    // Renk Paleti
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Mavi
    private final Color SUCCESS_COLOR = new Color(46, 204, 113); // Yeşil
    private final Color WARNING_COLOR = new Color(241, 196, 15); // Sarı
    private final Color DANGER_COLOR = new Color(231, 76, 60);   // Kırmızı
    private final Color PURPLE_COLOR = new Color(142, 68, 173);  // Mor
    private final Color BG_COLOR = new Color(240, 243, 248);     // Kirli Beyaz (Zemin)

    public AnaEkran(Kullanici kullanici) {
        this.kullanici = kullanici;

        setTitle("Staj Yönetim Sistemi - Ana Panel");
        setSize(1000, 650); // Biraz daha kompakt pencere boyutu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // setShape KULLANMIYORUZ (Hata vermemesi için)

        refreshDataInternal();

        if (kullanici.getRol().equalsIgnoreCase("OGRENCI")) {
            initOgrenciEkrani();
        } else if (kullanici.getRol().equalsIgnoreCase("DANISMAN")) {
            initDanismanEkrani();
        } else {
            initSirketEkrani();
        }
    }

    // --- ÖĞRENCİ EKRANI ---
    private void initOgrenciEkrani() {
        JPanel mainPanel = createMainPanel();

        // 1. ÜST KISIM (Header)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 5, 15, 5));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Panel Sola Hizalı

        // Başlık
        JLabel lblWelcome = new JLabel("Merhaba, " + kullanici.getAdSoyad());
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22)); // Font Küçüldü (28->22)
        lblWelcome.setForeground(new Color(44, 62, 80));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT); // Yazı Sola Hizalı

        headerPanel.add(lblWelcome);
        headerPanel.add(Box.createVerticalStrut(15));

        // İstatistikler
        int toplam = 0, bekleyen = 0, onayli = 0;
        if (tumBasvurular != null) {
            for (Basvuru b : tumBasvurular) {
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    toplam++;
                    if (b.getDurum().contains("Bekle") || b.getDurum().contains("Şirket")) bekleyen++;
                    if (b.getDurum().contains("Onay") || b.getDurum().contains("Staj") || b.getDurum().contains("Tamam")) onayli++;
                }
            }
        }

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setBackground(BG_COLOR);
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Kartlar Sola Hizalı

        cardsPanel.add(createModernCard("Toplam Başvuru", String.valueOf(toplam), new Color(100, 149, 237), "folder.png"));
        cardsPanel.add(createModernCard("Aktif / Onaylı", String.valueOf(onayli), SUCCESS_COLOR, "rocket.png"));
        cardsPanel.add(createModernCard("İşlem Bekleyen", String.valueOf(bekleyen), WARNING_COLOR, "clock.png"));

        // Kart Yüksekliği Küçüldüü (100px)
        cardsPanel.setMaximumSize(new Dimension(2000, 100));
        cardsPanel.setPreferredSize(new Dimension(800, 100));

        headerPanel.add(cardsPanel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. ORTA TABLO
        String[] cols = {"Başvuru ID", "Şirket", "Pozisyon", "Başlangıç", "Durum"};
        createModernTable(cols);

        if (tumBasvurular != null) {
            for (Basvuru b : tumBasvurular) {
                if (b.getOgrenciAdSoyad().equalsIgnoreCase(kullanici.getAdSoyad())) {
                    tableModel.addRow(new Object[]{b.getBasvuruId(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()});
                }
            }
        }
        mainPanel.add(wrapTableInPanel(table), BorderLayout.CENTER);

        // 3. ALT BUTONLAR
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(BG_COLOR);

        JButton btnStajlarim = createModernButton("Stajlarım & Rapor", PURPLE_COLOR, "internship.png");
        JButton btnYeni = createModernButton("Yeni Başvuru", PRIMARY_COLOR, "add.png");
        JButton btnCikis = createModernButton("Çıkış", DANGER_COLOR, "logout.png");

        bottomPanel.add(btnStajlarim);
        bottomPanel.add(btnYeni);
        bottomPanel.add(btnCikis);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Aksiyonlar
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });
        btnYeni.addActionListener(e -> {
            YeniBasvuruEkrani ekran = new YeniBasvuruEkrani(kullanici);
            ekran.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) { refreshData(); }
            });
            ekran.setVisible(true);
        });
        btnStajlarim.addActionListener(e -> new StajlarimEkrani(kullanici).setVisible(true));
    }

    // --- DANIŞMAN EKRANI ---
    // --- DANIŞMAN EKRANI (İSİM DÜZELTMELİ) ---
    // --- DANIŞMAN EKRANI (YAZI DÜZELTİLDİ: SansSerif) ---
    private void initDanismanEkrani() {
        JPanel mainPanel = createMainPanel();

        // BAŞLIK KISMI
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 5, 15, 5));

        // Hizalama sorununu kökten çözmek için paneli sola dayıyoruz
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // İSİM KISMI - FONT DEĞİŞTİ (SansSerif)
        JLabel lblWelcome = new JLabel("Merhaba, " + kullanici.getAdSoyad());
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 24)); // Segoe UI yerine SansSerif
        lblWelcome.setForeground(new Color(44, 62, 80));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Alt Başlık
        JLabel lblRole = new JLabel("Danışman Paneli - İşlemler");
        lblRole.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(lblWelcome);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblRole);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // TABLO
        String[] cols = {"ID", "Öğrenci", "Şirket", "Pozisyon", "Tarih", "Durum"};
        createModernTable(cols);

        BasvuruDAO dao = new BasvuruDAO();
        List<Basvuru> liste = dao.getBasvurularByDanisman(kullanici.getId());
        if (liste != null) {
            for (Basvuru b : liste) {
                if (b.getDurum().equals("Şirket Onayladı") || b.getDurum().equals("Danışmana Gönderildi")) {
                    tableModel.addRow(new Object[]{b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()});
                }
            }
        }
        mainPanel.add(wrapTableInPanel(table), BorderLayout.CENTER);

        // BUTONLAR
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(BG_COLOR);

        JButton btnOnay = createModernButton("Onayla / Reddet", PRIMARY_COLOR, "check.png");
        JButton btnNot = createModernButton("Stajı Değerlendir", PURPLE_COLOR, "report.png");
        JButton btnIstat = createModernButton("İstatistikler", WARNING_COLOR, "chart.png");
        JButton btnCikis = createModernButton("Çıkış", DANGER_COLOR, "logout.png");

        btnPanel.add(btnIstat);
        btnPanel.add(btnOnay);
        btnPanel.add(btnNot);
        btnPanel.add(btnCikis);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Aksiyonlar (Aynen Kalıyor)
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });
        btnIstat.addActionListener(e -> new IstatistikEkrani().setVisible(true));

        btnOnay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız."); return; }
            String durum = (String) tableModel.getValueAt(row, 5);
            String bid = (String) tableModel.getValueAt(row, 0);

            if (durum.equals("Şirket Onayladı")) {
                int secim = JOptionPane.showConfirmDialog(this, "Stajı başlatıyor musunuz?", "Onay", JOptionPane.YES_NO_OPTION);
                if (secim == 0) {
                    org.example.DataAccessLayer.StajDAO stajDao = new org.example.DataAccessLayer.StajDAO();
                    String yetkili = stajDao.otomatikYetkiliBul(bid);
                    if(yetkili == null) yetkili = JOptionPane.showInputDialog("Yetkili ID:");
                    if(stajDao.stajBaslat(bid, yetkili, kullanici.getId())) {
                        JOptionPane.showMessageDialog(this, "Başlatıldı!");
                        refreshData();
                    }
                }
            } else JOptionPane.showMessageDialog(this, "Sadece 'Şirket Onayladı' durumundakiler.");
        });

        btnNot.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız."); return; }
            String durum = (String) tableModel.getValueAt(row, 5);

            if (durum.equals("Danışmana Gönderildi")) {
                String bid = (String) tableModel.getValueAt(row, 0);
                String ogr = (String) tableModel.getValueAt(row, 1);
                org.example.DataAccessLayer.StajDAO sDao = new org.example.DataAccessLayer.StajDAO();
                String stajId = sDao.getStajIdByBasvuru(bid);

                // Danışman adını da gönderiyoruz
                StajDegerlendirmeEkrani ekran = new StajDegerlendirmeEkrani(stajId, ogr, kullanici.getId(), kullanici.getAdSoyad());
                ekran.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosed(java.awt.event.WindowEvent e) { refreshData(); }
                });
                ekran.setVisible(true);
            } else JOptionPane.showMessageDialog(this, "Sadece 'Danışmana Gönderildi' durumundakiler.");
        });
    }

    // --- ŞİRKET EKRANI ---
    private void initSirketEkrani() {
        JPanel mainPanel = createMainPanel();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Şirket Paneli");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));

        JLabel lblSub = new JLabel("Kurum başvuruları");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Öğrenci", "Pozisyon", "Tarih", "Durum"};
        createModernTable(cols);

        BasvuruDAO dao = new BasvuruDAO();
        List<Basvuru> list = dao.sirketBasvurulariniGetir(kullanici.getBagliSirketId());
        int bekleyen = 0;
        if (list != null) {
            for (Basvuru b : list) {
                if (b.getDurum().equals("Beklemede")) {
                    tableModel.addRow(new Object[]{b.getBasvuruId(), b.getOgrenciAdSoyad(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()});
                    bekleyen++;
                }
            }
        }
        mainPanel.add(wrapTableInPanel(table), BorderLayout.CENTER);

        // Sağ üstte küçük kart
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(BG_COLOR);
        // Kartı biraz daha küçültüyoruz
        JPanel miniCard = createModernCard("Bekleyen", String.valueOf(bekleyen), WARNING_COLOR, "clock.png");
        miniCard.setPreferredSize(new Dimension(180, 90));
        infoPanel.add(miniCard);
        headerPanel.add(infoPanel, BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(BG_COLOR);

        JButton btnIslem = createModernButton("Başvuruyu Yanıtla", SUCCESS_COLOR, "check.png");
        JButton btnCikis = createModernButton("Çıkış", DANGER_COLOR, "logout.png");

        btnPanel.add(btnIslem);
        btnPanel.add(btnCikis);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Şirket Aksiyonları
        btnCikis.addActionListener(e -> { dispose(); new LoginEkrani().setVisible(true); });

        btnIslem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız."); return; }
            String bid = (String) tableModel.getValueAt(row, 0);

            Object[] opt = {"Kabul Et", "Reddet", "İptal"};
            int secim = JOptionPane.showOptionDialog(this, "Başvuruyu onaylıyor musunuz?", "Onay",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opt, opt[0]);

            BasvuruDAO bDao = new BasvuruDAO();
            if (secim == 0) {
                if(bDao.basvuruDurumGuncelle(bid, "Şirket Onayladı")) {
                    JOptionPane.showMessageDialog(this, "Onaylandı.");
                    refreshData();
                }
            } else if (secim == 1) {
                if(bDao.basvuruDurumGuncelle(bid, "Reddedildi")) {
                    JOptionPane.showMessageDialog(this, "Reddedildi.");
                    refreshData();
                }
            }
        });
    }

    // --- MODERN UI METODLARI (KÜÇÜLTÜLMÜŞ VERSİYONLAR) ---

    private void refreshDataInternal() {
        BasvuruDAO dao = new BasvuruDAO();
        tumBasvurular = dao.tumBasvurulariGetir();
    }

    private void refreshData() {
        dispose();
        new AnaEkran(kullanici).setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(BG_COLOR);
        p.setBorder(new EmptyBorder(20, 20, 20, 20)); // Kenar boşlukları azaltıldı
        return p;
    }

    private JPanel wrapTableInPanel(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // İç boşluk azaltıldı
        tablePanel.add(scrollPane);
        return tablePanel;
    }

    private void createModernTable(String[] columns) {
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);

        // TABLO BOYUTLARI KÜÇÜLTÜLDÜ
        table.setRowHeight(35); // 45 -> 35
        table.setFont(new Font("SansSerif", Font.PLAIN, 12)); // 14 -> 12
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12)); // 14 -> 12
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(0, 35)); // Header yüksekliği azaldı
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        try {
            int statusColIndex = columns.length - 1;
            table.getColumnModel().getColumn(statusColIndex).setCellRenderer(new ModernStatusRenderer());
        } catch (Exception e) {}
    }

    // COMPACT KART TASARIMI
    private JPanel createModernCard(String title, String value, Color color, String iconName) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(225, 225, 225));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20); // Gölge

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20); // Kart

                GradientPaint gp = new GradientPaint(0, 0, color, 0, getHeight(), color.darker());
                g2d.setPaint(gp);

                Shape oldClip = g2d.getClip();
                g2d.setClip(new RoundRectangle2D.Double(0, 0, getWidth()-5, getHeight()-5, 20, 20));
                g2d.fillRect(0, 0, 12, getHeight()); // Şerit inceldi (20 -> 12)
                g2d.setClip(oldClip);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 25, 10, 10)); // İç boşluklar sıkılaştırıldı

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12)); // 14 -> 12
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 24)); // 30 -> 24
        lblValue.setForeground(new Color(50, 50, 50));

        JLabel lblIcon = new JLabel();
        try {
            URL imgUrl = getClass().getResource("/icons/" + iconName);
            if (imgUrl != null) {
                // İkon küçüldü (32px)
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    // COMPACT BUTON TASARIMI
    private JButton createModernButton(String text, Color bgColor, String iconName) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = isHovered ? bgColor.brighter().brighter() : bgColor.brighter();
                Color c2 = isHovered ? bgColor : bgColor.darker();
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
                });
            }
        };

        btn.setFont(new Font("SansSerif", Font.BOLD, 12)); // 14 -> 12
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40)); // Boyut küçüldü (50px -> 40px)

        try {
            URL imgUrl = getClass().getResource("/icons/" + iconName);
            if (imgUrl != null) {
                // İkon küçüldü (18px)
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(8);
            }
        } catch (Exception e) {}

        return btn;
    }

    class ModernStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            label.setText("  " + status + "  ");
            label.setFont(new Font("SansSerif", Font.BOLD, 11)); // Status font küçüldü
            label.setOpaque(true);

            if (status.contains("Onay") || status.contains("Staj") || status.contains("Tamam")) {
                label.setForeground(new Color(21, 87, 36));
                label.setBackground(new Color(212, 237, 218));
            } else if (status.contains("Red")) {
                label.setForeground(new Color(114, 28, 36));
                label.setBackground(new Color(248, 215, 218));
            } else {
                label.setForeground(new Color(133, 100, 4));
                label.setBackground(new Color(255, 243, 205));
            }
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            return label;
        }
    }
}