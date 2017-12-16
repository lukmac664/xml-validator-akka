package com.xml.validator.akka.xmlvalidatorakka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationRequest;
import org.apache.cxf.helpers.IOUtils;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;


public class FactorialFrontend extends AbstractActor {
    final int upToN;
    final boolean repeat;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    ActorRef xmlSplitWorkerRouter = getContext().actorOf(FromConfig.getInstance().props(),
            "xmlSplitWorkerRouter");

    public FactorialFrontend(int upToN, boolean repeat) {
        this.upToN = upToN;
        this.repeat = repeat;
    }

    @Override
    public void preStart() {
        sendJobs();
        getContext().setReceiveTimeout(Duration.create(20, TimeUnit.SECONDS));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Integer.class, result -> {
                    sendJobs();
                })
                .match(ReceiveTimeout.class, message -> {
                    log.info("Timeout");
                    sendJobs();
                })
                .build();
    }

    void sendJobs() {

        long startTime = System.currentTimeMillis();

        log.info("Starting batch of factorials up to [{}]", upToN);
        ArrayList<CompletionStage<Object>> futureArrayList = new ArrayList<>();
        Timeout timeout = new Timeout(Duration.create(20, "seconds"));
        ValidationRequest validationRequest = new ValidationRequest();
        byte[] schema = prepareSchemaAsByteArray("D:\\ship.xsd");
        byte[] xml = prepareSchemaAsByteArray("D:\\shiporder.xml");
        validationRequest.setXmlFile(xml);
        validationRequest.setXsdFile(schema);

        for (int n = 1; n <= upToN; n++) {
            futureArrayList.add(PatternsCS.ask(xmlSplitWorkerRouter, validationRequest, timeout));
        }

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        try {
            for (CompletionStage<Object> future : futureArrayList) {
                future.whenComplete((r, e) -> {
                    queue.add(((FactorialResult) r).resultXml);
                    log.info("Received response for n: " + " = " + ((FactorialResult) r).resultXml);
                });
            }


            while (queue.size() + 1 < upToN) {
                //throwExceptionIfTimeoutOccurred(startTime);

                Thread.sleep(50);
            }
            long time = System.currentTimeMillis() - startTime;
            System.out.println("Processing took: " + time);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private byte[] prepareSchemaAsByteArray(String schemaLocation) {
        try (FileInputStream fis = new FileInputStream(new File(schemaLocation))) {
            return IOUtils.readBytesFromStream(fis);
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    private void throwExceptionIfTimeoutOccurred(long startTime) {
        if ((System.currentTimeMillis() - startTime) > 120000) {
            throw new RuntimeException("Timeout exception during preparing data for download");
        }
    }

}
