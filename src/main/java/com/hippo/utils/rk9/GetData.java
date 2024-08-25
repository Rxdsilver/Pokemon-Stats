package com.hippo.utils.rk9;

import com.hippo.enums.PairingStatus;
import com.hippo.objects.rk9.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GetData {
    public static List<Player> getPlayers(String url) {
        String fullURL = "https://rk9.gg/roster/" + url;
        List<Player> players = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(fullURL).get();
            Elements rows = doc.select("table tbody tr");
            for (Element row : rows) {
                Elements cols = row.select("td");

                String name = cols.get(1).text() + " " + cols.get(2).text() + " [" + cols.get(3).text()+"]";
                // Assuming the team data is in the 4th column and needs to be parsed separately
                String teamUrl = cols.get(6).select("a").attr("href");
                if (!teamUrl.startsWith("http")) {
                    teamUrl = "https://rk9.gg" + teamUrl; // Si l'URL est relative, la compléter
                }
                Team team = getTeam(teamUrl);
                if (cols.get(4).text().equalsIgnoreCase("masters")){
                    players.add(new Player(name, team));
                }
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

    public static int getNumberOfRound(String url) {

        String fullURL = "https://rk9.gg/pairings/" + url;
        int iRoundsFromUrl = 0;

        try {
            Document doc = Jsoup.connect(fullURL).get();
            Elements ultags = doc.select("ul.nav.nav-pills");
            for (Element ultag : ultags) {
                // Parcourir tous les <li> dans chaque <ul>
                Elements litags = ultag.select("li");
                for (Element litag : litags) {
                    // Parcourir tous les <a> dans chaque <li>
                    Elements arias = litag.select("a");
                    for (Element aria : arias) {
                        // Diviser le texte du lien <a> en fonction des espaces
                        String[] sp = aria.text().split(" ");
                        // System.out.println(Arrays.toString(aria.text().split(" ")));
                        if (sp[0].equalsIgnoreCase("masters")){
                            iRoundsFromUrl = Integer.parseInt(sp[sp.length - 1]);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return iRoundsFromUrl;
    }

    public static List<List<Pairing>> getPairings(String url) {

        // Récupérer le nombre de rounds
        int iRounds = getNumberOfRound(url);

        List<List<Pairing>> allPairings = new ArrayList<>();

        for (int i = 1; i <= iRounds; i++) {
            // Récupérer les pairings pour chaque round
            List<Pairing> roundPairings = getPairingsForRound(url, i);
            allPairings.add(roundPairings);
        }

        return allPairings;
    }

    public static List<Pairing> getPairingsForRound(String url, int roundNumber) {

        // ex: https://rk9.gg/pairings/WCS02wi0zpmUDdrwWkd1?pod=2&rnd=1
        String fullURL = "https://rk9.gg/pairings/" + url + "?pod=2&rnd=" + roundNumber;
        List<Pairing> pairings = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(fullURL).get();
            Elements matches = doc.select("div.row.row-cols-3.match.no-gutter.complete");

            // Parcourir tous les éléments de match
            for (Element match : matches) {

                boolean isBye = match.select("div.player2 span.name").text().isEmpty();
                // Sélectionner le joueur 1
                Element player1Div = match.select("div.player1").first();
                String player1Name = player1Div.select("span.name").text();
                boolean player1IsWinner = player1Div.hasClass("winner");
                boolean isTie1 = player1Div.hasClass("tie");

                // Sélectionner le joueur 2
                Element player2Div = match.select("div.player2").first();
                String player2Name = player2Div.select("span.name").text();
                boolean player2IsWinner = player2Div.hasClass("winner");
                boolean isTie2 = player2Div.hasClass("tie");

                PairingStatus status;
                if (player1IsWinner){
                    status = PairingStatus.PLAYER1_WON;
                } else if (player2IsWinner) {
                    status = PairingStatus.PLAYER2_WON;
                } else if (isTie1 && isTie2) {
                    status = PairingStatus.DRAW;
                } else {
                    status = PairingStatus.IN_PROGRESS;
                }
                pairings.add(new Pairing(player1Name, player2Name, status, isBye));

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pairings;
    }

        public static Tournament createTournament (String url) {
        List<Player> players = getPlayers(url);
        List<List<Pairing>> pairings = getPairings(url);

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

            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            Date startDate = formatter.parse(startDateStr);
            Date endDate = formatter.parse(endDateStr);


            return new Tournament(name, outputFormat.format(startDate), outputFormat.format(endDate), pairings, players);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
