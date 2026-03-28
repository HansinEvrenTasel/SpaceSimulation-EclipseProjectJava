/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Zaman sınıfı, simülasyondaki zaman yönetimini sağlar. 
* Tarih ve saat bilgilerini tutar, belirli saat kadar ileri sarar ve farklı gezegenlerin gün uzunluklarına göre zamanı hesaplar. 
* Ayrıca, iki zaman arasındaki farkları hesaplama, tarih karşılaştırma ve belirli bir saat sonraki tarihi hesaplama gibi işlevler sunar.
* </p>
*/
package com.uzaysimulasyonu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Zaman {
    private Date tarih;
    private int saat;
    private int dakika;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    
    // Verilen tarih ve saati kullanarak Zaman nesnesi oluşturur
    public Zaman(String tarihStr, int saat, int dakika) throws ParseException {
        this.tarih = DATE_FORMAT.parse(tarihStr);
        this.saat = saat;
        this.dakika = dakika;
    }
    
    // Sadece tarih ile Zaman nesnesi oluşturur (saat 00:00 olarak ayarlanır)
    public Zaman(String tarihStr) throws ParseException {
        this(tarihStr, 0, 0);
    }
    
    // Belirli bir saati ileri sarar
    public void saatiIlerlet(int saatSayisi, int gunSaatSayisi) {
        // Gün içindeki saat ilerlemesi
        this.saat += saatSayisi;
        
        // Tam gün hesaplaması
        int gunSayisi = this.saat / gunSaatSayisi;
        this.saat = this.saat % gunSaatSayisi;
        
        // Tarihi güncelle
        if (gunSayisi > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.tarih);
            cal.add(Calendar.DATE, gunSayisi);
            this.tarih = cal.getTime();
        }
    }
    
    // İki tarih arasındaki farkı saat cinsinden hesaplar
    public static long saatFarki(Zaman zaman1, Zaman zaman2) {
        long farkMilisaniye = zaman2.tarih.getTime() - zaman1.tarih.getTime();
        long gunFarkiSaat = farkMilisaniye / (1000 * 60 * 60 * 24) * 24; // Gün farkını saat cinsinden hesapla
        long saatFarki = zaman2.saat - zaman1.saat;
        return gunFarkiSaat + saatFarki;
    }
    
    // Tarihi String formatında döndürür
    public String tarihToString() {
        return DATE_FORMAT.format(tarih);
    }
    
    // Saati String formatında döndürür (hh:mm)
    public String saatToString() {
        return String.format("%02d:%02d", saat, dakika);
    }
    
    // İki tarih karşılaştırması yapar
    public boolean sonraMi(Zaman digerZaman) {
        int tarihKarsilastirma = this.tarih.compareTo(digerZaman.tarih);
        if (tarihKarsilastirma > 0) {
            return true;
        } else if (tarihKarsilastirma == 0) {
            if (this.saat > digerZaman.saat) {
                return true;
            } else if (this.saat == digerZaman.saat) {
                return this.dakika > digerZaman.dakika;
            }
        }
        return false;
    }
    
    // İki tarih eşitliğini kontrol eder
    public boolean esitMi(Zaman digerZaman) {
        return this.tarih.equals(digerZaman.tarih) && this.saat == digerZaman.saat && this.dakika == digerZaman.dakika;
    }
    
    // Tarihin bir kopyasını döndürür
    public Date getTarih() {
        return (Date) tarih.clone();
    }
    
    public int getSaat() {
        return saat;
    }
    
    public int getDakika() {
        return dakika;
    }
    
    // Belirli bir günSaatSayisi'na göre tarih + saat bilgisini yeni bir tarih olarak hesaplar ve döndürür
    public String belirliSaatSonraTarih(int saatSayisi, int gunSaatSayisi) throws ParseException {
        // Yeni tarih nesnesi oluştur (deep copy)
        Zaman yeniZaman = new Zaman(this.tarihToString(), this.saat, this.dakika);
        
        // Saati ilerlet
        yeniZaman.saatiIlerlet(saatSayisi, gunSaatSayisi);
        
        // Tarih formatında döndür (gün.ay.yıl formatında)
        return yeniZaman.tarihToString();
    }
    
    @Override
    public String toString() {
        return tarihToString() + ":" + saatToString();
    }
}