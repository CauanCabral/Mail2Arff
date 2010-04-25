import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import parser.Mail;
import outputs.Arff;
import types.Key;


public class Mail2Arff {
	
	protected Mail m;
	protected Arff a;
	
	protected String output;
	protected File origin;
	
	
	private Vector<String> columnNames;
	private Vector<Vector<Key>> columnValues;
	
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
		this.a = new Arff("mails");
		
		this.columnNames = new Vector<String>();
		this.columnValues = new Vector<Vector<Key>>();
		
		this.origin = dir;
		this.output = output;
	}
	
	protected Set<Key> readMail(File s) {
		this.m.reset();
		this.m.setSource(s);
		return this.m.readMail();
	}
	
	protected void combineMails( Set<Key> mail )
	{
		Iterator<Key> it = mail.iterator();
		
		Key cur;
		
		int colIndex;
		Vector<Key> temp;
		
		while(it.hasNext())
		{
			cur = it.next();
			colIndex = this.columnNames.indexOf( cur.getName() );
			
			// caso coluna já exista
			if(colIndex != -1)
			{
				// apenas insere o valor na coluna apropriada
				temp = this.columnValues.elementAt( colIndex );
				temp.add( cur );
			}
			else
			{
				// adiciona nome da nova coluna
				this.columnNames.add( cur.getName() );
				
				// recupera o índice da coluna adicionada
				colIndex = this.columnNames.indexOf( cur.getName() );
				
				// adiciona um novo vetor para guardar o novo tipo de valor
				this.columnValues.insertElementAt(new Vector<Key>(), colIndex);
				temp = this.columnValues.elementAt(colIndex);
				
				// adiciona o valor a nova coluna
				temp.add( cur );
			}
		}
	}
	
	protected void writeArff(String s) {
		this.a.setOutput(s);
		this.a.setData(this.columnNames, this.columnValues);
		this.a.write();
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
	}

}
