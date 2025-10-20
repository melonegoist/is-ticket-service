package edu.itmo.isticketservice.services;

import edu.itmo.isticketservice.dto.PersonCreationRequest;
import edu.itmo.isticketservice.dto.PersonCreationResponse;
import edu.itmo.isticketservice.model.Person;
import edu.itmo.isticketservice.model.Ticket;
import edu.itmo.isticketservice.repository.PersonRepository;
import edu.itmo.isticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final TicketRepository ticketRepository;

    public PersonCreationResponse createPerson(PersonCreationRequest request) {
        if (personRepository.existsPersonByPassportID(request.getPassportID())) {
            throw new IllegalArgumentException("Person with this passport ID already exists");
        }

        Person person = Person.builder()
                .passportID(request.getPassportID())
                .eyeColor(request.getEyeColor())
                .hairColor(request.getHairColor())
                .location(request.getLocation())
                .nationality(request.getNationality())
                .build();

        personRepository.save(person);

        return toDto(person);
    }

    public List<Person> getAllPersons() {
        List<Person> persons = personRepository.findAll();

        return persons;
    }

    public boolean deletePerson(String passportID) {
        if (personRepository.existsPersonByPassportID(passportID)) {
            personRepository.deleteByPassportID(passportID);

            ticketRepository.findTicketByPersonPassportID(passportID).forEach(ticket -> {
                ticketRepository.deleteById(ticket.getId());
            });


            return true;
        } else {
            return false;
        }
    }

    private PersonCreationResponse toDto(Person person) {
        return new PersonCreationResponse(
                String.format("Person with passport ID %s created successfully", person.getPassportID())
        );
    }



}
