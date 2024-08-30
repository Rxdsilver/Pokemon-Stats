package com.hippo.objects;

import com.hippo.objects.rk9.Pokemon;

import java.util.List;

public class HeadToHeadSearchCriteria {

    private DateRange dates;
    List<Pokemon> winratePokemons;
    List<Pokemon> opposingPokemons;

    public DateRange getDates() {
        return dates;
    }

    public void setDates(DateRange dates) {
        this.dates = dates;
    }

    public List<Pokemon> getWinratePokemons() {
        return winratePokemons;
    }

    public void setWinratePokemons(List<Pokemon> winratePokemons) {
        this.winratePokemons = winratePokemons;
    }

    public List<Pokemon> getOpposingPokemons() {
        return opposingPokemons;
    }

    public void setOpposingPokemons(List<Pokemon> opposingPokemons) {
        this.opposingPokemons = opposingPokemons;
    }


}
