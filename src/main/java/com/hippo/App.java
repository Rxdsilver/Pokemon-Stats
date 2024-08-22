package com.hippo;

import com.hippo.objects.Pairing;
import com.hippo.objects.Player;
import com.hippo.objects.Pokemon;
import com.hippo.objects.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public List<Player> getPlayers(String url) {
        List<Player> players = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("table tbody tr");
            for (Element row : rows) {
                Elements cols = row.select("td");
                String firstname = cols.get(1).text();
                String lastname = cols.get(2).text();
                String country = cols.get(3).text();
                // Assuming the team data is in the 4th column and needs to be parsed separately
                String teamUrl = cols.get(6).select("a").attr("href");
                if (!teamUrl.startsWith("http")) {
                    teamUrl = "https://rk9.gg" + teamUrl; // Si l'URL est relative, la compléter
                }
                Team team = getTeam(teamUrl);
                players.add(new Player(firstname, lastname, country, team));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public static Team getTeam(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements pokemonDivs = doc.select("div#lang-EN div.pokemon.bg-light-green-50.p-3");
            Pokemon[] pokemons = new Pokemon[pokemonDivs.size()];
            int index = 0;
            for (Element pokemonDiv : pokemonDivs) {
                // Extraire tout le texte brut du div
                String wholeText = pokemonDiv.wholeText();

                // Le nom est le texte avant le premier "<b>"
                String name = wholeText.split("\\bTera Type:")[0].trim().split("\n")[0];

                // Extraire les autres informations
                String type = pokemonDiv.select("b:contains(Tera Type:)").first().nextSibling().toString().trim();
                String ability = pokemonDiv.select("b:contains(Ability:)").first().nextSibling().toString().replace("&nbsp;", "").trim();
                String item = pokemonDiv.select("b:contains(Held Item:)").first().nextSibling().toString().trim();

                // Extraire les moves
                List<String> moves = new ArrayList<>();
                Elements moveElements = pokemonDiv.select("h5 span.badge");
                for (Element moveElement : moveElements) {
                    moves.add(moveElement.text());
                }

                pokemons[index++] = new Pokemon(name, type, ability, item, moves);
            }
            return new Team(pokemons);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Pairing> getPairings(String url) {
        List<Pairing> pairings = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("table tbody tr");
            for (Element row : rows) {
                Elements cols = row.select("td");
                String player1 = cols.get(0).text();
                String player2 = cols.get(1).text();
                    pairings.add(new Pairing(player1, player2));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pairings;
    }

}
