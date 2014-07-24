package en.neuralnet.ocr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WeightManager {
	private static final String SEP_MAJOR = ":";
	private static final String SEP_MINOR = ",";
	private static final String FILE_PATH = "weights.txt";
	
	private final Map<Character,double[]> weightMap = new HashMap<Character,double[]>();
	private final Map<Character,Double>   biasMap   = new HashMap<Character,Double>();
	
	public WeightManager(int imgSize, char[] characters) {
		File weightFile = new File(FILE_PATH);
		
		if(!weightFile.exists()) {
			Random rand = new Random();
			for(char c : characters) {
				double[] weights = new double[imgSize];
				for(int i=0; i<weights.length; i++) {
					weights[i] = rand.nextDouble() * 2 - 1;
				}
				weightMap.put(c, weights);
				biasMap.put(c, rand.nextDouble() * 2 - 1);
			}
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(weightFile));
			String line;
			while((line = br.readLine()) != null) {
				String[] majorSplit = line.trim().split(SEP_MAJOR);
				if(majorSplit.length != 3 || majorSplit[0].length() != 1) continue;
				char character = majorSplit[0].charAt(0);
				
				Double bias = Double.valueOf(majorSplit[1]);
				
				String[] minorSplit = majorSplit[2].split(SEP_MINOR);
				if(minorSplit.length != imgSize) continue;
				
				double[] weights = new double[imgSize];
				for(int i=0; i<weights.length; i++) {
					weights[i] = Double.parseDouble(minorSplit[i]);
				}
				
				weightMap.put(character, weights);
				biasMap.put(character, bias);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double[] getWeights(char character) {
		return weightMap.get(character);
	}
	
	public void setWeights(char character, double[] weights) {
		weightMap.put(character, weights);
	}
	
	public double getBias(char character) {
		return biasMap.get(character);
	}
	
	public void setBias(char character, double bias) {
		biasMap.put(character, bias);
	}
	
	public void save() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
			for(Entry<Character,double[]> e : weightMap.entrySet()) {
				Character key = e.getKey();
				bw.write(key + SEP_MAJOR);
				bw.write(biasMap.get(key) + SEP_MAJOR);
				for(double d : e.getValue()) {
					bw.write(d + SEP_MINOR);
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
