package web.utils;


import com.google.common.util.concurrent.Uninterruptibles;
import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Table;
import com.thoughtworks.gauge.TableRow;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CommonUtils extends Driver {

    public static String loaderMessage = "//*[starts-with(@class,'sn-progress-loader')]";
    public static String applicationSectionLoader = "//*[contains(@id,'loader')]";

    public CommonUtils() {
        PageFactory.initElements(getDriver(), this);
    }

    public static void setScenarioStoreVal(String key, String val) {
        DataStoreFactory.getScenarioDataStore().put(key, val);
        if (!key.startsWith("exception")) {
            DataStoreFactory.getScenarioDataStore().put("sceanrioApprovalRequest2", val);
        }
    }

    public static String getSpecName() {
        waitForLoad(getDriver());
        return getEcontext().getCurrentSpecification().getName();
    }

    public static String getScenarioName() {
        waitForLoad(getDriver());
        return getEcontext().getCurrentScenario().getName();
    }

    public static void setSpecStoreVal(String key, String val) {
        try {
            DataStoreFactory.getSpecDataStore().put(key, val);
            if (!key.startsWith("exception")) {
                DataStoreFactory.getScenarioDataStore().put("sceanrioApprovalRequest2", val);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getScenarioStoreVal(String key) {
        try {
            return (String) DataStoreFactory.getScenarioDataStore().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSpecStoreVal(String key) {
        try {
            return (String) DataStoreFactory.getSpecDataStore().get(key);
        } catch (Exception e) {
            System.out.println("NOT ABLE TO GET SPEC DATA VALUE FOR KEY: " + key);
            return null;
        }
    }

    public static synchronized void setSuiteStoreVal(String key, String val) {
        DataStoreFactory.getSuiteDataStore().put(key, val);
    }

    public static String getSuiteStoreVal(String key) {
        try {
            return (String) DataStoreFactory.getSuiteDataStore().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentDate(String... format) {
        DateFormat df;
        if (format.length == 0)
            df = new SimpleDateFormat("yyyy-MM-dd");
        else
            df = new SimpleDateFormat(format[0]);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return (df.format(new Date()));
    }

    public static String getJsonPath(Response response, String key) {
        String json = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(json);
        return jsonPath.getString(key);
    }

    public static void setCustomImplicitWait(int seconds) {
        //Driver.getDriver().manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
    }

    public static void setDefaultImplicitWait() {
        getDriver().manage().timeouts().implicitlyWait(Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME")), TimeUnit.SECONDS);
    }

    public final static String getDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss_S");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }

    public final static String getDateTime(String format) {
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }

    public final static String addMonthsInDate(String format, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, month);
        return sdf.format(c.getTime());
    }

    public final static String getFutureDate(String format, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        return sdf.format(c.getTime());
    }

    public static void waitForElementInvisibility(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void waitForApplicationLoaderToDisappear() {
        try {
            waitForElementInvisibility(getDriver().findElement(By.xpath(loaderMessage)));
        } catch (Exception e) {
            System.out.println("loader not found on the page");
        }
    }

    public static void waitForApplicationComponentLoaderToDisappear() {
        try {
            waitForElementInvisibility(getDriver().findElement(By.xpath(applicationSectionLoader)));
        } catch (Exception e) {
            System.out.println("loader not found on the page");
        }
    }

    public static void screenShot(String pictureName) {
        try {
            String fileName = "images/" + pictureName + ".png";
            File file = new File(System.getenv("gauge_reports_dir") + "/html-report/specs/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, "png", file);
            Gauge.writeMessage("<a href='../" + fileName + "'><img src='../" + fileName + "' width='800' height='480'></a>");
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void captureScreen(String pictureName) {
        if (!System.getenv("Browser").equalsIgnoreCase("HTMLUNITDRIVER")) {
            try {
                String fileName = "images/" + pictureName.replace(" ", "_").concat(String.valueOf(System.currentTimeMillis())) + ".png";
                File file = new File(System.getenv("gauge_reports_dir") + "/html-report/specs/" + fileName);
                if (file.exists()) {
                    file.delete();
                }
                File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, file);
                Gauge.writeMessage("<a href='../" + fileName + "'><img src='../" + fileName + "' width='800' height='480'></a>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void beforePageLoad() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("document.mpPageReloaded='notYet';");
    }

    public static void waitForPageLoaded(WebDriver driver) {
        ExpectedCondition<Boolean> expectation = driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete");
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            wait.until(expectation);
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    public static void cacheCleanAction() {
        if (System.getenv("Browser").equalsIgnoreCase("CHROME")) {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            getDriver().navigate().to("chrome://settings/clearBrowserData");
            for (int i = 1; i <= 7; i++) {
                getDriver().switchTo().activeElement();
                getDriver().findElement(By.xpath("//settings-ui")).sendKeys(Keys.TAB);
            }
            getDriver().findElement(By.xpath("//settings-ui")).sendKeys(Keys.ENTER);
        } else if (System.getenv("Browser").equalsIgnoreCase("EDGE")) {
            getDriver().get("edge://settings/clearBrowserData");
            getDriver().switchTo().activeElement();
            CommonUtils.pause(500);
            getDriver().findElement(By.id("clear-now")).sendKeys(Keys.ENTER);
        }
    }

    public static void clearCacheAndCookies() {
        if (!System.getenv("BROWSER_HEADLESS_MODE").equalsIgnoreCase("Yes")) {
            try {
                cacheCleanAction();
            } catch (Exception e) {
                cacheCleanAction();
            }
            CommonUtils.pause(2000);
        }
        getDriver().manage().deleteAllCookies();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    }

    public static String getMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[1];
        return e.getMethodName();
    }

    public static void clickButton(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
        }
    }

    public static void clickElement(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
        }
    }

    public static void javascriptElementClick(WebElement element) {
        CommonUtils.checkElementIsClickable(element, 10);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
    }

    public static void javascriptEnterText(WebElement element, String data) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].value='" + data + "'", element);
    }

    public static void clickElement(String strElm, By by) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(40)).until(ExpectedConditions.visibilityOfElementLocated(by));
            getDriver().findElement(by).click();
        } catch (Exception e) {
            new WebDriverWait(getDriver(), Duration.ofSeconds(40)).until(ExpectedConditions.visibilityOfElementLocated(by));
            getDriver().findElement(by).click();
        }
    }

    public static void clickElementByText(String text) {
        getDriver().findElement(By.name(text));
    }

    public static void enterTextInTextBox(WebElement element, String txt) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(40)).until(ExpectedConditions.visibilityOf(element));
        if (element.isEnabled()) {
            element.click();
            element.clear();
            element.sendKeys(Keys.CONTROL + "a");
            element.sendKeys(Keys.DELETE);
            element.sendKeys(txt);
        }
    }

    public static String getValue(WebElement element) throws Exception {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        return element.getAttribute("value");
    }

    public static String getMessage(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(7)).until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }

    public static void selectRadioButton(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(7)).until(ExpectedConditions.visibilityOf(element));
        if (!element.isSelected())
            element.click();
    }

    public static boolean isAttributePresent(WebElement element, String attribute) {
        Boolean result = false;
        try {
            String value = element.getAttribute(attribute);
            if (value != null) {
                result = true;
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static void waitForLoad(WebDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30)).until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            System.out.println("wait for page load time exceeded");
        }
    }

    public static void deleAllTabsExceptDefault() {
        Set<String> winHandles = getDriver().getWindowHandles();
        if (winHandles.size() != 0) {
            List list = new ArrayList();
            for (String s : winHandles) {
                list.add(s);
            }
            if (winHandles.size() > 1) {
                for (int i = 0; i <= winHandles.size() - 2; i++) {
                    getDriver().switchTo().window(list.get(i).toString());
                    getDriver().close();
                }
                getDriver().switchTo().window(list.get(winHandles.size() - 1).toString());
            }
        }
        getDriver().manage().deleteAllCookies();
    }

    public static void selectCheckBox(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        if (!element.isSelected())
            element.click();
    }

    public static void selectDDValue(WebElement element, String val) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        Select dd = new Select(element);
        dd.selectByValue(val);
    }

    public static void selectDDVisibleText(WebElement element, String visibleText) throws Exception {
        for (int i = 0; i < 2; i++) {
            try {
                new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
                Select dd = new Select(element);
                dd.selectByVisibleText(visibleText);
                break;
            } catch (StaleElementReferenceException see) {

            }
        }
    }

    public static void selectDDByIndex(WebElement element, int index) throws Exception {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        Select dd = new Select(element);
        dd.selectByIndex(index);
    }

    public static String getSelectedDDValue(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        Select dd = new Select(element);
        return dd.getFirstSelectedOption().getText();
    }

    public static List<WebElement> getAllDDValue(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        Select dd = new Select(element);
        String ddOption = null;

        List<WebElement> options = dd.getOptions();
        /*for(int i=0;i<options.size();i++){
        ddOption=options.get(i).getText();
        }*/
        return options;
    }

    public static List getListOfValuesinDropDown(WebElement element) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        Select dd = new Select(element);
        List<String> ddValues = new ArrayList<String>();
        List<WebElement> options = dd.getOptions();
        for (int i = 0; i < options.size(); i++) {
            ddValues.add(options.get(i).getText());
        }
        return ddValues;
    }

    public static String getValueUsingAttribute(WebElement element, String attributeValue) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(element));
        return element.getAttribute(attributeValue);
    }

    public static double convertStringToDouble(String text) throws Exception {
        if (!text.equals("")) {
            double converted = Double.parseDouble(text);
            String.format("%.2f", new BigDecimal(converted));
            return converted;
        } else {
            return 0;
        }
    }

    public static Map convertTableToMap(Table table) {
        Map<String, Object> tableAsMap = new HashMap<>();
        for (TableRow row : table.getTableRows()) {
            tableAsMap.put(row.getCell("Key"), row.getCell("Value"));
        }
        ExtentTestManager.reportMapData(tableAsMap);
        return tableAsMap;
    }

    public static Map convertChildTableToMap(Table table) {
        Map<String, Object> tableAsMap = new HashMap<>();
        for (TableRow row : table.getTableRows()) {
            for (String colName : table.getColumnNames()) {
                tableAsMap.put(colName, row.getCell(colName));
            }
        }
        ExtentTestManager.reportMapData(tableAsMap);
        return tableAsMap;
    }

    public static Map<Integer, Map<String, Object>> getData(Table table) {
        int rowNumber = 1;
        Map<String, Object> tableAsMap;
        Hashtable<Integer, Map<String, Object>> data = new Hashtable<Integer, Map<String, Object>>();
        for (TableRow row : table.getTableRows()) {
            tableAsMap = new HashMap<>();
            for (String colName : table.getColumnNames()) {
                tableAsMap.put(colName, row.getCell(colName));
            }
            data.put(rowNumber, tableAsMap);
            rowNumber++;
        }
        return data;
    }

    public static int getTableRowCount(Table table) {
        return table.getTableRows().size();
    }

    public static List convertTableToList(Table table) {
        List<String> tableAsList = new ArrayList<>();
        for (TableRow row : table.getTableRows()) {
            tableAsList.add(row.getCell("Key"));
        }
        ExtentTestManager.reportInfo(tableAsList.toString());
        return tableAsList;
    }

    public static List convertToUpperCase(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String element : list) {
            newList.add(element.toUpperCase());
        }
        return newList;
    }

    public static boolean isElementPresentCheckUsingJavaScriptExecutor(WebElement element) {
        JavascriptExecutor jse = (JavascriptExecutor) getDriver();
        try {
            Object obj = jse.executeScript("return typeof(arguments[0])!='undefined' && arguments[0]!=null;", element);
            if (obj.toString().contains("true")) {
                System.out.println("isElementPresentCheckUsingJavaScriptExecutor: SUCCESS");
                return true;
            } else {
                System.out.println("isElementPresentCheckUsingJavaScriptExecutor: FAIL");
            }
        } catch (NoSuchElementException e) {
            System.out.println("isElementPresentCheckUsingJavaScriptExecutor: FAIL");
        }
        return false;
    }

    public static boolean checkElementIsVisible(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkElementIsClickable(WebElement element, int timeout) {
        if (timeout < Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME")))
            timeout = Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME")) + 1;

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkElementIsClickable(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkElementIsNotVisible(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkElementIsVisible(WebElement element, int timeout) {
        if (timeout < Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME")))
            timeout = Integer.parseInt(System.getenv("IMPLICIT_WAIT_TIME")) + 1;

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkElementTextToBePresent(WebElement element, String expectedVisibleText) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.textToBePresentInElement(element, expectedVisibleText));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement fluentWebElement(String elementXpath) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                .withTimeout(Duration.ofSeconds(TimeUnit.SECONDS.toSeconds(10)))
                .pollingEvery(Duration.ofSeconds(TimeUnit.SECONDS.toSeconds(1)))
                .ignoring(Exception.class);

        WebElement element = wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                WebElement element1 = driver.findElement(By.xpath(elementXpath));
                if (element1.isEnabled()) {
                    System.out.println("Element Found");
                }
                return element1;
            }
        });
        return element;
    }

    public static boolean checkElementIsNotVisible(WebElement element) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
            if (element.isDisplayed()) {
                ExtentTestManager.reportFail("Element is visible");
            }
            return false;
        } catch (Exception e) {
            ExtentTestManager.reportPass("Element is not visible");
            return true;
        }
    }

    public static void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        CommonUtils.pause(1000);
    }

    public static void scrollIntoView(WebElement element, int pixelmore) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollBy(40," + pixelmore + ")");
        CommonUtils.pause(500);
    }

    public static WebElement getWebElement(By by, String eleName) {
        WebElement eLe = null;
        try {
            eLe = getDriver().findElement(by);
            Gauge.writeMessage("Validate if " + eleName + " is present on screen", eleName + " should appear ", eleName + " is appearing");
            return eLe;
        } catch (Exception e) {
            Gauge.writeMessage("Validate if " + eleName + " is present on screen", eleName + " should appear ", eleName + " is not appearing");
            e.printStackTrace();
            return null;
        }
    }

    public static WebElement findAnyWebElement(WebElement element, String eleName) {
        try {
            Gauge.writeMessage("Validate if " + eleName + " is present on screen", eleName + " should appear ", eleName + " is appearing");
            return element;
        } catch (Exception e) {
            Gauge.writeMessage("Validate if " + eleName + " is present on screen", eleName + " should appear ", eleName + " is not appearing");
            e.printStackTrace();
            return null;
        }
    }

    public static void SelectOption(String expLabel, By by, String expValue) {
        WebElement wEle = getWebElement(by, expLabel);
        try {
            Select sel = new Select(wEle);
            sel.selectByVisibleText(expValue);
            Gauge.writeMessage(expValue + " is selected");
        } catch (Exception e) {
            Gauge.writeMessage(expValue + " unable to select");
            e.printStackTrace();
        }
    }

    public static void SelectOption(String expLabel, WebElement webElement, String expValue) {
        WebElement wEle = findAnyWebElement(webElement, expLabel);
        try {
            Select sel = new Select(wEle);
            sel.selectByVisibleText(expValue);
            Gauge.writeMessage(expValue + " is selected");
        } catch (Exception e) {
            Gauge.writeMessage(expValue + " unable to select");
            e.printStackTrace();
        }
    }

    public static Integer generateRandomNumber() {
        Random num = new Random();
        return num.nextInt(10000000);
    }

    public static Integer generateRandomNumberOfLength(int length) {
        Random num = new Random();
        int i = 1;
        for (int n = 1; n <= length; n++) {
            i = i * 10;
        }
        return num.nextInt(i);
    }

    public static void pause(Integer milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateAphaNumericRandomString(final int stringLength) {
        StringBuilder password = new StringBuilder(stringLength);
        String letters = ("abcdefghijklmnopqrstuvwxyz");
        String digits = ("1234567890");
        String symbols = ("@#$%!");

        Random num = new Random();

        for (int i = 0; i <= stringLength - 2; i++) {
            if (i % 2 == 0) {
                password.append(letters.charAt(num.nextInt(letters.length())));
            } else {
                password.append(letters.charAt(num.nextInt(digits.length())));
            }
        }
        //Add digit and symbol to match password criteria
        password.append(digits.charAt(num.nextInt(digits.length())));
        return password.toString();
    }

    public static boolean isFileDownloaded(String downloadPath, String fileName) {
        boolean flag = false;
        File dir = new File(downloadPath);
        File[] dir_contents = dir.listFiles();

        for (int i = 0; i < dir_contents.length; i++) {
            if (dir_contents[i].getName().startsWith(fileName))
                return flag = true;
        }
        return flag;
    }

    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("Today Int:" + todayInt + "\n");
        String todayStr = Integer.toString(todayInt);
        System.out.println("Today Str:" + todayStr + "\n");
        return todayStr;
    }

    public static String getPreviousMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("Today Int:" + todayInt + "\n");
        String todayStr = Integer.toString(todayInt);
        System.out.println("Today Str:" + todayStr + "\n");
        return todayStr;
    }

    public static String getPreviousDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int todayInt = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        System.out.println("Today Int:" + todayInt + "\n");
        String todayStr = Integer.toString(todayInt);
        System.out.println("Today Str:" + todayStr + "\n");
        return todayStr;
    }

    public static String getCurrentDayPlus1Day() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int todayInt = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        System.out.println("Today Int:" + todayInt + "\n");
        String todayStr = Integer.toString(todayInt);
        System.out.println("Today Str:" + todayStr + "\n");
        return todayStr;
    }

    public static String splitText(String text, String delimeter, int stringIndex, int startIndex, int endIndex) {
        String textStr[] = text.split(delimeter);
        String str = textStr[stringIndex].substring(startIndex, endIndex);
        return str;
    }

    public static boolean scrollWithinElement(String x, String y, String xpath, String scrollElementProperty) {
        boolean flag = false;
        while (!flag) {
            ((JavascriptExecutor) getDriver()).executeScript("document.querySelector(\"" + scrollElementProperty + "\").scroll(" + x + "," + y + ")");
            x = y;
            y = String.valueOf(Integer.parseInt(x) + 250);
            try {
                CommonUtils.checkElementIsVisible(getDriver().findElement(By.xpath(xpath)));
                flag = true;
            } catch (NoSuchElementException e) {

            }
        }
        return flag;
    }

    public static void hoverElement(String hoverElement) {
        ((JavascriptExecutor) getDriver()).executeScript("document.querySelector(\"" + hoverElement + "\").onmouseover");
    }

    public static void switchToParentTab() {
        try {
            String subWindowHandler = null;
            String parentWinHandle = getDriver().getWindowHandle();
            Set<String> handles = getDriver().getWindowHandles();
            Iterator<String> iterator = handles.iterator();
            while (iterator.hasNext()) {
                subWindowHandler = iterator.next();
                if (!subWindowHandler.equals(parentWinHandle)) {
                    getDriver().switchTo().window(subWindowHandler);
                    break;
                }
            }
            CommonUtils.pause(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchTab() {
        try {
            String subWindowHandler = null;
            String parentWinHandle = getDriver().getWindowHandle();
            Set<String> handles = getDriver().getWindowHandles();
            Iterator<String> iterator = handles.iterator();
            while (iterator.hasNext()) {
                subWindowHandler = iterator.next();
                if (!subWindowHandler.equals(parentWinHandle)) {
                    getDriver().switchTo().window(subWindowHandler);
                    //break;
                }
            }
            CommonUtils.pause(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> boolean areEqual(T ob1, T ob2) {
        //Lets assume if both the objects are null then they are also equals
        //Covers our case when object is not oming from ODC and hence is absent in XmartEntity

        if (ob1 == null && ob2 == null) {
            return true;
        }
        //both are not null here so one is
        if (ob1 == null || ob2 == null) {
            return false;
        }
        if (!ob1.getClass().equals(ob2.getClass())) {
            return false;
        }
        if (ob1 instanceof Number && ob1.equals(ob2)) {
            return true;
        }
        if (ob1 instanceof String && ob1.equals(ob2)) {
            return true;
        }
        if (ob1 instanceof LocalTime && ob1.equals(ob2)) {
            return true;
        }
        if (ob1 instanceof Date && ob1.equals(ob2)) {
            return true;
        }
        if (ob1 != ob2) {
            return false;
        }
        return true;
    }

    public static void scrollToEndOfPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            int len = CommonUtils.generateRandomNumberOfLength(1);
            if (len == 0) {
                len = 1;
            }
            long lastHeight = ((Number) js.executeScript("return document.body.scrollHeight")).longValue();
            String preValue, nextValue;
            int value;
            if (lastHeight > 0) {
                while (len > 0) {
                    value = getDriver().findElements(By.tagName("tr")).size() / 3;
                    preValue = getDriver().findElements(By.tagName("tr")).get(value).getText();
                    CommonUtils.scrollIntoView(getDriver().findElements(By.tagName("tr")).get(getDriver().findElements(By.tagName("tr")).size() / 3));
                    value = getDriver().findElements(By.tagName("tr")).size() / 3;
                    nextValue = getDriver().findElements(By.tagName("tr")).get(value).getText();
                    if (nextValue.equalsIgnoreCase(preValue)) {
                        break;
                    }
                    len--;
                }
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scrollByPixel(int pixel) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollBy(0," + pixel + ")");
        CommonUtils.pause(500);
    }

    public static long calculateDateDifference(String inputString1) {
        //DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long daysBetween = 0;
        try {
            String inputString2 = getCurrentDate();
            //parsing the date
            LocalDate vamUIDate = LocalDate.parse(inputString1);
            LocalDate currentDate = LocalDate.parse(inputString2);
            //calculating number of days in between
            daysBetween = ChronoUnit.DAYS.between(vamUIDate, currentDate);
            //displaying the number of days
            System.out.println(daysBetween);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daysBetween;
    }

    public static void fluentWaitForPageLoad(WebDriver driver, WebElement element, String waitMsgCondition) {
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);

        Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (!(element.findElements(By.tagName("tr")).get(0).getText().equalsIgnoreCase(waitMsgCondition))) {
                    return true;
                }
                return false;
            }
        };
        wait.until(function);
    }

    public static List<String> getListOfFileNamesFromDirectory(String pathToDirectory) {
        List<String> results = new ArrayList<String>();
        File[] files = new File(pathToDirectory).listFiles();

        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }

    public static String renameFileInDirectory(String existingName, String newName) {
        File sourceFileName = new File(existingName);
        File destFileName = new File(newName);
        sourceFileName.renameTo(destFileName);
        return destFileName.getName();
    }

    public static List<String> returnFileNamesBasedOnPartialName(String partialName, String pathToDirectory) {
        List<String> fileName = getListOfFileNamesFromDirectory(pathToDirectory);
        List<String> returnableFileList = new ArrayList<>();

        for (String name : fileName) {
            if (name.contains(partialName))
                returnableFileList.add(name);
        }
        return returnableFileList;
    }

    public static String getCurrentDateTimeSecondAsString() {
        Date d = new Date();
        return d.toString().replace(":", "_").replace(" ","_");
    }
    public static void deleteFolderAndFilesInDirectory(File folder){
        File[] files = folder.listFiles();
        if (files!=null) {
            for(File f:files){
                if(f.isDirectory()) {
                    deleteFolderAndFilesInDirectory(f);
                }
                else{
                    f.delete();
                }
            }
        }

    }
    public static void deleteFolder(File folder){
        File[] files = folder.listFiles();
        if (files!=null) {
            for(File f:files){
                if(f.isDirectory()) {
                    deleteFolderAndFilesInDirectory(f);
                }
                else{
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    public final static String converYYYYMMDDToDDMMYYYY(String strDate){
        String dateSplit[]=strDate.split("-");
        strDate=dateSplit[2]+"-"+dateSplit[1]+"-"+dateSplit[0];
        return strDate;
    }

    public static String getCSSValue(WebElement element, String attributeName){
        try{
            new WebDriverWait(getDriver(),Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
        }
        catch (Exception e){
            System.out.println(e);
        }
        return element.getCssValue(attributeName);
    }

    public static void scrollAndSelectElementFromDynamicDropdownList(WebElement dropdownElement,List<WebElement> dropdownOptionsRowLocator,String textToSelect){
        int counter=0;
        Actions actions = new Actions(getDriver());
        actions.click(dropdownElement).build().perform();
        String element = "//*[@class='scrollable-content']//*[contains(text(),'"+textToSelect+"')]";
        Iterator<WebElement> iterator=dropdownOptionsRowLocator.listIterator();
        List<WebElement> initList = dropdownOptionsRowLocator;
        List<WebElement> runtimeList;
        while(!iterator.next().getText().equalsIgnoreCase(textToSelect) && counter <500){
            actions.moveToElement(dropdownElement).sendKeys(Keys.ARROW_DOWN).build().perform();
            runtimeList = dropdownOptionsRowLocator;
            if(!initList.equals(runtimeList))
                iterator= dropdownOptionsRowLocator.listIterator();
            counter++;
        }
        CommonUtils.scrollIntoView(getDriver().findElement(By.xpath(element)));
        CommonUtils.clickButton(getDriver().findElement(By.xpath(element)));
    }
}
