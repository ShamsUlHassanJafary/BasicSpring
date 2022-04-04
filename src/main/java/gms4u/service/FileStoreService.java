package gms4u.service;

import gms4u.domain.Garage;
import gms4u.repository.GarageRepository;
import gms4u.service.dto.FileDto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;

@Service
@Transactional
public class FileStoreService {

    private final Logger log = LoggerFactory.getLogger(FileStoreService.class);

    private final GarageRepository garageRepository;

    public FileStoreService(GarageRepository garageRepository) {
        this.garageRepository = garageRepository;
    }

    public FileDto loadFileAsResource(Garage garage) throws FileNotFoundException {
        try {
            Path filePath = getFileStorageLocation(garage).normalize();

            FileDto fileDto = new FileDto();
            if (filePath.getFileName() != null && filePath.toFile().isFile()) {
                fileDto.setFilename(filePath.getFileName().toString());
                fileDto.setData(FileUtils.readFileToByteArray(filePath.toFile()));
                fileDto.setFileType(FilenameUtils.getExtension(fileDto.getFilename()));
            }
            return fileDto;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new FileNotFoundException("Logo not found in path " + garage.getLogoUrl());
        }
    }

    public boolean deleteFile(Garage garage) throws IOException {
        Path filePath = getFileStorageLocation(garage).normalize();
        return !Files.isDirectory(filePath) && Files.deleteIfExists(filePath);
    }

    /**
     * Stores Garage Logo to the file system upon uploading
     *
     * @param garage Garage Entity.
     * @param file   Uploaded file.
     * @throws IOException
     */
    public void storeGarageLogo(Garage garage, MultipartFile file) throws IOException {
        log.info("Storing {} Logo with file name", garage.getBusinessName());
//        If garage has logo
        garage.setLogoUrl(null);//Reset logo url
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            Path fileStorageLocation = getFileStorageLocation(garage);
            String fileName = generateGarageFileName(garage, originalFilename.split("\\.")[1]);

//        Create path if not exists
            if (!Files.exists(fileStorageLocation)) {
                try {
                    Files.createDirectory(fileStorageLocation);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    ex.printStackTrace();
                    throw new FileSystemException("Could not create the directory where the uploaded files will be stored.");
                }
            }

            Path targetLocation = fileStorageLocation.resolve(fileName);
            //       Replace file if exists
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            garage.setLogoUrl(generateGarageFilePath(garage) + fileName);
            garageRepository.save(garage);
        }


    }

    private Path getFileStorageLocation(Garage garage) {
        String filePath = System.getProperty("user.dir") + generateGarageFilePath(garage);
        if (garage.getLogoUrl() != null)
            filePath = System.getProperty("user.dir") + garage.getLogoUrl();

        return Paths.get(filePath)
            .toAbsolutePath().normalize();
    }

    public String generateGarageFilePath(Garage garage) {
        StringBuilder builder = new StringBuilder();
        builder.append("/garages/");
        builder.append(garage.getId());
        builder.append("/");
        return builder.toString();
    }

    private String generateGarageFileName(Garage garage, String fileType) {
        StringBuilder builder = new StringBuilder(garage.getBusinessName().replace(" ", "-").toLowerCase(Locale.ROOT));
        builder.append("-");
        builder.append(garage.getId());
        builder.append(".");
        builder.append(fileType);
        return builder.toString();
    }


}
