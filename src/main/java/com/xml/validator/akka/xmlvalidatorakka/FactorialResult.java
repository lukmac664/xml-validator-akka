package com.xml.validator.akka.xmlvalidatorakka;

import java.math.BigInteger;
import java.io.Serializable;

public class FactorialResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public final String requestXml;
	public final String resultXml;

	public FactorialResult(String requestXml, String resultXml) {
		this.requestXml = requestXml;
		this.resultXml = resultXml;
	}
}
