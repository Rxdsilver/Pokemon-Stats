package com.hippo.objects.rk9;

import java.util.List;

public class Team {
    List<Pokemon> pokemons;

    public Team(List<Pokemon> pokemons)  {
        this.pokemons = pokemons;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }



}
