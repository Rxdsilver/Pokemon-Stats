package com.hippo.utils.rk9;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hippo.objects.rk9.Pairing;
import com.hippo.objects.rk9.Player;
import com.hippo.objects.rk9.Tournament;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WriteData {

    // Ecrire un fichier JSON avec les données du tournoi, incluant les joueurs et leurs équipes
    public static void writeTournament(Tournament tournament) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Créer une structure de données pour contenir les informations du tournoi
        Map<String, Object> tournamentData = new LinkedHashMap<>();
        tournamentData.put("name", tournament.getName());
        tournamentData.put("startDate", tournament.getStartDate());
        tournamentData.put("endDate", tournament.getEndDate());
        tournamentData.put("players", tournament.getPlayers());
        tournamentData.put("pairings", tournament.getRounds());

        try (FileWriter writer = new FileWriter(tournament.getName().replace(" ", "_")+".json")) {
            gson.toJson(tournamentData, writer);
            System.out.println("Les données du tournoi ont été écrites dans tournament.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
