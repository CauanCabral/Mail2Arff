package parser;

import types.Key;

import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;

import au.com.bytecode.opencsv.*;

public class Csv extends Text {
	
	public boolean isEOF;
	
	protected char valuesSeparator;
	
	protected String delimiters;
	
	protected LinkedList<Key> especialTypes;
	
	public Csv(char separator) {
		this.initAttributes();
		
		this.isEOF = false;
		this.valuesSeparator = separator;
	}
	
	protected void initAttributes() {
		super.initAttributes();
		
		this.especialTypes = new LinkedList<Key>();
		
		this.especialTypes.add(new Key("\\[[a-zA-Z]*\\](.+?)\\[\\/[a-zA-Z]\\]", "bbcode"));
		
		this.delimiters = "\\s|\\[|\\]|<|>|\\p{Punct}";
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
			
			for( int c = 0; c < csvColumns.length - 1; c++) {
				this.parseLine(csvColumns[c]);
			}
			
			Key k = new Key("class", "{0, 1}");
			k.setValue(csvColumns[csvColumns.length - 1]);
			
			this.m.put("class", k);
		}
		
		this.isEOF = !this.source.hasNextLine();
		
		return this.m;
	}
	
	protected void parseLine(String l) {
		Key k;
		
		int init, end;
		
		String t;
		
		init = 0;
		end = 1;
		
		while( end < l.length() )
		{
			// enquanto não lê um delimitador, caminha na string
			while( !l.substring(end - 1, end).matches(this.delimiters) && end < l.length() )
				end++;
			
			t = l.substring(init, end - 1);
			
			// ignoro texto não-ascii, palavras com menos de 3 caracteres e números
			if(!Text.isPureAscii(t) || t.length() < 3 || t.matches("[0-9]+"))
			{
				init = end++;
				continue;
			}
			
			k = this.m.get(t);
			
			if(k == null)
			{
				k = new Key(t);
				
				int i = this.especialTypes.indexOf(k); 
				if( i > 0 )
				{
					k.setType(this.especialTypes.get(i).getType());
				}
			}
			else
			{
				k.count++;
			}
			
			this.m.put(t, k);
			
			init = end++;
		}
	}
	
	public void reset() {
		super.reset();
		
		this.isEOF = false;
	}
}
