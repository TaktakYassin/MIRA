package com.example.MIRA.service;

import com.example.MIRA.model.ClientStat;
import com.example.MIRA.model.PortfolioResult;
import com.example.MIRA.model.RandomPortfolioResult;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@NoArgsConstructor
@Slf4j
@Data
@Getter
public class MainService {

    private List<String> dates;
    private List<String> clients;
    private double[][] data;
    private double[][] newData;
    private double[] variationMoyenne;

    private static final int PERIOD=12;

    public void readFromExcelFile(File file) {
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            data=new double[sheet.getRows()-1][sheet.getColumns()-1];
            Cell[] row;
            dates=new ArrayList<>();
            clients=new ArrayList<>();
            for(int i=1;i<sheet.getRow(0).length;i++) {
                clients.add(sheet.getRow(0)[i].getContents());
            }
            for(int i=1;i<sheet.getColumn(0).length;i++) {
                dates.add(sheet.getColumn(0)[i].getContents());
            }
            for(int i=1;i<sheet.getRows();i++)
            {
                row=sheet.getRow(i);
                for(int j=1;j<sheet.getColumns();j++)
                {
                    data[i-1][j-1]=Double.valueOf(row[j].getContents().replace(",","."));
                }
            }
            System.out.println("les données récupérées du fichier excel : ");
            displayMatrice(data);
            /*Matrix matrix = new Matrix(data);
            matrix.print(matrix.rank(), 0);*/
        }
        catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    public double[][] calculateVariationNormalised(){
        newData=new double[data.length][data[0].length];
        for(int j=0;j<data[0].length;j++)
        {
            newData[0][j]=0.0;
            for(int i=1;i<data.length;i++)
            {
                newData[i][j]=doublePrecision((data[i][j]-data[i-1][j])/data[i-1][j],6);
            }
        }
        System.out.println("Aprés le calcul de la variation et la normalisation : ");
        displayMatrice(newData);
        return newData;
    }

    public Map<String,Double> meanVariation(){
        Map<String,Double> variations=new HashMap<>();
        double moy;
        variationMoyenne=new double[newData[0].length];
        for(int j=0;j<newData[0].length;j++)
        {
            moy=meanVariation(newData,j);
            variationMoyenne[j]=moy;
            variations.put(clients.get(j),moy);
        }
        return variations;
    }

    public List<ClientStat> recuperateStat(){

        List<ClientStat> stats=new ArrayList<>();
        int count=data.length;
        double mean;
        double std;
        double min;
        double max;
        double twentyFive;
        double fifty;
        double seventyFive;
        double[] buffer;
        for(int j=0;j<data[0].length;j++)
        {
            System.out.println(clients.get(j));
            buffer=extractColumn(data,j);
            mean=moyenne(buffer);
            std=equarType(buffer,mean,count);
            min=minimum(buffer);
            max=maximum(buffer);
            twentyFive=(max-min)/4+min;
            fifty=(max-min)/2+min;
            seventyFive=(max-min)*3/4+min;
            stats.add(new ClientStat(clients.get(j),count,mean,std,min,twentyFive,fifty,seventyFive,max));
        }
        return stats;
    }

    private double equarType(double[]tab,double moyenne,double count){

        double v=0;
        for(double x:tab)
        {
            v+=Math.pow((x-moyenne),2)/count;
        }
        return Math.sqrt(v);
    }

    private double minimum(double[] tab){
        double min=Double.MAX_VALUE;
        for(double d:tab){
            if(d<min)
                min=d;
        }
        return min;
    }

    private int indexMinimum(double[] tab){
        double min=Double.MAX_VALUE;
        int index=0;
        for(int i=0;i<tab.length;i++){
            if(tab[i]<min)
            {
                index=i;
                min=tab[i];
            }
        }
        return index;
    }

    private double maximum(double[] tab){
        double max=Double.MIN_VALUE;
        for(double d:tab){
            if(d>max)
                max=d;
        }
        return max;
    }

    private int indexMaximum(double[] tab){
        double max=Double.MIN_VALUE;
        int index=0;
        for(int i=0;i<tab.length;i++){
            if(tab[i]>max)
            {
                index=i;
                max=tab[i];
            }
        }
        return index;
    }

    private double moyenne(double[] tab){

        double result=0;
        for(double x:tab)
        {
            result+=x;
        }
        if(tab.length>0)
            result=doublePrecision(result/tab.length,6);
        return result;
    }

    private double meanVariation(double[][] matrice,int indexColumn){

        double result=0;
        for(int i=1;i<matrice.length;i++)
        {
            result+=matrice[i][indexColumn];
        }
        if(matrice.length>1)
            result=doublePrecision(result/(matrice.length-1),6);
        return result;
    }

    private void displayMatrice(double mat[][]) {
        // Loop through all rows
        for (double[] row : mat)

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
    }

    private double[] extractColumn(double[][] matrice,int indexColumn){
        double[] column=new double[matrice.length];
        for(int i=0;i<matrice.length;i++)
        {
            column[i]=matrice[i][indexColumn];
        }
        return column;
    }

    private double[] extractLigne(double[][] matrice,int indexLigne){
       double[] ligne = new double[matrice[0].length];
        for (int j = 0; j < matrice[0].length; j++) {
            ligne[j] = matrice[indexLigne][j];
        }
        return ligne;
    }

    private double doublePrecision(double value,int precision){
        value=value*Math.pow(10,precision);
        long valueInteger=(long) value;
        value=(double) valueInteger;
        value=value/Math.pow(10,precision);
        return value;
    }

