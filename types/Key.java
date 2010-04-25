package types;

public class Key {
	//
	// Fields
	//

	protected String name;
	protected String value;
	public String syntax;
	
	//
	// Constructors
	//
	/**
	 * @param	name
	 * @param	value
	 */
	public Key( String name, String value)
	{
		this.name = name;
		this.value = value;
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
}
