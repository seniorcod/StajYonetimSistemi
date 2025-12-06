package org.example.Model;

public class Danisman {
    private String id;
    private String adSoyad;

    public Danisman(String id, String adSoyad) {
        this.id = id;
        this.adSoyad = adSoyad;
    }

    public String getId() {
        return id;
    }

    // ComboBox'ta ismin düzgün görünmesi için bu şart
    @Override
    public String toString() {
        return adSoyad;
    }
}
