package com.xml.validator.akka.xmlvalidatorakka;

import com.xml.validator.akka.xmlvalidatorakka.validation.ValidationWorkersNode;
import com.xml.validator.akka.xmlvalidatorakka.xmlsplit.XmlSplitWorkersNode;

public class ClusterMain {

    public static void main(String[] args) {
        // starting 3 xmlSplitWorkerRouter nodes and 1 frontend node
        XmlSplitWorkersNode.main(new String[]{"2581"});
        //XmlSplitWorkersNode.main(new String[] { "2583" });
        //XmlSplitWorkersNode.main(new String[] { "2584" });
        ValidationWorkersNode.main(new String[]{"2556"});
        ValidationWorkersNode.main(new String[]{"2557"});
        ValidationWorkersNode.main(new String[]{"2558"});
        //ValidationWorkersNode.main(new String[]{"2562"});
        //ValidationWorkersNode.main(new String[]{"2559"});
        //ValidationWorkersNode.main(new String[]{"2560"});
        // ValidationWorkersNode.main(new String[] { "2561" });
        // ValidationWorkersNode.main(new String[] { "2563" });
        //  ValidationWorkersNode.main(new String[] { "2564" });

        //ValidationWorkersNode.main(new String[0]);
        //FactorialFrontendMain.main(new String[0]);
    }
}
