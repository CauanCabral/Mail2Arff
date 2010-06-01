/**
 * Classe para escrita de um arquivo Arff
 * 
 * @author Cauan Cabral
 * @link cauancabral.net
 * 
 */

package parser;

import types.Key;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Iterator;

import java.util.NoSuchElementException;
import java.util.regex.PatternSyntaxException;

/**
 * Classe para leitura de arquivos de email
 * 
 * @author Cauan Cabral
 * @link cauancabral.net
 * 
 */

public class Mail extends Text {
	
	/**
	 * Marca inicio da leitura do body
	 */
	private boolean isBody = false;

	/**
	 * Uma tabela hash com todas as chaves pre-definidas de um email e seu formato
	 */
	private LinkedList<Key> especialKeys;
	
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
		super.initAttributes();
		
		this.especialKeys = new LinkedList<Key>();
		
		this.especialKeys.add( new Key("Return-Path", "string") );
		this.especialKeys.add(new Key("Delivered-To", "string"));
		this.especialKeys.add(new Key("To", "string"));
		this.especialKeys.add(new Key("Cc", "string"));
		this.especialKeys.add(new Key("Bcc", "string"));
		this.especialKeys.add(new Key("From", "string"));
		this.especialKeys.add(new Key("Subject", "string"));
		this.especialKeys.add(new Key("In-Reply-To", "string"));
		this.especialKeys.add(new Key("MIME-Version", "string"));
		this.especialKeys.add(new Key("Content-Transfer-Encoding", "string"));
		this.especialKeys.add(new Key("Status", "string"));
		this.especialKeys.add(new Key("Content-Type", "string"));
		this.especialKeys.add(new Key("Received-SPF", "string"));
		
		this.especialKeys.add(new Key("Content-Transfer-Encoding", "string"));
		this.especialKeys.add(new Key("Content-Disposition", "string"));
		
		this.especialKeys.add(new Key("X-MSMail-Priorit", "string"));
		this.especialKeys.add(new Key("X-Priority", "string"));
		this.especialKeys.add(new Key("X-Mailer", "string"));
		this.especialKeys.add(new Key("X-Status", "string"));
	}
	
	public HashMap<String, Key> readMail() {
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
			this.readBody();
		}
		
		this.readBody();
		
		// fecha a leitura
		this.source.close();
		
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
			
			k = new Key(tk[0].trim());
			
			// caso seja uma chave especial
			if( (i = this.especialKeys.indexOf(k)) == -1 )
			{
				k = null;
			}
			else
			{
				k.setValue(tk[1].trim());
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
	
	protected void readBody() {
		String tk;
		Key k;
		int len;
		
		while( this.hasNext() ) {
			// ignorar case do texto
			tk = this.nextToken().toLowerCase();
			len = tk.length();
			
			// ignoro tokens que possuem caracteres não-ascii
			if( !Text.isPureAscii(tk) )
				continue;
			
			// ignoro números
			if( tk.matches("[0-9]*") )
				continue;
			
			// ignoro tokens grandes (normalmente codificação de anexo)
			if( len >= 40)
				continue;
			
			// ignoro palavras com menos de três caracteres, que não sejam caracteres especiais
			if( len < 3 && !(len == 1 && !Character.isLetterOrDigit(tk.codePointAt(0))) )
				continue;
			
			k = this.m.get(tk);
			
			if( k == null )
			{
				k = new Key(tk);
			}
			else
			{
				k.count++;
			}
			
			this.m.put(tk, k);
		}
	}
	
	protected boolean nextKeyLine() {
		try {
			this.currentLine = this.source.nextLine();
		}
		catch(NoSuchElementException e) {
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
		Iterator<Entry<String, Key>> it = this.m.entrySet().iterator();
		Entry <String, Key> e;
		Key k;
		
		while(it.hasNext())
		{
			e = it.next();
			k = e.getValue();
			
			System.out.println("Chave = " + k.getName() +  " >> Valor = " + k.getValue() );
		}
	}
	
	public HashMap<String, Key> getMail() {
		return this.m;
	}
	
	public LinkedList<Key> getHeaders() {
		return this.especialKeys;
	}
	
	public void reset() {
		super.reset();
		
		this.isBody = false;
	}
}
