package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import java.util.List;

public class BlockFunction {
	private final String name;
	private final List<String> arguments;
	
	public BlockFunction(String name, List<String> arguments) {
		this.name = name;
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public List<String> getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return "BlockFunction [name=" + name + ", arguments=" + arguments + "]";
	}
}
