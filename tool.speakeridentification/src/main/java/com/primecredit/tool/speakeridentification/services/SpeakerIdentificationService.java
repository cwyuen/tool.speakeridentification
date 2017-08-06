package com.primecredit.tool.speakeridentification.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.primecredit.tool.common.domain.DiarizationSpeech;

import edu.cmu.sphinx.speakerid.Segment;
import edu.cmu.sphinx.speakerid.SpeakerCluster;
import edu.cmu.sphinx.speakerid.SpeakerIdentification;

@Service
public class SpeakerIdentificationService {
	
	private static Logger logger = LoggerFactory.getLogger(SpeakerIdentificationService.class);
	
	public List<DiarizationSpeech> diarization(String sourceFileName) {
		return diarization(new File(sourceFileName));
	}
	
	public List<DiarizationSpeech> diarization(File sourceFile) {
		SpeakerIdentification sd = new SpeakerIdentification();
		URL url;
		try {
			url = sourceFile.toURI().toURL();
			ArrayList<SpeakerCluster> clusters = sd.cluster(url.openStream());
			List<DiarizationSpeech> dsList = calculateSpeakerIntervals(clusters, sourceFile.getName());
			return dsList;
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException (Method - public List<DiarizationSpeech> diarization(File sourceFile))");
			logger.error(e.getMessage());				
		} catch (IOException e) {
			logger.error("IOException (Method - public List<DiarizationSpeech> diarization(File sourceFile))");
			logger.error(e.getMessage());		
		}
		
		return null;
	}
	
	private List<DiarizationSpeech> calculateSpeakerIntervals(ArrayList<SpeakerCluster> speakers, String fileName) {
		
		List<DiarizationSpeech> dsList = new ArrayList<DiarizationSpeech>();
		
		int idx = 0;
		for (SpeakerCluster spk : speakers) {
			idx++;
			ArrayList<Segment> segments = spk.getSpeakerIntervals();
			for (Segment seg : segments) {
				
				DiarizationSpeech ds = new DiarizationSpeech();
				ds.setName("Speaker" + idx);
				ds.setSegmentStart(toSecond(seg.getStartTime()));
				ds.setSegmentLen(toSecond(seg.getLength()));
				
				dsList.add(ds);
			}
		}
		
		Collections.sort(dsList);
		
		return dsList;
	}
	
	private int toSecond(int milliseconds) {
		return (int) (Math.round((double) (milliseconds) / 1000));
	}
}