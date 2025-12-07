package org.example.Model;

public class Degerlendirme {
    private int puan;
    private String yorum;

    public Degerlendirme(int puan, String yorum) {
        this.puan = puan;
        this.yorum = yorum;
    }

    public int getPuan() { return puan; }
    public String getYorum() { return yorum; }
}