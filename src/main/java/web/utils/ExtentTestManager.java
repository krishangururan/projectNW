package web.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.thoughtworks.gauge.Gauge;

import java.util.List;
import java.util.Map;

public class ExtentTestManager {

    public static ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();
    ExtentReports extent = ExtentManager.getReporter();


    public static ExtentTest getTest() {
        return testReport.get();
    }

    public static synchronized boolean addScreenShotOnFailureForGauge(ExtentTest extentTest){
        Driver.stepStatus.set(false);
        String screenshot = ExtentManager.captureScreenshot();
        try {
            extentTest.fail("<font color=" + "orange>" + "Failure Screenshot: " + CommonUtils.getScenarioStoreVal("screenshotName")
                    + "</font>" + "<<br>", MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static void reportFail(ExtentTest extentTest){
        Driver.stepStatus.set(false);
        String failureLogg="SCENARIO FAILED";
        Markup m = MarkupHelper.createLabel(failureLogg,ExtentColor.RED);
        extentTest.log(Status.FAIL,m);
    }
    public static void reportPass(ExtentTest extentTest){
        String failureLogg="SCENARIO PASSED";
        Markup m = MarkupHelper.createLabel(failureLogg,ExtentColor.GREEN);
        extentTest.log(Status.PASS,m);
    }
    public static void reportInfo(ExtentTest extentTest,String information) {
        extentTest.log(Status.INFO,information);
    }
    public static void reportStepFail(ExtentTest extentTest,String information){
        Driver.stepStatus.set(false);
        String message = "<font color="+"orange>"+"FAILED STEP: "+"</font>"+"<font color="+"red>"+information+"</font>";
        extentTest.log(Status.FAIL,message);
    }
    public static void reportSkip(ExtentTest extentTest){
        String failureLogg = "SCENARIO SKIPPED";
        Markup m = MarkupHelper.createLabel(failureLogg,ExtentColor.ORANGE);
        extentTest.log(Status.SKIP,m);
    }
    public static boolean addScreenshots(){
        ExtentManager.captureScreenshot();
        try{
            Driver.getCurrentExtent().info(("<font color="+"orange>"+"Screenshot"+"</font>"+"<br>"),
                    MediaEntityBuilder.createScreenCaptureFromPath(CommonUtils.getSpecStoreVal("screenshotName")).build());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static boolean addScreenShotsOnStep(ExtentTest extentTest){
        ExtentManager.captureScreenshot();
        try{
            Driver.getCurrentExtent().info(("<font color="+"green>"+"Screenshot"+"</font>"+"<br>"),
                    MediaEntityBuilder.createScreenCaptureFromPath(CommonUtils.getSpecStoreVal("screenshotName")).build());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static void reportStackTrace(String message){
        Driver.getCurrentExtent().log(Status.WARNING,"<font color="+"orange>"+"Stack Trace: "+"</font>"+"<br>"+message);
    }

    public static void reportException(String message){
        Driver.getCurrentExtent().log(Status.WARNING,"<font color="+"orange>"+"Exception: "+"</font>"+"<br>"+message);
    }
    public static void reportFail(String message){
        Driver.stepStatus.set(false);
        Driver.getCurrentExtent().log(Status.FAIL,MarkupHelper.createLabel(message,ExtentColor.RED));
    }
    public static void reportFailWithScreenshot(String message){
        Driver.stepStatus.set(false);
        Driver.getCurrentExtent().log(Status.FAIL,MarkupHelper.createLabel(message,ExtentColor.RED));
        addScreenshots();
    }
    public static void reportFail(String message,String captureScreenShotState){
        Driver.stepStatus.set(false);
        Driver.getCurrentExtent().log(Status.FAIL,MarkupHelper.createLabel(message,ExtentColor.RED));
        if(captureScreenShotState.equalsIgnoreCase("Yes")){
            addScreenshots();
        }
    }
    public static void reportListData(List<String> list){
        Driver.getCurrentExtent().info(MarkupHelper.createUnorderedList(list).getMarkup());
    }
    public static void reportMapData(Map map){
        Driver.getCurrentExtent().info(MarkupHelper.createUnorderedList(map).getMarkup());
    }
    public static void reportPass(String message){
        Driver.getCurrentExtent().log(Status.PASS,MarkupHelper.createLabel(message,ExtentColor.GREEN));
    }
    public static void reportInfo(String message){
        Driver.getCurrentExtent().log(Status.INFO,MarkupHelper.createLabel(message,ExtentColor.BLUE));
    }
    public static void reportHighlightedInfo(String message){
        Driver.getCurrentExtent().log(Status.INFO,MarkupHelper.createLabel(message,ExtentColor.INDIGO));
    }
    public static void reportSkip(String message){
        Driver.getCurrentExtent().log(Status.SKIP,MarkupHelper.createLabel(message,ExtentColor.ORANGE));
    }
    public static void reportAssert(String expected, String actual, String msg){
        if(expected.equalsIgnoreCase(actual)){
            reportPass(msg+" matched:: Expected: "+expected +" Actual: "+actual);
        }
        else{
            reportFail(msg+" mismatched:: Expected: "+expected +" Actual: "+actual);
            Gauge.writeMessage("Expected: "+expected +" Actual: "+actual+"- Values Mismatch");
        }
    }
    public static void reportAssert(String expected, String actual){
        if(expected.equalsIgnoreCase(actual)){
            reportPass("Expected: "+expected +" Actual: "+actual +" - Values Match");
        }
        else{
            reportFail("Expected: "+expected +" Actual: "+actual+"- Values mismatch");
            Gauge.writeMessage("Expected: "+expected +" Actual: "+actual+"- Values Mismatch");
        }
    }
    public static void reportAssert(int actual, int expected){
        if(expected ==actual) {
        }
        else{
            reportFail("Expected: "+expected +" Actual: "+actual+"- Values Mismatch");
            Gauge.writeMessage("Expected: "+expected +" Actual: "+actual+"- Values Mismatch");
        }
    }
    public static void reportAssertTrue(boolean condition, String message){
        if(!condition) {
            reportFail(message+" "+"Fail");
            Gauge.writeMessage(message+" "+"Fail");
        }
        else{
            reportPass(message);
        }
    }
    public static void logExpectedAndActualResult(Object expected,Object actual){
        reportInfo("Expected: "+expected.toString()+"<br>"+"Actual:"+actual.toString());
    }
}
