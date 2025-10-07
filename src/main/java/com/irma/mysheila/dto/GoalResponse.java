package com.irma.mysheila.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {

    private Integer idGoal;
    private String name;
    private String categoryName;
    private String periodicity;
    private Integer frequency;
    private Boolean isSelected;
}
