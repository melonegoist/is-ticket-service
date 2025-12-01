package edu.itmo.isticketservice.security;

import edu.itmo.isticketservice.model.ImportOperation;
import edu.itmo.isticketservice.model.ImportStatus;
import edu.itmo.isticketservice.model.User;
import edu.itmo.isticketservice.repository.ImportOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOperationService {

    private final ImportOperationRepository importOperationRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportOperation startOperation(User user) {
        ImportOperation operation = ImportOperation.builder()
                .user(user)
                .status(ImportStatus.IN_PROGRESS)
                .build();

        return importOperationRepository.save(operation);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long operationId, int createdCount) {
        importOperationRepository.findById(operationId).ifPresent(operation -> {
            operation.setStatus(ImportStatus.SUCCESS);
            operation.setCreatedCount(createdCount);
            operation.setCompletedAt(LocalDateTime.now());
            importOperationRepository.save(operation);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailure(Long operationId, String errorMessage) {
        importOperationRepository.findById(operationId).ifPresent(operation -> {
            operation.setStatus(ImportStatus.FAILED);
            operation.setErrorMessage(errorMessage);
            operation.setCompletedAt(LocalDateTime.now());
            importOperationRepository.save(operation);
        });
    }

    @Transactional(readOnly = true)
    public List<ImportOperation> findAll() {
        return importOperationRepository.findAll(Sort.by(Sort.Direction.DESC, "startedAt"));
    }

    @Transactional(readOnly = true)
    public List<ImportOperation> findAllForUser(User user) {
        return importOperationRepository.findAllByUserOrderByStartedAtDesc(user);
    }

}
