package hei.school.sarisary.service.event;

import hei.school.sarisary.file.BucketComponent;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PojaSarisaryService {

  private final BucketComponent bucketComponent;
  private final String directory = "images/";

  @Autowired
  public PojaSarisaryService(BucketComponent bucketComponent) {
    this.bucketComponent = bucketComponent;
  }

  public String processImage(String id, MultipartFile image) throws IOException {
    String suffix = "." + FilenameUtils.getExtension(image.getOriginalFilename());
    String prefixOriginal = id + "-original";
    String prefixTransformed = id + "-grayscale";
    String bucketKeyOriginal = directory + prefixOriginal + suffix;
    String bucketKeyTransformed = directory + prefixTransformed + suffix;

    File toUpload = File.createTempFile(prefixOriginal, suffix);
    image.transferTo(toUpload);
    File grayscaled = this.toGrayscale(id, toUpload);

    bucketComponent.upload(toUpload, bucketKeyOriginal);
    bucketComponent.upload(grayscaled, bucketKeyTransformed);

    return bucketComponent.presign(bucketKeyTransformed, Duration.ofMinutes(30)).toString();
  }

  private File toGrayscale(String name, File original) throws IOException {
    BufferedImage image = ImageIO.read(original);
    int width = image.getWidth();
    int height = image.getHeight();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Color C = new Color(image.getRGB(j, i));
        int r = (int) (C.getRed() * 0.299);
        int g = (int) (C.getGreen() * 0.587);
        int b = (int) (C.getBlue() * 0.114);
        Color newColor = new Color(r + g + b, r + g + b, r + g + b);
        image.setRGB(j, i, newColor.getRGB());
      }
    }

    String extension = FilenameUtils.getExtension(original.getName());
    File transformed = File.createTempFile(name + "-grayscale", "." + extension);
    ImageIO.write(image, extension, transformed);
    return transformed;
  }
}
