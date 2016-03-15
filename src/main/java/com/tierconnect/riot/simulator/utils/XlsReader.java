package com.tierconnect.riot.simulator.utils;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * XLS READER
 * Created by angelchambi on 3/15/16.
 */
public class XLSReader{
    private String xlsFullPath;


    public XLSReader(String path, String fileNameWithEx){
        xlsFullPath = Paths.get(System.getProperty("user.dir"), path, fileNameWithEx).toString();
    }

    public List<List<String>> loadXls(String sheetName){
        List<List<String>> transitionMatrix = null;
        try{

            FileInputStream file = new FileInputStream(new File(xlsFullPath));

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheet(sheetName);

            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            //Instance Transition Matrix
            transitionMatrix = new ArrayList<>();
            while(rowIterator.hasNext()){
                Row row = rowIterator.next();

                //For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();

                //instance array list row
                List<String> rowList = new ArrayList<>();
                while(cellIterator.hasNext()){

                    Cell cell = cellIterator.next();
                    ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
                    switch(cell.getCellType()){
                        case Cell.CELL_TYPE_BOOLEAN:
                            rowList.add(convertUtilsBean.convert(cell.getBooleanCellValue()));
                            System.out.print(cell.getBooleanCellValue() + "\t\t");
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            rowList.add(convertUtilsBean.convert(cell.getNumericCellValue()));
                            System.out.print(cell.getNumericCellValue() + "\t\t");
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + "\t\t");
                            rowList.add(cell.getStringCellValue());
                            break;
                    }
                }
                transitionMatrix.add(rowList);
                System.out.println("");
            }
            file.close();
            FileOutputStream out = new FileOutputStream(new File(xlsFullPath));
            workbook.write(out);
            out.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return transitionMatrix;
    }
}
