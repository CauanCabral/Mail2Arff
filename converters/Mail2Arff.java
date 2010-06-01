/**
 * Classe para criação de arquivo Arff a partir de um conjunto de emails lidos
 * 
 * @author Cauan Cabral
 * @link cauancabral.net
 * 
 */

package converters;

import java.io.File;
import java.io.FileFilter;

import java.util.Iterator;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import parser.Mail;
import outputs.Arff;
import types.Key;

public class Mail2Arff{
	
	protected Mail m;
	protected Arff a;
	
	protected String output;
	protected File origin;
	
	private Vector<Key> columnNames;
	private Vector<Vector<Key>> columnValues;
	
	/**
	 * Caso seja setado como TRUE, remove atributos do tipo
	 * string, que não são suportados pelo algoritmo de Bayes
	 */
	private boolean toBayes = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length <= 2) {
			System.err.println("Você deve passar pelo menos um diretório e nome do arquivo alvo como parâmetro.\nAbortando.");
			System.exit(0);
		}
		
		File dir = new File(args[0]);
		
		if(!dir.isDirectory())
		{
			System.err.println("O endereço passado não é um diretório.\nAbortando.");
			System.exit(0);
		}
		
		Mail2Arff m2a = new Mail2Arff(dir, args[1]);
		
		try {
			String alg = args[2];
			
			if(alg.equals("bayes"))
				m2a.toBayes = true;
		}
		catch( IndexOutOfBoundsException e ) {}
		
		System.out.println("Iniciando programa");
		
		m2a.run();
		
		System.out.println("Finalizando programa");
	}
	 
	public Mail2Arff(File dir, String output) {
		this.m = new Mail();
		this.a = new Arff("mails");
		
		this.columnNames = new Vector<Key>();
		this.columnValues = new Vector<Vector<Key>>();
		
		this.origin = dir;
		this.output = output;
	}
	
	protected HashMap<String, Key> readMail(File s) {
		this.m.reset();
		this.m.setSource(s);
		return this.m.readMail();
	}
	
	protected void combineMails( HashMap<String, Key> mail ) {
		
		Iterator<Entry<String, Key>> it = mail.entrySet().iterator();
		Entry<String, Key> cur;
		
		Key k;
		
		int colIndex;
		Vector<Key> temp;
		
		while(it.hasNext())
		{
			cur = it.next();
			k = cur.getValue();
			
			if(this.toBayes && k.getType().equals("string"))
				continue;
						
			colIndex = this.columnNames.indexOf( k );
			
			// caso coluna já exista
			if(colIndex != -1)
			{
				// apenas insere o valor na coluna apropriada
				temp = this.columnValues.elementAt( colIndex );
				temp.add( k );
			}
			else
			{
				// adiciona nome da nova coluna
				this.columnNames.add( k );
				
				// recupera o índice da coluna adicionada
				colIndex = this.columnNames.indexOf( k );
				
				// adiciona um novo vetor para guardar o novo tipo de valor
				this.columnValues.insertElementAt(new Vector<Key>(), colIndex);
				temp = this.columnValues.elementAt(colIndex);
				
				// adiciona o valor a nova coluna
				temp.add( k );
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
		
		System.out.println("Iniciando leitura dos emails");
		
		File[] files = this.origin.listFiles(fileFilter);
		
		for(int i = 0, t = files.length; i < t; i++)
		{
			this.combineMails( this.readMail(files[i]) );
		}
		
		System.out.println("Início da escrita");
		
		this.writeArff(this.output);
	}
}
