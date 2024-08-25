package com.hippo.objects.rk9;

import java.util.List;

public class Pokemon {
    private String name;
    private String type;
    private String ability;
    private String item;
    private List<String> moves;

    public Pokemon(String name, String type, String ability, String item, List<String> moves) {
        this.name = name;
        this.type = type;
        this.ability = ability;
        this.item = item;
        this.moves = moves;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAbility() {
        return ability;
    }

    public String getItem() {
        return item;
    }

    public List<String> getMoves() {
        return moves;
    }
}