package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function;

public abstract class CompilableFunction {
	protected final String name;
	
	public CompilableFunction(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
