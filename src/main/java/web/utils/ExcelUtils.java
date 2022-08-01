package web.utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtils {
    static PrintWriter out = null;

    public static void initReporter() {
        System.out.println("const");
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("c:\\temp\\myfile.txt", true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write() {
        System.out.println("the text");
        System.out.println("in write");
    }

    public static void closeReporter() {
        out.close();
    }

    public static ArrayList<LinkedHashMap<String, String>> getTestDataRowUI(String fileName, String sSheetName) {
        String sMasterTDWorkbook = fileName;
        ArrayList<LinkedHashMap<String, String>> tdRows = getMapFromExcel(sMasterTDWorkbook, sSheetName);
        return tdRows;
    }

    private static String getCellValue(XSSFCell cell) {
        String value = null;
        System.out.println("cell.getCellType():" + cell.getCellType());
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                value = "" + cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                double floating_value = cell.getNumericCellValue();
                double fractionalPart = floating_value % 1;
                long integralPart = (long) Math.floor(floating_value);

                if (fractionalPart >= 0.0001)
                    value = "" + floating_value;
                else
                    value = "" + integralPart;
                break;

            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                String type = cell.getCellFormula();
                if (type.toUpperCase().contains("TODAY")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = cell.getStringCellValue();
                }
                break;
        }
        return value;
    }

    private static String getCellValue(HSSFCell cell) {
        String value = null;
        System.out.println("cell.getCellType():" + cell.getCellType());
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                value = "" + cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                double floating_value = cell.getNumericCellValue();
                double fractionalPart = floating_value % 1;
                long integralPart = (long) Math.floor(floating_value);

                if (fractionalPart >= 0.0001)
                    value = "" + floating_value;
                else
                    value = "" + integralPart;
                break;

            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                String type = cell.getCellFormula();
                if (type.toUpperCase().contains("TODAY")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = cell.getStringCellValue();
                }
                break;
        }
        return value;
    }

    public static ArrayList<LinkedHashMap<String, String>> getMapFromExcel(String sWorkBook, String sSheetName) {
        if (sWorkBook.endsWith(".xls"))
            return getMapsFromXLS(sWorkBook, sSheetName);
        else
            return getMapsFromXLSMultipleTestCase(sWorkBook, sSheetName);
    }

    public static ArrayList<LinkedHashMap<String, String>> getMapsFromXLS(String sWorkBook, String sSheetName) {
        ArrayList<String> colNames = new ArrayList<String>();
        ArrayList<LinkedHashMap<String, String>> mapArray = null;
        HSSFRow row = null;
        HSSFSheet sheet = null;
        int sheetRows = 0;
        int rowCols = 0;
        LinkedHashMap<String, String> rowMap = null;
        System.out.println("sWorkbook:" + sWorkBook);
        try {
            FileInputStream file = new FileInputStream(new File(sWorkBook));
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            sheet = workbook.getSheet(sSheetName);
            sheetRows = sheet.getPhysicalNumberOfRows();
            mapArray = new ArrayList<LinkedHashMap<String, String>>(sheetRows - 1);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                colNames.add("" + row.getCell(i).getStringCellValue());
            }
            rowCols = colNames.size();
            for (int i = 1; i < sheetRows; i++) {
                row = sheet.getRow(i);
                if (row.getCell(1).getStringCellValue().equalsIgnoreCase("Y")) {
                    rowMap = new LinkedHashMap<String, String>(rowCols);
                    for (int c = 0; c < rowCols; c++) {
                        String key = colNames.get(c);
                        String value = "" + getCellValue(row.getCell(c));
                        rowMap.put(key, value);
                    }
                    mapArray.add(rowMap);
                    System.out.println(rowMap);
                }
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapArray;
    }

    public static ArrayList<LinkedHashMap<String, String>> getMapFromXLSX(String sWorkBook, String sSheetName, String sTCName) {
        ArrayList<String> colNames = new ArrayList<String>();
        ArrayList<LinkedHashMap<String, String>> mapArray = null;
        HSSFRow row = null;
        HSSFSheet sheet = null;
        int sheetRows = 0;
        int rowCols = 0;
        LinkedHashMap<String, String> rowMap = null;
        System.out.println("sWorkbook:" + sWorkBook);
        try {
            FileInputStream file = new FileInputStream(new File(sWorkBook));
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            sheet = workbook.getSheet(sSheetName);
            sheetRows = sheet.getPhysicalNumberOfRows();
            mapArray = new ArrayList<LinkedHashMap<String, String>>(sheetRows - 1);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                colNames.add("" + row.getCell(i).getStringCellValue());
            }
            rowCols = colNames.size();
            for (int i = 1; i < sheetRows; i++) {
                row = sheet.getRow(i);
                if (row.getCell(1).getStringCellValue().equalsIgnoreCase(sTCName)) {
                    rowMap = new LinkedHashMap<String, String>(rowCols);
                    for (int c = 0; c < rowCols; c++) {
                        String key = colNames.get(c);
                        String value = "" + getCellValue(row.getCell(c));
                        rowMap.put(key, value);
                    }
                    mapArray.add(rowMap);
                    System.out.println(rowMap);
                }
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapArray;
    }

    public static ArrayList<LinkedHashMap<String, String>> getMapsFromXLSMultipleTestCase(String sWorkBook, String sSheetName) {
        ArrayList<String> colNames = new ArrayList<String>();
        ArrayList<LinkedHashMap<String, String>> mapArray = null;
        XSSFRow row = null;
        XSSFSheet sheet = null;
        int sheetRows = 0;
        int rowCols = 0;
        LinkedHashMap<String, String> rowMap = null;
        System.out.println("sWorkbook:" + sWorkBook);
        try {
            FileInputStream file = new FileInputStream(new File(sWorkBook));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheet(sSheetName);
            sheetRows = sheet.getPhysicalNumberOfRows();
            mapArray = new ArrayList<LinkedHashMap<String, String>>(sheetRows - 1);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                colNames.add("" + row.getCell(i).getStringCellValue());
            }
            rowCols = colNames.size();
            for (int i = 1; i < sheetRows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    if (row.getCell(1) != null && row.getCell(1).getStringCellValue().equalsIgnoreCase("Y")) {
                        rowMap = new LinkedHashMap<String, String>(rowCols);
                        for (int c = 0; c < rowCols; c++) {
                            String key = colNames.get(c);
                            String value = "" + getCellValue(row.getCell(c));
                            rowMap.put(key, value);
                        }
                        mapArray.add(rowMap);
                        System.out.println(rowMap);
                    }
                }
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapArray;
    }

    public static void updateExcel(String sSheetname, int rowNo, String columnName, String value) throws Exception {
        FileOutputStream outFile = null;
        FileInputStream fis = null;
        String filePath = System.getenv("testdataImportLC");

        try {
            fis = new FileInputStream(new File(filePath));
            final HSSFWorkbook workbook = new HSSFWorkbook(fis);
            final HSSFSheet sheet = workbook.getSheet(sSheetname);

            int endRow = sheet.getLastRowNum();
            int startRow = sheet.getFirstRowNum();

            HSSFRow headerRow = sheet.getRow(startRow);
            Iterator<Cell> itr = headerRow.iterator();
            while (itr.hasNext()) {
                Cell cell = itr.next();
                System.out.println(cell.getStringCellValue());
                if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                    System.out.println("Updating excel");
                    int colNo = cell.getColumnIndex();
                    System.out.println("Row-" + rowNo + "cell num - " + colNo);
                    System.out.println(value);
                }
            }
            fis.close();
            outFile = new FileOutputStream(new File(filePath));
            workbook.write(outFile);
            outFile.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    static String headers="Scenario,Iterate,UserNo,FirstName,LastName,UserID,Pin,Password,ActivationCode1,ActivationCode2,Registered";
    static String sheetName = "UserCreation";
    ArrayList<String> columnNames;
    String workBookName = "utdMaster.xls";
    String workBookPath = System.getProperty("user.dir")+"\\"+workBookName;
    String sSheetNames;

    FileInputStream fis;
    HSSFWorkbook xlsWorkbook;
    Sheet xlsSheet;
    Cell xlsCell;
    org.apache.poi.ss.usermodel.Row xlsRow;

    String scenario="UserCreation";
    String pin="1221";
    String password = "Password1";
    String iterate="Y";
    String registered = "N";
    int counter =0;
    int userCount=10;

    public void createXLS(){
        createExcel(headers);
    }
    public void createExcel(String headers){
        try{
            File excelFile = new File(workBookPath);
            if(!excelFile.exists()){
                xlsWorkbook = new HSSFWorkbook();
                xlsSheet = xlsWorkbook.createSheet(sheetName);
                xlsRow = xlsSheet.createRow(0);
                addHeadersToExcelSheet(headers.split(","));
                saveExcelWithChanges(workBookPath,xlsWorkbook);
            }
            addNewUserRecordsToExcel();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void addHeadersToExcelSheet(String[] arrHdrs){
        for(int cellNum = 0;cellNum <arrHdrs.length;cellNum++)
            updateCellData(0,cellNum,arrHdrs[cellNum],true);
    }
    public void addNewUserRecordsToExcel(){
        String firstName,lastName,userID;
        String recordNumFormatted;
        int finalCounter = counter;

        try{
            fis=new FileInputStream(workBookPath);
            xlsWorkbook = new HSSFWorkbook(fis);
            xlsSheet = xlsWorkbook.getSheet(sheetName);
            for(int recordNum = counter+1;recordNum<=counter+userCount;recordNum++){
                recordNumFormatted = String.format("%05d",recordNum);
                firstName = "Ftest"+recordNumFormatted;
                lastName = "Ltest"+recordNumFormatted;
                userID = "Auc"+recordNumFormatted;
                addSingleUserRecordToExcel(recordNum,recordNum+"",firstName,lastName,userID);
                finalCounter++;
            }
            counter = finalCounter;
            saveExcelWithChanges(workBookPath,xlsWorkbook);
        }
        catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }
    private void addSingleUserRecordToExcel(int rowNum,String userNo,String firstName,String lastName,String userID) {
        updateCellDataByColName(rowNum, "Scenario", scenario, false);
        updateCellDataByColName(rowNum, "Iterate", iterate, false);
        updateCellDataByColName(rowNum, "UserNo", userNo, false);
        updateCellDataByColName(rowNum, "FirstName", firstName, false);
        updateCellDataByColName(rowNum, "LastName", lastName, false);
        updateCellDataByColName(rowNum, "UserID", userID, false);
        updateCellDataByColName(rowNum, "Pin", pin, false);
        updateCellDataByColName(rowNum, "Password", password, false);
        updateCellDataByColName(rowNum, "Registered", registered, false);
    }
    public void updateCellDataAndCloseExcel(int rowNum,int colNum,String data,boolean makeBold) {
        updateCellData(rowNum, colNum, data, makeBold);
        saveExcelWithChanges(workBookPath,xlsWorkbook);
    }
    public void updateCellDataByColNameAndCloseExcel(int rowNum,String colName,String data,boolean makeBold) {
        updateCellDataByColName(rowNum, colName, data, makeBold);
        saveExcelWithChanges(workBookPath, xlsWorkbook);
    }
    private void updateCellData(int rowNum,int colNum,String data,boolean makeBold) {
        try {
            xlsRow = xlsSheet.getRow(rowNum);
            if (xlsRow == null)
                xlsRow = xlsSheet.createRow(rowNum);
            xlsCell = xlsRow.getCell(colNum);
            if (xlsCell == null)
                xlsCell = xlsRow.createCell(colNum);
            xlsCell.setCellValue(data);
            if (makeBold)
                makeCellBold(xlsCell);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private void updateCellDataByColName(int rowNum,String colName,String data,boolean makeBold) {
        try {
            readColumnHeadingsToList(xlsSheet);
            int colNum = columnNames.indexOf(colName);
            updateCellData(rowNum, colNum, data, makeBold);
        }
        catch (Exception e){
            System.out.println(e.getMessage());

        }
    }
    public void makeCellBold(Cell cell){
        HSSFFont font = xlsWorkbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName(HSSFFont.FONT_ARIAL);
        HSSFCellStyle style = xlsWorkbook.createCellStyle();
        style.setFont(font);
        cell.setCellStyle(style);
    }
    private void readColumnHeadingsToList(Sheet sheetName){
        try{
            columnNames = new ArrayList<String>();
            org.apache.poi.ss.usermodel.Row row=xlsSheet.getRow(0);
            Iterator<Cell> cells = row.cellIterator();
            while(cells.hasNext()){
                columnNames.add(cells.next().getStringCellValue());
            }
        }
        catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }
    private void saveExcelWithChanges(String excelPath,HSSFWorkbook workbook){
        try{
            FileOutputStream fos= new FileOutputStream(excelPath);
            workbook.write(fos);
            fos.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private void addSingleRowToExcel(int rowNum, HashMap<String,String> colNameAndValueMap){
        for(Map.Entry<String,String> cellData:colNameAndValueMap.entrySet())
            updateCellDataByColName(rowNum,cellData.getKey(),cellData.getValue(),true);
    }
    public static void updateExcelXLSX(String fileName,String sheetName,String testName,String columnName,String value) throws Exception{
        FileOutputStream outFile=null;
        FileInputStream fis = null;
        XSSFRow row=null;
        XSSFSheet sheet = null;
        int sheetRows=0;
        int rowCols = 0;
        int rowNo=0;
        try{
            System.out.println(fileName);
            fis=new FileInputStream(new File(fileName));
            final XSSFWorkbook workbook=new XSSFWorkbook(fis);
            sheet=workbook.getSheet(sheetName);
            int endRow=sheet.getLastRowNum();
            int startRow = sheet.getFirstRowNum();
            sheetRows=sheet.getPhysicalNumberOfRows();
            row=sheet.getRow(0);
            for(int i=1;i<sheetRows;i++){
                row=sheet.getRow(i);
                if(row!=null){
                    if(row.getCell(0).getStringCellValue().equalsIgnoreCase(testName) && row.getCell(1).getStringCellValue().equalsIgnoreCase("Y")) {
                        rowNo = i;
                        XSSFRow headerRow = sheet.getRow(startRow);
                        Iterator<Cell> itr = headerRow.iterator();
                        while (itr.hasNext()) {
                            Cell cell = itr.next();
                            System.out.println(cell.getStringCellValue());
                            if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                                System.out.println("Updating Excel..");
                                int colNo = cell.getColumnIndex();
                                System.out.println("Row - " + rowNo + "cell num-" + colNo);
                                System.out.println(value);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            fis.close();
            outFile = new FileOutputStream(new File(fileName));
            workbook.write(outFile);
            outFile.close();
        }
        catch(final FileNotFoundException e){
            e.printStackTrace();
            throw e;
        }
        catch (final IOException e){
            e.printStackTrace();
            throw e;
        }
    }
    public static void updateExcelXLSXForAllEvents(String fileName,String sheetName,String testName,String columnName,String value) throws Exception{
        FileOutputStream outFile=null;
        FileInputStream fis=null;
        XSSFRow row = null;
        XSSFSheet sheet=null;
        int sheetRows=0;
        int rowCols=0;
        int rowNo=0;
        try{
            System.out.println(fileName);
            fis=new FileInputStream(new File(fileName));
            final XSSFWorkbook workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);

            int endRow=sheet.getLastRowNum();
            int startRow=sheet.getFirstRowNum();
            sheetRows=sheet.getPhysicalNumberOfRows();
            row=sheet.getRow(0);
            for(int i=0;i<sheetRows;i++){
                row=sheet.getRow(i);
                if(row!=null){
                    String TestCaseName=row.getCell(0).getStringCellValue();
                    String[] ActualTestName=TestCaseName.split("\\$");

                    if(ActualTestName[0].equalsIgnoreCase(testName)) {
                        rowNo = i;
                        XSSFRow headerRow = sheet.getRow(startRow);
                        Iterator<Cell> itr = headerRow.iterator();
                        while (itr.hasNext()) {
                            Cell cell = itr.next();
                            System.out.println(cell.getStringCellValue());
                            if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                                System.out.println("Updating Excel..");
                                int colNo = cell.getColumnIndex();
                                System.out.println("Row - " + rowNo + "cell num-" + colNo);
                                System.out.println(value);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            fis.close();
            outFile = new FileOutputStream(new File(fileName));
            workbook.write(outFile);
            outFile.close();
        }
        catch(final FileNotFoundException e){
            e.printStackTrace();
            throw e;
        }
        catch (final IOException e){
            e.printStackTrace();
            throw e;
        }
    }
    public static void updateExcel(String fileName,String sheetName,String testName,String columnName,String value) throws Exception{
        FileOutputStream outFile=null;
        FileInputStream fis=null;
        HSSFRow row = null;
        HSSFSheet sheet=null;
        int sheetRows=0;
        int rowCols=0;
        int rowNo=0;
        try{
            System.out.println(fileName);
            fis=new FileInputStream(new File(fileName));
            final HSSFWorkbook workbook = new HSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);

            int endRow=sheet.getLastRowNum();
            int startRow=sheet.getFirstRowNum();
            sheetRows=sheet.getPhysicalNumberOfRows();
            row=sheet.getRow(0);
            for(int i=0;i<sheetRows;i++){
                row=sheet.getRow(i);
                if(row.getCell(0).getStringCellValue().equalsIgnoreCase(testName) && row.getCell(1).getStringCellValue().equalsIgnoreCase("Y")){
                    rowNo = i;
                    HSSFRow headerRow = sheet.getRow(startRow);Iterator<Cell> itr = headerRow.iterator();
                    while (itr.hasNext()) {
                        Cell cell = itr.next();
                        System.out.println(cell.getStringCellValue());
                        if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                            System.out.println("Updating Excel..");
                            int colNo = cell.getColumnIndex();
                            System.out.println("Row - " + rowNo + "cell num-" + colNo);
                                System.out.println(value);
                                break;
                            }
                        }
                        break;
                    }
                }

            fis.close();
            outFile = new FileOutputStream(new File(fileName));
            workbook.write(outFile);
            outFile.close();
        }
        catch(final FileNotFoundException e){
            e.printStackTrace();
            throw e;
        }
        catch (final IOException e){
            e.printStackTrace();
            throw e;
        }
    }
    public static void main(String[] args) throws Exception{
        getDataKeyValuePairColData();
    }
    public static Map<String,String> getDataKeyValuePairColData() throws Exception{
        String path=System.getProperty("user.dir")+"/target/downloadFiles";
        String fileName=null;
        File directory=new File(path);
        File[] files=directory.listFiles();
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for(File file:files){
            if(file.getName().endsWith("xlsx")){
                fileName=file.getName();
                break;
            }
        }
        String filePath=path+File.separator+fileName;
        ArrayList rowList;
        ArrayList al=new ArrayList();
        ArrayList al2=new ArrayList();
        HashMap<String,String> map=new HashMap<>();
        HashMap<String,String> map2=new HashMap<>();
        File myFile = new File(filePath);
        FileInputStream fis=new FileInputStream(myFile);

        XSSFWorkbook myWorkBook=new XSSFWorkbook(fis);
        Sheet sheet=myWorkBook.getSheetAt(0);
        Iterator< Row> itr=sheet.iterator();
        while(itr.hasNext()) {
            rowList = new ArrayList();
            Row row = itr.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        al.add(cell.getNumericCellValue());
                        rowList.add(cell.getNumericCellValue());
                        break;
                    case Cell.CELL_TYPE_STRING:
                        al.add(cell.getStringCellValue());
                        rowList.add(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        al.add(cell.getBooleanCellValue());
                        rowList.add(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        al.add("blank");
                        rowList.add("blank");
                        break;
                }
            }
            System.out.println("-");
            if (!map.containsKey(rowList.get(0)))
                map.put((String) rowList.get(0), (String) rowList.get(1));
        }
        System.out.println(al);
        System.out.println(map);
        return map;
    }
    public List<ArrayList<String>> readExcelDataAsListPerRow(String datasheetPath) throws IOException {
        String excelFile = System.getProperty("user.dir") + datasheetPath;
        List<ArrayList<String>> depts = new ArrayList<ArrayList<String>>();
        FileInputStream excelFileToRead = new FileInputStream(new File(excelFile));
        XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;
        int maxDataCount = 0;
        Iterator<Row> rows = sheet.rowIterator();
        ArrayList<String> innerArrayList;
        while (rows.hasNext()) {
            row = (XSSFRow) rows.next();
            if (row.getRowNum() ==0) {
                maxDataCount = row.getLastCellNum();
                continue;
            }
            if (this.isRowEmpty(row, maxDataCount)) {
                break;
            }
            innerArrayList = new ArrayList<String>();
            for (int cn = 0; cn < maxDataCount; cn++) {
                cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        innerArrayList.add(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        innerArrayList.add(String.valueOf(cell.getNumericCellValue()));
                        break;
                    default:
                        innerArrayList.add(cell.getStringCellValue());
                        break;
                }

            }
            depts.add(innerArrayList);
        }
        return depts;
    }
    public boolean isRowEmpty(Row row,int lastCellNo){
        for(int c=row.getFirstCellNum();c<lastCellNo;c++){
            Cell cell = row.getCell(c,Row.CREATE_NULL_AS_BLANK);
            if(cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK){
                return false;
            }
        }
        return true;
    }
}
