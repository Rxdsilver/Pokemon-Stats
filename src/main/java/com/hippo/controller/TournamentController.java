package com.hippo.controller;

import com.hippo.enums.PairingStatus;
import com.hippo.objects.DateRange;
import com.hippo.objects.PokemonSearchCriteria;
import com.hippo.objects.Winrate;
import com.hippo.objects.rk9.*;
import com.hippo.objects.stats.usage.SinglePokemonUsage;
import com.hippo.objects.stats.winrate.SinglePokemonWinrate;
import com.hippo.repository.TournamentRepository;
import com.hippo.utils.rk9.GetData;
import com.hippo.utils.stats.usage.GetUsage;
import com.hippo.utils.stats.winrate.GetWinrate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @GetMapping("/usage")
    public ResponseEntity<List<SinglePokemonUsage>> getUsage(
            @RequestParam(required = false) String name,
            @RequestBody(required = false) DateRange dateRange){
        List<Tournament> tournaments;
        if (name != null) {
            tournaments = tournamentRepository.findByName(name);
        } else {
            tournaments = tournamentRepository.findAll();
        }

        String startDate = dateRange.getStartDate();
        String endDate = dateRange.getEndDate();

        // System.out.println("Start Date: " + startDate + " | End Date: " + endDate);

        if (startDate != null && endDate != null) {
            tournaments = tournaments.stream()
                    .filter(t -> isWithinDateRange(t.getStartDate(), startDate, endDate))
                    .collect(Collectors.toList());
        }
        List<SinglePokemonUsage> usage = new GetUsage().getUsageData(tournaments);
        usage.sort((o1, o2) -> o2.getUsage() - o1.getUsage());

        // Sort each map (moves, tera, item, ability) in descending order
        usage.forEach(singlePokemonUsage -> {
            singlePokemonUsage.setMoves(sortByValue(singlePokemonUsage.getMoves()));
            singlePokemonUsage.setTera(sortByValue(singlePokemonUsage.getTera()));
            singlePokemonUsage.setItem(sortByValue(singlePokemonUsage.getItem()));
            singlePokemonUsage.setAbility(sortByValue(singlePokemonUsage.getAbility()));
        });


        return ResponseEntity.ok(usage);

    }

    @GetMapping("/winrate")
    public ResponseEntity<Winrate> getWinrate(@RequestBody PokemonSearchCriteria criteria) {
        // Extraire les dates du JSON
        String startDate = criteria.getDates() != null ? criteria.getDates().getStartDate() : null;
        String endDate = criteria.getDates() != null ? criteria.getDates().getEndDate() : null;

        // Filtrer les tournois par date si les dates sont spécifiées
        List<Tournament> tournaments = tournamentRepository.findAll().stream()
                .filter(t -> isWithinDateRange(t.getStartDate(), startDate, endDate))
                .toList();

        for (Tournament tournament : tournaments) {
            System.out.println(tournament.getName());
        }

        List<Player> players = new ArrayList<>();

        int totalWins = 0;
        int totalMatches = 0;

        // Parcourir chaque tournoi
        for (Tournament tournament : tournaments) {
            // Ajouter les joueurs du tournoi dont l'équipe correspond aux critères
            for (Player player : tournament.getPlayers()) {

                if (doesTeamMatchCriteria(player.getTeam(), criteria.getPokemons())) {
                    players.add(player);
                    System.out.println(player.getName()+" in "+tournament.getName());
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

    @GetMapping("/teams")
    public Map<String, Object> getTeams(@RequestBody PokemonSearchCriteria criteria) {
        List<Map<String, Object>> results = new ArrayList<>();
        int totalTeams = 0;

        // Extraire les dates du JSON
        String startDate = criteria.getDates() != null ? criteria.getDates().getStartDate() : null;
        String endDate = criteria.getDates() != null ? criteria.getDates().getEndDate() : null;

        // Filtrer les tournois par date si les dates sont spécifiées
        List<Tournament> tournaments = tournamentRepository.findAll().stream()
                .filter(t -> isWithinDateRange(t.getStartDate(), startDate, endDate))
                .toList();

        // Parcourir chaque tournoi
        for (Tournament tournament : tournaments) {
            // Filtrer les joueurs selon les critères
            List<Player> filteredPlayers = filterPlayersByCriteria(tournament.getPlayers(), criteria.getPokemons());

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

    // Méthode pour vérifier si une date est dans la plage spécifiée
    private boolean isWithinDateRange(String tournamentDate, String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate tournamentStartDate = LocalDate.parse(tournamentDate, formatter);
            LocalDate start = startDate != null ? LocalDate.parse(startDate, formatter) : LocalDate.MIN;
            LocalDate end = endDate != null ? LocalDate.parse(endDate, formatter) : LocalDate.MAX;

            boolean isWithinRange = !tournamentStartDate.isBefore(start) && !tournamentStartDate.isAfter(end);
            // System.out.println("Tournament Date: " + tournamentDate + " | Start Date: " + startDate + " | End Date: " + endDate + " | Within Range: " + isWithinRange);
            return isWithinRange;
        } catch (DateTimeParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
            return false;
        }
    }

    // Méthode pour filtrer les joueurs selon les critères
    private List<Player> filterPlayersByCriteria(List<Player> players, List<Pokemon> criteriaPokemons) {
        return players.stream()
                .filter(player -> {
                    Team team = player.getTeam();
                    boolean matches = true;

                    for (Pokemon criterion : criteriaPokemons) {
                        boolean foundPokemon = team.getPokemons().stream().anyMatch(pokemon -> pokemon.getName().equals(criterion.getName()) &&
                                        (criterion.getType() == null || criterion.getType().equals(pokemon.getType())) &&
                                        (criterion.getAbility() == null || criterion.getAbility().equals(pokemon.getAbility())) &&
                                        (criterion.getItem() == null || criterion.getItem().equals(pokemon.getItem())) &&
                                        (criterion.getMoves() == null || pokemon.getMoves() != null && pokemon.getMoves().containsAll(criterion.getMoves())));

                        if (!foundPokemon) {
                            matches = false;
                            break;
                        }
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    // Méthode pour vérifier si une équipe correspond aux critères
    private boolean doesTeamMatchCriteria(Team team, List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            boolean found = false;
            for (Pokemon teamPokemon : team.getPokemons()) {
                if (pokemon.getName().equals(teamPokemon.getName())) {
                    found = true;

                    // Vérifier si le type correspond
                    if (pokemon.getType() != null && !pokemon.getType().equals(teamPokemon.getType())) {
                        // System.out.println("Type mismatch: " + pokemon.getType() + " != " + teamPokemon.getType());
                        found = false;
                        break;
                    }

                    // Vérifier si l'ability correspond
                    if (pokemon.getAbility() != null && !pokemon.getAbility().equals(teamPokemon.getAbility())) {
                        // System.out.println("Ability mismatch: " + pokemon.getAbility() + " != " + teamPokemon.getAbility());
                        found = false;
                        break;
                    }

                    // Vérifier si l'item correspond
                    if (pokemon.getItem() != null && !pokemon.getItem().equals(teamPokemon.getItem())) {
                        // System.out.println("Item mismatch: " + pokemon.getItem() + " != " + teamPokemon.getItem());
                        found = false;
                        break;
                    }

                    // Vérifier si les moves sont inclus dans le Pokémon de l'équipe
                    if (pokemon.getMoves() != null && !new HashSet<>(teamPokemon.getMoves()).containsAll(pokemon.getMoves())) {
                        // System.out.println("Moves mismatch: " + pokemon.getMoves() + " not in " + teamPokemon.getMoves());
                        found = false;
                        break;
                    }


                }
                // Vérifier si le type correspond

            }
            if (!found) {
                return false;
            }
        }
        return true;
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
