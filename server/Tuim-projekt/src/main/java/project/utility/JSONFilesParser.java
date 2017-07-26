package project.utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import project.pojo.ComponentListPOJO;
import project.pojo.ComponentPOJO;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class JSONFilesParser {
    private JSONFilesParser(){}

    public static int writeToFile(ComponentListPOJO componentList) {
        JSONObject jsonObj = new JSONObject();
        //Komponenty
        JSONArray componentsToArray = new JSONArray();
        try {
            for (ComponentPOJO component : componentList.getApplications()) {
                JSONObject objToArray = new JSONObject();
                objToArray.put("name", component.getName());
                objToArray.put("packageName", component.getPackageName());
                objToArray.put("versionNumber", component.getVersionNumber());
                objToArray.put("versionName", component.getVersionName());
                objToArray.put("activity", component.getActivity());
                Date dateTofile = new Date(component.getInstalledData().getTime());
                SimpleDateFormat outputDf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
                objToArray.put("installedData", outputDf.format(dateTofile));
                dateTofile = new Date(component.getModifiedData().getTime());
                objToArray.put("modifiedData", outputDf.format(dateTofile));
                JSONArray providers = new JSONArray();
                if(component.getContentProviders() != null) {
                    for (String provider : component.getContentProviders()) {
                        providers.add(provider);
                    }
                }else{
                    providers.add("null");
                }
                objToArray.put("contentProviders", providers);
                JSONArray permissions = new JSONArray();
                if(component.getPermissions() != null) {
                    for (String permission : component.getPermissions()) {
                        permissions.add(permission);
                    }
                }else{
                    permissions.add("null");
                }
                objToArray.put("permissions", permissions);
                componentsToArray.add(objToArray);
            }
            jsonObj.put("components", componentsToArray);
            jsonObj.put("id", componentList.getId());
            jsonObj.put("author", componentList.getAuthor());
            //Data
            Date dateTofile = new Date(componentList.getUploadDate().getTime());
            SimpleDateFormat outputDf = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
            jsonObj.put("uploadDate", outputDf.format(dateTofile));
        }catch(NullPointerException e){
            e.printStackTrace();
            return -4;
        }catch (Exception e){
            e.printStackTrace();
        }
        //Tworzenie folderu parent
        String parentPath = new File(".").getAbsolutePath().concat("\\Files");
        System.out.println(parentPath);
        File parentDirectory = new File(parentPath);
        if(!parentDirectory.exists()){
            System.out.println("Creating parent dir: " + parentPath);
            try{
                parentDirectory.mkdir();
            }
            catch(SecurityException se){
                System.out.println("Security - no access");
                return -2;
            }
            System.out.println("Parent dir created");
        }
        //Tworzenie folderu dla usera
        String path = parentPath.concat("\\"+componentList.getAuthor());
        System.out.println(path);
        File directory = new File(path);
        if(!directory.exists()){
            System.out.println("Creating dir: " + path);
            try{
                directory.mkdir();
            }
            catch(SecurityException se){
                System.out.println("Security - no access");
                return -2;
            }
            System.out.println("dir created");
        }
        //Zapis na dysku
        try(FileWriter file = new FileWriter(path+"\\"+componentList.getAuthor()+
                "_"+componentList.getUploadDate().getTime())){
            file.write(jsonObj.toJSONString());
            file.flush();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Write gone wrong");
            return -1;
        }
    return 0;
    }

    public static JSONObject readFromFile(String fileName, String author) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        String path = new File(".").getAbsolutePath();
        path = path.substring(0, path.length()-2);
        path = path.concat("\\Files\\").concat(author).concat("\\".concat(fileName));
        Object objectToRead = parser.parse(new FileReader(path));
        JSONObject jsonObject = (JSONObject) objectToRead;
        System.out.println(jsonObject);
        return jsonObject;
    }

    public static ArrayList<JSONObject> getAllFilesByAuthor(String author) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        String path = new File(".").getAbsolutePath();
        path = path.substring(0, path.length()-2);
        path = path.concat("\\Files\\").concat(author);
        File directory = new File(path);
        File[] listOfFiles = directory.listFiles();
        ArrayList<JSONObject> listOfJSONS = new ArrayList<>();
        for (File file : listOfFiles) {
            listOfJSONS.add((readFromFile(file.getName(), author)));
        }
        return listOfJSONS;
    }
}
