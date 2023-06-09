package com.example.bank.reportsLogic;

import android.os.Environment;

import com.example.bank.api.models.ReportClientsCurrenciesDto;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateWordFile {
    List<ReportClientsCurrenciesDto> clientCurrencies;

    public CreateWordFile(List<ReportClientsCurrenciesDto> clientCurrencies){
        this.clientCurrencies = clientCurrencies;
    }

    public boolean saveWordFile(String fileName) throws IOException {
        String path;
        File dir;
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            System.out.println("Storage not available or read only");
            return false;
        }
        boolean success = false;

        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph paragraphHeader = doc.createParagraph();
        paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
        paragraphHeader.setSpacingAfter(500);
        XWPFRun runHeader = paragraphHeader.createRun();

        runHeader.setText("Список валют по клиентам");
        runHeader.setBold(true);
        runHeader.setFontSize(30);

        for(ReportClientsCurrenciesDto client:clientCurrencies) {
            XWPFParagraph paragraphClient = doc.createParagraph();
            XWPFRun runClient = paragraphClient.createRun();
            paragraphClient.setSpacingBefore(400);
            runClient.setBold(true);
            runClient.setFontSize(24);
            runClient.setText(client.clientFIO);
            if(!client.getCurrencies().isEmpty()) {
                for (String currency : client.getCurrencies()) {
                    XWPFParagraph paragraphCurrency = doc.createParagraph();
                    XWPFRun runCurrency = paragraphCurrency.createRun();
                    runCurrency.setFontSize(24);
                    runCurrency.setText(currency);
                }
            }else{
                XWPFParagraph paragraphCurrencyEmpty = doc.createParagraph();
                XWPFRun runCurrencyEmpty = paragraphCurrencyEmpty.createRun();
                runCurrencyEmpty.setFontSize(22);
                runCurrencyEmpty.setItalic(true);
                runCurrencyEmpty.setColor("ff0000");
                runCurrencyEmpty.setText("Нет ни вкладов, ни кредитных программ");
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
            doc.write(new FileOutputStream(file));
            doc.close();
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

//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
