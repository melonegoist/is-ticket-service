package edu.itmo.isticketservice.controller;

import edu.itmo.isticketservice.dto.PersonCreationRequest;
import edu.itmo.isticketservice.dto.PersonCreationResponse;
import edu.itmo.isticketservice.model.Person;
import edu.itmo.isticketservice.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personService.getAllPersons();

        return ResponseEntity.ok(persons);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonCreationResponse> createPerson(@Valid @RequestBody PersonCreationRequest request) {
        PersonCreationResponse createdPerson = personService.createPerson(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        if (personService.deletePerson(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
