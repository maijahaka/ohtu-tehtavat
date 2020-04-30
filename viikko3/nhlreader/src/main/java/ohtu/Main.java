package ohtu;

import com.google.gson.Gson;
import java.io.IOException;
import org.apache.http.client.fluent.Request;

import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://nhlstatisticsforohtu.herokuapp.com/players";
        
        String bodyText = Request.Get(url).execute().returnContent().asString();
                
        // System.out.println("json-muotoinen data:");
        // System.out.println( bodyText );

        Gson mapper = new Gson();
        Player[] players = mapper.fromJson(bodyText, Player[].class);
        
        Date pvm = new Date();
        
        System.out.println("Players from FIN " + pvm);
        System.out.println();

        ArrayList<Player> finnishPlayers = new ArrayList<>();

        for (Player player : players) {
            if (player.getNationality().equals("FIN")) {
                // System.out.println(player);
                // player.tulostaPelaajanTiedot();
                finnishPlayers.add(player);
            }
        } 

        finnishPlayers.stream().sorted().forEach(p -> p.tulostaPelaajanTiedot());
        
    }
  
}

