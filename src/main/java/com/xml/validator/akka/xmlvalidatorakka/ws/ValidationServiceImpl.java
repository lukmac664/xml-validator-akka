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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.PatternsCS.ask;


@Service
public class ValidationServiceImpl implements ValidationService {

    ActorSystem actorSystem;
    ActorRef xmlRouter;

    @Override
    public ValidationResponse validateXml(ValidationRequest validationRequest) {
       // boolean isValid = internalValidationService.validate(validationRequest);
       // internalValidationService.mapToEntityAndPresist(validationRequest, isValid);

        Timeout timeout = new Timeout(Duration.create(200, "seconds"));
        CompletionStage<Object> resultFuture = ask(xmlRouter, validationRequest, timeout);
        ValidationResult validationResult = ValidationResult.NOT_VALID;
        try {
            validationResult = (ValidationResult) resultFuture.toCompletableFuture().get(200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
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
    }
}
