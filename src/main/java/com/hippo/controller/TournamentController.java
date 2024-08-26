package com.hippo.controller;

import com.hippo.objects.stats.usage.SinglePokemonUsage;
import com.hippo.utils.stats.usage.GetUsage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TournamentController {

    @GetMapping("api/tournament")
    public List<SinglePokemonUsage> getUsage() {

        GetUsage getUsage = new GetUsage();
        List<SinglePokemonUsage> usage = getUsage.readUsageData("2024_Pokémon_VGC_World_Championship.json");

        // Ensure data is sorted just before returning
        usage.sort((o1, o2) -> o2.getUsage() - o1.getUsage());

        usage.forEach(singlePokemonUsage -> {
            singlePokemonUsage.setMoves(sortByValue(singlePokemonUsage.getMoves()));
            singlePokemonUsage.setTera(sortByValue(singlePokemonUsage.getTera()));
            singlePokemonUsage.setItem(sortByValue(singlePokemonUsage.getItem()));
            singlePokemonUsage.setAbility(sortByValue(singlePokemonUsage.getAbility()));
        });

        return usage; // This will be automatically converted to JSON and returned
    }

    private <K> LinkedHashMap<K, Integer> sortByValue(Map<K, Integer> map) {
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
}
