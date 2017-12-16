package com.xml.validator.akka.xmlvalidatorakka;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SchemaValidator  {

    public boolean validate(byte[] input, byte[] xmlSchema) {
        Schema schema = getSchema(xmlSchema);
        try {
            validateXmlAgainstSchema(input, schema);
        } catch (SAXException e) {
            String message = "Given data did not pass schema validationWorkerRouter.";
            return false;
        }

        return true;
    }

    private void validateXmlAgainstSchema(byte[] xml, Schema schema) throws SAXException {
        try (ByteArrayInputStream byteInStream = new ByteArrayInputStream(xml)) {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(byteInStream));
        } catch (IOException e) {
           //
        }
    }

    private Schema getSchema(byte[] xsd) {
        try (ByteArrayInputStream byteInStream = new ByteArrayInputStream(xsd)) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return factory.newSchema(new StreamSource(byteInStream));
        } catch (SAXException e) {
return null;
        } catch (IOException e) {
            return null;
        }
    }
}