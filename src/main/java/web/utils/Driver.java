package web.utils;

import com.thoughtworks.gauge.*;

import java.io.IOException;

public class Driver extends DriverSupport {
    @BeforeSpec
    public synchronized void getBeforeSpecName(ExecutionContext context) {

        beforeSpecActions(context);
    }

    @AfterSpec
    public synchronized void getAfterSpecName() {
        afterSpecActions();
    }
    @BeforeScenario(tags={"Web"})
    public synchronized void initializeDriver(ExecutionContext context) {
        beforeScenarioActions(context);
    }
    @AfterScenario(tags={"Web","NonWeb"},tagAggregation = Operator.OR)
    public synchronized void closeDriver(ExecutionContext context) {
        afterScenarioActions(context);
    }
    @BeforeSuite
    public synchronized void init() throws IOException {
        beforeSuiteActions();
    }
    @AfterSuite
    public void quitDriver() throws Throwable {
        afterSuiteActions();
    }
    @BeforeStep
    public synchronized void getBeforeStepName(ExecutionContext context){
        beforeStepActions(context);
    }
    @AfterStep(tags = {"Web"})
    public synchronized void screenshot(ExecutionContext context){
        afterStepActions(context);
    }
    @BeforeScenario(tags={"NonWeb"})
    public synchronized void initializeWithoutBrowser(ExecutionContext context) {
        beforeScenarioNonUIActions(context);
    }
}
