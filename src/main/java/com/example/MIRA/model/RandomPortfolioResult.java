package com.example.MIRA.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RandomPortfolioResult {

    private double[][] weightsRecord;
    private double[][] results ;

}