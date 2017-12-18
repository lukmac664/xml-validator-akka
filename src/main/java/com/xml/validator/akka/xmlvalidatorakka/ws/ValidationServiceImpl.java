package com.xml.validator.akka.xmlvalidatorakka.ws;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.routing.FromConfig;
import akka.util.Timeout;
import com.xml.validator.akka.xmlvalidatorakka.FactorialResult;
import org.springframework.stereotype.Service;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.PatternsCS.ask;


@Service
public class ValidationServiceImpl implements ValidationService {

    ActorSystem actorSystem;
    ActorRef xmlRouter;
    ActorRef validationWorkerRouter;

    @Override
    public ValidationResponse validateXml(ValidationRequests validationRequests) {
       // boolean isValid = internalValidationService.validate(validationRequest);
       // internalValidationService.mapToEntityAndPresist(validationRequest, isValid);

        Timeout timeout = new Timeout(Duration.create(200, "seconds"));
        //CompletionStage<Object> resultFuture = ask(xmlRouter, validationRequests, timeout);
       // xmlRouter.tell(validationRequests, ActorRef.noSender());
        ValidationResult validationResult = ValidationResult.VALID;

        ValidationRequests request = new ValidationRequests();

        List<ValidationRequest> validationReq = new ArrayList<>();

        Path xmlPath = Paths.get("D:\\test\\standard\\data.xml");
        byte[] xsd = null;
        Path xsdPath = Paths.get("D:\\test\\standard\\data.xsd");
        byte[] xml = null;
        try {
            xsd = Files.readAllBytes(xsdPath);
            xml = Files.readAllBytes(xmlPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.setMessageId("1");
        request.setValidationRequests(validationReq);

        for (int i=0; i<=5000; i++) {
            ValidationRequest v = new ValidationRequest();
            v.setXsdName("asfa" + i);
            v.setXmlFile(xml);
            v.setXsdFile(xsd);
            validationReq.add(v);
            //validationWorkerRouter.tell(v, ActorRef.noSender());
        }

        for (int i=0; i<=15; i++) {
            xmlRouter.tell(validationRequests, ActorRef.noSender());
        }




        return prepareResponse(validationResult);
    }

    private ValidationResponse prepareResponse(ValidationResult validationResult) {
        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationResult(validationResult);
        return validationResponse;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        xmlRouter =  actorSystem.actorOf(FromConfig.getInstance().props(),
                "xmlSplitWorkerRouter");
        validationWorkerRouter = actorSystem.actorOf(FromConfig.getInstance().props(),
                "validationWorkerRouter");
    }
}
