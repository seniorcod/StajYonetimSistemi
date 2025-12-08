package org.example.Gui;

import org.example.DataAccessLayer.IstatistikDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class IstatistikEkrani extends JFrame {

    private Point initialClick;

    // Renkler
    private final Color COLOR_TOTAL = new Color(52, 152, 219);   // Mavi
    private final Color COLOR_DONE = new Color(46, 204, 113);    // Yeşil
    private final Color COLOR_ACTIVE = new Color(241, 196, 15);  // Sarı
    private final Color COLOR_POPULAR = new Color(155, 89, 182); // Mor

    public IstatistikEkrani() {
        // --- PENCERE AYARLARI ---
        setUndecorated(true);
        setSize(850, 350); // Geniş ve basık, dashboard şeridi gibi
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, 850, 350, 25, 25));

        // --- 1. ARKA PLAN (GRADIENT) ---
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // Koyu ve Şık bir tema (Gece Modu gibi)
                Color c1 = new Color(44, 62, 80);  // Koyu Lacivert
                Color c2 = new Color(20, 30, 48);  // Siyaha yakın
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);
        addDragFunctionality(mainPanel);

        // --- 2. ÜST KISIM (BAŞLIK ve KAPATMA) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Sistem İstatistikleri");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblClose = new JLabel("X");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblClose.setForeground(new Color(255, 255, 255, 150));
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(new Color(255, 255, 255, 150)); }
        });

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(lblClose, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- 3. VERİLERİ ÇEK ---
        IstatistikDAO dao = new IstatistikDAO();
        int toplamBasvuru = dao.getToplamBasvuruSayisi();
        int onayliStaj = dao.getOnayliStajSayisi();
        int aktifStaj = dao.getAktifStajSayisi();
        String populerSirket = dao.getEnPopulerSirket();
        if (populerSirket == null || populerSirket.equals("")) populerSirket = "-";

        // --- 4. KARTLAR PANELİ ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(30, 0, 10, 0));

        // Kartları Oluştur
        cardsPanel.add(createDashboardCard("Toplam Başvuru", String.valueOf(toplamBasvuru), COLOR_TOTAL, "chart.png"));
        cardsPanel.add(createDashboardCard("Tamamlanan", String.valueOf(onayliStaj), COLOR_DONE, "check.png"));
        cardsPanel.add(createDashboardCard("Aktif Staj", String.valueOf(aktifStaj), COLOR_ACTIVE, "clock.png"));
        cardsPanel.add(createDashboardCard("Popüler Şirket", populerSirket, COLOR_POPULAR, "star.png"));

        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // --- 5. ALT BİLGİ ---
        JLabel lblFooter = new JLabel("Veriler anlık olarak veritabanından çekilmektedir.", SwingConstants.CENTER);
        lblFooter.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFooter.setForeground(new Color(255, 255, 255, 100));
        lblFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainPanel.add(lblFooter, BorderLayout.SOUTH);
    }

    // --- GARANTİ ÇALIŞAN DASHBOARD KARTI (JTextArea ile) ---
    private JPanel createDashboardCard(String title, String value, Color color, String iconName) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Kart Arkaplanı
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Alt Renkli Çizgi
                g2d.setColor(color);
                g2d.fillRoundRect(0, getHeight() - 6, getWidth(), 6, 20, 20);
                g2d.fillRect(0, getHeight() - 6, getWidth(), 3);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 15, 12, 10)); // Kenar boşlukları

        // 1. BAŞLIK
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0)); // Başlık ile değer arasına boşluk

        // 2. DEĞER (JTextArea Kullanıyoruz - Garanti Yöntem)
        JTextArea txtValue = new JTextArea(value);
        txtValue.setOpaque(false); // Arkaplanı şeffaf yap
        txtValue.setEditable(false); // Düzenlenemesin
        txtValue.setFocusable(false); // Tıklanamasın
        txtValue.setLineWrap(true); // Satır kaydırma AÇIK
        txtValue.setWrapStyleWord(true); // Kelime bütünlüğünü bozmadan kaydır
        txtValue.setForeground(new Color(50, 50, 50));

        // Dinamik Font Ayarı
        if (value.length() > 25) {
            txtValue.setFont(new Font("SansSerif", Font.BOLD, 14)); // Çok uzunsa küçük font
        } else if (value.length() > 12) {
            txtValue.setFont(new Font("SansSerif", Font.BOLD, 18)); // Orta uzunlukta orta font
        } else {
            txtValue.setFont(new Font("SansSerif", Font.BOLD, 28)); // Kısaysa büyük font
        }

        // 3. İKON
        JLabel lblIcon = new JLabel();
        try {
            URL imgUrl = getClass().getResource("/icons/" + iconName);
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image img = originalIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {}

        // İkonu yukarı hizalamak için ayrı bir panele koyalım-
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.add(lblIcon, BorderLayout.NORTH);

        // 4. YERLEŞİM
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(txtValue, BorderLayout.CENTER);
        card.add(iconPanel, BorderLayout.EAST);

        return card;
    }

    private void addDragFunctionality(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); } });
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + (e.getX() - initialClick.x), getLocation().y + (e.getY() - initialClick.y));
            }
        });
    }
}