package com.hippo.objects.rk9;

public class Player {
    String name;
    Team team;

    public Player(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }
    public Team getTeam() {
        return team;
    }

}
