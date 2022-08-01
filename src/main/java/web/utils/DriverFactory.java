package web.utils;

import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    // Get a new WebDriver Instance.
    // There are various implementations for this depending on browser. The required browser can be set as an environment variable.
    // Refer http://getgauge.io/documentation/user/current/managing_environments/README.html
    public static WebDriver getDriver() {

        String browser = null;

        if(Driver.prop.containsKey("Browser") && !Driver.prop.getProperty("Browser").isEmpty())
            browser = Driver.prop.getProperty("Browser");
        else if(System.getenv("Browser")!=null)
            browser = System.getenv("Browser");
        else
            browser = CommonUtils.getSpecStoreVal("Browser");

        if(System.getenv("Browser")!=null)
            browser = System.getenv("Browser");
        else
            browser = CommonUtils.getSpecStoreVal("Browser");

        DataStoreFactory.getSpecDataStore().put("Browser",browser);
        String driverPath = System.getenv("DRIVERPATH");
        String binaryPath;
        String machine = System.getenv("MACHINE");
        String ChromeDriverPath = null;

        if(!machine.equalsIgnoreCase("VCSE")) {
            if (System.getenv("AGENT_NAME").contains("8411"))
                ChromeDriverPath = System.getenv("CHROMEDRIVERPATH_ECP_8411");
        }
        else
            ChromeDriverPath = System.getenv("CHROMEDRIVERPATH_VCSE");
        System.out.println("CHROME DRIVER PATH: "+ChromeDriverPath);

        switch (browser){
            case "FF":
                System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+ File.separator+"libs/drivers/Firefox/geckodriver.exe");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setAcceptInsecureCerts(true);
                firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
                firefoxOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
                return new FirefoxDriver(firefoxOptions);
            case "IE":
                String IeDriverPath = System.getenv("IEDRIVERPATH");
                System.setProperty("webdriver.ie.driver",IeDriverPath);
                InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
                internetExplorerOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,true);
                return new InternetExplorerDriver(internetExplorerOptions);
            case "EDGE":
                System.setProperty("webdriver.edge.driver",System.getenv("EDGEDRIVER"));
                EdgeOptions opt = new EdgeOptions();
                opt.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
                opt.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
                opt.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT,true);
                opt.setCapability(CapabilityType.SUPPORTS_ALERTS,true);
                opt.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.NORMAL);
                if(System.getenv("BROWSER_HEADLESS_MODE").equalsIgnoreCase("Yes")&& System.getenv("BROWSER_HEADLESS_MODE")!=null){
                    opt.addArguments("--headless");
                    opt.addArguments("--window-size=1440,900");
                }
                return new EdgeDriver(opt);

            case "CHROME":
                ChromeOptions options = new ChromeOptions();
                options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
                options.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
                if(System.getenv("PAGE_LOAD_STRATEGY").equalsIgnoreCase("EAGER"))
                    options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                else if(System.getenv("PAGE_LOAD_STRATEGY").equalsIgnoreCase("NORMAL"))
                    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                else if(System.getenv("PAGE_LOAD_STRATEGY").equalsIgnoreCase("NONE"))
                    options.setPageLoadStrategy(PageLoadStrategy.NONE);

                Map<String,Object> prefs = new HashMap<>();
                switch (machine) {
                    case "LINUX":
                        options.addArguments("headless");
                        break;

                    case "WINBUILDAGENT":
                        if (System.getenv("ENABLE_PROXY").equalsIgnoreCase("Yes") & System.getenv("Environment").contains("NFTE"))
                            setChromeProxySettings(System.getenv("PROXY_PORT"), options);
                        if (System.getenv("BROWSER_HEADLESS_MODE").equalsIgnoreCase("Yes") && System.getenv("BROWSER_HEADLESS_MODE") != null) {
                            options.addArguments("--headless");
                            options.addArguments("--window-size=1400,900");
                        }
                        options.addArguments("--ignore-certificate-errors");
                        options.addArguments("--disable-notifications");
                        options.addArguments("--disable-dev-shm-usage");
                        options.addArguments("--no-sandbox");
                        options.addArguments("--priviledged");

                        prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
                        options.setExperimentalOption("prefs", prefs);

                        System.out.println("On WinBuildAgent");
                        break;
                    case "VCSE":
                        binaryPath = System.getenv("BINARYPATH");
                        System.out.println("BINARY CHROME PATH::::" + binaryPath);
                        if (System.getenv("BROWSER_HEADLESS_MODE").equalsIgnoreCase("Yes") && System.getenv("BROWSER_HEADLESS_MODE") != null) {
                            options.addArguments("--no-sandbox");
                            options.addArguments("--priviledged");
                            options.addArguments("--headless", "--window-size=1440,900");
                            options.addArguments("--disable-gpu");
                            options.addArguments("--ignore-certificate-errors");
                            options.addArguments("--disable-notifications");
                        }
                        options.addArguments("--disable-dev-shm-usage");
                        options.addArguments("--no-sandbox");
                        options.setBinary(binaryPath);
                        break;
                    default:
                        System.out.println("Invalid web.utils machine. Please check property file for valid information");
                }
                System.setProperty("webdriver.chrome.driver",ChromeDriverPath);
                System.setProperty("webdriver.chrome.silentOutput","true");
                prefs.put("download.default_directory",System.getProperty("user.dir")+File.separator + "target"+File.separator +"downloadFiles");
                prefs.put("useAutomationExtension",false);
                options.setExperimentalOption("prefs",prefs);
                options.setAcceptInsecureCerts(true);

                if(System.getenv().containsKey("ENABLE_GRID") && System.getenv("ENABLE_GRID").equalsIgnoreCase("Yes")){
                    try{
                        options.setCapability(CapabilityType.BROWSER_NAME,"chrome");
                        options.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);
                        return new RemoteWebDriver(new URL("http://localhost:4444"),options);
                    }
                    catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                }
                else
                    return new ChromeDriver(options);
            default:
                System.out.println("Invalid web.util browser type. Please check property file for valid information");
                return null;

        }
    }
    public static void closeDriver(WebDriver driver){
        try {
            if (driver != null)
                driver.close();
        }
        catch (Exception e){
        }
    }
    public static void quitDriver(WebDriver driver){
        try {
            if (driver != null)
                driver.quit();
        }
        catch (Exception e){
        }
    }
    public static void setChromeProxySettings(String proxyUrlPort, ChromeOptions options){
        options.addArguments("--proxy-server=http://"+proxyUrlPort);
        options.addArguments("--proxy-bypass-list=teamcity-3-dts.fm.com");
    }
}
