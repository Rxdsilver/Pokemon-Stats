package com.hippo.objects;

import com.hippo.enums.RoundStatus;

import java.util.List;

public class Round {
    private List<Pairing> pairings;
    private int roundNumber;
    private RoundStatus status;

    public Round(List<Pairing> pairings, int roundNumber) {
        this.pairings = pairings;
        this.roundNumber = roundNumber;
        this.status = RoundStatus.NOT_STARTED;
    }

}
