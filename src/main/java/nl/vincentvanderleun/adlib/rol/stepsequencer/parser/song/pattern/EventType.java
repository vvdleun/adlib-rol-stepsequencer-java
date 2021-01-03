package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * Represents all implemented events that a pattern can contain during parsing.
 *
 * @author Vincent
 */
public enum EventType {
	FUNCTION,
	HOLD,
	NOTE,
	PITCH,
	REST,
	VOLUME
}
