/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* DosyaOkuma sınıfı, simülasyon için gerekli verileri dosyalardan okumak ve bu verileri uygun nesnelere dönüştürmek için kullanılır. 
* Gezegenler.txt, Araclar.txt ve Kisiler.txt dosyalarından bilgileri okuyarak Gezegen, UzayAraci ve Kisi nesnelerini oluşturur. 
* Ayrıca, araçları başlangıç gezegenlerine yerleştirir ve kişileri uygun araçlara ekler.
* </p>
*/
package com.uzaysimulasyonu.util;

import com.uzaysimulasyonu.model.Gezegen;
import com.uzaysimulasyonu.model.Kisi;
import com.uzaysimulasyonu.model.UzayAraci;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DosyaOkuma {
    
    /**
     * Gezegenler.txt dosyasından gezegen bilgilerini okur
     * @param dosyaYolu Gezegenler.txt dosyasının yolu
     * @return Gezegen nesnelerinin listesi
     * @throws IOException Dosya okuma hatası
     * @throws ParseException Tarih çevirme hatası
     */
    public static List<Gezegen> gezegenlerDosyasiniOku(String dosyaYolu) throws IOException, ParseException {
        List<Gezegen> gezegenler = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dosyaYolu))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                String[] parcalar = satir.split("#");
                if (parcalar.length == 3) {
                    String gezegenAdi = parcalar[0];
                    int gunSaatSayisi = Integer.parseInt(parcalar[1]);
                    String tarih = parcalar[2];
                    
                    Gezegen gezegen = new Gezegen(gezegenAdi, gunSaatSayisi, tarih);
                    gezegenler.add(gezegen);
                }
            }
        }
        
        return gezegenler;
    }
    
    /**
     * Araclar.txt dosyasından uzay aracı bilgilerini okur
     * @param dosyaYolu Araclar.txt dosyasının yolu
     * @return UzayAraci nesnelerinin listesi
     * @throws IOException Dosya okuma hatası
     */
    public static List<UzayAraci> araclarDosyasiniOku(String dosyaYolu) throws IOException {
        List<UzayAraci> araclar = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dosyaYolu))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                String[] parcalar = satir.split("#");
                if (parcalar.length == 5) {
                    String aracAdi = parcalar[0];
                    String cikisGezegeni = parcalar[1];
                    String varisGezegeni = parcalar[2];
                    String cikisTarihi = parcalar[3];
                    int mesafe = Integer.parseInt(parcalar[4]);
                    
                    UzayAraci arac = new UzayAraci(aracAdi, cikisGezegeni, varisGezegeni, cikisTarihi, mesafe);
                    araclar.add(arac);
                }
            }
        }
        
        return araclar;
    }
    
    /**
     * Kisiler.txt dosyasından kişi bilgilerini okur ve
     * ilgili uzay araçlarına ekler
     * @param dosyaYolu Kisiler.txt dosyasının yolu
     * @param araclarMap Araç adı -> UzayAraci eşleştirmesi
     * @return Kisi nesnelerinin listesi
     * @throws IOException Dosya okuma hatası
     */
    public static List<Kisi> kisilerDosyasiniOku(String dosyaYolu, Map<String, UzayAraci> araclarMap) throws IOException {
        List<Kisi> kisiler = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dosyaYolu))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                String[] parcalar = satir.split("#");
                if (parcalar.length == 4) {
                    String isim = parcalar[0];
                    int yas = Integer.parseInt(parcalar[1]);
                    int kalanOmur = Integer.parseInt(parcalar[2]);
                    String uzayAraciAdi = parcalar[3];
                    
                    Kisi kisi = new Kisi(isim, yas, kalanOmur, uzayAraciAdi);
                    kisiler.add(kisi);
                    
                    // Kişiyi ilgili uzay aracına ekle
                    if (araclarMap.containsKey(uzayAraciAdi)) {
                        araclarMap.get(uzayAraciAdi).yolcuEkle(kisi);
                    }
                }
            }
        }
        
        return kisiler;
    }
    
    /**
     * Tüm veri dosyalarını okur ve ilgili nesneleri oluşturur
     * @param gezegenlerDosyaYolu Gezegenler.txt dosyasının yolu
     * @param araclarDosyaYolu Araclar.txt dosyasının yolu
     * @param kisilerDosyaYolu Kisiler.txt dosyasının yolu
     * @return Oluşturulan nesneleri içeren bir Map
     * @throws IOException Dosya okuma hatası
     * @throws ParseException Tarih çevirme hatası
     */
    public static Map<String, Object> tumDosyalariOku(String gezegenlerDosyaYolu, 
                                                      String araclarDosyaYolu, 
                                                      String kisilerDosyaYolu) 
                                                      throws IOException, ParseException {
        Map<String, Object> sonuc = new HashMap<>();
        
        // Önce araçları oku
        List<UzayAraci> araclar = araclarDosyasiniOku(araclarDosyaYolu);
        
        // Araçları Map'e çevir (aracAdi -> UzayAraci)
        Map<String, UzayAraci> araclarMap = new HashMap<>();
        for (UzayAraci arac : araclar) {
            araclarMap.put(arac.getAracAdi(), arac);
        }
        
        // Kişileri oku ve araçlara ekle
        List<Kisi> kisiler = kisilerDosyasiniOku(kisilerDosyaYolu, araclarMap);
        
        // Gezegenleri oku
        List<Gezegen> gezegenler = gezegenlerDosyasiniOku(gezegenlerDosyaYolu);
        
        // Araçları başlangıç gezegenlerine yerleştir
        for (UzayAraci arac : araclar) {
            for (Gezegen gezegen : gezegenler) {
                if (gezegen.getAdi().equals(arac.getCikisGezegeni())) {
                    gezegen.aracEkle(arac);
                    break;
                }
            }
        }
        
        // Sonuçları Map'e ekle
        sonuc.put("gezegenler", gezegenler);
        sonuc.put("araclar", araclar);
        sonuc.put("kisiler", kisiler);
        
        return sonuc;
    }
}