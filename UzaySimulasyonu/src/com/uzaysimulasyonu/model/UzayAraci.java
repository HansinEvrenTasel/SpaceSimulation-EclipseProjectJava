/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Bu sınıf simülasyondaki uzay araçlarını temsil eder. 
* Her aracın çıkış ve varış gezegenleri, hareket durumu, kalan mesafesi ve içindeki yolcuları vardır. 
* Araçlar "Bekliyor", "Yolda", "Vardı" veya "İMHA" durumlarından birinde olabilir. 
* Yolcuların ömrü tükenirse araç imha olabilir.
* </p>
*/
package com.uzaysimulasyonu.model;

import com.uzaysimulasyonu.util.Zaman;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class UzayAraci {
    private String aracAdi;
    private String cikisGezegeni;
    private String varisGezegeni;
    private String cikisTarihi;
    private int mesafe; // Aracın kat edeceği mesafe saat cinsinden (örn. 100 saat süren yolculuk)
    private String durum; // Aracın durumu: "Bekliyor", "Yolda", "Vardı", "İMHA"
    private int hedefeKalanSaat;
    private String hedefeVarisZamani;
    private List<Kisi> yolcular;
    
    /**
     * UzayAraci sınıfının yapıcı metodu
     * Araç başlangıçta "Bekliyor" durumunda oluşturuluyor.
     */
    public UzayAraci(String aracAdi, String cikisGezegeni, String varisGezegeni, String cikisTarihi, int mesafe) {
        this.aracAdi = aracAdi;
        this.cikisGezegeni = cikisGezegeni;
        this.varisGezegeni = varisGezegeni;
        this.cikisTarihi = cikisTarihi;
        this.mesafe = mesafe;
        this.durum = "Bekliyor";
        this.hedefeKalanSaat = mesafe; // Başlangıçta kalan mesafe toplam mesafeye eşit
        this.hedefeVarisZamani = "--"; // Başlangıçta hesaplanmamış durumdaki varış zamanı
        this.yolcular = new ArrayList<>(); // Boş bir yolcu listesi oluşturur
    }
    
    /**
     * Uzay aracına yolcu ekler
     * @param kisi Eklenecek kişi
     */
    public void yolcuEkle(Kisi kisi) {
        yolcular.add(kisi);
    }
    
    /**
     * Uzay aracındaki tüm yolcuların ömrünü belirtilen saat kadar azaltır.
     * Simülasyonun her adımında çağrılıyor ve yolcuların durumunu güncelliyor.
     * @param saatSayisi Azaltılacak saat sayısı
     * @return Hayatta kalan yolcu sayısı
     */
    public int yolcularinOmrunuAzalt(int saatSayisi) {
        int hayattaKalanlar = 0;
        
        // Her yolcunun ömrünü azalt ve hayatta kalanları say.
        for (Kisi kisi : yolcular) {
            // Sadece hayatta olan kişilerin ömrünü azalt.
            if (kisi.isHayattaMi() && kisi.omruAzalt(saatSayisi)) {
                hayattaKalanlar++;
            }
        }
        
        // Eğer tüm yolcular ölmüşse ve araçta yolcu varsa, aracı imha et
        if (hayattaKalanlar == 0 && !yolcular.isEmpty()) {
            this.durum = "İMHA";
            this.hedefeKalanSaat = 0; // Artık hedefe gitmeyeceği için kalan saati sıfırla
            this.hedefeVarisZamani = "--"; // Varış zamanını iptal et
        }
        
        return hayattaKalanlar;
    }
    
    /**
     * Uzay aracını hareket ettirir (seyahate başlatır)
     * Çıkış gezegenindeki tarih ve zaman bilgisine göre varış zamanını hesaplar
     * @param gezegenTarih Gezegendeki mevcut zaman
     * @param gezegenGunUzunlugu Varış gezegeninin günü kaç saat
     */
    public void hareketEttir(Zaman gezegenTarih, int gezegenGunUzunlugu) throws ParseException {
        this.durum = "Yolda"; // Durumu güncelle
        
        // Varış zamanını hesapla
        // Başlangıç zamanına mesafe kadar saat ekleyerek varış zamanını buluyoruz.
        // Gezegenin gün uzunluğuna göre tarih hesabı değişebilir.
        this.hedefeVarisZamani = gezegenTarih.belirliSaatSonraTarih(this.mesafe, gezegenGunUzunlugu);
    }
    
    /**
     * Uzay aracını belirtilen süre kadar ilerletir.
     * Simülasyonun her saatlik iterasyonunda yoldaki araçlar için çağrılır.
     * @param saatSayisi İlerletilecek saat sayısı
     * @return Hedefe varıldıysa true, aksi halde false
     */
    public boolean saatiIlerlet(int saatSayisi) {
        if (durum.equals("Yolda")) {
            this.hedefeKalanSaat -= saatSayisi;
            
            // Eğer hedefe ulaşıldıysa durumu güncelle
            if (this.hedefeKalanSaat <= 0) {
                this.hedefeKalanSaat = 0; // Negatif değer olmasını önle
                this.durum = "Vardı"; // Aracın durumunu güncelle
                return true; // Hedefe varıldı.
            }
        }
        return false; // Hala yolda veya başka bir durumda
    }
    
    /**
     * Uzay aracındaki hayatta olan yolcuların sayısını döndürür
     * Gezegenlerin nüfus hesabında kullanılıyor
     * @return Hayatta olan yolcu sayısı
     */
    public int hayattakiYolcuSayisi() {
        int sayac = 0;
        for (Kisi kisi : yolcular) {
            if (kisi.isHayattaMi()) {
                sayac++;
            }
        }
        return sayac;
    }
    
    // Dışarıdan erişim için getter metotları yazdık
    public String getAracAdi() {
        return aracAdi;
    }
    
    public String getCikisGezegeni() {
        return cikisGezegeni;
    }
    
    public String getVarisGezegeni() {
        return varisGezegeni;
    }
    
    public String getCikisTarihi() {
        return cikisTarihi;
    }
    
    public int getMesafe() {
        return mesafe;
    }
    
    public String getDurum() {
        return durum;
    }
    
    public int getHedefeKalanSaat() {
        return hedefeKalanSaat;
    }
    
    public String getHedefeVarisZamani() {
        return hedefeVarisZamani;
    }
    
    // Listeyi değiştirilememesi için kopyalayarak dönüyoruz
    public List<Kisi> getYolcular() {
        return new ArrayList<>(yolcular); // Liste kopyası dön
    }
    
    // Varış zamanını dışarıdan ayarlamak için setter metodu
    // Diğer değişkenler için setter eklemedim çünkü değiştirilmelerine gerek yok
    public void setHedefeVarisZamani(String hedefeVarisZamani) {
        this.hedefeVarisZamani = hedefeVarisZamani;
    }
    
    // Debug ve log amaçlı toString metodu
    @Override
    public String toString() {
        return "UzayAraci [aracAdi=" + aracAdi + ", cikisGezegeni=" + cikisGezegeni + ", varisGezegeni=" 
            + varisGezegeni + ", cikisTarihi=" + cikisTarihi + ", mesafe=" + mesafe + ", durum=" 
            + durum + ", hedefeKalanSaat=" + hedefeKalanSaat + ", hedefeVarisZamani=" + hedefeVarisZamani + "]";
    }
}