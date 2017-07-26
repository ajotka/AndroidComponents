package project.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.pojo.ComponentListPOJO;
import project.pojo.ComponentRequestPOJO;
import project.pojo.ResponsePOJO;
import project.utility.JSONFilesParser;

import java.io.IOException;

@RestController
@RequestMapping("/service")
public class UploadDownloadController implements UploadDownloadControllerInterface {

    private static final Logger logger = LoggerFactory.getLogger(UploadDownloadController.class);

    @Override
    //@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity postComponents(@RequestBody ComponentRequestPOJO components){
        ComponentListPOJO toSave = new ComponentListPOJO();
        try {
            toSave.setAuthor(components.getAuthor());
            toSave.setApplications(components.getApplications());
        }catch (Exception e){
            return new ResponseEntity(new ResponsePOJO("Negtive", "Data proccessing error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        int operationResult = JSONFilesParser.writeToFile(toSave);
        switch (operationResult) {
            case 0 :{
                return new ResponseEntity(new ResponsePOJO("Positive", "Component list uploaded"), HttpStatus.OK);
            }
            case -1 :{
                return new ResponseEntity(new ResponsePOJO("Negtive", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            case -2: {
                return new ResponseEntity(new ResponsePOJO("Negtive", "Method not allowed"), HttpStatus.METHOD_NOT_ALLOWED);
            }
            case -4:{
                return new ResponseEntity(new ResponsePOJO("Negtive", "Something is missing in Jason or file write problem"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            default:{
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    @RequestMapping(value = "/download/{author}/{fileName}", method = RequestMethod.GET)
    public ResponseEntity getComponents(@PathVariable String fileName, @PathVariable String author) throws IOException {
        JSONObject response = null;
        try {
            response = JSONFilesParser.readFromFile(fileName, author);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity(new ResponsePOJO("Negtive", "Data proccessing error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/download/{author}", method = RequestMethod.GET)
    public ResponseEntity getComponentsByAuthor(@PathVariable String author){
        try {
            return new ResponseEntity(JSONFilesParser.getAllFilesByAuthor(author), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/debug", method = RequestMethod.POST)
    public void postDebug(@RequestBody String object){
        logger.info(object);
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity giveMeEmpty(){
        return new ResponseEntity(new ComponentRequestPOJO(), HttpStatus.OK);
    }


}
