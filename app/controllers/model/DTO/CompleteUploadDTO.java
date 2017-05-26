package controllers.model.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CompleteMultipartUpload")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompleteUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Part")
    private List<PartDTO> parts = new ArrayList<PartDTO>();

    public List<PartDTO> getParts() {
        return parts;
    }

    public void setParts(List<PartDTO> parts) {
        this.parts = parts;
    }
}
