package com.xml.validator.akka.xmlvalidatorakka.validation;

import java.time.LocalDateTime;
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

/*                    CompletableFuture<ValidationResult> result =
                            CompletableFuture.supplyAsync(() -> validate(n));
                                    //.thenApply((result2) -> true ? ValidationResult.VALID : ValidationResult.NOT_VALID);

                    pipe(result, getContext().dispatcher()).to(sender());*/
                validate(n);
                })
                .build();
    }

    ValidationResult validate(ValidationRequest n) {
        System.out.println("Validation Worker is working!" + getSelf().path().name());
        boolean valid = schemaValidator.validate(n.getXmlFile(), n.getXsdFile());

        System.out.println("Validation result: " + valid + " " + LocalDateTime.now());
        return valid ? ValidationResult.VALID : ValidationResult.NOT_VALID;
    }
}
