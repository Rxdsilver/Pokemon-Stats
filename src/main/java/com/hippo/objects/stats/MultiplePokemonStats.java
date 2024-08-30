package com.hippo.objects.stats;

import java.util.List;

public class MultiplePokemonStats {

    private int usage;
    private Combination combination;
    private List<PokemonUsage> pokemons;

    public MultiplePokemonStats(int usage, Combination combination, List<PokemonUsage> pokemons) {
        this.usage = usage;
        this.combination = combination;
        this.pokemons = pokemons;
    }

    public int getUsage() {
        return usage;
    }

    public Combination getCombination() {
        return combination;
    }

    public List<PokemonUsage> getPokemons() {
        return pokemons;
    }

    public void setUsage(int usage) {
        this.usage = this.usage;
    }

    // Méthode pour incrémenter l'usage
    public void incrementUsage() {
        this.usage++;
    }

    public void setCombination(Combination combination) {
        this.combination = combination;
    }

    public void setPokemons(List<PokemonUsage> pokemons) {
        this.pokemons = pokemons;
    }
}