package com.example.bank.reportsLogic;

import android.os.Environment;

import com.example.bank.api.models.ReportClientsCurrenciesDto;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateExcelFile {

    List<ReportClientsCurrenciesDto> clientCurrencies;

    public CreateExcelFile(List<ReportClientsCurrenciesDto> clientCurrencies){
        this.clientCurrencies = clientCurrencies;
    }

    public boolean saveExcelFile(String fileName) throws IOException {
        String path;
        File dir;
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            System.out.println("Storage not available or read only");
            return false;
        }
        boolean success = false;

        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Список валют по клиентам");

        //Заголовок
        Row rowTitle = sheet.createRow(0);
        Cell title = rowTitle.createCell(0);
        title.setCellValue("Валюты по клиентам");
        int rowIndex = 1;
        for (ReportClientsCurrenciesDto client:clientCurrencies) {
            Row rowEmpty = sheet.createRow(rowIndex);
            Row rowClient = sheet.createRow(rowIndex + 1);
            Cell cellFio = rowClient.createCell(0);
            cellFio.setCellValue(client.clientFIO);
            rowIndex++;
            if (!client.getCurrencies().isEmpty()) {
                for (String cur : client.getCurrencies()) {
                    Row rowCur = sheet.createRow(rowIndex+1);
                    Cell cellCur = rowCur.createCell(0);
                    cellCur.setCellValue(cur);
                    rowIndex++;
                }
            } else {
                Row rowCur = sheet.createRow(rowIndex+1);
                Cell cellCur = rowCur.createCell(0);
                cellCur.setCellValue("Нет ни вкладов, ни кредитных программ");
                rowIndex++;
            }
        }

        //Сохранение в файл
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Reports/";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try {
            // Записываем всё в файл
            book.write(new FileOutputStream(file));
            book.close();
            System.out.println("Writing file" + file);
            success = true;
        } catch (IOException e) {
            System.out.println("Error writing " + file + e);
        } catch (Exception e) {
            System.out.println("Failed to save file" + e);
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}

//        Font font = book.createFont();
//        font.setFontHeightInPoints((short)10);
//        font.setFontName("Arial");
//        font.setColor(IndexedColors.WHITE.getIndex());
//        font.setBold(true);
//        font.setItalic(false);
//
//        CellStyle styleBold = book.createCellStyle();
//        styleBold.setFont(font);

//cellFio.setCellStyle(styleBold);
//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
