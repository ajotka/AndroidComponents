package project.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import project.pojo.ComponentRequestPOJO;

import java.io.IOException;

public interface UploadDownloadControllerInterface {

    public ResponseEntity postComponents(@RequestBody ComponentRequestPOJO components);

    public ResponseEntity getComponents(@PathVariable String fileName, @PathVariable String author) throws IOException;

    public ResponseEntity getComponentsByAuthor(@PathVariable String author);

}
