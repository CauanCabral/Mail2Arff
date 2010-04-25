package parser;

import types.Key;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import java.util.HashSet;
import java.util.Set;

import java.util.Iterator;

import java.io.IOException;
import java.io.FileNotFoundException;
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
	private HashSet<Key> especialKeys;
	
	/**
	 * Guarda o email destrinchado
	 */
	private HashSet<Key> m;
	
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
		this.m = new HashSet<Key>();
		this.especialKeys = new HashSet<Key>();
		
		this.especialKeys.add(new Key("Return-Path"));
		this.especialKeys.add(new Key("Delivered-To"));
		this.especialKeys.add(new Key("To"));
		this.especialKeys.add(new Key("Cc"));
		this.especialKeys.add(new Key("Bcc"));
		this.especialKeys.add(new Key("From"));
		this.especialKeys.add(new Key("Subject"));
		this.especialKeys.add(new Key("In-Reply-To"));
		this.especialKeys.add(new Key("Date"));
		this.especialKeys.add(new Key("MIME-Version"));
		this.especialKeys.add(new Key("Content-Transfer-Encoding"));
		this.especialKeys.add(new Key("Date"));
		this.especialKeys.add(new Key("Status"));
		this.especialKeys.add(new Key("Content-Type"));
		this.especialKeys.add(new Key("Received-SPF"));
		
		this.especialKeys.add(new Key("Content-Transfer-Encoding"));
		this.especialKeys.add(new Key("Content-Disposition"));
		
		this.especialKeys.add(new Key("X-MSMail-Priorit"));
		this.especialKeys.add(new Key("X-Priority"));
		this.especialKeys.add(new Key("X-Mailer"));
		this.especialKeys.add(new Key("X-Status"));
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
	
	public Set<Key> readMail()
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
					this.m.add(k);
				}
			}
		}
		
		// le o body
		if(this.isBody)
		{
			m.add(new Key("Body", this.readBody()));
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
				k = new Key(tk[0].trim(), tk[1].trim());
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
		Iterator<Key> it = this.m.iterator();
		Key k;
		
		while(it.hasNext())
		{
			k = it.next();
			
			System.out.println("Chave = " + k.getName() +  " || Valor = " + k.getValue() );
		}
	}
	
	public Set<Key> getMail()
	{
		return this.m;
	}
	
	public void reset()
	{
		this.source = null;
		this.currentLine = null;
		this.isBody = false;
		this.m.clear();
	}
}
