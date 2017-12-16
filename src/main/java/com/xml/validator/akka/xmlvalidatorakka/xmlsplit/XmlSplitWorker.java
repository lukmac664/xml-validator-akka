package com.xml.validator.akka.xmlvalidatorakka.xmlsplit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.PatternsCS;
import akka.routing.FromConfig;
import akka.util.Timeout;
import com.xml.validator.akka.xmlvalidatorakka.FactorialResult;
import com.xml.validator.akka.xmlvalidatorakka.SchemaValidator;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationRequest;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationResult;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import scala.concurrent.duration.Duration;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

import static akka.pattern.PatternsCS.pipe;

public class XmlSplitWorker extends AbstractActor {
    ActorRef validationWorkerRouter = getContext().actorOf(FromConfig.getInstance().props(),
           "validationWorkerRouter");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValidationRequest.class, n -> {

                    CompletableFuture<Object> result =
                            CompletableFuture.supplyAsync(() -> splitXmlAndValidate(n));
                                   // .thenApply((result2) -> new FactorialResult("testfdsfs", "ttestst"));

                    pipe(result, getContext().dispatcher()).to(sender());

                })
                .build();
    }

    ValidationResult splitXmlAndValidate(ValidationRequest validationRequest) {
        //split xml
        // send to validation
        System.out.println("XmlSplit Worker is working!" + "Result for = " + " " + self().path().address() + self().path().name());
        Timeout timeout = new Timeout(Duration.create(20, "seconds"));

       // XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
       XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(new ByteArrayInputStream(validationRequest.getXsdFile())));
       // schema.getElements().values().forEach(s -> s.);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
           doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(validationRequest.getXsdFile()));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        SchemaTypeSystem sts = null;
        try {
            sts = XmlBeans.compileXsd(new XmlObject[] {
                    XmlObject.Factory.parse(new ByteArrayInputStream(validationRequest.getXsdFile())) }, XmlBeans.getBuiltinTypeSystem(), null);
        } catch (XmlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // send parts in loop
        // gather results from CompletableStages
        // return result
        try {
            ValidationResult validationResult = (ValidationResult) PatternsCS.ask(validationWorkerRouter, validationRequest, timeout).toCompletableFuture().get(20, TimeUnit.SECONDS);
            return validationResult;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
