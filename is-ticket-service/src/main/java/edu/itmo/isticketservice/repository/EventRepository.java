package edu.itmo.isticketservice.repository;

import edu.itmo.isticketservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsEventById(int id);
    Optional<Event> findEventById(int id);

}
