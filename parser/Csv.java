package parser;

import types.Key;

import java.io.IOException;

import java.util.HashMap;
import java.util.Scanner;

import au.com.bytecode.opencsv.*;

public class Csv extends Text {
	
	public boolean isEOF;
	
	protected char valuesSeparator;
	
	public Csv(char separator) {
		super();
		
		this.isEOF = false;
		this.valuesSeparator = separator;
	}

	public HashMap<String, Key> readCsv()
	{
		this.m.clear();
		
		CSVParser csv = new CSVParser(this.valuesSeparator);
		String[] csvColumns;
		
		if(this.source.hasNextLine()) {
			this.currentLine = this.source.nextLine();
			try {
				csvColumns = csv.parseLine(this.currentLine);
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			for( String t : csvColumns) {
				this.parseLine(t);
			}
		}
		
		this.isEOF = !this.source.hasNextLine();
		
		return this.m;
	}
	
	protected void parseLine(String l) {
		Key k;
		Scanner s = new Scanner(l);
		String t;
		
		while( s.hasNext() )
		{
			t = s.next();
			k = this.m.get(t);
			
			if(k == null)
			{
				k = new Key(t);
			}
			else
			{
				k.count++;
			}
			
			this.m.put(t, k);
		}
	}
	
	public void reset() {
		super.reset();
		
		this.isEOF = false;
	}
}
