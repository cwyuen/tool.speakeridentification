package com.primecredit.tool.speakeridentification.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.primecredit.tool.common.domain.DiarizationSpeech;
import com.primecredit.tool.common.util.WavFileHandler;

@Service
public class LiumSpeakerIdentificationService {
	
	@Value("${temp.path}")
	private String workPath;
	
	public List<DiarizationSpeech> diarization(File waveFile) {
		
		WavFileHandler wavFileHandler = WavFileHandler.getInstance();
		
		long now = System.currentTimeMillis();
		String destinationFileName = workPath + "temp_" + now + ".wav";
		String segmentFile = workPath + "temp_" + now + ".seg";

		wavFileHandler.convertULAW2PCM(waveFile.getAbsolutePath(), destinationFileName);
		
		String[] args = new String[] { 
				"--fInputMask=" + destinationFileName, 
				"--sOutputMask=" + segmentFile,
				"--doCEClustering", 
				"Diarization" };
		
		try {
			LiumDiarizationService.startDiarization(args);
			LiumDiarizationService.stopDiarization();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		
		
		try {
			List<DiarizationSpeech> diarizations = parseDiarizationList(segmentFile);
			return diarizations;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	public List<DiarizationSpeech> parseDiarizationList(String segmentFile) throws Exception {
		List<DiarizationSpeech> results = new ArrayList<DiarizationSpeech>();
		
		File file = new File(segmentFile);
		if(!file.exists()) {
			return results; 
		}
		
		
		List<String> fileContent = new ArrayList<String>();

		try (FileReader fr = new FileReader(segmentFile); BufferedReader br = new BufferedReader(fr)) {

			while (br.ready()) {
				fileContent.add(br.readLine());
			}
		}
		
		//Convert to object
		for(String line: fileContent) {
			char[] linechar = line.toCharArray();
			if (linechar.length == 0) {
				continue; // empty line
			}
			if (linechar[0] == '\n') {
				continue; // empty line
			}
			if (linechar[0] == '#') {
				continue; // empty line
			}
			if ((linechar[0] == ';') && (linechar[1] == ';')) {
				continue; // rem line
			}
			DiarizationSpeech ds = new DiarizationSpeech();
			
			StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
			int result = 0;
			while (stringTokenizer.hasMoreTokens()) {
				if (result == 0) {
					ds.setShow(stringTokenizer.nextToken());
					
				} else if (result == 1) {
					ds.setSegmentChannel(stringTokenizer.nextToken());
					
				} else if (result == 2) {
					ds.setSegmentStart(Integer.parseInt(stringTokenizer.nextToken())/100);
					
				} else if (result == 3) {
					ds.setSegmentLen(Integer.parseInt(stringTokenizer.nextToken())/100);
					
				} else if (result == 4) {
					ds.setSegmentGender(stringTokenizer.nextToken());
					
				} else if (result == 5) {
					ds.setSegmentBand(stringTokenizer.nextToken());
					
				} else if (result == 6) {
					ds.setSegmentEnvironement(stringTokenizer.nextToken());
					
				} else if (result == 7) {
					ds.setName(stringTokenizer.nextToken());
					break;
				}
				result++;
			}		
			if (result != 7) {
				throw new IOException("segmentation read error \n" + line + "\n ");
			}
			results.add(ds);
		}
		System.out.println(fileContent);
		Collections.sort(results);
		return results;
	}
}
