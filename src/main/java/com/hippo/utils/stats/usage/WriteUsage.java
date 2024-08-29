package com.hippo.utils.stats.usage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hippo.objects.stats.usage.SinglePokemonUsage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WriteUsage {

    public static void writeUsage(List<SinglePokemonUsage> usage, String filename) {
        // Write usage to file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename + "_USAGE.json")) {
            // Sort the usage by descending order
            usage.sort((o1, o2) -> o2.getUsage() - o1.getUsage());

            // Sort each map (moves, tera, item, ability) in descending order
            usage.forEach(singlePokemonUsage -> {
                singlePokemonUsage.setMoves(sortByValue(singlePokemonUsage.getMoves()));
                singlePokemonUsage.setTera(sortByValue(singlePokemonUsage.getTera()));
                singlePokemonUsage.setItem(sortByValue(singlePokemonUsage.getItem()));
                singlePokemonUsage.setAbility(sortByValue(singlePokemonUsage.getAbility()));
            });

            gson.toJson(usage, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic method to sort a map by its values in descending order and return a LinkedHashMap
    private static <K> LinkedHashMap<K, Integer> sortByValue(Map<K, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // in case of key collision, keep existing key
                        LinkedHashMap::new // return a LinkedHashMap to maintain order
                ));
    }
}
