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
public class XlsReader{
    private String xlsFullPath;


    public XlsReader(String path, String fileNameWithEx){
        xlsFullPath = Paths.get(System.getProperty("user.dir"), path, fileNameWithEx).toString();
    }

    public List<List<String>> loadXls(String sheetName){
        List<List<String>> transitionMatrix = null;
        try{

            InputStream file = new FileInputStream(xlsFullPath);

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file,false);

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
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            rowList.add(convertUtilsBean.convert(cell.getNumericCellValue()));
                            break;
                        case Cell.CELL_TYPE_STRING:
                            rowList.add(cell.getStringCellValue());
                            break;
                    }
                }
                if (rowList.size() != 0) {
                    transitionMatrix.add(rowList);
                }
            }
            file.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return transitionMatrix;
    }
}
