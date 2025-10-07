package com.irma.mysheila.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyAvailabilityRequest {
    @NotEmpty(message = "La liste des disponibilités ne peut pas être vide")
    @Valid
    private List<AvailabilityRequest> availabilities;
}
