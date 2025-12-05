package org.example.Model;

public class Kullanici {
    private String id;
    private String adSoyad;
    private String rol; // "OGRENCI", "DANISMAN", "SIRKET"
    private String email;

    public Kullanici(String id, String adSoyad, String rol, String email) {
        this.id = id;
        this.adSoyad = adSoyad;
        this.rol = rol;
        this.email = email;
    }

    public String getId() { return id; }
    public String getAdSoyad() { return adSoyad; }
    public String getRol() { return rol; }
}