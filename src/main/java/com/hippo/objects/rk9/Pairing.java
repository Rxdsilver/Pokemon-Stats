package com.hippo.objects.rk9;

import com.hippo.enums.PairingStatus;

public class Pairing {

    private String player1;
    private String player2;
    private PairingStatus status;
    private boolean isBye;

    public Pairing(String player1, String player2, PairingStatus status, boolean isBye) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = status;
        this.isBye = isBye;
    }

    public String getPlayer1() {
        // Hotfix for ">TABLE 201 Nicholas> Borghi [US]"
        if (player1.equals(">TABLE 201 Nicholas> Borghi [US]")) {
            return "Nicholas Borghi [US]";
        }

        // Hotfix for "Chuppa Cross IV [US]"
        if (player1.equals("Chuppa Cross IV [US]")) {
            return "Chuppa Cross [US]";
        }

        // Hotfix for "O'Bryant Vison"
        if (player1.equals("O'Bryant Vison")) {
            return "O'Bryant Vinson [US]";
        }

        return player1;
    }

    public String getPlayer2() {
        // Hotfix for ">TABLE 201 Nicholas> Borghi [US]"
        if (player2.equals(">TABLE 201 Nicholas> Borghi [US]")) {
            return "Nicholas Borghi [US]";
        }

        // Hotfix for "Chuppa Cross IV [US]"
        if (player2.equals("Chuppa Cross IV [US]")) {
            return "Chuppa Cross [US]";
        }

        // Hotfix for "O'Bryant Vison"
        if (player2.equals("O'Bryant Vison")) {
            return "O'Bryant Vinson [US]";
        }

        return player2;
    }

    public PairingStatus getStatus() {
        return status;
    }

    public boolean isBye() {
        return isBye;
    }

    public void setStatus(PairingStatus status) {
        this.status = status;
    }
}
