package org.example.Gui;

import com.formdev.flatlaf.FlatLightLaf; // Tema kütüphanesi (Varsa)
import org.example.DataAccessLayer.AuthDAO;
import org.example.Model.Kullanici;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginEkrani extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginEkrani() {
        // Pencere Ayarları
        setTitle("Staj Yönetim Sistemi - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında açılır
        setLayout(new BorderLayout());

        // --- BAŞLIK KISMI ---
        JLabel titleLabel = new JLabel("Hoşgeldiniz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- FORM KISMI (ORTA) ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("E-Posta:"));
        emailField = new JTextField("ali.yilmaz@ogr.edu.tr"); // Test için hazır doldurdum
        formPanel.add(emailField);

        formPanel.add(new JLabel("Şifre:"));
        passwordField = new JPasswordField("12345"); // Test için hazır doldurdum
        formPanel.add(passwordField);

        formPanel.add(new JLabel("")); // Boşluk
        loginButton = new JButton("Giriş Yap");
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);

        // --- BUTON AKSİYONU ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                girisIslemiYap();
            }
        });

        // Enter tuşuna basınca da giriş yapsın
        getRootPane().setDefaultButton(loginButton);
    }

    private void girisIslemiYap() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- ARKADAŞININ YAZDIĞI BACKEND BURADA ÇALIŞIYOR ---
        AuthDAO auth = new AuthDAO();
        System.out.println("Veritabanına bağlanılıyor...");

        try {
            Kullanici kullanici = auth.girisYap(email, password);

            if (kullanici != null) {
                // Giriş Başarılı
                JOptionPane.showMessageDialog(this, "Hoşgeldin: " + kullanici.getAdSoyad() + "\nRol: " + kullanici.getRol());

                // Buradan sonra Ana Ekrana yönlendireceğiz
                this.dispose(); // Login ekranını kapat
                new AnaEkran(kullanici).setVisible(true); // Ana ekranı aç (Aşağıda kodu var)

            } else {
                // Giriş Başarısız
                JOptionPane.showMessageDialog(this, "Hatalı E-Posta veya Şifre!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Tema ayarı (Görünümü güzelleştirir)
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.out.println("Tema yüklenemedi, varsayılan kullanılıyor.");
        }

        SwingUtilities.invokeLater(() -> {
            new LoginEkrani().setVisible(true);
        });
    }
}