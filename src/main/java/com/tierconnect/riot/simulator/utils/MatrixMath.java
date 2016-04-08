package com.tierconnect.riot.simulator.utils;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Convert List
 * Created by angelchambi on 3/16/16.
 */
public class MatrixMath{
    public static RealMatrix convertMatrixReal(List<List<String>> matrix){

        double[][] realMatrix = new double[matrix.size() - 1][matrix.size() - 1];
        for(int i = 0; i < realMatrix.length; i++){
            List<String> row = matrix.get(i + 1);
            for(int j = 0; j < realMatrix[i].length; j++){
                realMatrix[i][j] = Double.parseDouble(row.get(j + 1));
            }
        }
        return MatrixUtils.createRealMatrix(realMatrix);
    }

    public static RealMatrix createTransitionVector(RealMatrix realMatrix){
        for(int i = 0; i < realMatrix.getRowDimension(); i++){
            double rowSum = DoubleStream.of(realMatrix.getRow(i)).sum();
            realMatrix.setRowVector(i, realMatrix.getRowVector(i).mapDivideToSelf(rowSum));
        }
        return realMatrix;
    }

    public static int getRandomIndex(double[] vector){
        List<Integer> probabilities = new ArrayList<>();
        for(int i = 0; i < vector.length; i++){
            if (vector[i] != 0) {
                probabilities.add(i);
            }
        }
        return probabilities.get((int)(Math.random() * probabilities.size()));
    }
}
