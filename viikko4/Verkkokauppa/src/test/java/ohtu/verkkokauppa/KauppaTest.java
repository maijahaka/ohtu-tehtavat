package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.checkerframework.dataflow.qual.TerminatesExecution;

public class KauppaTest {

    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;
    Kauppa k;
    
    @Before
    public void setUp() {
        // luodaan ensin mock-oliot
        pankki = mock(Pankki.class);

        viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        varasto = mock(Varasto.class);
        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(10); 
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "juusto", 8));
        when(varasto.saldo(3)).thenReturn(0); 
        when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "kerma", 4));

        // sitten testattava kauppa 
        k = new Kauppa(varasto, pankki, viite);      
    }

    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {        

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),anyInt());   
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaParametreilla() {             

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(),eq(5)); 
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }

    @Test
    public void kahdenEriTuotteenOstaminenKutsuuPankinMetodiaTilisiirtoOikeillaParametreilla() {
        
        // aloitetaan asiointi
        k.aloitaAsiointi();

        // koriin lisätään kaksi eri tuotetta, joita varastossa on
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);

        // suoritetaan ostos
        k.tilimaksu("pekka", "12345");
        // varmista että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(),eq(13));
    }

    @Test
    public void kahdenSamanTuotteenOstaminenKutsuuPankinMetodiaTilisiirtoOikeillaParametreilla() {
        
        // aloitetaan asiointi
        k.aloitaAsiointi();

        // koriin lisätään kaksi samaa tuotetta, jota on varastossa tarpeeksi
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);

        // suoritetaan ostos
        k.tilimaksu("pekka", "12345");
        // varmista että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(),eq(10));
    }

    @Test
    public void varstostaPuuttuvaTuoteEiSiirryOstoskoriin() {
        
        // aloitetaan asiointi
        k.aloitaAsiointi();

        // koriin lisätään tuote, jota on varastossa tarpeeksi 
        k.lisaaKoriin(1);

        // ja tuote joka on loppu 
        k.lisaaKoriin(3);

        // suoritetaan ostos
        k.tilimaksu("pekka", "12345");

        // varmista että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(),eq(5));
    }

    @Test
    public void asioinninAloittaminenNollaaEdellisenOstoksenTiedot() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        // k.tilimaksu("pekka", "12345");

        k.aloitaAsiointi();
        k.lisaaKoriin(2);

        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),eq(8));
    }

    @Test
    public void kauppaPyytaaUudenViitenumeronJokaiselleMaksutapahtumalle() {
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("jukka", "1111");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kerran
        verify(viite, times(1)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("matti", "1234");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kaksi kertaa
        verify(viite, times(2)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(3);
        k.tilimaksu("pekka", "4444");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kolme kertaa        
        verify(viite, times(3)).uusi();
    }

    @Test
    public void ostoskoristaPoistettuTuoteEiOleMukanaMaksettavassaSummassa() {
        k.aloitaAsiointi();

        k.lisaaKoriin(1);
        k.lisaaKoriin(2);

        k.poistaKorista(1);

        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),eq(8));
    }

}