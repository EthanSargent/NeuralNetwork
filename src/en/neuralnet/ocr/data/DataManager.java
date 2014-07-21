package en.neuralnet.ocr.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Keeps track of all the data.
 * 
 * @author Greg Carlin
 *
 */
public class DataManager {
	private static final String WEIGHT_NAME = "weights.txt";
	private static final String VAL_NAME = "vals.txt";
	
	private final String dataPath;
	private final HashMap<String,Float> weights;
	private final HashMap<String,HashMap<Character,Float>> avgVals;
	
	public DataManager(String dataPath) throws DataNotFoundException {
		this.dataPath = dataPath;
		if(new File(dataPath).isFile()) throw new IllegalArgumentException("DataPath must be a directory.");
		this.weights = new HashMap<String,Float>();
		this.avgVals = new HashMap<String,HashMap<Character,Float>>();
		
		try {
			File weightFile = new File(dataPath + WEIGHT_NAME);
			if(weightFile.exists()) {
				BufferedReader brWeights = new BufferedReader(new FileReader(weightFile));
				String line;
				int lineNum = 0;
				while((line = brWeights.readLine()) != null) {
					lineNum++;
					line = line.trim();
					if(line.length() <= 0) continue;
					String[] split = line.split(":");
					try {
						Float val = Float.valueOf(split[1]);
						setWeight(split[0], val);
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new DataNotFoundException(dataPath, lineNum);
					}
				}
				brWeights.close();
			}
			
			File valFile = new File(dataPath + VAL_NAME);
			if(valFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(valFile));
				String line;
				int lineNum = 0;
				while((line = br.readLine()) != null) {
					lineNum++;
					line = line.trim();
					if(line.length() <= 0) continue;
					String[] split = line.split(":");
					try {
						setAvgValue(split[0], split[1].charAt(0), Float.valueOf(split[2]));
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new DataNotFoundException(dataPath, lineNum);
					}
				}
				br.close();
			}
		} catch (IOException e) {
			throw new DataNotFoundException();
		}
	}
	
	public void save() throws DataNotFoundException {
		File weightFile = new File(dataPath + WEIGHT_NAME);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(weightFile));
			for(Entry<String,Float> e : weights.entrySet()) {
				bw.write(e.getKey() + ":" + e.getValue());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			throw new DataNotFoundException();
		}
		
		File valFile = new File(dataPath + VAL_NAME);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(valFile));
			for(Entry<String,HashMap<Character,Float>> e : avgVals.entrySet()) {
				String key = e.getKey();
				for(Entry<Character,Float> pair : e.getValue().entrySet()) {
					bw.write(key + ":" + pair.getKey() + ":" + pair.getValue());
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			throw new DataNotFoundException();
		}
	}
	
	/**
	 * Gets the stored weight of a factor.
	 * 
	 * @param factorName
	 * @return
	 * @throws DataNotFoundException 
	 */
	public float getWeight(String factorName) throws DataNotFoundException {
		Float val = weights.get(factorName);
		if(val == null) throw new DataNotFoundException(factorName);
		return val;
	}
	
	/**
	 * Sets the stored weight of a factor.
	 * 
	 * @param factorName
	 * @param weight
	 */
	public void setWeight(String factorName, float weight) {
		weights.put(factorName, weight);
	}
	
	/**
	 * Gets the stored average value of a factor for the given character.
	 * 
	 * @param factorName
	 * @param c
	 * @return
	 * @throws DataNotFoundException 
	 */
	public float getAvgValue(String factorName, char c) throws DataNotFoundException {
		HashMap<Character,Float> map = avgVals.get(factorName);
		if(map == null) throw new DataNotFoundException(factorName);
		Float f = map.get(c);
		if(f == null) throw new DataNotFoundException(factorName, c);
		return f;
	}
	
	/**
	 * Sets the stored average value of a factor for the given character.
	 * 
	 * @param factorName
	 * @param c
	 * @param value
	 */
	public void setAvgValue(String factorName, char c, float value) {
		HashMap<Character,Float> map = avgVals.get(factorName);
		if(map == null) {
			map = new HashMap<Character,Float>();
			avgVals.put(factorName, map);
		}
		map.put(c, value);
	}
}
