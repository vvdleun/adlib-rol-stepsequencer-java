package nl.vincentvanderleun.adlib.rol.monosynth.parser.song;

/**
 * Represents supported values for the "target" field in the [HEADER] block.
 *
 * Right now only ROL version 0.4 files designed by Ad Lib, Inc. in the '80s, can be targeted.
 *
 * @author Vincent
 */
public enum Target {
	ADLIB_ROL;	// AdLib ROL 0.4 format
}
