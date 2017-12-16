package com.xml.validator.akka.xmlvalidatorakka.xmlsplit;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.xml.validator.akka.xmlvalidatorakka.MetricsListener;

public class XmlSplitWorkersNode {
    public static void main(String[] args) {
        // Override the configuration of the port when specified as program argument
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [xmlsplit]")).
                withFallback(ConfigFactory.load("validation"));

        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        system.actorOf(Props.create(XmlSplitWorker.class), "xmlSplitWorker");
        //system.actorOf(Props.create(XmlSplitWorker.class), "xmlSplitWorker2"+ port);
       // system.actorOf(Props.create(XmlSplitWorker.class), "xmlSplitWorker3"+ port);
        //system.actorOf(Props.create(XmlSplitWorker.class), "xmlSplitWorker4"+ port);
       // system.actorOf(Props.create(XmlSplitWorker.class), "xmlSplitWorker5"+ port);
        //system.actorOf(Props.create(ValidationWorker.class), "validationWorker1");

        system.actorOf(Props.create(MetricsListener.class), "metricsListener");

    }
}
