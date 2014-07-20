package en.neuralnet.ocr;

import java.io.File;

public class Trainer {
	public static void main(String[] args) {
		File data = new File("training-data\\");
		for(File character : data.listFiles()) {
			if(character.isFile()) continue;
			File lower = new File(character.getPath() + "\\lower\\");
			if(lower.exists() && lower.isDirectory()) {
				for(File test : lower.listFiles()) {
					NeuralNetwork.train(test.getAbsolutePath(), character.getName().charAt(0));
				}
			}
			File upper = new File(character.getPath() + "\\upper\\");
			if(upper.exists() && upper.isDirectory()) {
				for(File test : upper.listFiles()) {
					NeuralNetwork.train(test.getAbsolutePath(), character.getName().charAt(0));
				}
			}
		}
	}
}
