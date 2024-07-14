package dev.arun.ecomuserservice.security.repository;


import dev.arun.ecomuserservice.security.model.Client1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client1, String> {
    Optional<Client1> findByClientId(String clientId);
}