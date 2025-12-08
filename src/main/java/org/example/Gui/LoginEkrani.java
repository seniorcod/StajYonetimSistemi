package org.example.Gui;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.DataAccessLayer.AuthDAO;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class LoginEkrani extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Point initialClick;

    public LoginEkrani() {
        // --- PENCERE AYARLARI ---
        setUndecorated(true); // Çerçevesiz modern mod
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Pencere kenarlarını da yuvarlatalım (Windows 11 stili)
        setShape(new RoundRectangle2D.Double(0, 0, 450, 600, 30, 30));

        // --- 1. ARKA PLAN (GRADIENT) ---
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Modern Renk Paleti: Koyu Mavi -> Mor Geçişi
                Color c1 = new Color(74, 144, 226); // Parlak Mavi
                Color c2 = new Color(144, 19, 254); // Derin Mor
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null); // Özgür yerleşim
        setContentPane(mainPanel);

        // Pencere Sürükleme Özelliği
        addDragFunctionality(mainPanel);

        // --- 2. ORTA KART (SOFT & SHADOW) ---
        // Gölge efekti için özel panel
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gölge Çizimi
                g2d.setColor(new Color(0, 0, 0, 40)); // %40 Siyah şeffaf gölge
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 40, 40);

                // Kartın Kendisi (Beyaz)
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 40, 40);

                g2d.dispose();
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setOpaque(false); // Arka planı biz çizdik
        cardPanel.setBounds(35, 80, 380, 440); // Ortaya yerleştir
        mainPanel.add(cardPanel);

        // --- 3. İÇERİKLER ---

        // Kapatma Butonu (Sağ Üst Köşe - Ana Panelde)
        JLabel lblClose = new JLabel("✕");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblClose.setForeground(new Color(255, 255, 255, 200));
        lblClose.setBounds(410, 15, 30, 30);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(new Color(255, 255, 255, 200)); }
        });
        mainPanel.add(lblClose);

        // Başlık
        JLabel lblTitle = new JLabel("Hoşgeldiniz");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28)); // Daha temiz font
        lblTitle.setForeground(new Color(51, 51, 51));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 30, 370, 40);
        cardPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Hesabınıza giriş yapın");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(new Color(150, 150, 150));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 70, 370, 20);
        cardPanel.add(lblSub);

        // E-Posta
        JLabel lblEmail = new JLabel("E-Posta");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEmail.setForeground(new Color(100, 100, 100));
        lblEmail.setBounds(35, 110, 300, 20);
        cardPanel.add(lblEmail);

        emailField = new JTextField("ali.yilmaz@ogr.edu.tr");
        emailField.setBounds(35, 135, 300, 45);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.putClientProperty("Component.arc", 999); // Tam yuvarlak (Hap şeklinde)
        emailField.putClientProperty("JComponent.roundRect", true);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                emailField.getBorder(),
                new EmptyBorder(0, 10, 0, 10))); // İçeriden biraz boşluk
        safeIconLoad(emailField, "/icons/user.png");
        cardPanel.add(emailField);

        // Şifre
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPass.setForeground(new Color(100, 100, 100));
        lblPass.setBounds(35, 200, 300, 20);
        cardPanel.add(lblPass);

        passwordField = new JPasswordField("12345");
        passwordField.setBounds(35, 225, 300, 45);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.putClientProperty("Component.arc", 999); // Tam yuvarlak
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                new EmptyBorder(0, 10, 0, 10)));
        safeIconLoad(passwordField, "/icons/padlock.png");
        cardPanel.add(passwordField);

        // Buton (Hap Şeklinde)
        loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(35, 310, 300, 50);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(74, 144, 226));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        // Butonu tam yuvarlak yapıyoruz
        loginButton.putClientProperty("Component.arc", 999);

        // Buton Hover Efekti
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginButton.setBackground(new Color(50, 120, 210)); }
            public void mouseExited(MouseEvent e) { loginButton.setBackground(new Color(74, 144, 226)); }
        });
        cardPanel.add(loginButton);

        // Footer Text
        JLabel lblFooter = new JLabel("Staj Yönetim Sistemi v1.0", SwingConstants.CENTER);
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFooter.setForeground(Color.LIGHT_GRAY);
        lblFooter.setBounds(0, 390, 370, 20);
        cardPanel.add(lblFooter);

        // Enter Tuşu
        getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(e -> girisIslemiYap());
    }

    // Pencereyi sürüklemek için gerekli metod
    private void addDragFunctionality(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Pencere konumunu al
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Mouse'un ne kadar hareket ettiğini bul
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Yeni konuma taşı
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });
    }

    private void safeIconLoad(JComponent comp, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
                if (comp instanceof JTextField) ((JTextField) comp).putClientProperty("JTextField.leadingIcon", icon);
            }
        } catch (Exception e) {}
    }

    private void girisIslemiYap() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen alanları doldurunuz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loginButton.setText("Giriş Yapılıyor...");
        loginButton.setEnabled(false);
        new SwingWorker<Kullanici, Void>() {
            @Override
            protected Kullanici doInBackground() {
                AuthDAO auth = new AuthDAO();
                return auth.girisYap(email, password);
            }
            @Override
            protected void done() {
                try {
                    Kullanici k = get();
                    if (k != null) {
                        dispose();
                        new AnaEkran(k).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginEkrani.this, "Bilgiler Hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
                        loginButton.setText("Giriş Yap");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            // Global yuvarlaklık ayarları (Fallback olarak)-
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);
            UIManager.put("TextComponent.arc", 999);
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginEkrani().setVisible(true));
    }
}