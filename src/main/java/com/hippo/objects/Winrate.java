package com.hippo.objects;

public class Winrate {

    private int wins;
    private int total;

    public Winrate(int wins, int total) {
        this.wins = wins;
        this.total = total;
    }

    public int getWins() {
        return wins;
    }

    public int getTotal() {
        return total;
    }
}
