package types;

public class Key implements Comparable<Key> {
	
	protected String name;
	protected String value;
	protected String type;
	protected String syntax;
	
	//
	// Constructors
	//
	/**
	 * @param	name
	 * @param	value
	 */
	public Key( String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public Key( String name ) {
		this.name = name;
	}
	
	public Key( String name, String type, String syntax) {
		this.name = name;
		this.type = type;
		this.syntax = syntax;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	
	/**
	 * Sobrecarga de métodos utilitários e implementação de interface 
	 */
	
	public int compareTo(Key o) throws ClassCastException {
		if(!(o instanceof Key))
		{
			throw new ClassCastException("Esperado um objeto da classe Key");
		}
		
		return this.name.compareTo(o.getName());
	}
	
	public boolean equals(Key o)
	{	
		return this.name.equals(o.getName());
	}
	
	public String toString()
	{
		return "[" + this.name + " : " + this.value + "]";
	}
}
