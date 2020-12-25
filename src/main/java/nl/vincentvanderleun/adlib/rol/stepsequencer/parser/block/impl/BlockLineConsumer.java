package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import java.io.IOException;

@FunctionalInterface
public interface BlockLineConsumer {
	public void accept(BlockLine line) throws IOException;
}
