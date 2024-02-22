package hei.school.sarisary.endpoint;

import hei.school.sarisary.service.event.PojaSarisaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PojaSarisaryController {

    private final PojaSarisaryService pojaSarisaryService;

    @Autowired
    public PojaSarisaryController(PojaSarisaryService pojaSarisaryService) {
        this.pojaSarisaryService = pojaSarisaryService;
    }

    @PutMapping(value = "/black-and-white/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> processImage(@PathVariable("id") String id, @RequestPart("image") MultipartFile image)
            throws IOException {
        String presignedUrl = pojaSarisaryService.processImage(id, image);
        return ResponseEntity.ok(presignedUrl);
    }
}
