package com.hippo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hippo.enums.PairingStatus;
import com.hippo.objects.rk9.*;
import com.hippo.utils.GetData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    // Test for getTeam method in App class
    public void testGetTeam() {
        String url = "https://rk9.gg/teamlist/public/WCS02wi0zpmUDdrwWkd1/9bMC6A6RpFDm3wD5GVDY";
        Team team = GetData.getTeam(url);
        List<Pokemon> pokemons = team.getPokemons();

        assertEquals("Miraidon", pokemons.get(0).getName());
        assertEquals("Fairy", pokemons.get(0).getType());
        assertEquals("Hadron Engine", pokemons.get(0).getAbility());
        assertEquals("Choice Specs", pokemons.get(0).getItem());
        assertEquals("Electro Drift", pokemons.get(0).getMoves().get(0));
        assertEquals("Draco Meteor", pokemons.get(0).getMoves().get(1));
        assertEquals("Dazzling Gleam", pokemons.get(0).getMoves().get(2));
        assertEquals("Volt Switch", pokemons.get(0).getMoves().get(3));

        assertEquals("Ogerpon [Hearthflame Mask]", pokemons.get(1).getName());
        assertEquals("Fire", pokemons.get(1).getType());
        assertEquals("Mold Breaker", pokemons.get(1).getAbility());
        assertEquals("Hearthflame Mask", pokemons.get(1).getItem());
        assertEquals("Spiky Shield", pokemons.get(1).getMoves().get(0));
        assertEquals("Ivy Cudgel", pokemons.get(1).getMoves().get(1));
        assertEquals("Wood Hammer", pokemons.get(1).getMoves().get(2));
        assertEquals("Follow Me", pokemons.get(1).getMoves().get(3));

    }

    // Test for getPlayers method in App class
    public void testGetPlayers() {
        String url = "https://rk9.gg/roster/WCS02wi0zpmUDdrwWkd1";
        GetData getData = new GetData();
        List<Player> players = getData.getPlayers(url);

        assertEquals("Brendan Zheng [US]", players.get(0).getName());
        assertEquals("Incineroar", players.get(0).getTeam().getPokemons().get(0).getName());
        assertEquals("Bug", players.get(0).getTeam().getPokemons().get(0).getType());
        assertEquals("Intimidate", players.get(0).getTeam().getPokemons().get(0).getAbility());
        assertEquals("Safety Goggles", players.get(0).getTeam().getPokemons().get(0).getItem());
        assertEquals("Fake Out", players.get(0).getTeam().getPokemons().get(0).getMoves().get(0));
        assertEquals("Knock Off", players.get(0).getTeam().getPokemons().get(0).getMoves().get(1));
        assertEquals("Parting Shot", players.get(0).getTeam().getPokemons().get(0).getMoves().get(2));
        assertEquals("Flare Blitz", players.get(0).getTeam().getPokemons().get(0).getMoves().get(3));
    }

    // Test for getNumberOfRounds method in App class
    public void testGetNumberOfRounds() {
        String url = "WCS02wi0zpmUDdrwWkd1";
        int numberOfRounds = GetData.getNumberOfRound(url);

        assertEquals(16, numberOfRounds);
    }

    // Test for getPairingsForRound method in App class
    public void testGetPairingsForRound() {
        String url = "WCS02wi0zpmUDdrwWkd1";
        int round = 15;
        List<Pairing> pairings = GetData.getPairingsForRound(url, round);
        assertEquals(pairings.get(0).getPlayer1(), "Michael Kelsch [DE]");
        assertEquals(pairings.get(0).getPlayer2(), "Yuta Ishigaki [JP]");
        assertEquals(pairings.get(0).getStatus(), PairingStatus.PLAYER2_WON);
        assertEquals(pairings.get(1).getPlayer1(), "Luca Ceribelli [IT]");
        assertEquals(pairings.get(1).getPlayer2(), "SEONG JAE JEONG [KR]");
        assertEquals(pairings.get(1).getStatus(), PairingStatus.PLAYER1_WON);
    }

    // Test for writePlayers method in WriteData class
    /*public void testWritePlayers() {
        String url = "WCS02wi0zpmUDdrwWkd1";
        GetData getData = new GetData();
        List<Player> players = getData.getPlayers(url);
        WriteData.writePlayers(players);
    }*/

    /*public void testWritePairings() {
        String url = "WCS02wi0zpmUDdrwWkd1";
        List<List<Pairing>> allPairings = GetData.getPairings(url);
        System.out.println(allPairings.size());
        WriteData.writePairings(allPairings);
    }*/

    /*public void testWriteUsage() {
        GetUsage getUsage = new GetUsage();
        String name = "2024_Pokémon_VGC_World_Championship";
        List<SinglePokemonUsage> pokemonUsage = getUsage.getUsageData(name+".json");
        WriteUsage.writeUsage(pokemonUsage, name);
    }*/

    public void testGetTournamentInfos(){
        String url = "WCS02wi0zpmUDdrwWkd1";
        String fullURL = "https://rk9.gg/tournament/" + url;
        try {
            Document doc = Jsoup.connect(fullURL).get();
            // Trouver le nom du tournoi dans la balise h3 avec la classe "mb-0" en supprimant le contenu de la balise "small" (inclu dans h3)
            String name = doc.select("h3.mb-0").first().ownText();
            System.out.println(name);

            // Trouver les dates du tournoi dans la balise small dans la balise h3 avec la classe "my-0 px-3"
            String date = doc.select("h3.mb-0 small").first().text();

            // Convertir la String (ex: "August 16-18, 2024") en deux Date au format "dd/MM/yyyy"
            String[] parts = date.split(" ", 2);
            String[] days = parts[1].split("-", 2);

            String startDateStr = parts[0] + " " + days[0] + ", " + days[1].split(",")[1].trim();
            String endDateStr = parts[0] + " " + days[1];

            System.out.println(startDateStr);
            System.out.println(endDateStr);

            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            Date startDate = formatter.parse(startDateStr);
            Date endDate = formatter.parse(endDateStr);

            System.out.println(outputFormat.format(startDate));
            System.out.println(outputFormat.format(endDate));

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void testWriteTournament() {
        String url = "NA02mtILnc5ycfC7jXkD";
        Tournament tournament = GetData.createTournament(url);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Créer une structure de données pour contenir les informations du tournoi
        Map<String, Object> tournamentData = new LinkedHashMap<>();
        tournamentData.put("name", tournament.getName());
        tournamentData.put("startDate", tournament.getStartDate());
        tournamentData.put("endDate", tournament.getEndDate());
        tournamentData.put("players", tournament.getPlayers());
        tournamentData.put("pairings", tournament.getRounds());

        try (FileWriter writer = new FileWriter(tournament.getName().replace(" ", "_")+".json")) {
            gson.toJson(tournamentData, writer);
            System.out.println("Les données du tournoi ont été écrites dans tournament.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test searchTeams method in SearchTeam class
    /*public void testSearchTeams(){
        SearchTeam searchTeam = new SearchTeam();
        List<Pokemon> pokemons = List.of(
                new Pokemon("Calyrex [Shadow Rider]", "Fairy", "As One", "Covert Cloak", List.of("Astral Barrage", "Draining Kiss", "Calm Mind", "Protect")),
                new Pokemon("Clefairy", "Grass", "Friend Guard", "Eviolite", List.of("Follow Me", "Protect", "Helping Hand", "After You")),
                new Pokemon("Roaring Moon", null, null, null, null)
        );
        List<Player> players = searchTeam.searchTeams("2024_Pokémon_VGC_World_Championship.json", pokemons);
        List<String> playersName = List.of("Adam Cherfaoui [FR]", "Alban Badin [FR]");
        // Vérfier que les joueurs trouvés correspondent bien aux critères de recherche
        for (String playerName : playersName) {
            assertTrue(players.stream().anyMatch(player -> player.getName().equals(playerName)));
        }
    }*/
}
