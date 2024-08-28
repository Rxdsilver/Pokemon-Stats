package com.hippo.controller;

import com.hippo.objects.rk9.Tournament;
import com.hippo.repository.TournamentRepository;
import com.hippo.utils.rk9.GetData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository; // Injection du repository

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTournament(@RequestBody String rk9) {
        // Vérifie si un tournoi avec le même rk9 existe déjà
        Optional<Tournament> existingTournament = tournamentRepository.findByRk9(rk9);
        if (existingTournament.isPresent()) {
            // Retourne un statut 409 Conflict si un tournoi existe déjà
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "A tournament with the same RK9 already exists."));
        }

        // Crée un tournoi à partir des données rk9
        Tournament tournament = GetData.createTournament(rk9);

        // Enregistre le tournoi dans la base de données MongoDB
        tournamentRepository.save(tournament);

        // Prépare les données pour la réponse JSON
        Map<String, Object> tournamentData = new LinkedHashMap<>();
        tournamentData.put("name", tournament.getName());
        tournamentData.put("rk9", tournament.getRk9());
        tournamentData.put("startDate", tournament.getStartDate());
        tournamentData.put("endDate", tournament.getEndDate());
        tournamentData.put("rounds", tournament.getRounds());
        tournamentData.put("players", tournament.getPlayers());

        // Retourne une réponse 201 Created avec les données du tournoi
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentData);
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getTournaments(@RequestParam(required = false) String name){
        List<Tournament> tournaments;
        if (name != null) {
            tournaments = tournamentRepository.findByName(name);
        } else {
            tournaments = tournamentRepository.findAll();
        }
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable("id") String id) {
        return tournamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable("id") String id) {
        tournamentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private <K> LinkedHashMap<K, Integer> sortByValue(Map<K, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // in case of key collision, keep existing key
                        LinkedHashMap::new // return a LinkedHashMap to maintain order
                ));
    }
}
