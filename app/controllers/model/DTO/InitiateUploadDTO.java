package controllers.model.DTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InitiateMultipartUploadResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class InitiateUploadDTO {

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "Bucket")
    private String bucket;

    @XmlElement(name = "UploadId")
    private String uploadId;

    public InitiateUploadDTO() {}

    public InitiateUploadDTO(String key, String bucket) {
        super();
        this.key = key;
        this.bucket = bucket;
        this.uploadId = this.key;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

}
