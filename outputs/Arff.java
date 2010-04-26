package outputs;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.util.Vector;

import types.Key;

/**
 * 
 * @author Cauan Cabral
 *
 * Classe para geração de um arquivo Arff
 * 
 */
public class Arff {
	
	protected String relation;
	protected Vector<Key> attributes;
	protected Vector<Vector<Key>> data;
	protected Writer output;

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
			
			this.output = new BufferedWriter( new OutputStreamWriter(fout, "US-ASCII") );
			
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
		String attr;
		Key tmp;
		
		try
		{
			this.output.append("@RELATION " + this.relation + "\n\n");
			
			for(int i = 0, t = this.attributes.size(); i < t; i++)
			{
				tmp = this.attributes.get(i);
				attr = "@Attribute " + tmp.getName() + " " + tmp.getType();
				
				if(tmp.getSyntax() != null)
				{
					attr += " \"" + tmp.getSyntax() + "\"";
				}
				
				attr += '\n';
				
				this.output.append(attr);
			}
			
			this.output.append("\n@DATA\n");
		
			for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
			{
				for(int j = 0, k = this.attributes.size(); j < k; j++)
				{
					try {
						if(this.data.get(j) != null)
						{
							this.output.append( this.arffString(this.data.get(j).get(i).getValue()) + ",");
						}
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						
						this.output.append("?,");
					}
				}
			
				this.output.append('\n');
			}
		}
		catch(IOException e)
		{
			System.err.println("Erro na escrita do arquivio de saída.");
			return false;
		}
		
		return true;
	}
	
	protected int getMaxValuesLength()
	{
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
		s = s.replaceAll("\\s", " ");
		s = s.replaceAll("\"", "\'");
		s = "\"" + s + "\"";
		
		return s;
	}
	
	public void print() {
		
		for(int i = 0, t = this.attributes.size(); i < t; i++)
		{
			System.out.println("@Attribute " + this.attributes.get(i));
		}
		
		for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
		{
			for(int j = 0, k = this.attributes.size(); j < k; j++)
			{
				try {	
					if(this.data.get(j) != null)
					{
						System.out.print(this.arffString(this.data.get(j).get(i).getValue()) + ",");
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.out.print("?,");
				}
			}
			
			System.out.print("\n");
		}
	}
}
