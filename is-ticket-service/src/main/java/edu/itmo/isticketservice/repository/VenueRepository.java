package edu.itmo.isticketservice.repository;

import edu.itmo.isticketservice.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    Optional<Venue> findVenueById(Long id);
    boolean existsVenueById(Long id);

}
