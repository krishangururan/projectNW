package web.utils;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

public class ExtentManager {

    static ExtentReports extent;
    public static synchronized ExtentReports getReporter() {
        Date d = new Date();
        String strDate = "_"+d.toString().replace(":","_").replace(" ","_");
        String reportName="index.html";
        if(extent==null){
            String path = System.getProperty("user.dir")+"/target/extent-reports/"+reportName;
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(path);
            String branchName="";
            try {
                if (!System.getenv("BRANCH_NAME").isEmpty() || System.getenv("BRANCH_NAME") != null)
                    branchName = " | " + System.getenv("BRANCH_NAME");
            }
            catch (Exception ignored){
            }
            htmlReporter.config().setDocumentTitle(System.getenv("Environment")+branchName);
            htmlReporter.config().setEncoding("utf-8");
            htmlReporter.config().setReportName(System.getenv("Environment")+branchName);
            htmlReporter.config().setTheme(Theme.DARK);
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            try {
                if (System.getenv("ExtentReportLevelType").equalsIgnoreCase("suite"))
                    extent.setAnalysisStrategy(AnalysisStrategy.BDD);
                else
                    extent.setAnalysisStrategy(AnalysisStrategy.TEST);
            }
            catch(Exception e){
                extent.setAnalysisStrategy(AnalysisStrategy.BDD);
            }
        }
        if(CommonUtils.getSuiteStoreVal(Thread.currentThread().getName()) == null){
            String envInfo = System.getenv("Environment")+"_"+strDate;
            extent.setSystemInfo("[Thread ID - "+ Thread.currentThread().getId()+"]"+" Machine OS", System.getProperty("os.name"));
            extent.setSystemInfo("[Thread ID - "+ Thread.currentThread().getId()+"]"+" Browser", System.getenv("Browser"));
            extent.setSystemInfo("[Thread ID - "+ Thread.currentThread().getId()+"]"+" Environment Execution Info",envInfo);
            CommonUtils.setSuiteStoreVal(Thread.currentThread().getName(),Thread.currentThread().getName());
        }
        return extent;
    }
    public static synchronized String captureScreenshot(){
        File scrFile  = ((TakesScreenshot)Driver.getDriver()).getScreenshotAs(OutputType.FILE);

        Date d = new Date();
        String separator = "\\";
        String[] specNameArr = Driver.getEcontext().getCurrentSpecification().getFileName().replace(Pattern.quote(separator),"\\\\").split("\\\\");
        String specNameArr2 = specNameArr[specNameArr.length - 1];
        String screenshotName = specNameArr2 + "_"+ Driver.getEcontext().getCurrentScenario().getName()+"_"+d.toString().replace(":","_").replace(" ","_")+".jpg";
        CommonUtils.setScenarioStoreVal("screenshotName",screenshotName);
        try {
            FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir") + "/target/extent-reports/" + screenshotName));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        CommonUtils.pause(1000);
        return screenshotName;
    }
}

