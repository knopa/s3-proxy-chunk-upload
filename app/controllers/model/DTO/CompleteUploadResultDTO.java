package controllers.model.DTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CompleteMultipartUploadResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompleteUploadResultDTO {

    @XmlElement(name = "Location")
    private String location;

    @XmlElement(name = "Bucket")
    private String bucket;

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "ETag")
    private String eTag;

    public CompleteUploadResultDTO() {}

    public CompleteUploadResultDTO(String location, String bucket,
            String key) {
        super();
        this.location = location;
        this.bucket = bucket;
        this.key = key;
        this.eTag = key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

}
