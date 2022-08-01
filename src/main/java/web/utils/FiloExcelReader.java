package web.utils;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class FiloExcelReader {
    public static List<Map<String, String>> getColumnData(String sheetName, String colName, String relativeTestExcelPath) {
        Hashtable<String, String> table = null;
        List<Map<String, String>> dataList = null;
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(System.getProperty("user.dir") + relativeTestExcelPath);
            String query = "SELECT " + colName + " from " + sheetName;
            Recordset rs = connection.executeQuery(query);

            dataList = new ArrayList<>();
            while (rs.next()) {
                table = new Hashtable<String, String>();
                table.put(colName, rs.getField(colName));
                dataList.add(table);
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dataList;
    }
    public static List<Map<String, String>> getData(String sheetName, String relativeTestExcelPath) {
        Hashtable<String, String> table = null;
        List<Map<String, String>> dataList = null;
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(System.getProperty("user.dir") + relativeTestExcelPath);
            String query = "SELECT " + "*" + " from " + sheetName;
            Recordset rs = connection.executeQuery(query);
            List<String> list=rs.getFieldNames();

            dataList = new ArrayList<>();
            while (rs.next()) {
                table = new Hashtable<String, String>();
                for(String s:list){
                    table.put(s,rs.getField(s));
                }
                dataList.add(table);
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dataList;
    }
    public static String getColumnFirstRowData(String sheetName,String colName,String excelName){
        String sheetPath = "/src/test/"+excelName;
        String data=null;
        try{
            data=FiloExcelReader.getColumnData("UKNWB",colName,sheetPath).get(0).get(colName.toUpperCase());
            System.out.println("DATA: "+data);
            return data;
        }
        catch (Exception e){

        }
        return data;
    }
}


