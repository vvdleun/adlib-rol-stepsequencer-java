package nl.vincentvanderleun.adlib.rol.monosynth.parser;

import java.io.IOException;

public class ParseException extends IOException {
	private static final long serialVersionUID = 1L;

	public ParseException(String msg) {
		super(msg);
	}
	
	public ParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
