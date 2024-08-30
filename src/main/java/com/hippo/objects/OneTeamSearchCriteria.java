package com.hippo.objects;

import com.hippo.objects.rk9.Pokemon;

import java.util.List;

public class OneTeamSearchCriteria {
    private DateRange dates;
    private List<Pokemon> pokemons;

    // Getters and Setters
    public DateRange getDates() {
        return dates;
    }

    public void setDates(DateRange dates) {
        this.dates = dates;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
