package com.xml.validator.akka.xmlvalidatorakka.validation;

import java.util.concurrent.CompletableFuture;

import akka.actor.AbstractActor;
import com.xml.validator.akka.xmlvalidatorakka.FactorialResult;
import com.xml.validator.akka.xmlvalidatorakka.SchemaValidator;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationRequest;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationResult;

import static akka.pattern.PatternsCS.pipe;

public class ValidationWorker extends AbstractActor {
    SchemaValidator schemaValidator = new SchemaValidator();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValidationRequest.class, n -> {

                    CompletableFuture<ValidationResult> result =
                            CompletableFuture.supplyAsync(() -> validate(n));
                                    //.thenApply((result2) -> true ? ValidationResult.VALID : ValidationResult.NOT_VALID);

                    pipe(result, getContext().dispatcher()).to(sender());
                })
                .build();
    }

    ValidationResult validate(ValidationRequest n) {
        System.out.println("Validation Worker is working!");
        boolean valid = schemaValidator.validate(n.getXmlFile(), n.getXsdFile());

       // System.out.println("Validation result: " + valid);
        return valid ? ValidationResult.VALID : ValidationResult.NOT_VALID;
    }
}
