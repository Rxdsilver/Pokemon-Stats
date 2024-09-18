package com.hippo.controller;

import com.hippo.enums.PairingStatus;
import com.hippo.objects.DateRange;
import com.hippo.objects.HeadToHeadSearchCriteria;
import com.hippo.objects.OneTeamSearchCriteria;
import com.hippo.objects.Winrate;
import com.hippo.objects.rk9.*;
import com.hippo.objects.stats.MultiplePokemonStats;
import com.hippo.objects.stats.SinglePokemonStats;
import com.hippo.repository.TournamentRepository;
import com.hippo.utils.GetData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository; // Injection du repository

    @PostMapping("/tournaments")
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

    @GetMapping("/tournaments")
    public ResponseEntity<List<Tournament>> getTournaments(@RequestParam(required = false) String name){
        List<Tournament> tournaments;
        if (name != null) {
            tournaments = tournamentRepository.findByName(name);
        } else {
            tournaments = tournamentRepository.findAll();
        }
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("tournaments/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable("id") String id) {
        return tournamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("tournaments/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable("id") String id) {
        tournamentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usage/{num}")
    public ResponseEntity<List<MultiplePokemonStats>> getUsageForMultiplePokemon(
            @PathVariable("num") int num,
            @RequestBody(required = false) DateRange dateRange){

        List<Tournament> tournaments = tournamentRepository.findAll();

        if (dateRange != null) {
            String startDate = dateRange.getStartDate();
            String endDate = dateRange.getEndDate();

            tournaments = tournamentRepository.findAll().stream()
                    .filter(t -> new GetData().isWithinDateRange(t.getStartDate(), startDate, endDate))
                    .toList();
        }

        List<MultiplePokemonStats> usage = new GetData().getUsageDataForMultiplePokemon(num, tournaments);

        usage.sort((o1, o2) -> o2.getUsage() - o1.getUsage());

        usage.forEach(multiplePokemonStats -> {
            multiplePokemonStats.getPokemons().forEach(pokemonUsage -> {
                pokemonUsage.setMoves(new GetData().sortByValue(pokemonUsage.getMoves()));
                pokemonUsage.setTera(new GetData().sortByValue(pokemonUsage.getTera()));
                pokemonUsage.setItem(new GetData().sortByValue(pokemonUsage.getItem()));
                pokemonUsage.setAbility(new GetData().sortByValue(pokemonUsage.getAbility()));
            });
        });

        return ResponseEntity.ok(usage);

    }


    @GetMapping("/usage")
    public ResponseEntity<List<SinglePokemonStats>> getUsage(
            @RequestBody(required = false) DateRange dateRange){
        List<Tournament> tournaments = tournamentRepository.findAll();

        if (dateRange != null) {
            String startDate = dateRange.getStartDate();
            String endDate = dateRange.getEndDate();

            tournaments = tournaments.stream()
                    .filter(t -> new GetData().isWithinDateRange(t.getStartDate(), startDate, endDate))
                    .collect(Collectors.toList());
        }


        List<SinglePokemonStats> usage = new GetData().getUsageData(tournaments);
        usage.sort((o1, o2) -> o2.getUsage() - o1.getUsage());

        // Sort each map (moves, tera, item, ability) in descending order
        usage.forEach(singlePokemonStats -> {
            singlePokemonStats.getPokemon().setMoves(new GetData().sortByValue(singlePokemonStats.getPokemon().getMoves()));
            singlePokemonStats.getPokemon().setTera(new GetData().sortByValue(singlePokemonStats.getPokemon().getTera()));
            singlePokemonStats.getPokemon().setItem(new GetData().sortByValue(singlePokemonStats.getPokemon().getItem()));
            singlePokemonStats.getPokemon().setAbility(new GetData().sortByValue(singlePokemonStats.getPokemon().getAbility()));
        });


        return ResponseEntity.ok(usage);

    }

    @GetMapping("/winrate")
    public ResponseEntity<Winrate> getWinrate(@RequestBody OneTeamSearchCriteria criteria) {
        // Extraire les dates du JSON
        String startDate = criteria.getDates() != null ? criteria.getDates().getStartDate() : null;
        String endDate = criteria.getDates() != null ? criteria.getDates().getEndDate() : null;

        // Filtrer les tournois par date si les dates sont spécifiées
        List<Tournament> tournaments = tournamentRepository.findAll().stream()
                .filter(t -> new GetData().isWithinDateRange(t.getStartDate(), startDate, endDate))
                .toList();

        List<Player> players = new ArrayList<>();

        int totalWins = 0;
        int totalMatches = 0;

        // Parcourir chaque tournoi
        for (Tournament tournament : tournaments) {
            // Ajouter les joueurs du tournoi dont l'équipe correspond aux critères
            for (Player player : tournament.getPlayers()) {

                if (new GetData().doesTeamMatchCriteria(player.getTeam(), criteria.getPokemons())) {
                    players.add(player);
                }
            }

            for (Player player : players) {
                for (List<Pairing> pairings: tournament.getRounds()) {
                    for (Pairing pairing: pairings) {
                        if (pairing.getPlayer1().equals(player.getName()) || pairing.getPlayer2().equals(player.getName())) {
                            totalMatches++;
                            // System.out.println(pairing.getPlayer1()+" vs "+pairing.getPlayer2());
                            if (pairing.getStatus() == PairingStatus.PLAYER1_WON && pairing.getPlayer1().equals(player.getName())) {
                                totalWins++;
                            } else if (pairing.getStatus() == PairingStatus.PLAYER2_WON && pairing.getPlayer2().equals(player.getName())) {
                                totalWins++;
                            }
                        }
                    }
                }
            }
        }

        Winrate combinedWinrate = new Winrate(totalWins, totalMatches);
        return ResponseEntity.ok(combinedWinrate);
    }

    @GetMapping("/head2head")
    public ResponseEntity<Winrate> getHead2Head(@RequestBody HeadToHeadSearchCriteria criteria) {
        // Extraire les dates du JSON
        String startDate = criteria.getDates() != null ? criteria.getDates().getStartDate() : null;
        String endDate = criteria.getDates() != null ? criteria.getDates().getEndDate() : null;

        // Filtrer les tournois par date si les dates sont spécifiées
        List<Tournament> tournaments = tournamentRepository.findAll().stream()
                .filter(t -> new GetData().isWithinDateRange(t.getStartDate(), startDate, endDate))
                .toList();

        List<Player> players = new ArrayList<>();

        int totalWins = 0;
        int totalMatches = 0;

        // Parcourir chaque tournoi
        for (Tournament tournament : tournaments) {
            // Ajouter les joueurs du tournoi dont l'équipe correspond aux critères
            for (Player player : tournament.getPlayers()) {
                if (new GetData().doesTeamMatchCriteria(player.getTeam(), criteria.getWinratePokemons())) {
                    players.add(player);
                    // System.out.println(player.getName()+" in "+tournament.getName());
                }
            }

            for (Player player : players) {
                for (List<Pairing> pairings: tournament.getRounds()) {
                    for (Pairing pairing: pairings) {
                        if (pairing.isBye()){
                            continue;
                        }
                        if (pairing.getPlayer1().equals(player.getName())) {
                            // System.out.println("Team matches with "+pairing.getPlayer1());
                            Team oppTeam = new GetData().searchTeamWithPlayerName(tournament.getPlayers(), pairing.getPlayer2());
                            if (oppTeam == null) {
                                continue;
                            }
                            if (new GetData().doesTeamMatchCriteria(oppTeam, criteria.getOpposingPokemons())) {
                                // System.out.println(pairing.getPlayer1()+ " vs. "+pairing.getPlayer2());
                                totalMatches++;
                                if (pairing.getStatus() == PairingStatus.PLAYER1_WON) {
                                    totalWins++;
                                }
                            }
                        } else if (pairing.getPlayer2().equals(player.getName())) {
                            // System.out.println("Team matches with "+pairing.getPlayer2());
                            Team oppTeam = new GetData().searchTeamWithPlayerName(tournament.getPlayers(), pairing.getPlayer1());
                            if (oppTeam == null) {
                                continue;
                            }
                            if (new GetData().doesTeamMatchCriteria(oppTeam, criteria.getOpposingPokemons())) {
                                // System.out.println(pairing.getPlayer2()+ " vs. "+pairing.getPlayer1());
                                totalMatches++;
                                if (pairing.getStatus() == PairingStatus.PLAYER2_WON) {
                                    totalWins++;
                                }
                            }
                        }
                    }
                }
            }
        }

        Winrate combinedWinrate = new Winrate(totalWins, totalMatches);
        return ResponseEntity.ok(combinedWinrate);
    }

    @GetMapping("/teams")
    public Map<String, Object> getTeams(@RequestBody OneTeamSearchCriteria criteria) {
        List<Map<String, Object>> results = new ArrayList<>();
        int totalTeams = 0;

        // Extraire les dates du JSON
        String startDate = criteria.getDates() != null ? criteria.getDates().getStartDate() : null;
        String endDate = criteria.getDates() != null ? criteria.getDates().getEndDate() : null;

        // Filtrer les tournois par date si les dates sont spécifiées
        List<Tournament> tournaments = tournamentRepository.findAll().stream()
                .filter(t -> new GetData().isWithinDateRange(t.getStartDate(), startDate, endDate))
                .toList();

        // Parcourir chaque tournoi
        for (Tournament tournament : tournaments) {
            // Filtrer les joueurs selon les critères
            List<Player> filteredPlayers = new GetData().filterPlayersByCriteria(tournament.getPlayers(), criteria.getPokemons());

            // Pour chaque joueur filtré, ajouter les infos à la liste de résultats
            for (Player player : filteredPlayers) {
                Map<String, Object> result = new HashMap<>();
                result.put("tournamentName", tournament.getName());
                result.put("playerName", player.getName());
                result.put("team", player.getTeam());
                result.put("startDate", tournament.getStartDate());
                results.add(result);
                totalTeams++;
            }
        }

        // Préparer la réponse avec le nombre total d'équipes
        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", totalTeams);
        response.put("teams", results);

        return response;
    }


}
