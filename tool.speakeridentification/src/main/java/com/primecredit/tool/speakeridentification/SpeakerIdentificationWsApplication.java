package com.primecredit.tool.speakeridentification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpeakerIdentificationWsApplication {

	private static Logger logger = LoggerFactory.getLogger(SpeakerIdentificationWsApplication.class);
	
	public static void main(String[] args) {
		logger.debug("SpeechWsApplication - Start");
		SpringApplication.run(SpeakerIdentificationWsApplication.class, args);
	}
}
