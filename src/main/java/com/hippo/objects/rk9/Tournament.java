package com.hippo.objects.rk9;

import com.hippo.enums.TournamentStatus;

import java.util.Date;
import java.util.List;

public class Tournament {

    private String name;
    private List<List<Pairing>> rounds;
    private List<Player> players;
    private String startDate;
    private String endDate;

    public Tournament(String name, String startDate, String endDate, List<List<Pairing>> rounds, List<Player> players) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rounds = rounds;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public List<List<Pairing>>getRounds() {
        return rounds;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
