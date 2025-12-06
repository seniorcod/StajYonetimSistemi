package org.example.Model;

public class Sirket {
    private String id;
    private String ad;

    public Sirket(String id, String ad) {
        this.id = id;
        this.ad = ad;
    }

    public String getId() { return id; }
    public String getAd() { return ad; }

    // ComboBox ekranda ne göstereceğini bu metoda bakarak karar verir
    @Override
    public String toString() {
        return ad;
    }
}
