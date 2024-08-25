package com.hippo.utils.stats.team;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hippo.objects.rk9.Player;
import com.hippo.objects.rk9.Pokemon;
import com.hippo.objects.rk9.Team;
import com.hippo.objects.rk9.Tournament;
import com.hippo.objects.stats.usage.SinglePokemonUsage;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchTeam {

    public List<Team> getTeamsFromJSON(String path) {
        // Charger les données du fichier JSON

        Gson gson = new Gson();
        Type tournamentType = new TypeToken<Tournament>(){}.getType();
        List<Team> teams = new ArrayList<>();

        try (FileReader reader = new FileReader(path)) {
            Tournament tournament = gson.fromJson(reader, tournamentType);
            for (Player player : tournament.getPlayers()) {
                teams.add(player.getTeam());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return teams;
    }


    // Rechercher la ou les équipes qui correspondent aux critères de recherche
    public List<Team> searchTeams(String path, List<Pokemon> pokemons) {
        // Lire les données du fichier JSON combiné
        List<Team> teamsFromJSON = getTeamsFromJSON(path);
        List<Team> teams = new ArrayList<>();

        int count = 0;

        for (Team team : teamsFromJSON) {
            boolean found = true;
            for (Pokemon pokemon : pokemons) {
                boolean foundPokemon = false;
                for (Pokemon teamPokemon : team.getPokemons()) {
                    if (pokemon.getName().equals(teamPokemon.getName())) {
                        foundPokemon = true;
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
                        if (pokemon.getMoves()!=null) {
                            // Chercher si le move est dans la liste des moves du pokemon
                            boolean foundMove = true;
                            for (String move : pokemon.getMoves()) {
                                if (!teamPokemon.getMoves().contains(move)) {
                                    foundMove = false;
                                    break;
                                }
                            }
                            if (foundMove) {
                                found = true;
                                break;
                            } else {
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
                teams.add(team);
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No team found with the given criteria.");
        } else {
            System.out.println(count + " teams found with the given criteria.");
        }
        return teams;
    }
}
