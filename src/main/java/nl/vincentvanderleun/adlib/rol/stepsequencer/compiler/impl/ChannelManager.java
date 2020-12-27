package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;

public class ChannelManager {
	private int channelsTotal;
	private int channelsClaimed;
	
	public ChannelManager(SongMode songMode) {
		channelsTotal = songMode.getChannels();
	}

	public int claimChannel() throws CompileException {
		if(!channelsRemaining()) {
			throw new CompileException("No spare channels are available. All channels are in use.");
		}
		return channelsClaimed++;
	}

	public boolean channelsRemaining() {
		return channelsClaimed < channelsTotal;
	}
}
