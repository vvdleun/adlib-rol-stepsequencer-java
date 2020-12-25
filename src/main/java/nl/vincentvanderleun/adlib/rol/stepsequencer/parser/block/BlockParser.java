package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.StructureParser;

abstract class BlockParser<T> {
	protected final LineParser lineParser;
	protected final StructureParser structureParser;
	protected final Supplier<T> defaultValueSupplier;

	public BlockParser(LineParser lineParser, Supplier<T> defaultValueSupplier) {
		this.lineParser = lineParser;
		this.structureParser = new StructureParser(lineParser);
		this.defaultValueSupplier = defaultValueSupplier;
	}
	
	public abstract T parse() throws IOException;
}
