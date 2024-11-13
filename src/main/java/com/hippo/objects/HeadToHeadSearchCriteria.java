package com.hippo.objects;

import com.hippo.objects.rk9.Pokemon;

import java.util.List;

public class HeadToHeadSearchCriteria {

    private DateRange dates;
    List<Pokemon> pokemons;
    List<Pokemon> opposingPokemons;

    public DateRange getDates() {
        return dates;
    }

    public void setDates(DateRange dates) {
        this.dates = dates;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setWinratePokemons(List<Pokemon> setWinratePokemons) {
        this.pokemons = pokemons;
    }

    public List<Pokemon> getOpposingPokemons() {
        return opposingPokemons;
    }

    public void setOpposingPokemons(List<Pokemon> opposingPokemons) {
        this.opposingPokemons = opposingPokemons;
    }


}
