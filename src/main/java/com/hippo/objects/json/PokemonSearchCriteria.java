package com.hippo.objects.json;

import com.hippo.objects.rk9.Pokemon;

import java.util.List;

public class PokemonSearchCriteria {
    private List<Pokemon> pokemons;

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}