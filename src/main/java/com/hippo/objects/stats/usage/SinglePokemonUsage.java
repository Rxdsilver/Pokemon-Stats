package com.hippo.objects.stats.usage;

import java.util.LinkedHashMap;

public class SinglePokemonUsage {
    private String name;
    private int usage;
    private LinkedHashMap<String, Integer> tera;
    private LinkedHashMap<String, Integer> item;
    private LinkedHashMap<String, Integer> ability;
    private LinkedHashMap<String, Integer> moves;

    public SinglePokemonUsage(String name, int usage, LinkedHashMap<String, Integer> tera, LinkedHashMap<String, Integer> item, LinkedHashMap<String, Integer> ability, LinkedHashMap<String, Integer> moves) {
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

    public LinkedHashMap<String, Integer> getTera() {
        return tera;
    }

    public LinkedHashMap<String, Integer> getItem() {
        return item;
    }

    public LinkedHashMap<String, Integer> getAbility() {
        return ability;
    }

    public LinkedHashMap<String, Integer> getMoves() {
        return moves;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public void setTera(LinkedHashMap<String, Integer> tera) {
        this.tera = tera;
    }

    public void setItem(LinkedHashMap<String, Integer> item) {
        this.item = item;
    }

    public void setAbility(LinkedHashMap<String, Integer> ability) {
        this.ability = ability;
    }

    public void setMoves(LinkedHashMap<String, Integer> moves) {
        this.moves = moves;
    }
}
