package web.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraApis {
    public static String auth = "ZHViZXl2ZDpS";
    public static String authorisation = "Basic"+auth;
    //This code is commented to generate JIRA username and password in encoded format
    /*public static void main(String[] args){
        String userName = "";
        String password="";
        String auth = new String(Base64.getEncoder().encodeToString((userName+":"+password).getBytes()));
        System.out.println(auth);
    }*/

    public static void cleanFoldersFromCycle(String projectId,String cycleId,String versionId,int limit, int offSet){
        try{
            if(getFolderListOfCycle(projectId,cycleId,versionId,limit,offSet).length>=1){
                for(String folderId : getFolderListOfCycle(projectId,cycleId,versionId,limit,offSet)){
                    deleteFolderUnderCycle(folderId,projectId,versionId,cycleId);
                }
            }
        }
        catch (Exception e){
            System.out.println("Folder is empty");
        }
    }
    public static String getCycleNameFromCycleId(String cycleId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../cycle/"+cycleId);
        System.out.println(response.asString());
        return response.getBody().jsonPath().get("name");
    }
    public static int getProjectIdFromCycleId(String cycleId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../cycle/"+cycleId);
        System.out.println(response.asString());
        return response.getBody().jsonPath().get("projectId");
    }
    public static int getVersionIdFromCycleId(String cycleId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../cycle/"+cycleId);
        System.out.println(response.asString());
        return response.getBody().jsonPath().get("versionId");
    }
    public static String getDefectResponseAsString(String defectNumber){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../issue/"+defectNumber+"?fields=status");
        System.out.println(response.asString());
        return response.asString();
    }
    public static String getTestIdFromJiraId(String jiraId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../issue/"+jiraId+"?fields=status");
        System.out.println(response.asString());
        System.out.println(response.getBody().jsonPath().get("id").toString());
        return response.getBody().jsonPath().get("id").toString();
    }
    public static String getJiraDefectStatus(String defectNumber){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../issue/"+defectNumber+"?fields=status");
        System.out.println(response.asString());
        System.out.println(response.getBody().jsonPath().get("fields.status.name").toString());
        return response.getBody().jsonPath().get("fields.status.name").toString();
    }
    public static void getProjectVersionId(String projectId) {
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type", "application/json")
                .header("Authorization", authorisation).get("https://jira-dts../project/" + projectId + "/versions");
        System.out.println(response.asString());
    }
    public static Response getTestCycleId(String projectId,String versionId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../projectId/"+projectId+"&versionId"+versionId);
        System.out.println(response.asString());
        return response;
    }
    public static String getTestCycleIdByCycleName(String cycleName,String projectId, String versionId){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../projectId/"+projectId+"&versionId"+versionId);
        System.out.println(response.asString());
        String cycleId="";
        if(response.asString().contains(cycleName)){
            String[] split1=response.asString().split(cycleName);
            String[] split2=split1[0].split("},\"");
            String cycleIdString = split2[split2.length-2];
            if(!cycleIdString.contains("projectKey"))
                cycleIdString=split2[split2.length-3];
            String[] cycleIdString2 = cycleIdString.split("\"");
            cycleId=cycleIdString2[0];
        }
        else{
            System.out.println("Cycle is not available");
            cycleId="Cycle is not available";
        }
        return cycleId;
    }
    public static String getCycleIdBySearchingCycleName(String projectId, String cycleName){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../projectId/"+projectId+"&searchQuery"+cycleName);
        System.out.println(response.asString());
        try{
            return response.getBody().jsonPath().get("cycles[0].id").toString();
        }
        catch (NullPointerException e){
            return "Cycle is not available";
        }
    }
    public static String generateExecutionId(String issueId,String projectId,String versionId,String cycleId){
        String body = "{\"issueId\":"+issueId+",\"versionId\":"+versionId+",\"cycleId\":"+cycleId+",\"projectId\":"+projectId+"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/execution");
        System.out.println(response.asString());
        String[] data = response.asString().split("\"");
        String executionId=data[1];
        return executionId;
    }
    public static String generateExecutionIdForTestInFolder(String issueId,String projectId,String versionId,String cycleId,String folderId){
        String body = "{\"issueId\":"+issueId+",\"versionId\":"+versionId+",\"cycleId\":"+cycleId+",\"projectId\":"+projectId+",\"folderId\":"+folderId+"\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/execution");
        System.out.println(response.asString());
        String[] data = response.asString().split("\"");
        String executionId=data[1];
        return executionId;
    }
    public static void executeTestFail(String issueId){
        String body="{\"status\":\"2\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void executeTestPass(String issueId){
        String body="{\"status\":\"1\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void executeTestWIP(String issueId){
        String body="{\"status\":\"3\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void executeTestBlocked(String issueId){
        String body="{\"status\":\"4\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void executeTestDescoped(String issueId){
        String body="{\"status\":\"5\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void executeTestUnexecuted(String issueId){
        String body="{\"status\":\"-1\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+issueId+"/execute");
        System.out.println(response.asString());
    }
    public static void attachDefectWithTest(String defectId, String executionId){
        String body="{\"defectList\":[\""+defectId+"\"],\"updateDefectList\":\"true\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).put("https://jira-dts../projectId/latest/execution/"+executionId+"/execute");
        System.out.println(response.asString());
    }

    public static String createNewCycleId(String cycleName, String projectId,String versionId){
        String body="{\"cloneCycleId\":\",\"name\":\""+cycleName+"\",\"build\":\"\",\"environment\":\"\",\"description\":\"Create cycle with sprint\",\"startDate\":\"\",\"endDate\":\"\",\"projectId\":\""+projectId+"\",\"versionId\":"+versionId+"\"}";
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/cycle");
        System.out.println(response.asString());
        System.out.println(response.getBody().jsonPath().get("id").toString());
        return response.getBody().jsonPath().get("id").toString();
    }
    public static void deleteCycleId(String Id){
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).delete("https://jira-dts../projectId/latest/cycle");
        System.out.println(response.asString());
        System.out.println("Cycle Deleteed Successfully");
    }
    public static void addTestToCycle(String testId,String cycleId,String versionId,String projectId){
        String body = "{"+
                ""+
                "\"versionId\":"+versionId+"\","+
                "\"method\":\"1\","+
                "\"cycleId\":"+cycleId+"\","+
                "\"issues\":[\""+testId+"\","+
                "\"projectId\":[\""+projectId+"\","+
                "\"folderId\":\"\""+
                "}";

        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/execution");
        System.out.println(response.asString());
    }
    public static void addTestToCycleFolder(String testId,String folderId,String cycleId,String versionId,String projectId){
        String body = "{"+
                ""+
                "\"versionId\":"+versionId+"\","+
                "\"method\":\"1\","+
                "\"cycleId\":"+cycleId+"\","+
                "\"issues\":[\""+testId+"\","+
                "\"projectId\":[\""+projectId+"\","+
                "\"folderId\":\""+folderId+"\""+
                "}";

        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/addTestToCycle");
        System.out.println(response.asString());
    }
    public static String createFolderUnderCycle(String testFolder,String folderDescription,String cycleId,String projectId,String versionId){
        String body = "{"+
                ""+
                "\"cycleId\":\""+cycleId+"\""+
                "\"name\":"+testFolder+"\","+
                "\"description\":[\""+folderDescription+"\","+
                "\"projectId\":[\""+projectId+"\","+
                "\"versionId\":"+versionId+"\","+
                "}";

        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(body).post("https://jira-dts../projectId/latest/create");
        System.out.println(response.asString());
        return response.getBody().jsonPath().get("id").toString();
    }
    public static void deleteFolderUnderCycle(String folderId,String projectId,String versionId,String cycleId){

        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).delete("https://jira-dts../projectId/latest/addTestToCycle"+folderId.trim()+"?project"+projectId);
        System.out.println(response.asString());
    }
    public static String[] getFolderListOfCycle(String projectId,String cycleId,String versionId,int limit, int offSet){
        String[] finalFolderList=null;
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../projectId/latest/addTestToCycle"+cycleId+"?project"+projectId);
        System.out.println(response.asString());
        if(response.asString().length()==2 || response.asString().equalsIgnoreCase("[]")){
            System.out.println("folder is not available under the cycle:"+cycleId);
        }
        else{
            System.out.println(CommonUtils.getJsonPath(response,"folderId"));
            String list = CommonUtils.getJsonPath(response,"folderId").replaceAll("\\[","").replaceAll("\\]","");
            String[] folderList = list.split(",");
            finalFolderList = folderList;
        }
        return finalFolderList;
    }

    public static Map<String,String> getFolderNameListOfCycle(String projectId,String cycleId,String versionId,int limit, int offSet){
        Map<String,String> finalFolderList = new HashMap<>();
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).get("https://jira-dts../projectId/latest/addTestToCycle"+cycleId+"?project"+projectId);
        System.out.println(response.asString());
        if(response.asString().length()==2 || response.asString().equalsIgnoreCase("[]")){
            System.out.println("folder is not available under the cycle:"+cycleId);
        }
        else{
            System.out.println(CommonUtils.getJsonPath(response,"folderId"));
            String list = CommonUtils.getJsonPath(response,"folderId").replaceAll("\\[","").replaceAll("\\]","");
            String list2 = CommonUtils.getJsonPath(response,"folderName").replaceAll("\\[","").replaceAll("\\]","");
            String[] folderIdList = list.split(",");
            String[] folderNameList = list2.split(",");
            for(int i=0;i<folderIdList.length;i++){
                finalFolderList.put(folderIdList[i],folderNameList[i]);
            }
        }
        return finalFolderList;
    }
    public static void updateBulkStatus(List<String> executionIdList){
        String part1 = "{ \"executions\":[";
        String part2 ="],\"status\":\"4\"}";
        String jsonString="";
        for(String list:executionIdList){
            String execIdString = "\""+list+"\""+",";
            jsonString = jsonString+part1+execIdString+part2;
        }
        Response response = RestAssured.given().relaxedHTTPSValidation().header("Content-Type","application/json")
                .header("Authorization",authorisation).body(jsonString).post("https://jira-dts../projectId/latest/addTestToCycd");
        System.out.println(response.asString());

    }
}
