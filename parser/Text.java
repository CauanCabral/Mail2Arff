package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.HashMap;

import types.Key;

public class Text {
	
	/**
	 * Armazena referência para o buffer com o arquivo fonte
	 */
	protected Scanner source;
	
	/**
	 * Guarda o buffer da linha que está sendo lida
	 */
	protected String currentLine;
	
	/**
	 * Guarda os dados destrinchado
	 */
	protected HashMap<String, Key> m;
	
	public Text() {
		this.initAttributes();
	}
	
	protected void initAttributes() {
		this.m = new HashMap<String, Key>();
	}
	
	public void setSource ( String newSource ) {
		try
		{
			this.setSource( new File(newSource) );
		}
		catch(NullPointerException e)
		{
			System.err.println("Não foi possível abrir o arquivo fonte: " + e.getMessage());
			System.exit(0);
		}
	}
	
	public void setSource ( File newSource ) {
		try
		{
			this.source = new Scanner(new FileReader(newSource));
			this.source.useDelimiter("\\s|\\p{Punct}");
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Não foi possível abrir o arquivo fonte: " + e.getMessage());
			System.exit(0);
		}
	}
	
	protected String nextToken() {
		return this.source.next();
	}
	
	protected boolean hasNext() {
		return this.source.hasNext();
	}
	
	public void reset() {
		this.source = null;
		this.currentLine = null;
		this.m.clear();
	}
}
