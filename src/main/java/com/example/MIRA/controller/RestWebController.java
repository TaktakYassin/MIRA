package com.example.MIRA.controller;


import com.example.MIRA.model.ClientStat;
import com.example.MIRA.service.MainService;
import com.example.MIRA.service.Outils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.Map;

@ComponentScan
@Scope("prototype")
@RestController
@Api(value = "Guidelines", description = "Describes the guidelines for Spring boot 2.0.1 for uploading large file using Swagger UI")
public class RestWebController extends Outils {

    @Autowired
    private MainService mainService;

    @Value("${numPortfolios}")
    private int numPortfolios;
    @Value("${riskFreeRate}")
    private double riskFreeRate;
    @Value("${period}")
    private int period;

    @PostMapping(value = "01/recuperateStat",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ClientStat> recuperateStat(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart("file") MultipartFile file) {
            mainService.readFromExcelFile(convertMultiPartToFile(file));
            return mainService.recuperateStat();
    }

    @PostMapping(value = "02/recuperateVariations",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,Double> recuperateVariations(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart("file") MultipartFile file) {
        mainService.readFromExcelFile(convertMultiPartToFile(file));
        mainService.calculateVariationNormalised();
        return mainService.meanVariation();
    }

    private File convertMultiPartToFile(MultipartFile file ) {
        File convFile = new File( file.getOriginalFilename() );
        try {
            FileOutputStream fos = new FileOutputStream( convFile );
            fos.write( file.getBytes() );
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }

    @PostMapping(value = "03/randomPortfolios",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void randomPortfolios(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart("file") MultipartFile file) {
        mainService.readFromExcelFile(convertMultiPartToFile(file));
        //récupérer seulement les données des 12 derniers mois
        //double[][] recentData=extractRecentDataFromMatrice(mainService.getData(),period);
        //mainService.setData(recentData);
        mainService.calculateVariationNormalised();
        mainService.meanVariation();
        mainService.displaySimulated(mainService.getVariationMoyenne(),mainService.getVariations(),numPortfolios,riskFreeRate,period);
    }






}
