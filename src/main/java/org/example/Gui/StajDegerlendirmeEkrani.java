package org.example.Gui;

import org.example.DataAccessLayer.DegerlendirmeDAO;
import org.example.DataAccessLayer.RaporDAO;
import org.example.DataAccessLayer.StajDAO;
import org.example.Model.Rapor;

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

public class StajDegerlendirmeEkrani extends JFrame {

    private String stajId;
    private String ogrenciAdi;
    private String danismanId;
    private String danismanAdi;
    private DefaultTableModel tableModel;
    private JTable table;
    private Point initialClick;

    // Renkler
    private final Color COLOR_WARNING = new Color(230, 126, 34); // Turuncu
    private final Color COLOR_SUCCESS = new Color(46, 204, 113); // Yeşil

    public StajDegerlendirmeEkrani(String stajId, String ogrenciAdi, String danismanId, String danismanAdi) {
        this.stajId = stajId;
        this.ogrenciAdi = ogrenciAdi;
        this.danismanId = danismanId;
        this.danismanAdi = danismanAdi;

        // --- PENCERE AYARLARI ---
        setUndecorated(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 20, 20));

        // --- ARKA PLAN ---
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

        // --- KART ---
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
        cardPanel.setBounds(20, 50, 760, 530);
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(cardPanel);

