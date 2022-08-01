package web.utils;

import com.thoughtworks.gauge.datastore.DataStoreFactory;

import java.util.Date;
import java.util.Map;

public class JiraDriverSupport {
    public String get_current_date_time_as_String() {
        Date d = new Date();
        String strDate = "_" + d.toString().replace(":", "_").replace(" ", "_");
        return strDate;
    }

    public void update_jira_test_status(){
      System.out.println("UPDATE_JIRA_TEST_STATUS is set to Yes");
      //Getting folder name from cycle name
      String cycleIdFromName = JiraApis.getCycleIdBySearchingCycleName(System.getenv("PROJECT_ID"),CommonUtils.getSpecStoreVal("CYCLE_NAME").trim());

      //checking and deleting cycle if already exists
        if(!cycleIdFromName.equalsIgnoreCase("Cycle is not available"))
            JiraApis.deleteCycleId(cycleIdFromName);

        //Creating new cycle and assigning project, version and cycle id to custom properties
        DataStoreFactory.getSpecDataStore().put("PROJECT_ID",System.getenv("PROJECT_ID"));
        DataStoreFactory.getSpecDataStore().put("VERSION_ID",System.getenv("VERSION_ID"));
        String newCreatedCycleId = JiraApis.createNewCycleId(CommonUtils.getSpecStoreVal("CYCLE_NAME"),System.getenv("PROJECT_ID"),CommonUtils.getSpecStoreVal("VERSION_ID"));
        DataStoreFactory.getSpecDataStore().put("CYCLE_ID",newCreatedCycleId);
        CommonUtils.setSuiteStoreVal("PROJECT_ID",System.getenv("PROJECT_ID"));
        CommonUtils.setSuiteStoreVal("VERSION_ID",System.getenv("VERSION_ID"));
        CommonUtils.setSuiteStoreVal("CYCLE_ID",newCreatedCycleId);

        //creating new folder within created cycle
        String folderId = JiraApis.createFolderUnderCycle(CommonUtils.getSpecStoreVal("FOLDER_NAME")+get_current_date_time_as_String(),CommonUtils.getSpecStoreVal("FOLDER_NAME"),CommonUtils.getSpecStoreVal("CYCLE_ID"),CommonUtils.getSpecStoreVal("PROJECT_ID"),CommonUtils.getSpecStoreVal("VERSION_ID"));
        CommonUtils.setSuiteStoreVal("FOLDER_ID",folderId);
    }
    public void create_new_folder_for_existing_cycle_execution_on_each_run(){

        String cycleIdFromName = JiraApis.getCycleIdBySearchingCycleName(System.getenv("PROJECT_ID"),CommonUtils.getSpecStoreVal("CYCLE_NAME").trim());

        System.out.println("CYCLE ID::"+ cycleIdFromName);
        if(!cycleIdFromName.equalsIgnoreCase("Cycle is not available")) {
            DataStoreFactory.getSpecDataStore().put("CYCLE_ID", cycleIdFromName);
            String versionId = Integer.toString(JiraApis.getVersionIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("VERSION_ID", versionId);
            String projectId = Integer.toString(JiraApis.getProjectIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("PROJECT_ID", projectId);
            JiraApis.cleanFoldersFromCycle(CommonUtils.getSpecStoreVal("PROJECT_ID"), CommonUtils.getSpecStoreVal("CYCLE_ID"), CommonUtils.getSpecStoreVal("VERSION_ID"), 100, 0);
            CommonUtils.pause(4000);
            String folderId = JiraApis.createFolderUnderCycle(CommonUtils.getSpecStoreVal("FOLDER_NAME") + get_current_date_time_as_String(), CommonUtils.getSpecStoreVal("FOLDER_NAME"), CommonUtils.getSpecStoreVal("CYCLE_ID"), CommonUtils.getSpecStoreVal("PROJECT_ID"), CommonUtils.getSpecStoreVal("VERSION_ID"));
            DataStoreFactory.getSpecDataStore().put("FOLDER_ID", folderId);
            CommonUtils.setSuiteStoreVal("PROJECT_ID",projectId);
            CommonUtils.setSuiteStoreVal("VERSION_ID",versionId);
            CommonUtils.setSuiteStoreVal("CYCLE_ID",cycleIdFromName);
            CommonUtils.setSuiteStoreVal("FOLDER_ID", folderId);
            Driver.folderExists = true;
        }
        else{
            System.out.println("Cycle is not available in JIRA. Creating new cycle id");
            DataStoreFactory.getSpecDataStore().put("PROJECT_ID",System.getenv("PROJECT_ID"));
            DataStoreFactory.getSpecDataStore().put("VERSION_ID",System.getenv("VERSION_ID"));
            String newCreatedCycleId = JiraApis.createNewCycleId(CommonUtils.getSpecStoreVal("CYCLE_NAME"),System.getenv("PROJECT_ID"),CommonUtils.getSpecStoreVal("VERSION_ID"));
            DataStoreFactory.getSpecDataStore().put("CYCLE_ID",newCreatedCycleId);
            String versionId = Integer.toString(JiraApis.getVersionIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("VERSION_ID", versionId);
            String projectId = Integer.toString(JiraApis.getProjectIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("PROJECT_ID", projectId);
            String folderId = JiraApis.createFolderUnderCycle(CommonUtils.getSpecStoreVal("FOLDER_NAME") + get_current_date_time_as_String(), CommonUtils.getSpecStoreVal("FOLDER_NAME"), CommonUtils.getSpecStoreVal("CYCLE_ID"), CommonUtils.getSpecStoreVal("PROJECT_ID"), CommonUtils.getSpecStoreVal("VERSION_ID"));
            DataStoreFactory.getSpecDataStore().put("FOLDER_ID", folderId);
            CommonUtils.setSuiteStoreVal("PROJECT_ID",projectId);
            CommonUtils.setSuiteStoreVal("VERSION_ID",versionId);
            CommonUtils.setSuiteStoreVal("CYCLE_ID",cycleIdFromName);
            CommonUtils.setSuiteStoreVal("FOLDER_ID", folderId);
            Driver.folderExists = true;
        }
    }
    public void create_new_folder_along_with_the_existing_folders_for_a_cycle(){
        String cycleIdFromName = JiraApis.getCycleIdBySearchingCycleName(System.getenv("PROJECT_ID"),CommonUtils.getSpecStoreVal("CYCLE_NAME").trim());
        System.out.println("CYCLE ID::"+ cycleIdFromName);
        if(!cycleIdFromName.equalsIgnoreCase("Cycle is not available")) {
            DataStoreFactory.getSpecDataStore().put("CYCLE_ID", cycleIdFromName);
            String versionId = Integer.toString(JiraApis.getVersionIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("VERSION_ID", versionId);
            String projectId = Integer.toString(JiraApis.getProjectIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("PROJECT_ID", projectId);
            CommonUtils.pause(4000);
            String folderId = JiraApis.createFolderUnderCycle(CommonUtils.getSpecStoreVal("FOLDER_NAME") + get_current_date_time_as_String(), CommonUtils.getSpecStoreVal("FOLDER_NAME"), CommonUtils.getSpecStoreVal("CYCLE_ID"), CommonUtils.getSpecStoreVal("PROJECT_ID"), CommonUtils.getSpecStoreVal("VERSION_ID"));
            DataStoreFactory.getSpecDataStore().put("FOLDER_ID", folderId);
            CommonUtils.setSuiteStoreVal("PROJECT_ID",projectId);
            CommonUtils.setSuiteStoreVal("VERSION_ID",versionId);
            CommonUtils.setSuiteStoreVal("CYCLE_ID",cycleIdFromName);
            CommonUtils.setSuiteStoreVal("FOLDER_ID", folderId);
            Driver.folderExists = true;
        }
    }
    public void user_existing_cycle_folder_for_execution() {
        String cycleIdFromName = JiraApis.getCycleIdBySearchingCycleName(System.getenv("PROJECT_ID"), CommonUtils.getSpecStoreVal("CYCLE_NAME").trim());
        System.out.println("CYCLE ID::" + cycleIdFromName);
        if (!cycleIdFromName.equalsIgnoreCase("Cycle is not available")) {
            DataStoreFactory.getSpecDataStore().put("CYCLE_ID", cycleIdFromName);
            String versionId = Integer.toString(JiraApis.getVersionIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("VERSION_ID", versionId);
            String projectId = Integer.toString(JiraApis.getProjectIdFromCycleId(cycleIdFromName));
            DataStoreFactory.getSpecDataStore().put("PROJECT_ID", projectId);
            CommonUtils.setSuiteStoreVal("PROJECT_ID", projectId);
            CommonUtils.setSuiteStoreVal("VERSION_ID", versionId);
            CommonUtils.setSuiteStoreVal("CYCLE_ID", cycleIdFromName);
            Map<String, String> folderList = JiraApis.getFolderNameListOfCycle(System.getenv("PROJECT_ID"), cycleIdFromName, System.getenv("VERSION_ID"), 100, 0);
            if (folderList.size() >= 1) {
                for (Map.Entry<String, String> entry : folderList.entrySet()) {
                    if (entry.getValue().startsWith(System.getenv("FOLDER_NAME"))) {
                        String folderId = entry.getKey();
                        Driver.folderExists = true;
                        DataStoreFactory.getSpecDataStore().put("FOLDER_ID", folderId);
                        CommonUtils.setSuiteStoreVal("FOLDER_ID", folderId);
                    }
                }
            } else {
                Driver.folderId = "";
                Driver.folderExists = false;
            }
        }
    }
    public void get_create_cycle_folder() {
        String cycleIdFromName = JiraApis.getCycleIdBySearchingCycleName(System.getenv("PROJECT_ID"), CommonUtils.getSpecStoreVal("CYCLE_NAME").trim());
        DataStoreFactory.getSpecDataStore().put("CYCLE_ID", cycleIdFromName);
        String versionId = Integer.toString(JiraApis.getVersionIdFromCycleId(cycleIdFromName));
        DataStoreFactory.getSpecDataStore().put("VERSION_ID", versionId);
        String projectId = Integer.toString(JiraApis.getProjectIdFromCycleId(cycleIdFromName));
        DataStoreFactory.getSpecDataStore().put("PROJECT_ID", projectId);
        CommonUtils.setSuiteStoreVal("PROJECT_ID", projectId);
        CommonUtils.setSuiteStoreVal("VERSION_ID", versionId);
        CommonUtils.setSuiteStoreVal("CYCLE_ID", cycleIdFromName);
        Map<String, String> folderList = JiraApis.getFolderNameListOfCycle(System.getenv("PROJECT_ID"), cycleIdFromName, System.getenv("VERSION_ID"), 100, 0);
        if (folderList.size() >= 1) {
            for (Map.Entry<String, String> entry : folderList.entrySet()) {
                if (entry.getValue().startsWith(System.getenv("FOLDER_NAME"))) {
                    String folderId = entry.getKey();
                    Driver.folderExists = true;
                    CommonUtils.setSuiteStoreVal("FOLDER_ID", folderId);
                    }
                }
            } else {
                Driver.folderId = "";
                Driver.folderExists = false;
            }
        }
}
