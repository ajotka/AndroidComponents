package project.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import project.pojo.ComponentListPOJO;
import project.pojo.ComponentPOJO;
import project.pojo.ComponentRequestPOJO;
import project.service.UploadDownloadController;

import java.util.ArrayList;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(UploadDownloadController.class);
    private static final String REST_SERVICE_URI = "http://localhost:8090/service";

    public void getComponents(String author, String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        ComponentListPOJO response = restTemplate.getForObject(
                REST_SERVICE_URI + "/download/" + author + "/" + fileName,
                ComponentListPOJO.class);
        logger.info(String.valueOf(response.getId()));
        logger.info(response.getAuthor());
        logger.info(response.getApplications().get(0).getName());
    }

    public void getComponentByAuthor(String author){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ArrayList<ComponentListPOJO>> rateResponse =
                restTemplate.exchange(REST_SERVICE_URI + "/download/" + author,
                        HttpMethod.GET, null, new ParameterizedTypeReference<ArrayList<ComponentListPOJO>>() {
                        });
        ArrayList<ComponentListPOJO> components = rateResponse.getBody();
        for(ComponentListPOJO singleList : components) {
            logger.info(String.valueOf(singleList.getId()));
        }
    }

    public void postComponents(){
        ComponentRequestPOJO components = new ComponentRequestPOJO();
        ArrayList<ComponentPOJO> rawComponents = new ArrayList<>();
        ComponentPOJO component = new ComponentPOJO();
        component.setName("Komponent z klienta");
        component.setPackageName("krótki opis");
        rawComponents.add(component);
        components.setAuthor(System.getProperty("user.name"));
        components.setApplications(rawComponents);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.postForEntity(REST_SERVICE_URI + "/upload", components, ComponentRequestPOJO.class );
        logger.info(response.getStatusCode().name());
    }

    public static void main(String[] args){
        Client client = new Client();
        client.getComponents("Filip", "Filip_1497551723135");
        client.getComponentByAuthor("Filip");
        client.postComponents();
    }
}
