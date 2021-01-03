package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl;

public class FloatDiffUtils {
	private static final float BASE_PITCH = 1.0f;
	
	public static float changePitchAndKeepRatio(float currentValue, float requestedValue) {
		float newValue = keepRatio(currentValue, requestedValue, BASE_PITCH);
		
		if (newValue < 0f) {
			newValue = 0f;
		} else if (newValue > 2.0f) {
			newValue = 2.0f;
		}
		
		return newValue;	
	}
	
	private static float keepRatio(float currentValue, float requestedValue, float baseValue) {
		// Try to keep ratio of voice intact
		final float diff = baseValue - currentValue;
		return requestedValue - diff;
	}
}
