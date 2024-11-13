package com.hippo.objects;

public class Winrate {

    private int wins;
    private int total;
    private double winratePercentage;

    public Winrate(int wins, int total) {
        this.wins = wins;
        this.total = total;
        // Calculate winrate percentage with 2 decimal places
        this.winratePercentage = Math.round(((double) wins / total) * 10000) / 100.0;
    }

    public int getWins() {
        return wins;
    }

    public int getTotal() {
        return total;
    }

    public double getWinratePercentage() {
        return winratePercentage;
    }
}