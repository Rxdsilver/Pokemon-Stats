package com.hippo.objects.stats.winrate;

import com.hippo.objects.Winrate;
import com.hippo.objects.stats.usage.SinglePokemonUsage;

import java.util.LinkedHashMap;

public class SinglePokemonWinrate extends SinglePokemonUsage {


    public SinglePokemonWinrate(String name, int usage, LinkedHashMap<String, Integer> tera, LinkedHashMap<String, Integer> item, LinkedHashMap<String, Integer> ability, LinkedHashMap<String, Integer> moves) {
        super(name, usage, tera, item, ability, moves);
    }

    private Winrate winrate;

    public Winrate getWinrate() {
        return winrate;
    }

    public void setWinrate(Winrate winrate) {
        this.winrate = winrate;
    }
}
