package com.hippo.objects;

public class Player {
    String firstname;
    String lastname;
    String country;
    Team team;

    public Player(String firstname, String lastname, String country, Team team) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.country = country;
        this.team = team;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCountry() {
        return country;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
