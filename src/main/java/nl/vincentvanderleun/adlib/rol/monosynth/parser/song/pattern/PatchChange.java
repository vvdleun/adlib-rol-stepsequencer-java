package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.pattern;

/**
 * Event that changes the current patch. This will reset various parameters of the voices' channels that make
 * up this patch.
 *
 * @author Vincent
 */
public class PatchChange extends Event {
	private final String patchName;

	public PatchChange(String patchName) {
		super(EventType.PATCH);
		this.patchName = patchName;
	}

	public String getPatchName() {
		return patchName;
	}

	@Override
	public String toString() {
		return "Patch [patchName=" + patchName + "]";
	}
}
