package com.xml.validator.akka.xmlvalidatorakka.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationRequest implements Serializable {

    @XmlElement(required = true)
    private String xsdName;
    @XmlElement(required = true)
    private byte[] xsdFile;
    @XmlElement(required = true)
    private byte[] xmlFile;


    public String getXsdName() {
        return xsdName;
    }

    public void setXsdName(String xsdName) {
        this.xsdName = xsdName;
    }

    public byte[] getXsdFile() {
        return xsdFile;
    }

    public void setXsdFile(byte[] xsdFile) {
        this.xsdFile = xsdFile;
    }

    public byte[] getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(byte[] xmlFile) {
        this.xmlFile = xmlFile;
    }
}
