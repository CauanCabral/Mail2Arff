package outputs;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.FileWriter;

import java.util.Vector;

import java.io.IOException;

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
	protected Vector<String> attributes;
	protected Vector<Vector<Key>> data;
	protected FileWriter output;

	public Arff( String r )
	{
		this.relation = r;
	}
	
	public void setData(Vector<String> a, Vector<Vector<Key>> d)
	{
		this.attributes = a;
		this.data = d;
	}
	
	public void setOutput(String f)
	{
		try {
			this.output = new FileWriter(f);
		} catch (IOException e) {
			System.err.println("Não foi possível criar o arquivo para escrita.");
			System.exit(0);
		}
	}
	
	public boolean write()
	{
		try
		{
			this.output.append("@RELATION " + this.relation + "\n");
			
			for(int i = 0, t = this.attributes.size(); i < t; i++)
			{
				this.output.append("@Attribute " + this.attributes.get(i) + "\n");
			}
			
			this.output.append("\n@DATA\n");
		
			for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
			{
				for(int j = 0, k = this.attributes.size(); j < k; j++)
				{
					try {	
						if(this.data.get(j) != null)
						{
							this.output.append( StringEscapeUtils.escapeCsv(this.data.get(j).get(i).getValue()) + ",");
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
	
	public void print()
	{
		
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
						System.out.print(StringEscapeUtils.escapeCsv(this.data.get(j).get(i).getValue()) + ",");
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
}
