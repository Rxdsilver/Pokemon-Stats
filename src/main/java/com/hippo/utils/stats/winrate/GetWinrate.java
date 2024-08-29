package com.hippo.utils.stats.winrate;

import com.hippo.enums.PairingStatus;
import com.hippo.objects.Winrate;
import com.hippo.objects.rk9.Pairing;

import java.util.ArrayList;
import java.util.List;

public class GetWinrate {

    public Winrate getPlayerWinrate(List<Pairing> pairings, String playerName) {
        int wins = 0;
        int total = 0;

        for (Pairing pairing : pairings) {
            if (pairing.isBye()) {
                continue; // Ignorer les byes
            }
            boolean player1 = pairing.getPlayer1().equals(playerName);
            boolean player2 = pairing.getPlayer2().equals(playerName);

            if (player1 || player2) {
                total++;
                if ((player1 && pairing.getStatus() == PairingStatus.PLAYER1_WON) ||
                        (player2 && pairing.getStatus() == PairingStatus.PLAYER2_WON)) {
                    wins++;
                }
            }
        }

        return new Winrate(wins, total);
    }



    public Winrate getResults(List<Pairing> playerPairings, String playerName) {
        int wins = 0;
        int total = 0;

        for (Pairing pairing : playerPairings) {
            if ((pairing.getPlayer1().equals(playerName) && pairing.getStatus().equals(PairingStatus.PLAYER1_WON)) || (pairing.getPlayer2().equals(playerName) && pairing.getStatus().equals(PairingStatus.PLAYER2_WON))) {
                    wins++;
            }
            total++;

        }
        return new Winrate(wins, total);
    }

    public Winrate combineWinrates(List<Winrate> winrates) {
        int wins = 0;
        int total = 0;

        for (Winrate winrate : winrates) {
            wins += winrate.getWins();
            total += winrate.getTotal();
        }
        return new Winrate(wins, total);
    }

}
