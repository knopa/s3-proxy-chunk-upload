package controllers.model.DTO;

import global.GlobalParams;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDTO {
    
    @XmlElement(name = "Code")
    private String code;

    @XmlElement(name = "Message")
    private String message;
    
    @XmlElement(name = "Resource")
    private String resource;
    
    @XmlElement(name = "RequestId")
    private String requestId;
    
    public ErrorDTO() {}
    
    public ErrorDTO(String code, String message, String requestId) {
        super();
        this.code = code;
        this.message = message;
        this.resource = GlobalParams.AWS_S3_BUCKET_PATH + requestId;
        this.requestId = requestId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
