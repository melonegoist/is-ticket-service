package edu.itmo.isticketservice.controller;

import edu.itmo.isticketservice.dto.ImportOperationResponse;
import edu.itmo.isticketservice.model.Role;
import edu.itmo.isticketservice.model.User;
import edu.itmo.isticketservice.repository.UserRepository;
import edu.itmo.isticketservice.security.ImportOperationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportOperationController {

    private final ImportOperationService importOperationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ImportOperationResponse>> listOperations(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userDetails.getUsername()));

        List<ImportOperationResponse> operations = ((currentUser.getRole() == Role.ROLE_ADMIN)
                ? importOperationService.findAll()
                : importOperationService.findAllForUser(currentUser))
                .stream()
                .map(ImportOperationResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(operations);
    }

}
