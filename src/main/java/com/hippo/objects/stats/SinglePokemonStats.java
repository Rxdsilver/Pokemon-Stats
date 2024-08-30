package com.hippo.objects.stats;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "singlePokemonUsage")
public class SinglePokemonStats {

    private int usage;
    private PokemonUsage pokemon;


    public SinglePokemonStats(int usage, PokemonUsage pokemon) {
        this.usage = usage;
        this.pokemon = pokemon;
    }

    public int getUsage() {
        return usage;
    }

    public PokemonUsage getPokemon() {
        return pokemon;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public void setPokemon(PokemonUsage pokemon) {
        this.pokemon = pokemon;
    }
}
