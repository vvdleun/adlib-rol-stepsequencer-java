package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import java.io.IOException;

public class CompileException extends IOException {
	private static final long serialVersionUID = 1L;

	public CompileException(String msg) {
		super(msg);
	}
}
