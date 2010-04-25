import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import parser.Mail;
import outputs.Arff;
import types.Key;


public class Mail2Arff {
	
	protected Mail m;
	protected Arff a;
	
	protected String output;
	protected File origin;
	
	
	private Vector<String> columnNames;
	private Vector<Vector<String>> columnValues;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 2) {
			System.err.println("Você deve passar um diretório e nome do arquivo alvo como parâmetro.\nAbortando.");
			System.exit(0);
		}
		
		File dir = new File(args[0]);
		
		if(!dir.isDirectory())
		{
			System.err.println("O endereço passado não é um diretório.\nAbortando.");
			System.exit(0);
		}
		
		System.out.println("Iniciando programa");
		
		Mail2Arff m2a = new Mail2Arff(dir, args[1]);
		
		System.out.println("Executando conversão");
		
		m2a.run();
		
		System.out.println("Finalizando programa");
	}
	 
	public Mail2Arff(File dir, String output) {
		this.m = new Mail();
		this.a = new Arff();
		
		this.columnNames = new Vector<String>();
		this.columnValues = new Vector<Vector<String>>();
		
		this.origin = dir;
		this.output = output;
	}
	
	protected Set<Entry<String, Key>> readMail(File s) {
		this.m.reset();
		this.m.setSource(s);
		return this.m.readMail();
	}
	
	protected void combineMails( Set<Entry<String, Key>> mail )
	{
		Iterator<Entry<String, Key>> it = mail.iterator();
		
		Entry<String,Key> cur;
		
		int colIndex;
		Vector<String> temp;
		
		while(it.hasNext())
		{
			cur = it.next();
			colIndex = this.columnNames.indexOf(cur.getKey());
			
			if(colIndex != -1)
			{
				temp = this.columnValues.elementAt(colIndex);
				temp.add( cur.getValue().getValue() );
			}
			else
			{
				// adiciona nome da nova coluna
				this.columnNames.add(cur.getKey());
				
				// recupera o índice da coluna adicionada
				colIndex = this.columnNames.indexOf(cur.getKey());
				
				// adiciona um novo vetor para guardar o novo tipo de valor
				this.columnValues.insertElementAt(new Vector<String>(), colIndex);
				temp = this.columnValues.elementAt(colIndex);
				
				// adiciona o valor a nova coluna
				temp.add( cur.getValue().getValue() );
			}
		}
	}
	
	protected void writeArff(String s) {
		this.a.setOutput(s);
		this.a.setData(this.columnNames, this.columnValues);
	}
	
	public void run() {
		// crio filtro para listar apenas arquivos
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		
		File[] files = this.origin.listFiles(fileFilter);
		
		for(int i = 0, t = files.length; i < t; i++)
		{
			this.combineMails( this.readMail(files[i]) );
		}
		
		this.writeArff(this.output);
		this.a.print();
	}

}
