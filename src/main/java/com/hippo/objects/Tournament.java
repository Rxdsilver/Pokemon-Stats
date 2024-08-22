package com.hippo.objects;

import com.hippo.enums.TournamentStatus;

import java.util.Date;
import java.util.List;

public class Tournament {

    private List<Round> rounds;
    private List<Player> players;
    private int currentRound;
    private int totalRounds;
    private TournamentStatus status;
    private Date startDate;

    public Tournament(List<Round> rounds, List<Player> players, int totalRounds) {
        this.rounds = rounds;
        this.players = players;
        this.currentRound = 1;
        this.totalRounds = totalRounds;
        this.status = TournamentStatus.NOT_STARTED;
        this.startDate = new Date();
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }
}
