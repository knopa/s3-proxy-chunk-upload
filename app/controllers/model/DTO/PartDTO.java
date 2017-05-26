package controllers.model.DTO;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Part")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartDTO implements Serializable {

    public PartDTO() {}

    public PartDTO(String partNumber) {
        this.partNumber = partNumber;
        this.eTag = this.partNumber;
    }

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "PartNumber")
    private String partNumber;

    @XmlElement(name = "ETag")
    private String eTag;

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
}
