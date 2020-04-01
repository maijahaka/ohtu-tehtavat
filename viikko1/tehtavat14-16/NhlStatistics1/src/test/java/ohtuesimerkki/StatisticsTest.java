package ohtuesimerkki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StatisticsTest {
 
    Reader readerStub = new Reader() {
 
        public List<Player> getPlayers() {
            ArrayList<Player> players = new ArrayList<>();
 
            players.add(new Player("Semenko", "EDM", 4, 12));
            players.add(new Player("Lemieux", "PIT", 45, 54));
            players.add(new Player("Kurri",   "EDM", 37, 53));
            players.add(new Player("Yzerman", "DET", 42, 56));
            players.add(new Player("Gretzky", "EDM", 35, 89));
 
            return players;
        }
    };
 
    Statistics stats;

    @Before
    public void setUp(){
        // luodaan Statistics-olio joka käyttää "stubia"
        stats = new Statistics(readerStub);
    }
    
    @Test
    public void pelaajaLoytyy() {
        Player loytynyt = stats.search("emenko");

        assertEquals(new Player("Semenko", "EDM", 4, 12).toString(), loytynyt.toString());
    }

    @Test
    public void olematonPelaajaPalauttaaNull() {
        Player loytynyt = stats.search("Selanne");

        assertEquals(null, loytynyt);
    }

    @Test
    public void joukkueenPelaajiaOikeaMaara() {
        List<Player> pelaajat = stats.team("EDM");
        
        assertEquals(3, pelaajat.size());
    }

    @Test
    public void enitenPisteitaTuottanutPelaajaOnOikea() {
        assertTrue(stats.topScorers(1).get(0).getName().equals("Gretzky"));;
    }
}
