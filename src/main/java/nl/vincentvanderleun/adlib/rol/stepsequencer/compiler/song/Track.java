package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;

/**
 * A track is a independent track of music, that "owns" one or more channels
 * of a ROL file. The patch with the largest number of voices, that is played in
 * the track's patterns, determine how many channels of the ROL file are reserved
 * for this track.
 * 
 * Maybe a smarter algorithm for managing channels can be implemented later, where
 * channels that are finished can be returned for potential re-use.
 * 
 * Because there are only 9 channels (MELODIC mode) or even 6 (+5 percussion sounds, in
 * PERCUSSIVE mode), until that time, careful planning is needed by the user.
 * 
 * It's worth mentioning that at this time the Track compiler "compiles" directly
 * to the CompiledSong.
 * 
 * @author Vincent
 *
 */
public class Track {
	private final CompiledSong compiledSong;
	private final ChannelManager channelManager;
	private final List<Integer> claimedChannels;
	
	private int startTick = 0;
	private int endTick = 0;
	
	public Track(CompiledSong compiledSong, ChannelManager channelManager) {
		this.compiledSong = compiledSong;
		this.channelManager = channelManager;
		this.claimedChannels = new ArrayList<>();
	}

	public void claimTickOnChannels(int tick) {
		if(tick < startTick) {
			// Not possible right now, as all tracks start at tick 0.
			startTick = tick;
		}
		if(tick > endTick) {
			endTick = tick;
		}
	}
	
	public List<Channel> claimChannels(int number) throws CompileException {
		if(claimedChannels.size() >= number) {
			return toChannelList(claimedChannels.subList(0,  number));
		}
		for (int i = number - claimedChannels.size(); i > 0; i--) {
			claimNextChannel();
		}
		return toChannelList(claimedChannels);
	}
	
	public List<Channel> getChannels() {
		return toChannelList(claimedChannels);
	}
	
	private List<Channel> toChannelList(List<Integer> channelIndexes) {
		return channelIndexes.stream()
				.map(channelIndex -> compiledSong.getChannels().get(channelIndex))
				.collect(Collectors.toList());		
	}
	
	private int claimNextChannel() throws CompileException {
		int nextChannel = channelManager.claimChannel();
		claimedChannels.add(nextChannel);
		return nextChannel;
	}
	
	public int getStartTick() {
		return startTick;
	}

	public int getEndTick() {
		return endTick;
	}
}
