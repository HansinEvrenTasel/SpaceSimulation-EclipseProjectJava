/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Kisi sınıfı, simülasyondaki yolcuları temsil eder. 
* Her kişinin bir ismi, yaşı, kalan ömrü (saat cinsinden), bulunduğu uzay aracı ve hayatta olup olmadığı bilgisi bulunur.
* Kişilerin ömrü zamanla azalır ve ömrü tükendiğinde kişi hayatını kaybeder.
* </p>
*/
package com.uzaysimulasyonu.model;

public class Kisi {
    private String isim;
    private int yas;
    private int kalanOmur; // Kişinin kalan ömrü saat cinsinden tutuluyor
    private String bulunduguUzayAraciAdi;
    private boolean hayattaMi;
    
    /**
     * Kişi sınıfının yapıcı metodu
     * Bütün kişiler başlangıçta hayatta olarak işaretleniyor
     */
    public Kisi(String isim, int yas, int kalanOmur, String bulunduguUzayAraciAdi) {
        this.isim = isim;
        this.yas = yas;
        this.kalanOmur = kalanOmur;
        this.bulunduguUzayAraciAdi = bulunduguUzayAraciAdi;
        this.hayattaMi = true; // Başlangıçta herkes hayatta
    }
    
    /**
     * Kişinin ömrünü belirtilen saat kadar azaltır.
     * Simülasyon ilerledikçe bu metot çağrılarak kişinin yaşam durumu kontrol ediliyor.
     * @param saatSayisi Azaltılacak saat sayısı
     * @return Kişi hayatta ise true, ölmüşse false
     */
    public boolean omruAzalt(int saatSayisi) {
        this.kalanOmur -= saatSayisi;
        
        // Kalan ömür 0 veya daha az ise kişi ölmüş demektir
        if (this.kalanOmur <= 0) {
            this.kalanOmur = 0; // Negatif değer olmasını önle
            this.hayattaMi = false;
            return false; // Kişi artık hayatta değil
        }
        return true; // Kişi hala hayatta
    }
    
    /**
     * Kişinin bulunduğu uzay aracını değiştirir.
     * Bu metot, kişi bir araçtan diğerine transfer olursa kullanılabilir.
     * (Şu anki simülasyonda böyle bir durum yok ama gelecekte ekleyebilirim)
     * @param yeniUzayAraciAdi Kişinin geçeceği yeni aracın adı
     */
    public void uzayAraciniDegistir(String yeniUzayAraciAdi) {
        this.bulunduguUzayAraciAdi = yeniUzayAraciAdi;
    }
    
    // Getter metodları
    public String getIsim() {
        return isim;
    }
    
    public int getYas() {
        return yas;
    }
    
    public int getKalanOmur() {
        return kalanOmur;
    }
    
    public String getBulunduguUzayAraciAdi() {
        return bulunduguUzayAraciAdi;
    }
    
    public boolean isHayattaMi() {
        return hayattaMi;
    }
    
    // System.out.println(kisi) gibi bir çağrı yapıldığında bu metot çalışır
    @Override
    public String toString() {
        return "Kisi [isim=" + isim + ", yas=" + yas + ", kalanOmur=" + kalanOmur 
            + ", bulunduguUzayAraciAdi=" + bulunduguUzayAraciAdi + ", hayattaMi=" + hayattaMi + "]";
    }
}