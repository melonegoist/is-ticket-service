package edu.itmo.isticketservice.repository;

import edu.itmo.isticketservice.model.Person;
import edu.itmo.isticketservice.model.Ticket;
import edu.itmo.isticketservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByNameContainingIgnoreCase(String name);
    List<Ticket> findByNameStartingWithIgnoreCase(String prefix);
    List<Ticket> findByNumberLessThan(Integer number);
    List<Ticket> findByNumberGreaterThan(Integer number);

    List<Ticket> findTicketByPersonPassportID(String personPassportID);

    List<Ticket> findByPerson(Person person);
    List<Ticket> findByUser(User user);
    List<Ticket> findByPriceLessThan(Integer price);

    void deleteByVenue_Id(Long venueId);

}
