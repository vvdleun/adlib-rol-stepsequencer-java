package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Target;

public class SequenceTests {
	private static final CompiledSong COMPILED_SONG = new CompiledSong(Target.ADLIB_ROL, 120.0f, 4, 4, SongMode.MELODIC);

	private ChannelManager channelManager;
	private Sequence sequence;
	
	@BeforeEach
	public void init() throws Exception {
		channelManager = Mockito.mock(ChannelManager.class);

		when(channelManager.claimChannel())
			.thenReturn(0)
			.thenReturn(1)
			.thenReturn(2);
		
		sequence = new Sequence(COMPILED_SONG, channelManager);
	}

	@Test
	public void shouldClaimTwoChannels() throws Exception {
		List<Channel> channels = sequence.claimChannels(2);
		
		assertEquals(2, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldNotClaimNewChannelsWhenMoreChannelsWereClaimedPreviously() throws Exception {
		sequence.claimChannels(2);
		
		List<Channel> channels = sequence.claimChannels(1);

		assertEquals(1, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldNotClaimNewChannelsWhenTheSameAmountOfChannelsWereClaimedPreviously() throws Exception {
		sequence.claimChannels(2);
		
		List<Channel> channels = sequence.claimChannels(2);

		assertEquals(2, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldClaimNewChannelsWhenNotEnoughChannelsWereClaimedPreviously() throws Exception {
		sequence.claimChannels(2);
		
		List<Channel> channels = sequence.claimChannels(3);

		assertEquals(3, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		assertEquals(2, channels.get(2).getChannelNumber());
		
		verify(channelManager, times(3)).claimChannel();
	}
	
	
//	@Test
//	public void shouldClaimSecondBatchOfChannels() throws Exception {
//		List<Channel> channels = sequence.claimChannels(2);
//		
//		assertEquals(2, channels.size());
//	}
}
