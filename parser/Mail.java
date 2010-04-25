package parser;

import types.Key;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

public class Mail {

	/**
	 * Armazena referência para o buffer com o arquivo fonte MiniJava
	 */
	protected LineNumberReader source;
	
	/**
	 * Marca inicio da leitura do body
	 */
	private boolean isBody = false;
	
	/**
	 * Guarda o buffer da linha que está sendo lida
	 */
	private String currentLine;

	/**
	 * Uma tabela hash com  todas as chaves pre-definidas de um email e seu formato
	 */
	private HashSet<String> especialKeys;
	
	/**
	 * Guarda o email destrinchado
	 */
	private HashMap<String, Key> m;
	
	/**
	 *
	 */
	public Mail(String source) {
		
		this.initAttributes();
		
		this.setSource(source);
		
		this.readMail();
	}
	
	public Mail() {
		this.initAttributes();
	}
	
	
	protected void initAttributes() {
		this.m = new HashMap<String, Key>();
		this.especialKeys = new HashSet<String>();
		
		this.especialKeys.add("Return-Path");
		this.especialKeys.add("Delivered-To");
		this.especialKeys.add("To");
		this.especialKeys.add("Cc");
		this.especialKeys.add("Bcc");
		this.especialKeys.add("From");
		this.especialKeys.add("Subject");
		this.especialKeys.add("In-Reply-To");
		this.especialKeys.add("Date");
		this.especialKeys.add("MIME-Version");
		this.especialKeys.add("Content-Transfer-Encoding");
		this.especialKeys.add("Date");
		this.especialKeys.add("Status");
		this.especialKeys.add("Content-Type");
		this.especialKeys.add("Received-SPF");
		
		this.especialKeys.add("Content-Transfer-Encoding");
		this.especialKeys.add("Content-Disposition");
		
		this.especialKeys.add("X-MSMail-Priorit");
		this.especialKeys.add("X-Priority");
		this.especialKeys.add("X-Mailer");
		this.especialKeys.add("X-Status");
	}
	
	public void setSource ( String newSource ) {
		try
		{
			this.source = new LineNumberReader(new FileReader(newSource));
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Não foi possível abrir o arquivo fonte: " + e.getMessage());
			System.exit(0);
		}
	}
	
	public void setSource ( File newSource ) {
		try
		{
			this.source = new LineNumberReader(new FileReader(newSource));
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Não foi possível abrir o arquivo fonte: " + e.getMessage());
			System.exit(0);
		}
	}
	
	public Set<Entry<String, Key>> readMail()
	{
		Key k;
		
		// leio as chaves do email
		while( !this.isBody )
		{
			if(this.nextKeyLine())
			{
				k = this.identifyKey();
			
				if( k != null)
				{
					this.m.put(k.getName(), k);
				}
			}
		}
		
		// le o body
		if(this.isBody)
		{
			m.put("Body", new Key("Body", this.readBody()));
		}
		
		return this.getMail();
	}
	
	/**
	 * @return Key
	 */
	private Key identifyKey()
	{
		Key k = null;
		
		try {
			String[] tk = this.currentLine.split(":", 2);
			
			// caso seja uma chave especial
			if( this.especialKeys.contains(tk[0]) )
			{
				k = new Key(tk[0], tk[1]);
			}
		} 
		catch(PatternSyntaxException e)
		{
			System.err.println("Expressão Regular inválida");
		}
		
		return k;
	}
	
	protected String readBody()
	{
		StringBuffer b = new StringBuffer();
		
		try {
			String l = this.source.readLine();
			
			while(l != null) {
				b.append('\n');
				b.append(l);
				
				l = this.source.readLine();
			};
			
		}
		catch(IOException e) {
			System.err.println("Erro de leitura");
		}
		
		return this.currentLine = b.toString();
	}
	
	protected boolean nextKeyLine()
	{	
		try {
			this.currentLine = this.source.readLine();
		}
		catch(IOException e) {
			System.err.println("Erro de leitura");
		}
		
		if(this.currentLine == null )
		{
			return false;
		}
		
		// caso não tenha lido nenhuma chave, então próxima linha é o body
		if(this.currentLine.length() <= 1)
		{
			this.isBody = true;
		}
		
		return true;
	}
	
	public void print()
	{
		Iterator<Entry<String, Key>> it = this.m.entrySet().iterator();
		Entry<String, Key> k;
		
		while(it.hasNext())
		{
			k = it.next();
			
			System.out.println("Chave = " + k.getKey() +  " || Valor = " + k.getValue().getValue() );
		}
	}
	
	public Set<Entry<String, Key>> getMail()
	{
		return this.m.entrySet();
	}
	
	public void reset()
	{
		this.source = null;
		this.currentLine = null;
		this.isBody = false;
		this.m.clear();
	}
}
