package com.hippo.utils.stats.team;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.objects.rk9.Player;
import com.hippo.objects.rk9.Pokemon;
import com.hippo.objects.rk9.Team;
import com.hippo.objects.rk9.Tournament;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchTeam {

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


    // Rechercher la ou les équipes qui correspondent aux critères de recherche
    public List<Player> searchTeams(String path, List<Pokemon> pokemons) {
        // Lire les données du fichier JSON combiné
        List<Player> playersFromJSON = getTeamsFromJSON(path);
        List<Player> players = new ArrayList<>();

        int count = 0;

        for (Player player : playersFromJSON){
            Team team = player.getTeam();
            boolean found = true;
            for (Pokemon pokemon : pokemons) {
                boolean foundPokemon = false;
                for (Pokemon teamPokemon : team.getPokemons()) {
                    if (pokemon.getName().equals(teamPokemon.getName())) {
                        foundPokemon = true;
                        // Si le type, l'ability et l'item sont spécifiés, vérifier si le pokemon a ces attributs
                        if (pokemon.getType()!=null && !pokemon.getType().equals(teamPokemon.getType())) {
                            found = false;
                            break;
                        }

                        if (pokemon.getAbility()!=null && !pokemon.getAbility().equals(teamPokemon.getAbility())) {
                            found = false;
                            break;
                        }

                        if (pokemon.getItem()!=null && !pokemon.getItem().equals(teamPokemon.getItem())) {
                            found = false;
                            break;
                        }

                        // Si des moves sont spécifiés, vérifier que le Pokémon possède au moins tous les moves spécifiés
                        if (pokemon.getMoves()!=null) {
                            boolean foundMoves = true;
                            for (String move : pokemon.getMoves()) {
                                if (!teamPokemon.getMoves().contains(move)) {
                                    foundMoves = false;
                                    break;
                                }
                            }
                            if (!foundMoves) {
                                found = false;
                                break;
                            }
                        }

                        break;
                    }
                }
                if (!foundPokemon) {
                    found = false;
                    break;
                }
            }
            if (found) {
                players.add(player);
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No team found with the given criteria.");
        } else {
            System.out.println(count + " teams found with the given criteria.");
            for (Player player : players) {
                System.out.println(player.getName());
            }
        }
        return players;
    }

    public static Team searchTeamWithPlayerName(String path, String playerName) {
        // Lire les données du fichier JSON combiné
        List<Player> players = getTeamsFromJSON(path);
        Team team = null;

        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                return p.getTeam();
            }
        }
        System.out.println("No team found with the given player name.");
        return team;
    }
}
