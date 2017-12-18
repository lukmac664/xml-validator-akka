package com.xml.validator.akka.xmlvalidatorakka.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationRequests implements Serializable{

    @XmlElement(required = true)
    private String messageId;

    @XmlElement(required = true)
    private List<ValidationRequest> validationRequests;

    public List<ValidationRequest> getValidationRequests() {
        return validationRequests;
    }

    public void setValidationRequests(List<ValidationRequest> validationRequests) {
        this.validationRequests = validationRequests;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
