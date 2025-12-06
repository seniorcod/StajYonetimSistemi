package org.example.Gui;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.DataAccessLayer.AuthDAO;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginEkrani extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginEkrani() {
        // Pencere Ayarları
        setTitle("Staj Yönetim Sistemi");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Arka Plan Rengi
        getContentPane().setBackground(new Color(245, 245, 250)); // Çok açık gri/mavi

        // --- ORTA PANEL (LOGIN KARTI) ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40)); // İç boşluk

        // Başlık
        JLabel lblTitle = new JLabel("Hoşgeldiniz");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Lütfen kimliğinizi doğrulayın");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Alanları
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(30, 0, 20, 0));

        // Email
        JLabel lblEmail = new JLabel("E-Posta Adresi");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailField = new JTextField("ali.yilmaz@ogr.edu.tr"); // Test verisi
        emailField.putClientProperty("JTextField.placeholderText", "ornek@edu.tr");
        emailField.setPreferredSize(new Dimension(0, 35));

        // Şifre
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordField = new JPasswordField("12345"); // Test verisi
        passwordField.putClientProperty("JTextField.placeholderText", "••••••");
        passwordField.setPreferredSize(new Dimension(0, 35));

        fieldsPanel.add(lblEmail);
        fieldsPanel.add(emailField);
        fieldsPanel.add(lblPass);
        fieldsPanel.add(passwordField);

        // Giriş Butonu
        loginButton = new JButton("Giriş Yap");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(60, 120, 240)); // Modern Mavi
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 45));

        // Karta Ekle
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(lblSub);
        cardPanel.add(fieldsPanel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(loginButton);

        // Kartı Ortala
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245, 245, 250));
        centerWrapper.add(cardPanel);

        add(centerWrapper, BorderLayout.CENTER);

        // Alt Bilgi (Footer)
        JLabel footerLabel = new JLabel("Staj Yönetim Sistemi v1.0 - 2025", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(new EmptyBorder(10,0,10,0));
        add(footerLabel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        loginButton.addActionListener(e -> girisIslemiYap());

        // Enter Tuşu Desteği
        getRootPane().setDefaultButton(loginButton);
    }

    private void girisIslemiYap() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Giriş Yapılıyor...");

        // Arka planda çalıştır ki arayüz donmasın
        new SwingWorker<Kullanici, Void>() {
            @Override
            protected Kullanici doInBackground() {
                AuthDAO auth = new AuthDAO();
                return auth.girisYap(email, password);
            }

            @Override
            protected void done() {
                try {
                    Kullanici kullanici = get();
                    if (kullanici != null) {
                        dispose(); // Login ekranını kapat
                        new AnaEkran(kullanici).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginEkrani.this,
                                "Hatalı E-Posta veya Şifre!\nLütfen bilgilerinizi kontrol ediniz.",
                                "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
                        loginButton.setEnabled(true);
                        loginButton.setText("Giriş Yap");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        try {
            // FlatLaf Temasını Aktif Et (Mac/Win uyumlu)
            UIManager.put("Button.arc", 10); // Yuvarlak butonlar
            UIManager.put("Component.arc", 10); // Yuvarlak köşeler
            UIManager.put("TextComponent.arc", 10);
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginEkrani().setVisible(true));
    }
}