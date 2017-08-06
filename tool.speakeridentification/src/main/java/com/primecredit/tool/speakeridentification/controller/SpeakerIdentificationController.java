package com.primecredit.tool.speakeridentification.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primecredit.tool.common.domain.DiarizationSpeech;
import com.primecredit.tool.common.util.FileUtils;
import com.primecredit.tool.common.wsobject.request.DiarizationRequest;
import com.primecredit.tool.common.wsobject.response.DiarizationResponse;
import com.primecredit.tool.speakeridentification.services.SpeakerIdentificationService;

@RestController
@RequestMapping("/SpeakerIdentification")
public class SpeakerIdentificationController {

	private static Logger logger = LoggerFactory.getLogger(SpeakerIdentificationService.class);
	
	@Autowired
	private SpeakerIdentificationService speakerIdentificationService;
	
	@Value("${temp.path}")
	private String tempPath;

	@RequestMapping(value = "/diarization", method = RequestMethod.POST)
	public DiarizationResponse diarization(@RequestBody DiarizationRequest request) {
		DiarizationResponse response = new DiarizationResponse();
		response.setClientMachineId(request.getClientMachineId());
		response.setMillisecond(new Date().getTime());
		
		StringBuilder sbTempFileName = new StringBuilder();
		sbTempFileName.append(request.getClientMachineId() .replaceAll("[^\\p{Alpha}\\p{Digit}]+",""));
		sbTempFileName.append("_");
		sbTempFileName.append(request.getMillisecond());
		sbTempFileName.append(".wav");
		
		try {
			File sourceFile = FileUtils.generateFile(tempPath, sbTempFileName.toString(), request.getFileData());
			List<DiarizationSpeech> dsList = speakerIdentificationService.diarization(sourceFile);
			response.setDsList(dsList);
			sourceFile.delete();
		} catch (Exception e) {
			logger.error(e.toString());
			//e.printStackTrace();
		}
		
		
		return response;

	}

}
