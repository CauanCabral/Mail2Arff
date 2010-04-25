package outputs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * 
 * @author Cauan Cabral
 *
 * Classe para geração de um arquivo Arff
 * 
 */
public class Arff {
	
	protected Vector<String> attributes;
	protected Vector<Vector<String>> data;
	protected FileWriter output;

	public Arff()
	{
	}
	
	public void setData(Vector<String> a, Vector<Vector<String>> d)
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
			for(int i = 0, t = this.attributes.size(); i < t; i++)
			{
				this.output.append("@Attribute " + this.attributes.get(i) + "\n");
			}
			
			this.output.append('\n');
		
			for(int i = 0, t = this.getMaxValuesLength(); i < t; i++)
			{
				for(int j = 0, k = this.attributes.size(); j < k; j++)
				{
					try {	
						if(this.data.get(j) != null)
						{
							this.output.append(this.data.get(j).get(i) + ",");
						}
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						
						this.output.append(',');
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
						System.out.print(this.data.get(j).get(i) + ",");
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					
					System.out.print(",");
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
