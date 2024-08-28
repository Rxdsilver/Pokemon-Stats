package com.hippo.objects.rk9;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tournaments")
public class Tournament {

    @Id
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("rk9")
    private String rk9;

    @JsonProperty("rounds")
    private List<List<Pairing>> rounds;

    @JsonProperty("players")
    private List<Player> players;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;


    public Tournament() {
    }

    @JsonPropertyOrder({"name", "rk9", "startDate", "endDate", "rounds", "players"})
    public Tournament(String name, String rk9, String startDate, String endDate, List<List<Pairing>> rounds, List<Player> players) {
        this.name = name;
        this.rk9 = rk9;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rounds = rounds;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public String getRk9() {
        return rk9;
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
