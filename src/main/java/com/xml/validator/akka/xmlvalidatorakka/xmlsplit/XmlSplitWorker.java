package com.xml.validator.akka.xmlvalidatorakka.xmlsplit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.PatternsCS;
import akka.routing.FromConfig;
import akka.util.Timeout;
import com.xml.validator.akka.xmlvalidatorakka.FactorialResult;
import com.xml.validator.akka.xmlvalidatorakka.SchemaValidator;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationRequest;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationRequests;
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
                .match(ValidationRequests.class, n -> {

/*                    CompletableFuture<Object> result =
                            CompletableFuture.supplyAsync(() -> splitXmlAndValidate(n));
                                   // .thenApply((result2) -> new FactorialResult("testfdsfs", "ttestst"));*/
                    splitXmlAndValidate(n);
                    //pipe(result, getContext().dispatcher()).to(sender());

                })
                .build();
    }

    void splitXmlAndValidate(ValidationRequests validationRequests) {
        //split xml
        // send to validation
        System.out.println("XmlSplit Worker is working!" + "Result for = " + " " + self().path().address() + self().path().name());
        Timeout timeout = new Timeout(Duration.create(20, "seconds"));

        validationRequests
                .getValidationRequests()
                .stream()
                .forEach(vr -> validationWorkerRouter.tell(vr, getSelf()));


/*        // send parts in loop
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
        return null;*/
    }
}
