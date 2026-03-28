/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Simulasyon sınıfı, tüm simülasyon işlemlerini yöneten ana sınıftır. 
* Gezegenleri, uzay araçlarını ve yoldaki araçları takip eder, simülasyonu saat bazında ilerletir ve her adımda durumu ekrana gösterir. 
* Araçların hareketlerini, yolcuların durumlarını ve gezegenlerin zaman akışını kontrol eder.
* </p>
*/
package com.uzaysimulasyonu.simulasyon;

import com.uzaysimulasyonu.model.Gezegen;
import com.uzaysimulasyonu.model.UzayAraci;
import com.uzaysimulasyonu.util.DosyaOkuma;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Simulasyon {
    private List<Gezegen> gezegenler;
    private List<UzayAraci> araclar;
    private List<UzayAraci> yoldakiAraclar;
    private boolean simulasyonBittiMi;
    private int simulasyonSaati; // Simülasyonun kaç saat ilerlediğini tutar
    
    // HashMapler eklemek performansı artırır. (arama işlemlerini O(n)'den O(1)'e düşürür.)
    private Map<String, Gezegen> gezegenlerMap;
    private Map<String, UzayAraci> araclarMap;
    
    // Simülasyon hızı ayarları
    private int simulasyonHizi = 1; // Kaç saatlik adımlarla ilerleyeceğini belirler
    private int ekranGuncellemeFrekansi = 1; // Kaç adımda bir ekranın güncelleneceğini belirler
    private int beklemeZamani = 500; // Milisaniye cinsinden her adım arasındaki bekleme süresi
    
    /**
     * Simulasyon sınıfının yapıcı metodu
     * Tüm veri dosyalarını okuyup simülasyon için gereken nesneleri oluşturur.
     * @param gezegenlerDosyaYolu Gezegenler.txt dosya yolu
     * @param araclarDosyaYolu Araclar.txt dosya yolu
     * @param kisilerDosyaYolu Kisiler.txt dosya yolu
     * @throws IOException Dosya okuma hatası
     * @throws ParseException Tarih çevirme hatası
     */
    public Simulasyon(String gezegenlerDosyaYolu, String araclarDosyaYolu, String kisilerDosyaYolu) 
            throws IOException, ParseException {
        // Dosyalardan verileri okur.
        Map<String, Object> veriMap = DosyaOkuma.tumDosyalariOku(
                gezegenlerDosyaYolu, araclarDosyaYolu, kisilerDosyaYolu);
        
        // Map'ten gelen verileri kendi değişkenlerime atıyorum
        // Cast işlemini burada yapıyorum
        this.gezegenler = (List<Gezegen>) veriMap.get("gezegenler");
        this.araclar = (List<UzayAraci>) veriMap.get("araclar");
        this.yoldakiAraclar = new ArrayList<>(); // Başlangıçta yolda araç yok
        this.simulasyonBittiMi = false;
        this.simulasyonSaati = 0; // Simülasyon saati sıfırdan başlar
        
        // Hızlı arama için HashMap'ler oluşturur.
        this.gezegenlerMap = new HashMap<>();
        for (Gezegen gezegen : gezegenler) {
            gezegenlerMap.put(gezegen.getAdi(), gezegen);
        }
        
        this.araclarMap = new HashMap<>();
        for (UzayAraci arac : araclar) {
            araclarMap.put(arac.getAracAdi(), arac);
        }
    }
    
    /**
     * Simülasyon hızını ayarlar
     * @param hiz Simülasyonun kaç saatlik adımlarla ilerleyeceği
     */
    public void simulasyonHiziniAyarla(int hiz) {
        if (hiz > 0) {
            this.simulasyonHizi = hiz;
        }
    }
    
    /**
     * Ekran güncelleme frekansını ayarlar
     * @param frekans Ekranın kaç adımda bir güncelleneceği
     */
    public void ekranGuncellemeFrekansiAyarla(int frekans) {
        if (frekans > 0) {
            this.ekranGuncellemeFrekansi = frekans;
        }
    }
    
    /**
     * Her adım arasındaki bekleme süresini ayarlar
     * @param beklemeZamani Milisaniye cinsinden bekleme süresi
     */
    public void beklemeZamaniAyarla(int beklemeZamani) {
        if (beklemeZamani >= 0) {
            this.beklemeZamani = beklemeZamani;
        }
    }
    
    /**
     * Gezegenler ve uzay araçları hakkında mevcut durumu ekrana yazdırır
     */
    public void durumGoster() {
        // Ekranı temizle - Bu işlemi daha verimli hale getiriyoruz
        // Sadece yeni satırlar ekleyerek "temizleme" yapıyoruz
        // Böylece işletim sistemi çağrısı ve bekleme süresi azalıyor
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // Simülasyon saatini göster
        System.out.println("İterasyon (saat): " + simulasyonSaati);
        System.out.println();
        System.out.println("UZAY SİMÜLASYONU");
        System.out.println("Hız Ayarları: Her adımda " + simulasyonHizi + " saat, Ekran güncelleme: " 
                          + ekranGuncellemeFrekansi + ", Bekleme: " + beklemeZamani + "ms");
        System.out.println();
        
        // Gezegenler bölümü - StringBuilder kullanarak string birleştirme optimizasyonu
        StringBuilder sb = new StringBuilder();
        
        sb.append("▼ GEZEGENLER DURUMU ▼\n");
        sb.append(String.format("%-15s", "Gezegenler:"));
        for (Gezegen gezegen : gezegenler) {
            sb.append(String.format("[%-10s]", gezegen.getAdi()));
        }
        sb.append("\n");
        
        // Tarih satırı
        sb.append(String.format("%-15s", "Tarih:"));
        for (Gezegen gezegen : gezegenler) {
            sb.append(String.format("%-12s", gezegen.getMevcutZaman().tarihToString()));
        }
        sb.append("\n");
        
        // Nüfus satırı
        sb.append(String.format("%-15s", "Nüfus:"));
        for (Gezegen gezegen : gezegenler) {
            int nufus = gezegen.toplamNufusHesapla(araclar);
            sb.append(String.format("%-12d", nufus));
        }
        sb.append("\n\n");
        
        // Uzay Araçları bölümü - Tablo formatında gösteriyoruz
        sb.append("▼ UZAY ARAÇLARI DURUMU ▼\n");
        sb.append(String.format("%-15s %-15s %-15s %-15s %-15s %-15s\n", 
                "Araç Adı", "Durum", "Çıkış", "Varış", "Kalan Saat", "Varış Tarihi"));
        
        // Tablo ayırıcısı
        sb.append("───────────────────────────────────────────────────────────────────────────────────\n");
        
        for (UzayAraci arac : araclar) {
            String durum = arac.getDurum();
            // Duruma göre gösterilecek bilgiler değişiyor
            String hedefeKalanSaat = durum.equals("İMHA") || durum.equals("Vardı") ? "0" : 
                                    durum.equals("Bekliyor") ? String.valueOf(arac.getMesafe()) : 
                                    String.valueOf(arac.getHedefeKalanSaat());
            String hedefeVarisZamani = durum.equals("İMHA") || durum.equals("Bekliyor") ? "--" : arac.getHedefeVarisZamani();
            
            sb.append(String.format("%-15s %-15s %-15s %-15s %-15s %-15s\n", 
                    arac.getAracAdi(), durum, arac.getCikisGezegeni(), arac.getVarisGezegeni(), 
                    hedefeKalanSaat, hedefeVarisZamani));
        }
        sb.append("\n");
        
        // Simülasyon bittiğinde mesaj göster
        if (simulasyonBittiMi) {
            sb.append("Simülasyon tamamlandı! Tüm araçlar hedeflerine ulaştı veya imha oldu.");
        }
        
        // Bir seferde tüm çıktıyı yaz (daha verimli)
        System.out.print(sb.toString());
    }
    
    /**
     * Simülasyonu belirtilen saat kadar ilerletir
     * @param saatSayisi İlerletilecek saat sayısı
     * @throws ParseException Tarih çevirme hatası
     */
    public void saatleriIlerlet(int saatSayisi) throws ParseException {
        // TÜM ARAÇLARIN KİŞİLERİNİN ÖMRÜNÜ AZALT
        // İmha olmamış tüm araçlardaki kişilerin ömrünü azalt
        for (UzayAraci arac : araclar) {
            if (!arac.getDurum().equals("İMHA")) {
                arac.yolcularinOmrunuAzalt(saatSayisi);
            }
        }
        
        // Tüm gezegenlerin zamanını ilerlet
        for (Gezegen gezegen : gezegenler) {
            gezegen.zamanIlerlet(saatSayisi); // Her gezegenin kendi günü var
            
            // Çıkış tarihi gelen araçları hareket ettir
            List<UzayAraci> hareketEdenAraclar = gezegen.hareketEdecekAraclariKontrolEt();
            yoldakiAraclar.addAll(hareketEdenAraclar); // Hareket eden araçları yoldaki araçlara ekle
        }
        
        // Yoldaki araçları ilerlet ve varış kontrolü yap
        List<UzayAraci> varanAraclar = new ArrayList<>();
        for (UzayAraci arac : yoldakiAraclar) {
            // Arac imha olmadıysa yolda ilerlet
            if (!arac.getDurum().equals("İMHA")) {
                boolean varildiMi = arac.saatiIlerlet(saatSayisi);
                
                // Araç vardıysa varış gezegenine ekle
                if (varildiMi) {
                    varanAraclar.add(arac);
                    
                    // HashMap ile varış gezegenini hızlıca bul
                    Gezegen varisGezegeni = gezegenlerMap.get(arac.getVarisGezegeni());
                    if (varisGezegeni != null) {
                        varisGezegeni.aracVarisYapti(arac);
                    }
                }
            }
        }
        
        // Varan araçları yoldaki araçlardan çıkar
        yoldakiAraclar.removeAll(varanAraclar);
        
        // Simülasyon saatini ilerlet
        simulasyonSaati += saatSayisi;
        
        // Tüm araçların varıp varmadığını kontrol et
        simulasyonBittiMi = tumAraclarVarildiMi();
    }
    
    /**
     * Tüm araçların varış noktasına ulaşıp ulaşmadığını kontrol eder
     * @return Tüm araçlar vardıysa/imha olduysa true, aksi halde false
     */
    private boolean tumAraclarVarildiMi() {
        for (UzayAraci arac : araclar) {
            String durum = arac.getDurum();
            if (!durum.equals("Vardı") && !durum.equals("İMHA")) {
                return false; // Hala yolda veya bekleyen araç var
            }
        }
        return true; // Tüm araçlar ya vardı ya da imha oldu
    }
    
    /**
     * Simülasyonu çalıştırır, ana döngü burada
     * @throws ParseException Tarih çevirme hatası
     * @throws InterruptedException Thread sleep hatası
     */
    public void simulasyonuCalistir() throws ParseException, InterruptedException {
        // Başlangıç durumunu göster
        durumGoster();
        
        int adimSayisi = 0;
        
        // Simülasyon bitene kadar devam et
        while (!simulasyonBittiMi) {
            saatleriIlerlet(simulasyonHizi); // Birden fazla saati birden ilerlet
            adimSayisi++;
            
            // Sadece belirli aralıklarla ekranı güncelle
            if (adimSayisi % ekranGuncellemeFrekansi == 0) {
                durumGoster(); // Güncel durumu göster
                
                // Her adım sonrası bekleme süresi ekle
                if (beklemeZamani > 0) {
                    Thread.sleep(beklemeZamani);
                }
            }
        }
        
        // Son durumu göster
        durumGoster();
    }
    
    // Getter metodları - dışarıdan erişim için.
    public List<Gezegen> getGezegenler() {
        return new ArrayList<>(gezegenler); // Liste kopyası dön
    }
    
    public List<UzayAraci> getAraclar() {
        return new ArrayList<>(araclar); // Liste kopyası dön
    }
    
    public boolean isSimulasyonBittiMi() {
        return simulasyonBittiMi;
    }
    
    public int getSimulasyonSaati() {
        return simulasyonSaati;
    }
}