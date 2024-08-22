package com.hippo.objects;

import com.hippo.enums.PairingStatus;

public class Pairing {

    private Player player1;
    private Player player2;
    private PairingStatus status;

    public Pairing(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = PairingStatus.NOT_STARTED;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public PairingStatus getStatus() {
        return status;
    }

    public void setStatus(PairingStatus status) {
        this.status = status;
    }
}
