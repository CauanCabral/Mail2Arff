package types;

public class Key implements Comparable<Key> {
	
	protected String name;
	protected String value;
	protected String type;
	protected String syntax;
	
	public Integer count = 1;
	
	/**
	 * @param	name
	 * @param	value
	 */
	public Key( String name, String value) {
		this.name = name;
		this.value = value;
		this.type = "string";
	}
	
	public Key( String name ) {
		this.name = name;
		this.value = name;
		this.type = "numeric";
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
	
	public boolean equals(Object o) throws ClassCastException {
		if(!(o instanceof Key))
		{
			throw new ClassCastException("Esperado um objeto da classe Key");
		}
		
		if(this == o)
		{
			return true;
		}
		
		Key tmp = (Key)o;
		
		return this.name.equals(tmp.getName());
	}
	
	public int hashCode() {
		return this.name.hashCode(); 
	}
	
	public String toString() {
		return "[" + this.name + " : " + this.value + "]";
	}
}
