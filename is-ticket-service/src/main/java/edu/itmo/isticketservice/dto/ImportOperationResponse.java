package edu.itmo.isticketservice.dto;

import edu.itmo.isticketservice.model.ImportOperation;
import edu.itmo.isticketservice.model.ImportStatus;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ImportOperationResponse {

    Long id;
    ImportStatus status;
    String username;
    Integer createdCount;
    LocalDateTime startedAt;
    LocalDateTime completedAt;

    public static ImportOperationResponse fromEntity(ImportOperation operation) {
        return new ImportOperationResponse(
                operation.getId(),
                operation.getStatus(),
                operation.getUser().getUsername(),
                operation.getCreatedCount(),
                operation.getStartedAt(),
                operation.getCompletedAt()
        );
    }

}
