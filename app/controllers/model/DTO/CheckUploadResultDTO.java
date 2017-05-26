package controllers.model.DTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "CheckMultipartUploadResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckUploadResultDTO {
    
    @XmlElement(name = "Location")
    private String location;

    @XmlElement(name = "StatusCode")
    private Integer statusCode;
    
    public CheckUploadResultDTO() {}

    public CheckUploadResultDTO(String location, Integer statusCode) {
        super();
        this.location = location;
        this.statusCode = statusCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    

}
