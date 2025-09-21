package edu.itmo.isticketservice.repository;

import edu.itmo.isticketservice.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {

    Optional<Person> findPersonByPassportID(String passportID);
    boolean existsPersonByPassportID(String passportID);

}