        // --- KAPAT BUTONU (X) ---
        JLabel lblClose = new JLabel("X");
        lblClose.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblClose.setForeground(Color.WHITE);
        lblClose.setBounds(750, 15, 40, 40);
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dispose(); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(new Color(255, 100, 100)); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
        });
        mainPanel.add(lblClose);
        mainPanel.setComponentZOrder(lblClose, 0);

        // --- BAŞLIK ---
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Değerlendirme: " + ogrenciAdi);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(51, 51, 51));

        JLabel lblAdvisor = new JLabel("Danışman: " + danismanAdi);
        lblAdvisor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdvisor.setForeground(new Color(100, 149, 237));

        JLabel lblSub = new JLabel("Raporları incelemek için çift tıklayınız.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        headerPanel.add(lblTitle);
        headerPanel.add(lblAdvisor);
        headerPanel.add(lblSub);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABLO ---
        String[] columns = {"Rapor ID", "Tür", "Tarih", "Dosya Yolu"};
        createModernTable(columns);
        raporlariYukle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnPanel.setOpaque(false);

        JButton btnDuzeltme = createModernButton("Düzeltme İste", COLOR_WARNING, "warning.png");
        JButton btnOnayla = createModernButton("Onayla ve Not Ver", COLOR_SUCCESS, "check.png");

        btnPanel.add(btnDuzeltme);
        btnPanel.add(btnOnayla);
        cardPanel.add(btnPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String dosyaYolu = (String) tableModel.getValueAt(row, 3);
                        dosyayiAc(dosyaYolu);
                    }
                }
            }
        });

        btnDuzeltme.addActionListener(e -> {
            String mesaj = showModernInputDialog("Düzeltme Talebi", "Öğrenciye iletilecek notu giriniz:", COLOR_WARNING);
            if (mesaj != null && !mesaj.trim().isEmpty()) {
                StajDAO dao = new StajDAO();
                if (dao.stajDuzeltmeIste(stajId, mesaj)) {
                    JOptionPane.showMessageDialog(this, "✅ Düzeltme talebi iletildi.");
                    dispose();
                } else { JOptionPane.showMessageDialog(this, "Hata oluştu."); }
            }
        });

        btnOnayla.addActionListener(e -> openModernGradingDialog());
    }

    // --- MODERN INPUT DIALOG (RENKLİ BAŞLIKLI) ---
    private String showModernInputDialog(String title, String message, Color headerColor) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 240);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 240, 20, 20));

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // RENKLİ BAŞLIK ŞERİDİ
        JPanel header = new JPanel();
        header.setBounds(0, 0, 400, 10);
        header.setBackground(headerColor);
        pnl.add(header);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(20, 30, 350, 30);
        pnl.add(lblTitle);

        JLabel lblMsg = new JLabel(message);
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(Color.GRAY);
        lblMsg.setBounds(20, 65, 350, 20);
        pnl.add(lblMsg);

        JTextField txtInput = new JTextField();
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtInput.setBounds(20, 95, 360, 40);
        txtInput.putClientProperty("Component.arc", 999);
        pnl.add(txtInput);

        JButton btnOk = createModernButton("Gönder", headerColor, "");
        btnOk.setBounds(20, 160, 170, 40);
        btnOk.setPreferredSize(new Dimension(170, 40));

        JButton btnCancel = new JButton("İptal");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setForeground(Color.GRAY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setBounds(210, 160, 100, 40);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dialog.dispose());

        pnl.add(btnOk);
        pnl.add(btnCancel);
        dialog.add(pnl);

        final String[] result = {null};
        btnOk.addActionListener(e -> { result[0] = txtInput.getText(); dialog.dispose(); });

        dialog.setVisible(true);
        return result[0];
    }

    // --- MODERN NOT VERME PENCERESİ (RENKLİ BAŞLIKLI) ---
    private void openModernGradingDialog() {
        JDialog dialog = new JDialog(this, "Not Girişi", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 370, 20, 20));

        JPanel pnlMain = new JPanel(null);
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // RENKLİ BAŞLIK ŞERİDİ (YEŞİL)
        JPanel header = new JPanel();
        header.setBounds(0, 0, 400, 10);
        header.setBackground(COLOR_SUCCESS);
        pnlMain.add(header);

        JLabel lblHead = new JLabel("Staj Değerlendirme");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHead.setBounds(20, 30, 300, 30);
        pnlMain.add(lblHead);

        JLabel lblPuan = new JLabel("Puan (0-100):");
        lblPuan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPuan.setBounds(20, 80, 100, 20);
        pnlMain.add(lblPuan);

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(85, 0, 100, 1));
        spinner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        spinner.setBounds(20, 105, 360, 40);
        pnlMain.add(spinner);

        JLabel lblYorum = new JLabel("Danışman Yorumu:");
        lblYorum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblYorum.setBounds(20, 160, 150, 20);
        pnlMain.add(lblYorum);

        JTextField txtYorum = new JTextField();
        txtYorum.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtYorum.setBounds(20, 185, 360, 40);
        pnlMain.add(txtYorum);

        JButton btnKaydet = createModernButton("Kaydet ve Bitir", COLOR_SUCCESS, "save.png");
        btnKaydet.setBounds(20, 250, 360, 45);
        pnlMain.add(btnKaydet);

        JButton btnIptal = new JButton("İptal");
        btnIptal.setBounds(150, 310, 100, 30);
        btnIptal.setBorderPainted(false); btnIptal.setContentAreaFilled(false);
        btnIptal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIptal.addActionListener(e -> dialog.dispose());
        pnlMain.add(btnIptal);

        btnKaydet.addActionListener(ev -> {
            int puan = (int) spinner.getValue();
            String yorum = txtYorum.getText();
            DegerlendirmeDAO dao = new DegerlendirmeDAO();
            if (dao.degerlendirmeEkle(stajId, danismanId, puan, yorum)) {
                JOptionPane.showMessageDialog(dialog, "✅ İşlem Tamamlandı!");
                dialog.dispose();
                this.dispose();
            }
        });

        dialog.add(pnlMain);
        dialog.setVisible(true);
    }

    // --- YARDIMCI METODLAR ---
    private void raporlariYukle() {
        tableModel.setRowCount(0);
        RaporDAO dao = new RaporDAO();
        List<Rapor> raporlar = dao.stajRaporlariniGetir(stajId);
        for (Rapor r : raporlar) {
            tableModel.addRow(new Object[]{r.getRaporId(), r.getRaporTipi(), r.getYuklemeTarihi(), r.getDosyaYolu()});
        }
    }

    private void dosyayiAc(String dosyaYolu) {
        try {
            File file = new File(dosyaYolu);
            if (file.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
            else JOptionPane.showMessageDialog(this, "Dosya açılamadı.\nYol: " + dosyaYolu);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void createModernTable(String[] columns) {
        tableModel = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int row, int col) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    private JButton createModernButton(String text, Color bgColor, String iconName) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
        btn.setPreferredSize(new Dimension(220, 45));

        try {
            URL imgUrl = getClass().getResource("/icons/" + iconName);
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(8);
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
}