package parser;

import types.Key;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import java.util.TreeSet;
import java.util.LinkedList;

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
	private LinkedList<Key> especialKeys;
	
	/**
	 * Guarda o email destrinchado
	 */
	private TreeSet<Key> m;
	
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
		this.m = new TreeSet<Key>();
		this.especialKeys = new LinkedList<Key>();
		
		this.especialKeys.add( new Key("Return-Path", "string", null) );
		this.especialKeys.add(new Key("Delivered-To", "string", null));
		this.especialKeys.add(new Key("To", "string", null));
		this.especialKeys.add(new Key("Cc", "string", null));
		this.especialKeys.add(new Key("Bcc", "string", null));
		this.especialKeys.add(new Key("From", "string", null));
		this.especialKeys.add(new Key("Subject", "string", null));
		this.especialKeys.add(new Key("In-Reply-To", "string", null));
		this.especialKeys.add(new Key("MIME-Version", "string", null));
		this.especialKeys.add(new Key("Content-Transfer-Encoding", "string", null));
		this.especialKeys.add(new Key("Status", "string", null));
		this.especialKeys.add(new Key("Content-Type", "string", null));
		this.especialKeys.add(new Key("Received-SPF", "string", null));
		
		this.especialKeys.add(new Key("Content-Transfer-Encoding", "string", null));
		this.especialKeys.add(new Key("Content-Disposition", "string", null));
		
		this.especialKeys.add(new Key("X-MSMail-Priorit", "string", null));
		this.especialKeys.add(new Key("X-Priority", "string", null));
		this.especialKeys.add(new Key("X-Mailer", "string", null));
		this.especialKeys.add(new Key("X-Status", "string", null));
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
	
	public TreeSet<Key> readMail() {
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
			k = new Key("Body", this.readBody());
			k.setType("string");
			
			m.add(k);
		}
		
		return this.getMail();
	}
	
	/**
	 * @return Key
	 */
	private Key identifyKey() {
		Key k = null;
		int i;
		
		try {
			String[] tk = this.currentLine.split(":", 2);
			
			k = new Key(tk[0].trim(), tk[1].trim());
			
			// caso seja uma chave especial
			if( (i = this.especialKeys.indexOf(k)) == -1 )
			{
				k = null;
			}
			else
			{
				k.setSyntax(this.especialKeys.get(i).getSyntax());
				k.setType(this.especialKeys.get(i).getType());
			}
		}
		catch(PatternSyntaxException e)
		{
			System.err.println("Expressão Regular inválida");
			k = null;
		}
		catch(ClassCastException e)
		{
			System.err.println(e.getMessage());
			k = null;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			k = null;
		}
		
		return k;
	}
	
	protected String readBody() {
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
	
	protected boolean nextKeyLine() {
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
	
	public void print() {
		Iterator<Key> it = this.m.iterator();
		Key k;
		
		while(it.hasNext())
		{
			k = it.next();
			
			System.out.println("Chave = " + k.getName() +  " || Valor = " + k.getValue() );
		}
	}
	
	public TreeSet<Key> getMail() {
		return this.m;
	}
	
	public LinkedList<Key> getHeaders() {
		return this.especialKeys;
	}
	
	public void reset() {
		this.source = null;
		this.currentLine = null;
		this.isBody = false;
		this.m.clear();
	}
}
