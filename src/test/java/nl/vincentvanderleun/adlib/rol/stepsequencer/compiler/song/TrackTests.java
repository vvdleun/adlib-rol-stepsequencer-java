package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Target;

public class TrackTests {
	private static final CompiledSong COMPILED_SONG = new CompiledSong(Target.ADLIB_ROL, 120.0f, 4, 4, SongMode.MELODIC);
	private static final int MAX_CHANNELS = 3; // (See channelManager mock below, it starts throwing on 4th call)
	
	private ChannelManager channelManager;
	private Track track;
	
	@BeforeEach
	public void init() throws Exception {
		channelManager = Mockito.mock(ChannelManager.class);

		when(channelManager.claimChannel())
			.thenReturn(0)
			.thenReturn(1)
			.thenReturn(2)
			.thenThrow(new CompileException("Claimed more channels than mock provides"));
		
		track = new Track(COMPILED_SONG, channelManager);
	}

	@Test
	public void shouldClaimTwoChannels() throws Exception {
		List<Channel> channels = track.claimChannels(2);
		
		assertEquals(2, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldNotClaimNewChannelsWhenMoreChannelsWereClaimedPreviously() throws Exception {
		track.claimChannels(2);
		
		List<Channel> channels = track.claimChannels(1);

		assertEquals(1, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldNotClaimNewChannelsWhenTheSameAmountOfChannelsWereClaimedPreviously() throws Exception {
		track.claimChannels(2);
		
		List<Channel> channels = track.claimChannels(2);

		assertEquals(2, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		
		verify(channelManager, times(2)).claimChannel();
	}

	@Test
	public void shouldClaimNewChannelsWhenNotEnoughChannelsWereClaimedPreviously() throws Exception {
		track.claimChannels(2);
		
		List<Channel> channels = track.claimChannels(3);

		assertEquals(3, channels.size());

		assertEquals(0, channels.get(0).getChannelNumber());
		assertEquals(1, channels.get(1).getChannelNumber());
		assertEquals(2, channels.get(2).getChannelNumber());
		
		verify(channelManager, times(3)).claimChannel();
	}
	
	@Test
	public void shouldBePossibleToClaimAllChannels() throws Exception {
		track.claimChannels(MAX_CHANNELS);
	}
	
	@Test
	public void shouldThrowWhenClaimingTooManyChannels() throws Exception {
		assertThrows(CompileException.class, () -> {
			track.claimChannels(MAX_CHANNELS + 1);
		});
	}
	
	@Test
	public void shouldRegisterTicks() {
		assertEquals(0, track.getStartTick());
		assertEquals(0, track.getEndTick());
		
		track.claimTickOnChannels(2);
		track.claimTickOnChannels(3);
		track.claimTickOnChannels(1);
		
		assertEquals(0, track.getStartTick());
		assertEquals(3, track.getEndTick());
	}
}
