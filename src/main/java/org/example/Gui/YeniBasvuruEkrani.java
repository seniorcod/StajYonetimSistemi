package org.example.Gui;

import org.example.DataAccessLayer.BasvuruDAO;
import org.example.DataAccessLayer.DanismanDAO;
import org.example.DataAccessLayer.SirketDAO;
import org.example.Model.Danisman;
import org.example.Model.Kullanici;
import org.example.Model.Sirket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.List;

public class YeniBasvuruEkrani extends JFrame {

    private final Kullanici kullanici;

    // Form Elemanları
    private JComboBox<Sirket> cmbSirket;
    private JComboBox<Danisman> cmbDanisman;
    private JTextField txtPozisyon;
    private JTextField txtBaslangic;
    private JTextField txtBitis;

    private Point initialClick;

    public YeniBasvuruEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        // --- PENCERE AYARLARI ---
        setUndecorated(true);
        setSize(500, 720); // Boyutu biraz daha ideal hale getirdim
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 720, 25, 25)); // Köşeleri biraz daha keskinleştirdik

        // --- 1. ARKA PLAN (GRADIENT) ---
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color c1 = new Color(74, 144, 226);
                Color c2 = new Color(144, 19, 254);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);
        addDragFunctionality(mainPanel);

        // --- 2. ORTA KART (DAHA GENİŞ & İNCE KENARLIK) ---
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gölge
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);

                // Beyaz Kart
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setOpaque(false);

        // DEĞİŞİKLİK BURADA: Kenar boşluklarını azalttık (Margin 40 -> 15)
        // Kartı büyüttük, böylece renkli kısım inceldi.
        cardPanel.setBounds(15, 50, 470, 655);
        mainPanel.add(cardPanel);

        // --- 3. İÇERİK ---

        // Kapat Butonu (Sağ Üst Köşede, renkli kısmın üzerinde)
        JLabel lblClose = new JLabel("✕");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblClose.setForeground(new Color(255, 255, 255, 220));
        lblClose.setBounds(465, 15, 30, 30); // Köşeye yanaştırdık
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(new Color(255, 255, 255, 220)); }
        });
        mainPanel.add(lblClose);

        // Başlık
        JLabel lblTitle = new JLabel("Yeni Staj Başvurusu");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(51, 51, 51));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        // Kart genişlediği için başlığı ortaladık (Width: 460)
        lblTitle.setBounds(0, 25, 460, 30);
        cardPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Formu eksiksiz doldurunuz");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 55, 460, 20);
        cardPanel.add(lblSub);

        // -- FORM ELEMANLARI --
        // Kart genişlediği için elemanları da genişletiyoruz (Width: 380px)
        int fieldWidth = 380;
        int xPos = 40; // Sol boşluk
        int yStart = 90;
        int gap = 75;

        // 1. ŞİRKET
        addLabel(cardPanel, "Şirket Seçiniz", xPos, yStart);
        cmbSirket = createModernComboBox();
        cmbSirket.setBounds(xPos, yStart + 25, fieldWidth, 45);
        sirketleriYukle();
        cardPanel.add(cmbSirket);
        // İkonu sağa yaslayalım (Genişliğe göre)
        addIcon(cardPanel, "building.png", xPos + fieldWidth + 10, yStart + 35);

        // 2. DANIŞMAN
        addLabel(cardPanel, "Danışman Seçiniz", xPos, yStart + gap);
        cmbDanisman = createModernComboBox();
        cmbDanisman.setBounds(xPos, yStart + gap + 25, fieldWidth, 45);
        danismanlariYukle();
        cardPanel.add(cmbDanisman);
        addIcon(cardPanel, "manager.png", xPos + fieldWidth + 10, yStart + gap + 35);

        // 3. POZİSYON
        addLabel(cardPanel, "Pozisyon / Departman", xPos, yStart + gap*2);
        txtPozisyon = createModernTextField();
        txtPozisyon.setBounds(xPos, yStart + gap*2 + 25, fieldWidth, 45);
        safeIconLoad(txtPozisyon, "briefcase.png");
        cardPanel.add(txtPozisyon);

        // 4. BAŞLANGIÇ
        addLabel(cardPanel, "Başlangıç Tarihi (YYYY-AA-GG)", xPos, yStart + gap*3);
        txtBaslangic = createModernTextField();
        txtBaslangic.setText("2026-06-15");
        txtBaslangic.setBounds(xPos, yStart + gap*3 + 25, fieldWidth, 45);
        safeIconLoad(txtBaslangic, "calendar.png");
        cardPanel.add(txtBaslangic);

        // 5. BİTİŞ
        addLabel(cardPanel, "Bitiş Tarihi (YYYY-AA-GG)", xPos, yStart + gap*4);
        txtBitis = createModernTextField();
        txtBitis.setText("2026-08-15");
        txtBitis.setBounds(xPos, yStart + gap*4 + 25, fieldWidth, 45);
        safeIconLoad(txtBitis, "calendar.png");
        cardPanel.add(txtBitis);

        // -- BUTONLAR --
        JButton btnKaydet = createModernButton("Başvuruyu Kaydet", new Color(46, 204, 113));
        btnKaydet.setBounds(xPos, 530, fieldWidth, 50);

        JButton btnIptal = createModernButton("İptal", new Color(231, 76, 60));
        btnIptal.setBounds(xPos, 590, fieldWidth, 40);
        btnIptal.setFont(new Font("SansSerif", Font.PLAIN, 14));

        cardPanel.add(btnKaydet);
        cardPanel.add(btnIptal);

        // --- AKSİYONLAR ---
        btnIptal.addActionListener(e -> dispose());

        btnKaydet.addActionListener(e -> {
            try {
                Sirket secilenSirket = (Sirket) cmbSirket.getSelectedItem();
                Danisman secilenDanisman = (Danisman) cmbDanisman.getSelectedItem();

                if (secilenSirket == null || secilenDanisman == null) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm seçimleri yapınız.");
                    return;
                }

                btnKaydet.setText("İşleniyor...");
                btnKaydet.setEnabled(false);

                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() {
                        BasvuruDAO dao = new BasvuruDAO();
                        return dao.basvuruEkle(kullanici.getId(), secilenSirket.getId(), secilenDanisman.getId(),
                                txtPozisyon.getText(), txtBaslangic.getText(), txtBitis.getText());
                    }
                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(YeniBasvuruEkrani.this, "✅ Başvuru Gönderildi!");
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(YeniBasvuruEkrani.this, "Hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                                btnKaydet.setText("Başvuruyu Kaydet");
                                btnKaydet.setEnabled(true);
                            }
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }.execute();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    // --- YARDIMCI METODLAR ---
    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setBounds(x, y, 300, 20);
        panel.add(lbl);
    }

    private void addIcon(JPanel panel, String iconName, int x, int y) {
        try {
            URL url = getClass().getResource("/icons/" + iconName);
            if (url != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                JLabel lblIcon = new JLabel(icon);
                lblIcon.setBounds(x, y, 20, 20);
                // Bu ikonlar combobox yanına süs olarak konuyor
            }
        } catch (Exception e) {}
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.putClientProperty("Component.arc", 999);
        field.putClientProperty("JComponent.roundRect", true);
        field.setBorder(BorderFactory.createCompoundBorder(field.getBorder(), new EmptyBorder(0, 10, 0, 10)));
        return field;
    }

    private <T> JComboBox<T> createModernComboBox() {
        JComboBox<T> box = new JComboBox<>();
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        box.putClientProperty("Component.arc", 999);
        return box;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, bgColor.brighter(), 0, getHeight(), bgColor.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addDragFunctionality(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); } });
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + (e.getX() - initialClick.x), getLocation().y + (e.getY() - initialClick.y));
            }
        });
    }

    private void safeIconLoad(JComponent comp, String path) {
        try {
            URL url = getClass().getResource("/icons/" + path);
            if (url != null && comp instanceof JTextField) {
                ImageIcon icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
                ((JTextField) comp).putClientProperty("JTextField.leadingIcon", icon);
            }
        } catch (Exception e) {}
    }

    private void sirketleriYukle() {
        SirketDAO dao = new SirketDAO();
        List<Sirket> list = dao.tumSirketleriGetir();
        for (Sirket s : list) cmbSirket.addItem(s);
    }

    private void danismanlariYukle() {
        DanismanDAO dao = new DanismanDAO();
        List<Danisman> list = dao.tumDanismanlariGetir();
        for (Danisman d : list) cmbDanisman.addItem(d);
    }
}