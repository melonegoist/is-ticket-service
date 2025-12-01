package edu.itmo.isticketservice.repository;

import edu.itmo.isticketservice.model.ImportOperation;
import edu.itmo.isticketservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportOperationRepository extends JpaRepository<ImportOperation, Long> {

    List<ImportOperation> findAllByUserOrderByStartedAtDesc(User user);

}
