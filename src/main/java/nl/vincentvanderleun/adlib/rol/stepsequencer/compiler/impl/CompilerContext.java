package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;

public class CompilerContext {
	public int tick;	// Initialized by Track, consumed and updated by Patterns
	public int octave;	// Only used while compiling a Pattern of the current track
	public Patch patch;	// See octave
}
