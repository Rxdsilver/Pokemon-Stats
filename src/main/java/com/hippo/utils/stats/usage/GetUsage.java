package com.hippo.utils.stats.usage;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUsage {

    public List<SinglePokemonUsage> readUsageData(String path) {
        // Lire les données du fichier JSON combiné
        Gson gson = new Gson();
        Type tournamentType = new TypeToken<Tournament>(){}.getType();
        List<SinglePokemonUsage> pokemonUsage = new ArrayList<>();

        try (FileReader reader = new FileReader(path)) {
            Tournament tournament = gson.fromJson(reader, tournamentType);

            // Parcourir tous les joueurs dans le tournoi
            for (Player player : tournament.getPlayers()) {
                Team team = player.getTeam();
                for (Pokemon pokemon : team.getPokemons()) {
                    // Ajouter le pokemon à la liste si il n'est pas déjà dedans sinon augmenter son usage
                    boolean found = false;
                    for (SinglePokemonUsage singlePokemonUsage : pokemonUsage) {
                        if (singlePokemonUsage.getName().equals(pokemon.getName())) {
                            singlePokemonUsage.setUsage(singlePokemonUsage.getUsage() + 1);
                            found = true;

                            // Ajouter les autres informations du pokemon
                            Map<String, Integer> tera = singlePokemonUsage.getTera();
                            tera.put(pokemon.getType(), tera.getOrDefault(pokemon.getType(), 0) + 1);
                            singlePokemonUsage.setTera(tera);

                            Map<String, Integer> item = singlePokemonUsage.getItem();
                            item.put(pokemon.getItem(), item.getOrDefault(pokemon.getItem(), 0) + 1);
                            singlePokemonUsage.setItem(item);

                            Map<String, Integer> ability = singlePokemonUsage.getAbility();
                            ability.put(pokemon.getAbility(), ability.getOrDefault(pokemon.getAbility(), 0) + 1);
                            singlePokemonUsage.setAbility(ability);

                            Map<String, Integer> moves = singlePokemonUsage.getMoves();
                            for (String move : pokemon.getMoves()) {
                                moves.put(move, moves.getOrDefault(move, 0) + 1);
                            }
                            singlePokemonUsage.setMoves(moves);
                            break;
                        }
                    }
                    if (!found) {
                        Map<String, Integer> tera = new HashMap<>();
                        tera.put(pokemon.getType(), 1);
                        Map<String, Integer> item = new HashMap<>();
                        item.put(pokemon.getItem(), 1);
                        Map<String, Integer> ability = new HashMap<>();
                        ability.put(pokemon.getAbility(), 1);
                        Map<String, Integer> moves = new HashMap<>();
                        for (String move : pokemon.getMoves()) {
                            moves.put(move, 1);
                        }
                        pokemonUsage.add(new SinglePokemonUsage(pokemon.getName(), 1, tera, item, ability, moves));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pokemonUsage;
    }
}
