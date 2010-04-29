/**
 * Classe para escrita de um arquivo Arff
 * 
 * @author Cauan Cabral
 * @link cauancabral.net
 * 
 */

package outputs;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.util.Vector;

import types.Key;

public class Arff {
	
	protected String relation;
	protected Vector<Key> attributes;
	protected Vector<Vector<Key>> data;
	protected Writer output;
	
	/**
	 * Padrão do Arff é "US-ASCII" mas isso limita muito o uso
	 * 
	 * Em alguns casos ele aceita outra codificação, mas nada é garantido.
	 * Outros valores suportados pelo Java(não necessariamente pelo Weka): "UTF-8", "ISO-8859-1"
	 */
	private String outputFormat = "US-ASCII";

	public Arff( String r )
	{
		this.relation = r;
	}
	
	public void setData(Vector<Key> a, Vector<Vector<Key>> d)
	{
		this.attributes = a;
		this.data = d;
	}
	
	public void setOutput(String f)
	{
		try {
			FileOutputStream fout = new FileOutputStream(f);
			
			this.output = new BufferedWriter( new OutputStreamWriter(fout, this.outputFormat) );	
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Codificação de saída não suportada");
			System.exit(0);
		}
		catch (IOException e) {
			System.err.println("Não foi possível criar o arquivo para escrita.");
			System.exit(0);
		} 
	}
	
	public boolean write()
	{
		String aux;
		Key tmp;
		
		try
		{
			this.output.append("@RELATION " + this.relation + "\n\n");
			
			for(int i = 0, t = this.attributes.size(); i < t; i++)
			{
				tmp = this.attributes.get(i);
				
				aux = "@Attribute ";
				
				if( tmp.getName().contains(" ") ) {
					aux = aux.concat(" \"");
					aux = aux.concat( tmp.getName() );
					aux = aux.concat("\"");
				}
				else {
					aux = aux.concat( tmp.getName() );
				}
				
				aux = aux.concat(" ");
				aux = aux.concat(tmp.getType());
				
				if(tmp.getSyntax() != null)
				{
					aux = aux.concat(" \"");
					aux = aux.concat(tmp.getSyntax());
					aux = aux.concat("\"");
				}
				
				aux = aux.concat("\n");
				
				this.output.append(aux);
				
				aux = null;
				tmp = null;
			}
			
			this.output.append("\n@DATA\n");
			
			for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
			{
				for(int j = 0, k = this.attributes.size(); j < k; j++)
				{
					try {
						if(this.data.get(j) != null)
						{
							tmp = this.data.get(j).get(i);
							
							if(tmp.getType().equals("numeric")) {
								aux = tmp.count.toString();
							}
							else {
								aux = "\"";
								aux = aux.concat( this.arffString( tmp.getValue() ) );
								aux = aux.concat("\"");
							}	
							
							this.output.append( aux );
						}
						else
						{
							this.output.append("?");
						}
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						this.output.append("?");
					}
					
					if(j < k-1)
					{
						this.output.append(',');
					}
				}
				
				this.output.append('\n');
			}
			
			this.output.flush();
			this.output.close();
			
		}
		catch(IOException e)
		{
			System.err.println("Erro na escrita do arquivo de saída.");
			return false;
		}
		
		return true;
	}
	
	protected int getMaxValuesLength() {
		int c = 0;
		
		for(int i = 0, t = this.data.size(); i < t; i++)
		{
			if(this.data.get(i).size() > c)
			{
				c = this.data.get(i).size();
			}
		}
		
		return c;
	}
	
	private String arffString( String s ) {
		String out = s.replaceAll("\\s", " ");
				
		return out.replace('"', '\'');
	}
	
	
	
	public void print() {
		String aux;
		Key tmp;
		
		System.out.println("@RELATION " + this.relation);
		
		for(int i = 0, t = this.attributes.size(); i < t; i++)
		{
			tmp = this.attributes.get(i);
			
			aux = "@Attribute ";
			aux = aux.concat(tmp.getName());
			aux = aux.concat(" ");
			aux = aux.concat(tmp.getType());
			
			if(tmp.getSyntax() != null)
			{
				aux = aux.concat(" \"");
				aux = aux.concat(tmp.getSyntax());
				aux = aux.concat("\"");
			}
			
			aux = aux.concat("\n");
			
			System.out.println(aux);
		}
		
		System.out.println("\n@DATA");
	
		for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
		{
			for(int j = 0, k = this.attributes.size(); j < k; j++)
			{
				try {
					if(this.data.get(j) != null)
					{
						aux = "\"";
						aux = aux.concat( this.arffString(this.data.get(j).get(i).getValue()) );
						aux = aux.concat("\"");
						
						System.out.print( aux );
					}
					else
					{
						System.out.print("?");
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.out.print('?');
				}
				
				if(j < k-1)
				{
					System.out.print(',');
				}
			}
			
			System.out.print('\n');
		}
	}
}
