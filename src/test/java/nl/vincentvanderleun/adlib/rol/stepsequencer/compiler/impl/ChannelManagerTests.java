package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;

public class ChannelManagerTests {
	private int MAX_CHANNELS_MELODIC_MODE = 9;
	private int MAX_CHANNELS_PERCUSSIVE_MODE = 11;
	
	@Test
	public void shouldClaimAllChannelsInMelodicMode() throws Exception {
		ChannelManager channelManager = new ChannelManager(SongMode.MELODIC);
		
		for(int i = 0; i < MAX_CHANNELS_MELODIC_MODE; i++) {
			int channel = channelManager.claimChannel();
			assertEquals(i, channel);
		}
	}
	
	@Test
	public void shouldClaimAllChannelsInPercussiveMode() throws Exception {
		ChannelManager channelManager = new ChannelManager(SongMode.PERCUSSIVE);
		
		for(int i = 0; i < MAX_CHANNELS_PERCUSSIVE_MODE; i++) {
			int channel = channelManager.claimChannel();
			assertEquals(i, channel);
		}
	}
	
	@Test
	public void shouldThrowWhenClaimingTooManyChannelsInMelodicMode() throws Exception {
		assertThrows(CompileException.class, () -> {
			ChannelManager channelManager = new ChannelManager(SongMode.MELODIC);
			
			for(int i = 0; i < MAX_CHANNELS_MELODIC_MODE + 1; i++) {
				channelManager.claimChannel();
			}
		});
	}

	@Test
	public void shouldThrowWhenClaimingTooManyChannelsInPercussiveMode() throws Exception {
		assertThrows(CompileException.class, () -> {
			ChannelManager channelManager = new ChannelManager(SongMode.PERCUSSIVE);
			
			for(int i = 0; i < MAX_CHANNELS_PERCUSSIVE_MODE + 1; i++) {
				channelManager.claimChannel();
			}
		});
	}
}
