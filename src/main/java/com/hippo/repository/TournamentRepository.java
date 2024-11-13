package com.hippo.repository;

import com.hippo.objects.rk9.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends MongoRepository<Tournament, String>{
    List<Tournament> findByName(String name);
    Optional<Tournament> findByRk9(String rk9);

    Optional<Tournament> deleteByRk9(String rk9);
}
