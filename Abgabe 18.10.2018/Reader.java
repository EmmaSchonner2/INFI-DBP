import java.io.BufferedReader;

import java.io.FileNotFoundException;

import java.io.FileReader;

import java.io.IOException;

import java.util.ArrayList;



public class Reader {

	

	private ArrayList<String> text;

	

	public Reader(){

		

	}

	

	public ArrayList<String> readIn(String fileName) throws IOException{

		ArrayList<String> lines = new ArrayList<String>();

		FileReader fr;

		BufferedReader br = null;

		String line = "";

		try {

			fr = new FileReader(System.getProperty("user.dir") +

					"\\" + fileName);

			br = new BufferedReader(fr);

			line = br.readLine();

		}

		catch(FileNotFoundException e) {

			e.printStackTrace();

		}

		

		while (line != null) {

			lines.add(line);

			try {

				line = br.readLine();

			}

			catch(IOException e) {

				e.printStackTrace();

			}

		}

		return lines;

	}

	

	public ArrayList<String> getText(){

		return text;

	}

	

	public void setText(ArrayList<String> text) {

		this.text = text;

	}

	

}
