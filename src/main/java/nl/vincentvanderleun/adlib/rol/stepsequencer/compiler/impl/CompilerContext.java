package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl;

import java.util.Map;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;

public class CompilerContext {
	public int tick;							// Initialized by Track, consumed and updated by Patterns
	public int octave;							// Only used while compiling a Pattern of the current track
	public Map<String, Patch> modifiedPatches;	// Patches modified in a current Pattern
}
