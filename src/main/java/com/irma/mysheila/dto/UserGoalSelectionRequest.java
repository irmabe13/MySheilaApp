package com.irma.mysheila.dto;

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
public class UserGoalSelectionRequest {

    @NotEmpty(message = "La liste des IDs d'objectifs ne peut pas être vide")
    private List<Integer> goalIds;
}
