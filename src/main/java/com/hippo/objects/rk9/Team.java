package com.hippo.objects.rk9;

public class Team {
    Pokemon[] pokemons;

    public Team(Pokemon[] pokemons) {
        this.pokemons = pokemons;
    }

    public Pokemon[] getPokemons() {
        return pokemons;
    }

    public void setPokemons(Pokemon[] pokemons) {
        this.pokemons = pokemons;
    }

    public void setPokemon(int index, Pokemon pokemon) {
        this.pokemons[index] = pokemon;
    }

    public Pokemon getPokemon(int index) {
        return this.pokemons[index];
    }

}
