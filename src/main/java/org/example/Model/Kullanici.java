package org.example.Model;

public class Kullanici {
    private String id;
    private String adSoyad;
    private String rol;
    private String email;
    private String bagliSirketId; // YENİ ALAN (Sadece şirket yetkilisi için dolu olacak)

    public Kullanici(String id, String adSoyad, String rol, String email) {
        this.id = id;
        this.adSoyad = adSoyad;
        this.rol = rol;
        this.email = email;
    }

    // Şirket ID'sini sonradan set etmek için
    public void setBagliSirketId(String bagliSirketId) {
        this.bagliSirketId = bagliSirketId;
    }

    public String getBagliSirketId() { return bagliSirketId; }
    public String getId() { return id; }
    public String getAdSoyad() { return adSoyad; }
    public String getRol() { return rol; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return adSoyad + " (" + rol + ")"; }
}