    private PortfolioResult portfolioAnnualisedPerformance(double[] weights,double[] meanReturns,double[][] covMatrix){

        double returns=0;
        for(int i=0;i<weights.length;i++)
            returns+=weights[i]*meanReturns[i];
        returns=returns*PERIOD;
        double[] tab=multiplyMatriceByVector(covMatrix,weights);
        double std=equarType(tab,moyenne(tab),PERIOD);
        return new PortfolioResult(returns,std);
    }

    private double[] multiplyMatriceByVector(double[][] covMatrix,double[] weights){
        double[] vectorResult=new double[covMatrix.length];
        double buffer;
        for(int i=0;i<covMatrix.length;i++)
        {
            buffer=0;
            for(int j=0;j<weights.length;j++)
            {
                buffer+= weights[j]*covMatrix[i][j];
            }
            vectorResult[i]=buffer;
        }
        return vectorResult;
    }

    private RandomPortfolioResult randomPortfolios(int numPortfolios,double[] meanReturns, double[][] covMatrix,double riskFreeRate){
        double[][] results = initMatrice(3,numPortfolios);
        double[] weights;
        double[][] weightsRecord=new double[numPortfolios][meanReturns.length];
        for (int i=0;i<numPortfolios;i++) {
            weights=generateRandomVector(meanReturns.length);
            for (int j=0;j<weights.length;j++)
                weightsRecord[i][j]=weights[j];
            PortfolioResult portfolioResult = portfolioAnnualisedPerformance(weights,meanReturns,covMatrix);
            results[0][i] = portfolioResult.getPortfolioStdDev();
            results[1][i] = portfolioResult.getPortfolioReturn();
            results[2][i] = (portfolioResult.getPortfolioReturn() - riskFreeRate) / portfolioResult.getPortfolioStdDev();
        }
        return new RandomPortfolioResult(weightsRecord,results);
    }

    public void displaySimulated(double[] meanReturns, double[][] covMatrix, int numPortfolios, double riskFreeRate){

        RandomPortfolioResult randomPortfolioResult = randomPortfolios(numPortfolios,meanReturns, covMatrix, riskFreeRate);
        double[][] results=randomPortfolioResult.getResults();
        double[][] weights=randomPortfolioResult.getWeightsRecord();
        int max_sharpe_idx = indexMaximum(extractLigne(results,2));
        double sdp=results[0][max_sharpe_idx];
        double rp=results[1][max_sharpe_idx];
        double[] max_sharpe_allocation = extractLigne(weights,max_sharpe_idx);
        for(int i=0;i<max_sharpe_allocation.length;i++)
            max_sharpe_allocation[i]=doublePrecision(max_sharpe_allocation[i]*100,2);
        int  min_vol_idx = indexMinimum(extractLigne(results,0));
        double sdp_min=results[0][min_vol_idx];
        double rp_min =results[1][min_vol_idx];
        double[] min_vol_allocation = extractLigne(weights,min_vol_idx);
        for(int i=0;i<min_vol_allocation.length;i++)
            min_vol_allocation[i]=doublePrecision(min_vol_allocation[i]*100,2);
        System.out.println("----------------------------------------------------------");
        System.out.println("Maximum Sharpe Ratio Portfolio Allocation\n");
        System.out.println("Annualised Return:"+ doublePrecision(rp,2));
        System.out.println( "Annualised Volatility:"+ doublePrecision(sdp,2));
        System.out.println("\n");
        HashMap<String,Double> allocationMax=new HashMap<>();
        for(int i=0;i<max_sharpe_allocation.length;i++)
        {
            allocationMax.put(clients.get(i),max_sharpe_allocation[i]);
            System.out.println(clients.get(i)+" : "+ max_sharpe_allocation[i]);
        }
        System.out.println("-");
        System.out.println("Minimum Volatility Portfolio Allocation\n");
        System.out.println("Annualised Return:"+ doublePrecision(rp_min,2));
        System.out.println("Annualised Volatility:"+ doublePrecision(sdp_min,2));
        System.out.println("\n");

        HashMap<String,Double> allocationMin=new HashMap<>();
        for(int i=0;i<min_vol_allocation.length;i++)
        {
            allocationMin.put(clients.get(i),min_vol_allocation[i]);
            System.out.println(clients.get(i)+" : "+ min_vol_allocation[i]);
        }
        /*plt.figure(figsize=(10, 7))
        plt.scatter(results[0,:],results[1,:],c=results[2,:],cmap='YlGnBu', marker='o', s=10, plt.colorbar()
        plt.scatter(sdp,rp,marker='*',color='r',s=500, label='Maximum Sharpe ratio')
        plt.scatter(sdp_min,rp_min,marker='*',color='g',s=500, label='Minimum volatility')
        plt.title('Simulated Portfolio Optimization based on Efficient Frontier')
        plt.xlabel('annualised volatility')
        plt.ylabel('annualised returns')
        plt.legend(labelspacing=0.8)*/

    }

    private double[][] initMatrice(int nl,int nc){
        double[][] matrice = new double[nl][nc];
        for (int i=0;i<nl;i++) {
            for (int j =0; j < nc; j++) {
                matrice[i][j]=0.0;
            }
        }
        return matrice;
    }

    private double[] generateRandomVector(int sizeVector){

        double[] weights=new double[sizeVector];
        double somme=0;
        for(int i=0;i<sizeVector;i++)
        {
            weights[i]=Math.random();
            somme+=weights[i];
        }
        for(int i=0;i<sizeVector;i++)
            weights[i]=weights[i]/somme;

        return weights;

    }


}
