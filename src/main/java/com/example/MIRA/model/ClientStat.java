package com.example.MIRA.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientStat {

    private String client;
    private int count;
    private double mean;
    private double std;
    private double min;
    private double twentyFive;
    private double fifty;
    private double seventyFive;
    private double max;

}
