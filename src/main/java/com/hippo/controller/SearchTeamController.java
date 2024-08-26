package com.hippo.controller;

import com.hippo.objects.json.PokemonSearchCriteria;
import com.hippo.objects.rk9.Player;
import com.hippo.objects.rk9.Pokemon;
import com.hippo.utils.stats.team.SearchTeam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchTeamController {

    @PostMapping("/api/teams")
    public List<Player> getTeams(@RequestBody PokemonSearchCriteria criteria) {
        SearchTeam searchTeam = new SearchTeam();
        List<Pokemon> pokemons = criteria.getPokemons();
        return searchTeam.searchTeams("2024_Pokémon_VGC_World_Championship.json", pokemons);
    }
}
