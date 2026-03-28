/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Gezegen sınıfı, simülasyondaki gezegenleri temsil eder. Her gezegen kendi zaman akışına sahiptir
* üzerinde bekleyen uzay araçlarını takip eder ve varış yapan araçları kaydeder. 
* Ayrıca, çıkış zamanı gelen araçları tespit ederek bunları hareket ettirir ve gezegenin toplam nüfusunu hesaplar.
* </p>
*/
package com.uzaysimulasyonu.model;

import com.uzaysimulasyonu.util.Zaman;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Gezegen {
    private String adi;
    private int gunSaatSayisi; // Her gezegenin günü farklı sürebilir.
    private Zaman mevcutZaman;
    private List<UzayAraci> bekleyenAraclar;
    private List<String> varisYapanAraclar;
    
    /**
     * Gezegen sınıfının yapıcı metodu
     */
    public Gezegen(String adi, int gunSaatSayisi, String baslangicTarihi) throws ParseException {
        this.adi = adi;
        this.gunSaatSayisi = gunSaatSayisi;
        this.mevcutZaman = new Zaman(baslangicTarihi, 0, 0); // Başlangıç saati 00:00 olarak ayarlanıyor
        this.bekleyenAraclar = new ArrayList<>();
        this.varisYapanAraclar = new ArrayList<>();
    }
    
    /**
     * Gezegendeki zamanı belirtilen saat kadar ilerletir.
     * Her gezegenin kendi gün uzunluğuna göre tarih ilerliyor
     * @param saatSayisi İlerletilecek saat sayısı
     */
    public void zamanIlerlet(int saatSayisi) {
        mevcutZaman.saatiIlerlet(saatSayisi, gunSaatSayisi);
    }
    
    /**
     * Bekleyen uzay aracını gezegene ekler
     * Aracın çıkış gezegeni bu gezegen olmalı ve durumu "Bekliyor" olmalı
     * @param arac Eklenecek uzay aracı
     */
    public void aracEkle(UzayAraci arac) {
        if (arac.getCikisGezegeni().equals(this.adi) && arac.getDurum().equals("Bekliyor")) {
            bekleyenAraclar.add(arac);
        }
    }
    
    /**
     * Gezegene varan uzay aracını kaydeder
     * Aynı araç birden fazla kez kaydedilmemeli
     * @param arac Varan uzay aracı
     */
    public void aracVarisYapti(UzayAraci arac) {
        if (arac.getVarisGezegeni().equals(this.adi) && !varisYapanAraclar.contains(arac.getAracAdi())) {
            varisYapanAraclar.add(arac.getAracAdi());
        }
    }
    
    /**
     * Gezegendeki bekleyen araçları kontrol eder ve 
     * çıkış tarihi gelen araçları hareket ettirir
     * Bu metot simülasyonun en kritik parçalarından biri,
     * zaman ilerledikçe çağrılarak araçların hareketini sağlıyor
     * @return Hareket eden araçların listesi
     */
    public List<UzayAraci> hareketEdecekAraclariKontrolEt() throws ParseException {
        List<UzayAraci> hareketEdenAraclar = new ArrayList<>();
        List<UzayAraci> kalacakAraclar = new ArrayList<>();
        
        // Bekleyen her bir aracı kontrol et
        for (UzayAraci arac : bekleyenAraclar) {
            try {
                // Aracın çıkış tarihi ile gezegenin mevcut tarihini karşılaştır
                Zaman cikisTarihi = new Zaman(arac.getCikisTarihi());
                
                // Günün tarihi, çıkış tarihine eşit veya sonra ise aracı hareket ettir
                // Burada sadece tarih kontrolü yapıyorum, saat kontrolü yapmıyorum
                // Bu kısım geliştirilebilir, saat de kontrol edilebilirdi
                if (mevcutZaman.tarihToString().equals(cikisTarihi.tarihToString())) {
                    arac.hareketEttir(mevcutZaman, gunSaatSayisi);
                    hareketEdenAraclar.add(arac);
                } else {
                    kalacakAraclar.add(arac);
                }
            } catch (ParseException e) {
                // Tarih çevirmede bir hata olursa, aracı yine de kalacaklar listesine ekle
                System.err.println("Tarih çevirme hatası: " + e.getMessage());
                kalacakAraclar.add(arac);
            }
        }
        
        // Bekleyen araçları güncelle - artık hareket edenleri çıkar.
        bekleyenAraclar = kalacakAraclar;
        
        return hareketEdenAraclar;
    }
    
    /**
     * Gezegendeki toplam nüfusu hesaplar (sadece hayatta olan kişiler)
     * Bekleyen araçlardaki ve varış yapmış araçlardaki kişileri sayar.
     * @param tumAraclar Tüm uzay araçlarının listesi
     * @return Gezegendeki toplam nüfus
     */
    public int toplamNufusHesapla(List<UzayAraci> tumAraclar) {
        int toplamNufus = 0;
        
        // Bütün araçlar içinde bu gezegene ait olanları bul.
        for (UzayAraci arac : tumAraclar) {
            // Eğer araç bu gezegende "Bekliyor" veya "Vardı" durumundaysa ve "İMHA" olmamışsa
            // || operatörü OR işlemi için kullanılıyor, && operatörü AND işlemi için
            if ((arac.getCikisGezegeni().equals(this.adi) && arac.getDurum().equals("Bekliyor"))
                    || (arac.getVarisGezegeni().equals(this.adi) && arac.getDurum().equals("Vardı"))) {
                toplamNufus += arac.hayattakiYolcuSayisi();
            }
        }
        
        return toplamNufus;
    }
    
    // Getter metodları
    public String getAdi() {
        return adi;
    }
    
    public int getGunSaatSayisi() {
        return gunSaatSayisi;
    }
    
    public Zaman getMevcutZaman() {
        return mevcutZaman;
    }
    
    // Döndürülen listeleri yeni bir liste olarak kopyalıyoruz.
    public List<UzayAraci> getBekleyenAraclar() {
        return new ArrayList<>(bekleyenAraclar);
    }
    
    public List<String> getVarisYapanAraclar() {
        return new ArrayList<>(varisYapanAraclar);
    }
    
    // toString metodu, nesneyi String olarak temsil eder
    // Java'da Object sınıfından gelen bir metodu override ediyoruz
    @Override
    public String toString() {
        return "Gezegen [adi=" + adi + ", gunSaatSayisi=" + gunSaatSayisi 
            + ", mevcutZaman=" + mevcutZaman + ", bekleyenAracSayisi=" + bekleyenAraclar.size() 
            + ", varisYapanAracSayisi=" + varisYapanAraclar.size() + "]";
    }
}