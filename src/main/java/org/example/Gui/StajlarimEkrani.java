package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.DataAccessLayer.StajDAO;
import org.example.Model.Basvuru;
import org.example.Model.Kullanici;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URL;
import java.util.List;

public class StajlarimEkrani extends JFrame {

    private final Kullanici kullanici;
    private DefaultTableModel tableModel;
    private JTable table;
    private Point initialClick;

    // Renk Paleti
    private final Color COLOR_UPLOAD = new Color(155, 89, 182); // Mor
    private final Color COLOR_RESULT = new Color(46, 204, 113); // Yeşil
    private final Color COLOR_ALERT = new Color(231, 76, 60);   // Kırmızı
    private final Color COLOR_CLOSE = new Color(149, 165, 166); // Gri

    public StajlarimEkrani(Kullanici kullanici) {
        this.kullanici = kullanici;

        // --- PENCERE AYARLARI ---
        setUndecorated(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 20, 20));

        // --- 1. ARKA PLAN ---
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

        // --- 2. ORTA KART ---
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 30, 30);
            }
        };
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setOpaque(false);
        cardPanel.setBounds(20, 50, 860, 530);
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(cardPanel);

        // --- 3. İÇERİK ---

        // KAPAT BUTONU (MousePressed ile Hızlandırıldı)
        JLabel lblClose = new JLabel("X");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblClose.setForeground(Color.WHITE);
        lblClose.setBounds(850, 15, 30, 30);
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dispose(); } // <-- BURASI DEĞİŞTİ (Clicked -> Pressed)
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(new Color(255, 100, 100)); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
        });
        mainPanel.add(lblClose);
        mainPanel.setComponentZOrder(lblClose, 0);

        // Başlık
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Stajlarım");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(51, 51, 51));

        JLabel lblSub = new JLabel("Rapor yükleme ve sonuç görüntüleme işlemleri");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // Tablo
        String[] columns = {"Staj ID", "Şirket", "Pozisyon", "Başlangıç", "Durum"};
        createModernTable(columns);
        verileriYukle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // Butonlar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnNotOku = createModernButton("Düzeltme Notu", COLOR_ALERT, "alert.png");
        JButton btnBitir = createModernButton("Rapor Yükle", COLOR_UPLOAD, "upload.png");
        JButton btnSonuc = createModernButton("Sonucu Gör", COLOR_RESULT, "contract.png");

        btnPanel.add(btnNotOku);
        btnPanel.add(btnBitir);
        btnPanel.add(btnSonuc);
        cardPanel.add(btnPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---

        btnBitir.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen bir staj seçiniz."); return; }

            String stajId = (String) tableModel.getValueAt(row, 0);
            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Aktif") || durum.equals("Stajda") || durum.equals("Düzeltme Gerekli")) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Staj Raporunu Seçiniz (PDF)");
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    StajDAO dao = new StajDAO();
                    if (dao.stajiBitirVeRaporla(stajId, file.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(this, "✅ Rapor başarıyla yüklendi.");
                        verileriYukle();
                    }
                }
            } else if (durum.equals("Danışmana Gönderildi")) {
                JOptionPane.showMessageDialog(this, "Raporunuz şu an incelemede.");
            } else {
                JOptionPane.showMessageDialog(this, "Bu işlem için stajın aktif olması gerekir.");
            }
        });

        btnNotOku.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen bir staj seçiniz."); return; }
            String stajId = (String) tableModel.getValueAt(row, 0);
            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Düzeltme Gerekli")) {
                StajDAO dao = new StajDAO();
                String not = dao.getDuzeltmeNotu(stajId);

                JTextArea textArea = new JTextArea(not);
                textArea.setLineWrap(true); textArea.setWrapStyleWord(true); textArea.setEditable(false);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                textArea.setBackground(new Color(240, 240, 240));
                JScrollPane scrollMsg = new JScrollPane(textArea);
                scrollMsg.setPreferredSize(new Dimension(350, 150));
                scrollMsg.setBorder(BorderFactory.createEmptyBorder());
                JOptionPane.showMessageDialog(this, scrollMsg, "Hoca Düzeltme Notu", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Bu stajda düzeltme talebi yok.");
            }
        });

        btnSonuc.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen bir staj seçiniz."); return; }

            String stajId = (String) tableModel.getValueAt(row, 0);
            String durum = (String) tableModel.getValueAt(row, 4);

            if (durum.equals("Tamamlandı")) {
                DegerlendirmeDAO dao = new DegerlendirmeDAO();
                String[] sonuc = dao.getDegerlendirmeSonucu(stajId);
                if (sonuc != null) { showModernResultDialog(sonuc[0], sonuc[1]); }
            } else { JOptionPane.showMessageDialog(this, "Bu staj henüz değerlendirilmedi."); }
        });
    }

    // --- MODERN SONUÇ PENCERESİ (MousePressed İle) ---
    private void showModernResultDialog(String puan, String yorum) {
        JDialog dialog = new JDialog(this, "Staj Sonucu", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 350, 25, 25));

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // -- KAPAT BUTONU (X) ---
        JLabel lblClose = new JLabel("X");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblClose.setForeground(Color.GRAY);
        lblClose.setBounds(365, 10, 30, 30);
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dialog.dispose(); } // <-- BURASI DEĞİŞTİ
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(Color.GRAY); }
        });
        pnl.add(lblClose);

        JLabel lblHeader = new JLabel("Staj Sonucu", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 50, 50));
        lblHeader.setBounds(0, 20, 400, 30);
        pnl.add(lblHeader);

        JLabel lblScoreTitle = new JLabel("PUAN", SwingConstants.CENTER);
        lblScoreTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblScoreTitle.setForeground(Color.GRAY);
        lblScoreTitle.setBounds(0, 60, 400, 20);
        pnl.add(lblScoreTitle);

        JLabel lblScore = new JLabel(puan, SwingConstants.CENTER);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblScore.setForeground(new Color(46, 204, 113));
        lblScore.setBounds(0, 80, 400, 60);
        pnl.add(lblScore);

        JLabel lblCommentTitle = new JLabel("Danışman Yorumu:", SwingConstants.LEFT);
        lblCommentTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCommentTitle.setForeground(Color.GRAY);
        lblCommentTitle.setBounds(30, 160, 340, 20);
        pnl.add(lblCommentTitle);

        JTextArea txtComment = new JTextArea(yorum);
        txtComment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtComment.setLineWrap(true); txtComment.setWrapStyleWord(true); txtComment.setEditable(false);
        txtComment.setBackground(new Color(245, 247, 250));
        txtComment.setForeground(new Color(60, 60, 60));
        txtComment.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollComment = new JScrollPane(txtComment);
        scrollComment.setBounds(30, 185, 340, 80);
        scrollComment.setBorder(BorderFactory.createEmptyBorder());
        pnl.add(scrollComment);

        JButton btnClose = createModernButton("Kapat", COLOR_CLOSE, "");
        btnClose.setBounds(125, 290, 150, 40);
        btnClose.addActionListener(e -> dialog.dispose());
        pnl.add(btnClose);

        dialog.add(pnl);
        dialog.setVisible(true);
    }

    private void verileriYukle() {
        tableModel.setRowCount(0);
        StajDAO dao = new StajDAO();
        List<Basvuru> stajlar = dao.getOgrenciStajlari(kullanici.getId());
        for (Basvuru b : stajlar) {
            tableModel.addRow(new Object[]{b.getBasvuruId(), b.getSirketAd(), b.getPozisyon(), b.getBaslangicTarihi(), b.getDurum()});
        }
    }

    private void createModernTable(String[] columns) {
        tableModel = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int row, int col) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(0, 45));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        try { table.getColumnModel().getColumn(4).setCellRenderer(new ModernStatusRenderer()); } catch (Exception e) {}
    }

    private JButton createModernButton(String text, Color bgColor, String iconName) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = isHovered ? bgColor.brighter() : bgColor;
                Color c2 = isHovered ? bgColor.darker() : bgColor.darker();
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
            }
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
                });
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        try {
            URL imgUrl = getClass().getResource("/icons/" + iconName);
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(10);
            }
        } catch (Exception e) {}
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

    class ModernStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            label.setText("  " + status + "  ");
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(true);
            if (status.contains("Tamam") || status.contains("Onay")) {
                label.setForeground(new Color(21, 87, 36));
                label.setBackground(new Color(212, 237, 218));
            } else if (status.contains("Düzeltme") || status.contains("Red")) {
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