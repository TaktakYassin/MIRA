package com.example.MIRA.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Outils {

    double[][] initMatrice(int nl,int nc){
        double[][] matrice = new double[nl][nc];
        for (int i=0;i<nl;i++) {
            for (int j =0; j < nc; j++) {
                matrice[i][j]=0.0;
            }
        }
        return matrice;
    }

    double equarType(double[]tab,double moyenne,double count){

        double v=0;
        for(double x:tab)
        {
            v+=Math.pow((x-moyenne),2)/count;
        }
        return Math.sqrt(v);
    }

    double minimum(double[] tab){
        double min=Double.MAX_VALUE;
        for(double d:tab){
            if(d<min)
                min=d;
        }
        return min;
    }

    int indexMinimum(double[] tab){
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

    double maximum(double[] tab){
        double max=Double.MIN_VALUE;
        for(double d:tab){
            if(d>max)
                max=d;
        }
        return max;
    }

    int indexMaximum(double[] tab){
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

    double moyenne(double[] tab){

        double result=0;
        for(double x:tab)
        {
            result+=x;
        }
        if(tab.length>0)
            result=doublePrecision(result/tab.length,6);
        return result;
    }

    double meanVariation(double[][] matrice,int indexColumn){

        double result=0;
        for(int i=1;i<matrice.length;i++)
        {
            result+=matrice[i][indexColumn];
        }
        if(matrice.length>1)
            result=doublePrecision(result/(matrice.length-1),6);
        return result;
    }

    void displayMatrice(double mat[][]) {
        // Loop through all rows
        for (double[] row : mat)

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
    }

    double[] extractColumn(double[][] matrice,int indexColumn){
        double[] column=new double[matrice.length];
        for(int i=0;i<matrice.length;i++)
        {
            column[i]=matrice[i][indexColumn];
        }
        return column;
    }

    double[] extractLigne(double[][] matrice,int indexLigne){
        double[] ligne = new double[matrice[0].length];
        for (int j = 0; j < matrice[0].length; j++) {
            ligne[j] = matrice[indexLigne][j];
        }
        return ligne;
    }

    double doublePrecision(double value,int precision){
        value=value*Math.pow(10,precision);
        long valueInteger=(long) value;
        value=(double) valueInteger;
        value=value/Math.pow(10,precision);
        return value;
    }

    protected double[][] extractRecentDataFromMatrice(double[][] data,int periode){

        double[][] newData=new double[periode][data[0].length];
        for(int i=0;i<periode;i++){
            for(int j=0;j<newData[0].length;j++){
                newData[i][j]=data[i][j];
            }
        }
        return newData;
    }


}
