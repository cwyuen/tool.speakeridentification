package com.primecredit.tool.speakeridentification.services;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.primecredit.tool.common.domain.DiarizationSpeech;

import edu.cmu.sphinx.speakerid.Segment;
import edu.cmu.sphinx.speakerid.SpeakerCluster;
import edu.cmu.sphinx.speakerid.SpeakerIdentification;

@Service
public class SpeakerIdentificationService {
	
	public List<DiarizationSpeech> diarization(String sourceFileName) throws Exception {
		return diarization(new File(sourceFileName));
	}
	
	public List<DiarizationSpeech> diarization(File sourceFile) throws Exception {
		SpeakerIdentification sd = new SpeakerIdentification();
		URL url = sourceFile.toURI().toURL();
		ArrayList<SpeakerCluster> clusters = sd.cluster(url.openStream());
		List<DiarizationSpeech> dsList = calculateSpeakerIntervals(clusters, sourceFile.getName());
		return dsList;
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