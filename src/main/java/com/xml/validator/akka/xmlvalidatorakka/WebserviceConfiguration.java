package com.xml.validator.akka.xmlvalidatorakka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.xml.validator.akka.xmlvalidatorakka.ws.ValidationServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import javax.xml.ws.Endpoint;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration
@EnableWs
@Configuration
public class WebserviceConfiguration {
    @Autowired
    private Bus bus;

    @Bean
    public Endpoint endpoint(ValidationServiceImpl validationServiceImpl) {
        EndpointImpl endpoint = new EndpointImpl(bus, validationServiceImpl);
        endpoint.publish("/validation");
        return endpoint;
    }

    @Bean
    public ValidationServiceImpl validationServiceImpl() {
        ActorSystem actorSystem = prepareAkkaSystemWithWebservice();
        ValidationServiceImpl validationService = new ValidationServiceImpl();
        validationService.setActorSystem(actorSystem);
        return validationService;
    }

    public static ActorSystem prepareAkkaSystemWithoutWebservice() {
        final int numberOfValidations = 10;

        final Config config = ConfigFactory.parseString(
                "akka.cluster.roles = [frontend]").withFallback(
                ConfigFactory.load("validation.conf"));

        final ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.log().info(
                "Factorials will start when 2 validationWorkerRouter members in the cluster.");
        Cluster.get(system).registerOnMemberUp(new Runnable() {
            @Override
            public void run() {
                system.actorOf(Props.create(FactorialFrontend.class, numberOfValidations, true),
                        "factorialFrontend");
            }
        });

        Cluster.get(system).registerOnMemberRemoved(new Runnable() {
            @Override
            public void run() {
                // exit JVM when ActorSystem has been terminated
                final Runnable exit = new Runnable() {
                    @Override public void run() {
                        System.exit(0);
                    }
                };
                system.registerOnTermination(exit);

                // shut down ActorSystem
                system.terminate();

                // In case ActorSystem shutdown takes longer than 10 seconds,
                // exit the JVM forcefully anyway.
                // We must spawn a separate thread to not block current thread,
                // since that would have blocked the shutdown of the ActorSystem.
                new Thread() {
                    @Override public void run(){
                        try {
                            Await.ready(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
                        } catch (Exception e) {
                            System.exit(-1);
                        }

                    }
                }.start();
            }
        });

        return system;
    }

    public static ActorSystem prepareAkkaSystemWithWebservice() {
        final int numberOfValidations = 10;

        final Config config = ConfigFactory.parseString(
                "akka.cluster.roles = [frontend]").withFallback(
                ConfigFactory.load("validation.conf"));

        final ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.log().info(
                "Factorials will start when 2 validationWorkerRouter members in the cluster.");
       /* Cluster.get(system).registerOnMemberUp(new Runnable() {
            @Override
            public void run() {
                system.actorOf(Props.create(FactorialFrontend.class, numberOfValidations, true),
                        "factorialFrontend");
            }
        });*/

/*        Cluster.get(system).registerOnMemberRemoved(new Runnable() {
            @Override
            public void run() {
                // exit JVM when ActorSystem has been terminated
                final Runnable exit = new Runnable() {
                    @Override public void run() {
                        System.exit(0);
                    }
                };
                system.registerOnTermination(exit);

                // shut down ActorSystem
                system.terminate();

                // In case ActorSystem shutdown takes longer than 10 seconds,
                // exit the JVM forcefully anyway.
                // We must spawn a separate thread to not block current thread,
                // since that would have blocked the shutdown of the ActorSystem.
                new Thread() {
                    @Override public void run(){
                        try {
                            Await.ready(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
                        } catch (Exception e) {
                            System.exit(-1);
                        }

                    }
                }.start();
            }
        });*/

        return system;
    }
}
