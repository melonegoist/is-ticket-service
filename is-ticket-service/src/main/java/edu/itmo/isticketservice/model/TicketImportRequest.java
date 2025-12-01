package edu.itmo.isticketservice.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import edu.itmo.isticketservice.model.import_dto.TicketImportDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ticketsImport")
public class TicketImportRequest {

    @NotEmpty(message = "ticket list must not be empty")
    @Valid
    @JacksonXmlElementWrapper(localName = "tickets")
    @JacksonXmlProperty(localName = "ticket")
    private List<TicketImportDTO> tickets;

}
