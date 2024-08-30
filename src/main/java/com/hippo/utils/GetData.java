package com.hippo.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.enums.PairingStatus;
import com.hippo.objects.rk9.*;
import com.hippo.objects.stats.SinglePokemonStats;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GetData {
    public static List<Player> getPlayers(String url) {


        String fullURL = "https://rk9.gg/roster/" + url;


        List<Player> players = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(fullURL).maxBodySize(0).get();
            // Ecrire tout le contenu de l'url dans un fichier html
            // Files.write(Paths.get("roster.html"), doc.html().getBytes());

            Elements rows = doc.select("table tbody tr");
            int count = 0;
            for (Element row : rows) {
                Elements cols = row.select("td");
                Team team;

                String name = cols.get(1).text() + " " + cols.get(2).text() + " [" + cols.get(3).text()+"]";
                try {
                    String teamUrl = cols.select("a").attr("href");
                    team = getTeam(teamUrl);
                    if (cols.get(4).text().equalsIgnoreCase("masters")){
                        players.add(new Player(name, team));
                        count++;
                    }
                } catch (Exception e) {
                    System.out.println(count + " players added.");
                    System.out.println("Error: " + e + " for player " + name);
                    System.out.println("Row: \n"+row.text());
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public static Team getTeam(String url) {
        try {
            if (!url.startsWith("http")) {
                url = "https://rk9.gg" + url;
            }
            Document doc = Jsoup.connect(url).get();
            Elements pokemonDivs = doc.select("div#lang-EN div.pokemon.bg-light-green-50.p-3");
            List<Pokemon> pokemons = new ArrayList<>();
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

                pokemons.set(index++, new Pokemon(name, type, ability, item, moves));
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


            return new Tournament(name, url, outputFormat.format(startDate), outputFormat.format(endDate), pairings, players);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Player> getTeamsFromJSON(String path) {
        // Charger les données du fichier JSON
        Gson gson = new Gson();
        Type tournamentType = new TypeToken<Tournament>(){}.getType();

        try (FileReader reader = new FileReader(path)) {
            Tournament tournament = gson.fromJson(reader, tournamentType);
            return tournament.getPlayers();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }


    // Méthode pour vérifier si une date est dans la plage spécifiée
    public boolean isWithinDateRange(String tournamentDate, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate tournamentStartDate = LocalDate.parse(tournamentDate, formatter);
            LocalDate start = startDate != null ? LocalDate.parse(startDate, formatter) : LocalDate.MIN;
            LocalDate end = endDate != null ? LocalDate.parse(endDate, formatter) : LocalDate.MAX;

            boolean isWithinRange = !tournamentStartDate.isBefore(start) && !tournamentStartDate.isAfter(end);
            // System.out.println("Tournament Date: " + tournamentDate + " | Start Date: " + startDate + " | End Date: " + endDate + " | Within Range: " + isWithinRange);
            return isWithinRange;
        } catch (DateTimeParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
            return false;
        }
    }

    // Méthode pour filtrer les joueurs selon les critères
    public List<Player> filterPlayersByCriteria(List<Player> players, List<Pokemon> criteriaPokemons) {
        return players.stream()
                .filter(player -> {
                    Team team = player.getTeam();
                    boolean matches = true;

                    for (Pokemon criterion : criteriaPokemons) {
                        boolean foundPokemon = team.getPokemons().stream().anyMatch(pokemon -> pokemon.getName().equals(criterion.getName()) &&
                                (criterion.getType() == null || criterion.getType().equals(pokemon.getType())) &&
                                (criterion.getAbility() == null || criterion.getAbility().equals(pokemon.getAbility())) &&
                                (criterion.getItem() == null || criterion.getItem().equals(pokemon.getItem())) &&
                                (criterion.getMoves() == null || pokemon.getMoves() != null && pokemon.getMoves().containsAll(criterion.getMoves())));

                        if (!foundPokemon) {
                            matches = false;
                            break;
                        }
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    // Méthode pour vérifier si une équipe correspond aux critères
    public boolean doesTeamMatchCriteria(Team team, List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            boolean found = false;
            for (Pokemon teamPokemon : team.getPokemons()) {
                if (pokemon.getName().equals(teamPokemon.getName())) {
                    found = true;

                    // Vérifier si le type correspond
                    if (pokemon.getType() != null && !pokemon.getType().equals(teamPokemon.getType())) {
                        // System.out.println("Type mismatch: " + pokemon.getType() + " != " + teamPokemon.getType());
                        found = false;
                        break;
                    }

                    // Vérifier si l'ability correspond
                    if (pokemon.getAbility() != null && !pokemon.getAbility().equals(teamPokemon.getAbility())) {
                        // System.out.println("Ability mismatch: " + pokemon.getAbility() + " != " + teamPokemon.getAbility());
                        found = false;
                        break;
                    }

                    // Vérifier si l'item correspond
                    if (pokemon.getItem() != null && !pokemon.getItem().equals(teamPokemon.getItem())) {
                        // System.out.println("Item mismatch: " + pokemon.getItem() + " != " + teamPokemon.getItem());
                        found = false;
                        break;
                    }

                    // Vérifier si les moves sont inclus dans le Pokémon de l'équipe
                    if (pokemon.getMoves() != null && !new HashSet<>(teamPokemon.getMoves()).containsAll(pokemon.getMoves())) {
                        // System.out.println("Moves mismatch: " + pokemon.getMoves() + " not in " + teamPokemon.getMoves());
                        found = false;
                        break;
                    }


                }
                // Vérifier si le type correspond

            }
            if (!found) {
                return false;
            }
        }
        return true;
    }




    public <K> LinkedHashMap<K, Integer> sortByValue(Map<K, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // in case of key collision, keep existing key
                        LinkedHashMap::new // return a LinkedHashMap to maintain order
                ));
    }

    public List<SinglePokemonStats> getUsageData(List<Tournament> tournaments) {
        // Lire les données du fichier JSON combiné
        Gson gson = new Gson();
        Type tournamentType = new TypeToken<Tournament>(){}.getType();
        List<SinglePokemonStats> pokemonUsage = new ArrayList<>();

        for (Tournament tournament : tournaments) {
            // Parcourir tous les joueurs dans le tournoi
            for (Player player : tournament.getPlayers()) {
                Team team = player.getTeam();
                for (Pokemon pokemon : team.getPokemons()) {
                    // Ajouter le pokemon à la liste si il n'est pas déjà dedans sinon augmenter son usage
                    boolean found = false;
                    for (SinglePokemonStats singlePokemonStats : pokemonUsage) {
                        if (singlePokemonStats.getName().equals(pokemon.getName())) {
                            singlePokemonStats.setUsage(singlePokemonStats.getUsage() + 1);
                            found = true;

                            // Ajouter les autres informations du pokemon
                            LinkedHashMap<String, Integer> tera = singlePokemonStats.getTera();
                            tera.put(pokemon.getType(), tera.getOrDefault(pokemon.getType(), 0) + 1);
                            singlePokemonStats.setTera(tera);

                            LinkedHashMap<String, Integer> item = singlePokemonStats.getItem();
                            item.put(pokemon.getItem(), item.getOrDefault(pokemon.getItem(), 0) + 1);
                            singlePokemonStats.setItem(item);

                            LinkedHashMap<String, Integer> ability = singlePokemonStats.getAbility();
                            ability.put(pokemon.getAbility(), ability.getOrDefault(pokemon.getAbility(), 0) + 1);
                            singlePokemonStats.setAbility(ability);

                            LinkedHashMap<String, Integer> moves = singlePokemonStats.getMoves();
                            for (String move : pokemon.getMoves()) {
                                moves.put(move, moves.getOrDefault(move, 0) + 1);
                            }
                            singlePokemonStats.setMoves(moves);
                            break;
                        }
                    }
                    if (!found) {
                        LinkedHashMap<String, Integer> tera = new LinkedHashMap<>();
                        tera.put(pokemon.getType(), 1);
                        LinkedHashMap<String, Integer> item = new LinkedHashMap<>();
                        item.put(pokemon.getItem(), 1);
                        LinkedHashMap<String, Integer> ability = new LinkedHashMap<>();
                        ability.put(pokemon.getAbility(), 1);
                        LinkedHashMap<String, Integer> moves = new LinkedHashMap<>();
                        for (String move : pokemon.getMoves()) {
                            moves.put(move, 1);
                        }
                        pokemonUsage.add(new SinglePokemonStats(pokemon.getName(), 1, tera, item, ability, moves));
                    }
                }
            }
        }

        return pokemonUsage;
    }

    // Méthode pour vérifier si un Pokémon dans l'équipe correspond aux critères
    private boolean matchesCriteria(Pokemon criteria, Pokemon teamPokemon) {
        if (!criteria.getName().equals(teamPokemon.getName())) {
            return false;
        }

        if (criteria.getType() != null && !criteria.getType().equals(teamPokemon.getType())) {
            return false;
        }

        if (criteria.getAbility() != null && !criteria.getAbility().equals(teamPokemon.getAbility())) {
            return false;
        }

        if (criteria.getItem() != null && !criteria.getItem().equals(teamPokemon.getItem())) {
            return false;
        }

        if (criteria.getMoves() != null) {
            for (String move : criteria.getMoves()) {
                if (!teamPokemon.getMoves().contains(move)) {
                    return false;
                }
            }
        }

        return true;
    }


    public Team searchTeamWithPlayerName(List<Player> players, String playerName) {
        // Lire les données du fichier JSON combiné
        Team team = null;

        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                return p.getTeam();
            }
        }
        System.out.println("No team found with the given player name : " + playerName);
        return team;
    }
}
