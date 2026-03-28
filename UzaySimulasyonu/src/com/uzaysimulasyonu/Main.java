/**
*
* @author Hansın Evren Taşel hansin.tasel@ogr.sakarya.edu.tr
* @since 22.04.2025-27.04.2025
* <p>
* Bu sınıf, dosya yollarını belirler, simülasyon nesnesini oluşturur ve simülasyonu başlatır. 
* Ayrıca, çalışma sırasında oluşabilecek hataları ele alır.
* </p>
*/
package com.uzaysimulasyonu;

import com.uzaysimulasyonu.simulasyon.Simulasyon;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        try {
            String gezegenlerDosyaYolu = "data/Gezegenler.txt";
            String araclarDosyaYolu = "data/Araclar.txt";
            String kisilerDosyaYolu = "data/Kisiler.txt";
            
            // Eğer kullanıcı komut satırından dosya yolu belirttiyse onları kullanır.
            // Bu sayede program farklı veri setleriyle test edilebilir
            if (args.length >= 3) {
                gezegenlerDosyaYolu = args[0];
                araclarDosyaYolu = args[1];
                kisilerDosyaYolu = args[2];
            }
            
            // Dosya yollarını kullanarak yeni bir Simulasyon nesnesi oluştur
            // Bu nesne tüm simülasyon işlemlerini yönetecek
            Simulasyon simulasyon = new Simulasyon(gezegenlerDosyaYolu, araclarDosyaYolu, kisilerDosyaYolu);
            
            // Simülasyon hız ayarlarını kullanıcıdan al
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Simülasyon Hızı (her adımda kaç saat ilerlesin, varsayılan: 1): ");
            try {
                int hiz = Integer.parseInt(scanner.nextLine().trim());
                simulasyon.simulasyonHiziniAyarla(hiz);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz değer, varsayılan hız kullanılacak: 1");
            }
            
            System.out.println("Ekran Güncelleme Frekansı (kaç adımda bir ekran güncellensin, varsayılan: 1): ");
            try {
                int frekans = Integer.parseInt(scanner.nextLine().trim());
                simulasyon.ekranGuncellemeFrekansiAyarla(frekans);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz değer, varsayılan frekans kullanılacak: 1");
            }
            
            System.out.println("Bekleme Süresi (milisaniye, varsayılan: 10): ");
            try {
                int bekleme = Integer.parseInt(scanner.nextLine().trim());
                simulasyon.beklemeZamaniAyarla(bekleme);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz değer, varsayılan bekleme süresi kullanılacak: 500ms");
            }
            
            // Oluşturulan simulasyon nesnesini çalıştırır ve simülasyonu başlatır.
            simulasyon.simulasyonuCalistir();
            
        } catch (IOException e) {
            // Dosya okuma sırasında oluşabilecek hataları yakalayıp kullanıcıya bildir.
            // Dosyalar bulunamadığında veya okunamadığında bu hata oluşur.
            System.err.println("Dosya okuma hatası: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            // Tarih formatı hatalıysa oluşacak istisnaları yakala
            // Dosyalardaki tarih bilgileri yanlış formatta yazılmışsa bu hata oluşur.
            System.err.println("Tarih çevirme hatası: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Thread.sleep() sırasında oluşabilecek kesintileri yakala
            // Simülasyon çalışırken beklemeler sırasında bu hata oluşabilir
            System.err.println("İşlem kesintiye uğradı: " + e.getMessage());
            e.printStackTrace();
        }
    }
}