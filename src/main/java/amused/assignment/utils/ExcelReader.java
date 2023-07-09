package amused.assignment.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ExcelReader {

    static InputStream inputStream = null;
    static XSSFSheet sheet;

    public static void initiateFileReading(String fileName, String sheetName) {

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            inputStream = classloader.getResourceAsStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheet(sheetName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object[][] getCellValueForAColumnName(String fileName, String sheetName, String columnName, int rowIndex) {
        initiateFileReading(fileName, sheetName);
        int columnIndex = 0;

        Row headingRow = sheet.getRow(0);
        Iterator<Cell> cellIterator = headingRow.cellIterator();

        // Get column index
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getStringCellValue().equals(columnName)) {
                columnIndex = cell.getColumnIndex();
                break;
            }
        }

        Row dataRow = sheet.getRow(rowIndex);
        String cellValueString = dataRow.getCell(columnIndex).getStringCellValue();
        Object[][] objectArray = new Object[][]{{cellValueString}};
        closeInputStream();
        return objectArray;

    }

    public static Object[][] getAllCellValuesInARow(String fileName, String sheetName, int rowIndex) {
        initiateFileReading(fileName, sheetName);
        int columnIndex = 0;
        Object[][] valueArray = new Object[1][];

        Row valueRow = sheet.getRow(rowIndex);

        List<String> valueList = new ArrayList<>();
        for (int i = 0; i < valueRow.getLastCellNum(); i++) {
            Cell cell = valueRow.getCell(i);

            if (cell.getCellType().equals(NUMERIC)) {
                valueList.add(Double.toString(cell.getNumericCellValue()));
            }
            else if (cell.getCellType().equals(STRING)) {
                valueList.add(cell.getStringCellValue());
            }

        }
        String[] names = valueList.toArray(new String[0]);
        valueArray[0] = names;

        closeInputStream();
        return valueArray;

    }

    public static void closeInputStream() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
