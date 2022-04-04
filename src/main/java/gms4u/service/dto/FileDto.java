package gms4u.service.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class FileDto implements Serializable {

    private String filename;

    private byte[] data;

    private String path;

    private String fileType;

    public FileDto() {
    }

    public FileDto(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDto fileDto = (FileDto) o;
        return Objects.equals(filename, fileDto.filename) && Arrays.equals(data, fileDto.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(filename);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
