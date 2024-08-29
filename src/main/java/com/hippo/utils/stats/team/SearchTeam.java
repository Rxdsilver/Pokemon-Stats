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
    public List<Player> searchTeams(List<Tournament> tournaments, List<Pokemon> pokemons) {
        List<Player> players = new ArrayList<>();

        for (Tournament tournament : tournaments) {
            List<Player> playersFromTournament = tournament.getPlayers();

            for (Player player : playersFromTournament) {
                Team team = player.getTeam();
                boolean found = true;

                for (Pokemon pokemon : pokemons) {
                    boolean foundPokemon = false;

                    for (Pokemon teamPokemon : team.getPokemons()) {
                        if (matchesCriteria(pokemon, teamPokemon)) {
                            foundPokemon = true;
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
                }
            }
        }

        if (players.isEmpty()) {
            System.out.println("No team found with the given criteria.");
        } else {
            System.out.println(players.size() + " teams found with the given criteria.");
            for (Player player : players) {
                System.out.println(player.getName());
            }
        }

        return players;
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
