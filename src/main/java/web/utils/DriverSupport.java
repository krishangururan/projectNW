package web.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.gherkin.model.Feature;
import com.aventstack.extentreports.gherkin.model.Scenario;
import com.thoughtworks.gauge.*;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public class DriverSupport {
     public static FileInputStream fis;
     public static Properties prop;
     public static ThreadLocal<WebDriver> drive = new ThreadLocal<>();
     public static Date d;
     public static int resultFlag,int_Pass,int_Fail;
     public static int iStepRowCounter=1;
     public static int specScenarioCount,specScenarioFail,specScenarioPass;
     public static Date fromDate;
     public static String scenarioName,stepName;
     public static List<String> specNameList = new ArrayList<>();
     public static List<String> tagList;
     public static ThreadLocal<Boolean> stepStatus = new ThreadLocal<>();
     public static String specificationName;
     public static ThreadLocal<ExecutionContext> contextThreadLocal=new ThreadLocal<>();
     public static ThreadLocal<ExecutionContext> stepContextThreadLocal=new ThreadLocal<>();
     public static String Environment;
     public static String url;
     public static ThreadLocal<ExtentTest> testReport=new ThreadLocal<ExtentTest>();
     public static ThreadLocal<ExtentTest> scenrioExtent=new ThreadLocal<ExtentTest>();
     public static ThreadLocal<ExtentTest> stepExtent=new ThreadLocal<ExtentTest>();
     public static List<String> state = new ArrayList<>();
     public static boolean folderExists = false;
     public static String folderId = null;
     public static List<String> executionIdList=new ArrayList<>();
     public static ThreadLocal<String> uiCheck = new ThreadLocal<>();
     static List<Integer> totalScenariosCount=new ArrayList<>();
     static List<Integer> passScenariosCount=new ArrayList<>();
     static List<Integer> failScenariosCount=new ArrayList<>();
     static Map extentTestMap = new HashMap();
     ExtentReports extent = ExtentManager.getReporter();
     ExtentTest test;
     ExtentTest scenarioTest;
     ExtentTest stepTest;
     private WebDriver driver;
     private List<String> testList = new ArrayList<>();

     public static synchronized ExtentTest getTest()
     {
        return scenrioExtent.get();
     }
     public static ExtentTest getCurrentExtent(){
         return stepExtent.get();
     }
     public static void afterSpecActions(){
         totalScenariosCount.add(specScenarioPass+specScenarioFail);
         passScenariosCount.add(specScenarioPass);
         failScenariosCount.add(specScenarioFail);
     }
     public static ExecutionContext getEcontext(){
         return contextThreadLocal.get();
     }
     public static ExecutionContext getStepEcontext(){
         return stepContextThreadLocal.get();
     }
     public static synchronized String getStackTrace(){
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
         String stringStackTrace = "<font color="+"orange>"+"Stack Trace: "+"<br>"+"</font>";
         for(StackTraceElement st:stackTrace){
             stringStackTrace = stringStackTrace + st + "<br>";
         }
         return stringStackTrace;
     }
     public static synchronized void kill_Browser_Instance(){
         String browser=System.getenv("BROWSER");
         try{
             if(browser.equalsIgnoreCase("IE")){
                 Process process = Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
             }
             if(browser.equalsIgnoreCase("CHROME")){
                 Process process = Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
             }
         }
         catch (IOException e){
             e.printStackTrace();
         }
     }

     public static void customPropertyInit(){
         try{
             prop = new Properties();
             fis=new FileInputStream(System.getProperty("user.dir")+"/env/default/custom.properties");
             prop.load(fis);
            }
         catch (Exception e)
         {
             System.out.println(e.getMessage());
         }
     }
     public static void setRuntimeCustomProperty(String key,String value) throws IOException{
         if(value==null)
             value="";
         prop.setProperty(key,value);
        FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir")+"/env/default/custom.properties");
        prop.store(fileOut,"Storing the data in custom file");
        fileOut.close();
     }
     public static WebDriver getDriver(){
         if(uiCheck.get()==null){
             if(drive.get()==null){
                 new Driver().browserInitialisation();
             }
             return drive.get();
         }
         else
             return null;
     }
     public static synchronized void openURL(String url){
         getDriver().get(url);
         getDriver().manage().deleteAllCookies();
     }
     public static String getSpecName(ExecutionContext context){
         String[] specNameArr = context.getCurrentSpecification().getFileName().replace(Pattern.quote("\\"),"\\\\").split("\\\\");
         return specNameArr[specNameArr.length-1];
     }
     public static synchronized int successRateLog(){
         int testcasetotal = int_Pass + int_Fail;
         int SuccessRate = (int_Pass*100/testcasetotal);
         String FailRate = Integer.toString(100-SuccessRate);
         int Passwidth=(300*SuccessRate)/100;
         String Failwidth = Integer.toString(300-Passwidth);
         String tempSuiteThreasholdPassPercentage = System.getenv("suiteThreasholdPassPercentage");
         String tempFunctionalSuiteThreasholdPassPercentage = System.getenv("functionalSuiteThreasholdPassPercentage");
         String tempRegressionSuiteThreasholdPassPercentage = System.getenv("regressionSuiteThreasholdPassPercentage");

         if(SuccessRate >= Integer.parseInt(tempSuiteThreasholdPassPercentage)){
             System.out.println("--------------------ShakeDown Passed Threshold Reached");
         }
         else{
             System.out.println("--------------------ShakeDown Failed Threshold Reached");
         }
         if(SuccessRate >= Integer.parseInt(tempFunctionalSuiteThreasholdPassPercentage)){
             System.out.println("--------------------Functional Passed Threshold Reached");
         }
         else{
             System.out.println("--------------------Functional Failed Threshold Reached");
         }
         if(SuccessRate >= Integer.parseInt(tempRegressionSuiteThreasholdPassPercentage)){
             System.out.println("--------------------Regression Passed Threshold Reached");
         }
         else{
             System.out.println("--------------------Regression Failed Threshold Reached");
         }
        return SuccessRate;
     }
     public synchronized ExtentTest startTest(String testName){
         try{
             if(System.getenv("ExtentReportLevelType").equalsIgnoreCase("suite")){
                 scenarioTest = testReport.get().createNode(Scenario.class,testName);
                 scenrioExtent.set(scenarioTest);
                 testList.add(testName);
                 return scenarioTest;
             }
             else if(System.getenv("ExtentReportLevelType").equalsIgnoreCase("test")){
                 test = extent.createTest(Scenario.class,testName);
                 scenrioExtent.set(test);
                 testList.add(testName);
                 return test;
             }
             else{
                 System.out.println("ExtentReportLevelType is not defined either suite or test in pom file");
                 return null;
             }
         }
         catch (Exception e)
         {
             scenarioTest = testReport.get().createNode(testName);
             scenrioExtent.set(scenarioTest);
             testList.add(testName);
             return scenarioTest;
         }
     }
    public synchronized ExtentTest createNode(ExtentTest test,ExecutionContext context){
             return test.createNode(context.getCurrentStep().getText());
    }
    public synchronized void flushReport(){
         extent.flush();
    }
    public void beforeSpecActions(ExecutionContext context){
         String specName = context.getCurrentSpecification().getName();
         try {
             if (System.getenv("ExtentReportLevelType").equalsIgnoreCase("Suite")) {
                 test = extent.createTest(Feature.class, specName + "(" + getSpecName(context) + ")");
                 testReport.set(test);
             }
         }
         catch (Exception e)
         {
             test=extent.createTest(Feature.class,specName+"("+getSpecName(context)+")");
             testReport.set(test);
         }
         specificationName = specName;
         specScenarioCount =0;
         specScenarioFail=0;
         specScenarioPass=0;
         specName = context.getCurrentSpecification().getName().trim();
         specName = specName.replaceAll(" ","_");
         specNameList.add(specName);
    }
    public void beforeScenarioActions(ExecutionContext context)
    {
        stepStatus.set(true);
        if(System.getenv("ExtentReportLevelType").equalsIgnoreCase("test")){
            startTest(context.getCurrentScenario().getName()+"("+getSpecName(context)+")");
        }
        else{
            startTest(context.getCurrentScenario().getName());
        }
        try{
            for(String tag:context.getAllTags()){
                if(System.getenv("tags").contains(tag)){
                    getTest().assignCategory(tag);
                }
            }
        }
        catch (Exception e){
            System.out.println("Exception while fetching tags from scenario: "+e.getMessage());
        }
        getTest().assignCategory("TOTAL"+"&nbsp;"+"SCENARIO"+"&nbsp;"+"RESULT");
        String specFeatureName = context.getCurrentSpecification().getName().trim();
        specFeatureName = specFeatureName.replaceAll(" ","&nbsp;");
        tagList = context.getCurrentScenario().getTags();
        List<String> scenarioTagList = context.getCurrentScenario().getTags();
        getTest().assignAuthor(specFeatureName);
        for(String tag:scenarioTagList){
            if(tag.startsWith("CMST") || tag.startsWith("SDVAMJ")){
                try{
                    String status=JiraApis.getJiraDefectStatus(tag);
                    String reportTag = tag + "(Jira Status: "+status+")";
                    scenrioExtent.get().assignCategory(reportTag);
                }
                catch (Exception e)
                {
                    ExtentTestManager.reportException("JIRA EXCEPTION:"+e.getMessage());
                }
            }
        }
        contextThreadLocal.set(context);
        if(context!=null){
            scenarioName = context.getCurrentScenario().getName().trim();
            scenarioName = scenarioName.replaceAll(" ","_");
        }
        resultFlag=1;
        iStepRowCounter=1;
        browserInitialisation();
    }
    public WebDriver browserInitialisation(){
        WebDriverListener eventListener = new WebEventListener() ;
        try{
            if(drive.get()==null){
                driver = DriverFactory.getDriver();
                WebDriver decorated = new EventFiringDecorator(eventListener).decorate(driver);
                driver=decorated;
                drive.set(driver);
                drive.get().manage().window().maximize();
                drive.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME"))));
                drive.get().manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
                drive.get().manage().deleteAllCookies();
            }
            else{
                drive.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME"))));
                drive.get().manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
            }
            return drive.get();
        }
        catch (Exception e){
            drive.set(null);
            driver = DriverFactory.getDriver();
            WebDriver decorated = new EventFiringDecorator(eventListener).decorate(driver);
            driver=decorated;
            drive.set(driver);
            drive.get().manage().window().maximize();
            drive.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME"))));
            drive.get().manage().deleteAllCookies();
            return drive.get();
        }
    }
    public void beforeStepActions(ExecutionContext context){
         stepName = context.getCurrentStep().getText().trim();
         stepTest = scenrioExtent.get().createNode("* "+stepName);
         stepExtent.set(stepTest);
         stepContextThreadLocal.set(context);
    }

    public void afterScenarioActions(ExecutionContext context){
         boolean status=context.getCurrentScenario().getIsFailing();
         if(status | !stepStatus.get()){
             if(status){
                 ExtentTestManager.reportFail(getCurrentExtent());
                 System.out.println("TEST SCENARIO FAILED");
                 state.add("Fail");
             }
             else{
                 state.add("Fail");
                 ExtentTestManager.reportFail(getCurrentExtent());
             }
             int_Fail = int_Fail +1;
             specScenarioFail = specScenarioFail +1;
         }
         else if(!status){
             ExtentTestManager.reportPass(getCurrentExtent());
             int_Pass = int_Pass+1;
             specScenarioPass=specScenarioPass+1;
             state.add("Pass");
         }
         if(System.getenv("UPDATE_JIRA_TEST_STATUS").equalsIgnoreCase("Yes")){
             String defectNum = "";
             for(String tag: context.getCurrentScenario().getTags()){
                 if(tag.startsWith("JIRA_TEST_ID")){
                     String projectId = CommonUtils.getSuiteStoreVal("PROJECT_ID");
                     String versionId = CommonUtils.getSuiteStoreVal("VERSION_ID");
                     String cycleId = CommonUtils.getSuiteStoreVal("CYCLE_ID");
                     String folderId = CommonUtils.getSuiteStoreVal("FOLDER_ID");
                     System.out.println(projectId);
                     System.out.println(versionId);
                     System.out.println(cycleId);
                     System.out.println(folderId);

                     String[] tagNameArr = tag.split("ID_");
                     String tagName = tagNameArr[1];
                     String testCaseId="";
                     try{
                         System.out.println("JIRA TEST ID:"+tagName.trim());
                         testCaseId = JiraApis.getTestIdFromJiraId(tagName.trim());
                         CommonUtils.pause(1000);
                     }
                     catch (Exception e)
                     {
                         System.out.println("JIRA EXCEPTION");
                         System.out.println(e.getMessage());
                     }
                     String executionId = "";
                     try {
                         executionId = JiraApis.generateExecutionIdForTestInFolder(testCaseId, projectId, versionId, cycleId, folderId);
                     }
                     catch (Exception e){
                         System.out.println("JIRA EXCEPTION");
                         System.out.println(e.getMessage());
                     }
                     executionIdList.add(executionId);
                     System.out.println(executionId);
                     if(defectNum.startsWith("CMST") || tag.startsWith("SDVAMJ"))
                         JiraApis.attachDefectWithTest(defectNum,executionId);
                     if(!status)
                         JiraApis.executeTestPass(executionId);
                     else
                         JiraApis.executeTestFail(executionId);
                 }
             }
         }
         System.out.println("SCENARIOS EXECUTION COMPLETE LIST: "+ testList.toString());
         System.out.println(extentTestMap.size());
    }
    public void afterSuiteActions(){
         //successRateLog();
         if(uiCheck.get()==null){
             try{
                 drive.get().quit();
                 drive.remove();
             }
             catch (Exception ignored){

             }
         }
         folderExists=false;
         flushReport();
    }
    public void beforeSuiteActions() throws IOException{
         try {
             if (!System.getenv("BRANCH_NAME").isEmpty() || (System.getenv("BRANCH_NAME") != null))
                 System.out.println("Configured branch name is: " + System.getenv("BRANCH_NAME"));
         }
         catch (Exception ignored) {
         }
         //BELOW ACTION IS TO CLEAN THE IMAGES FOLDER FROM REPORTS > HTML-REPORTS > SPECS > IMAGES FOLDER , TO SAVE DISK SPACE
         CommonUtils.deleteFolderAndFilesInDirectory(new File(System.getProperty("user.dir")+"/reports/html-report/specs/images"));
         customPropertyInit();
        File applicationLog = new File(System.getProperty("user.dir")+"/logs/application.html");
        File file = new File(System.getProperty("user.dir")+"/env/default/custom.properties");

        CommonUtils.setSuiteStoreVal("CYCLE_NAME",System.getenv("CYCLE_NAME"));
        CommonUtils.setSuiteStoreVal("FOLDER_NAME",System.getenv("FOLDER_NAME"));
        CommonUtils.setSuiteStoreVal("Browser",System.getenv("Browser"));
        CommonUtils.setSuiteStoreVal("Environment",System.getenv("Environment"));

        FileUtils.write(file,"", Charset.defaultCharset());
        FileUtils.write(applicationLog,"", Charset.defaultCharset());
        setRuntimeCustomProperty("Environment",CommonUtils.getSuiteStoreVal("Environment"));
        setRuntimeCustomProperty("CYCLE_NAME",CommonUtils.getSuiteStoreVal("CYCLE_NAME"));
        setRuntimeCustomProperty("FOLDER_NAME",CommonUtils.getSuiteStoreVal("FOLDER_NAME"));
        setRuntimeCustomProperty("Browser",CommonUtils.getSuiteStoreVal("Browser"));
        try{
            DataStoreFactory.getSpecDataStore().put("CYCLE_NAME",System.getenv("CYCLE_NAME"));
            DataStoreFactory.getSpecDataStore().put("FOLDER_NAME",System.getenv("FOLDER_NAME"));
            DataStoreFactory.getSpecDataStore().put("Browser",System.getenv("Browser"));

            CommonUtils.setSuiteStoreVal("CYCLE_NAME",System.getenv("CYCLE_NAME"));
            CommonUtils.setSuiteStoreVal("FOLDER_NAME",System.getenv("FOLDER_NAME"));
            CommonUtils.setSuiteStoreVal("Browser",System.getenv("Browser"));

            if(System.getenv("UPDATE_JIRA_TEST_STATUS").equalsIgnoreCase("Yes")) {
                System.out.println("UPDATE_JIRA_TEST_STATUS is set to Yes");
                if (System.getenv("CREATE_NEW_CYCLE-FOLDER_FOR_EACH_RUN").equalsIgnoreCase("Yes")) {
                    System.out.println("CREATE_NEW_CYCLE-FOLDER_FOR_EACH_RUN is set to Yes");
                    new JiraDriverSupport().update_jira_test_status();
                } else if (System.getenv("CREATE_NEW_FOLDER_FOR_EXISTING_CYCLE_EXECUTION_ON_EACH_RUN").equalsIgnoreCase("Yes")) {
                    System.out.println("CREATE_NEW_FOLDER_FOR_EXISTING_CYCLE_EXECUTION_ON_EACH_RUN is set to Yes");
                    new JiraDriverSupport().create_new_folder_for_existing_cycle_execution_on_each_run();
                } else if (System.getenv("CREATE_NEW_FOLDER_ALONG_WITH_THE_EXISTING_FOLDERS_FOR_A_CYCLE").equalsIgnoreCase("Yes")) {
                    System.out.println("CREATE_NEW_FOLDER_ALONG_WITH_THE_EXISTING_FOLDERS_FOR_A_CYCLE is set to Yes");
                    new JiraDriverSupport().create_new_folder_along_with_the_existing_folders_for_a_cycle();
                } else if (System.getenv("USE_EXISTING_CYCLE-FOLDER_FOR_EXECUTION").equalsIgnoreCase("Yes")) {
                    System.out.println("USE_EXISTING_CYCLE-FOLDER_FOR_EXECUTION is set to Yes");
                    new JiraDriverSupport().user_existing_cycle_folder_for_execution();
                } else {
                    new JiraDriverSupport().get_create_cycle_folder();
                }
            }
            else {
                System.out.println("UPDATE_JIRA_TEST_STATUS is set to No");
            }

        }
        catch (Exception e) {
            System.out.println("JIRA_EXCEPTION");
            System.out.println(e.getMessage());
        }
        Environment = System.getenv("Environment");
        url= Environment;

        if(file.exists()) {
            try {
                Path path = Paths.get(file.getPath());
                Files.deleteIfExists(path);
                System.out.println(file.getName() + " is deleted!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("File does not exists");
        }
    }
    public void afterStepActions(ExecutionContext context){
         try{
             if(context.getCurrentStep().getIsFailing() && resultFlag==1) {
                 ExtentTestManager.reportStepFail(getCurrentExtent(), context.getCurrentStep().getText());
                 ExtentTestManager.addScreenShotOnFailureForGauge(getCurrentExtent());
                 if (context.getCurrentStep().getStackTrace() != null & context.getCurrentStep().getStackTrace().length() > 0)
                     ExtentTestManager.reportStackTrace(context.getCurrentStep().getStackTrace());
                 if (CommonUtils.getScenarioStoreVal("exceptionMessage") != null & CommonUtils.getScenarioStoreVal("exceptionMessage").length() > 0)
                     ExtentTestManager.reportException(CommonUtils.getScenarioStoreVal("exceptionMessage"));
                 Thread.sleep(1000);
                 stepStatus.set(false);
             }
             else {
                 if(System.getenv("Add_Screenshot_Each_Step")!=null && System.getenv("Add_Screenshot_Each_Step").equalsIgnoreCase("Yes"))
                     ExtentTestManager.addScreenShotsOnStep(getCurrentExtent());
                 else if(System.getenv("Add_Screenshot_Each_Step")!=null && System.getenv("Add_Screenshot_Each_Step").equalsIgnoreCase("No"))
                     System.out.println("SCREENSHOT CAPTURE AT EACH STEP IS DISABLED IN TEAMCITY JOB");
                 else if(System.getenv("Add_Screenshot_Each_Step")==null && System.getenv("Add_Screenshot_To_Each_Step").equalsIgnoreCase("Yes"))
                     ExtentTestManager.addScreenShotsOnStep(getCurrentExtent());
                 else
                     System.out.println("SCREENSHOT SETTING IS NOT CONFIGURED. DEFAULT SCREEN CAPTURE ON FAILED TEST IS IMPLEMENTED");
             }
         }
         catch (Exception e){
             e.printStackTrace();
         }
    }
    private synchronized void takeScreenshot(String pictureName){
         try{
             String fileName = "images/"+pictureName+".png";
             File file = new File(System.getenv("gauge_reports_dir")+"/html-report/"+fileName);
             if(file.exists()){
                 file.delete();
             }
             File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
             FileUtils.copyFile(scrFile,file);
             Gauge.writeMessage("<a href='../"+fileName+"'><img src='../"+fileName+"' width='800' height='480'></a>");
         }
         catch (IOException e){
             e.printStackTrace();
         }
    }
    public void closeBrowser(){
         getDriver().quit();
         drive.remove();
    }
    public void beforeScenarioNonUIActions(ExecutionContext context){
            uiCheck.set("nonUI");
            stepStatus.set(true);
            if(System.getenv("ExtentReportLevelType").equalsIgnoreCase("test")){
                startTest(context.getCurrentScenario().getName()+"("+getSpecName(context)+")");
            }
            else{
                startTest(context.getCurrentScenario().getName());
            }
            try{
                for(String tag:context.getAllTags()){
                    if(System.getenv("tags").contains(tag)){
                        getTest().assignCategory(tag);
                    }
                }
            }
            catch (Exception e){
                System.out.println("Exception while fetching tags from scenario: "+e.getMessage());
            }
            getTest().assignCategory("TOTAL"+"&nbsp;"+"SCENARIO"+"&nbsp;"+"RESULT");
            String specFeatureName = context.getCurrentSpecification().getName().trim();
            specFeatureName = specFeatureName.replaceAll(" ","&nbsp;");
            tagList = context.getCurrentScenario().getTags();
            List<String> scenarioTagList = context.getCurrentScenario().getTags();
            getTest().assignAuthor(specFeatureName);
            for(String tag:scenarioTagList){
                if(tag.startsWith("CMST") || tag.startsWith("SDVAMJ")){
                    try{
                        String status=JiraApis.getJiraDefectStatus(tag);
                        String reportTag = tag + "(Jira Status: "+status+")";
                        scenrioExtent.get().assignCategory(reportTag);
                    }
                    catch (Exception e)
                    {
                        ExtentTestManager.reportException("JIRA EXCEPTION:"+e.getMessage());
                    }
                }
            }
            contextThreadLocal.set(context);
            if(context!=null){
                scenarioName = context.getCurrentScenario().getName().trim();
                scenarioName = scenarioName.replaceAll(" ","_");
            }
            resultFlag=1;
            iStepRowCounter=1;

        }
}
