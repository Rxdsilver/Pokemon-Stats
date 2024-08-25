package com.hippo.objects.stats.usage;

import java.util.Map;

public class SinglePokemonUsage {
    private String name;
    private int usage;
    private Map<String, Integer> tera;
    private Map<String, Integer> item;
    private Map<String, Integer> ability;
    private Map<String, Integer> moves;

    public SinglePokemonUsage(String name, int usage, Map<String, Integer> tera, Map<String, Integer> item, Map<String, Integer> ability, Map<String, Integer> moves) {
        this.name = name;
        this.usage = usage;
        this.tera = tera;
        this.item = item;
        this.ability = ability;
        this.moves = moves;
    }

    public String getName() {
        return name;
    }

    public int getUsage() {
        return usage;
    }

    public Map<String, Integer> getTera() {
        return tera;
    }

    public Map<String, Integer> getItem() {
        return item;
    }

    public Map<String, Integer> getAbility() {
        return ability;
    }

    public Map<String, Integer> getMoves() {
        return moves;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public void setTera(Map<String, Integer> tera) {
        this.tera = tera;
    }

    public void setItem(Map<String, Integer> item) {
        this.item = item;
    }

    public void setAbility(Map<String, Integer> ability) {
        this.ability = ability;
    }

    public void setMoves(Map<String, Integer> moves) {
        this.moves = moves;
    }
}
