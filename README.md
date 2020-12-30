# adlib-rol-stepsequencer-java

It's 2020. My country is in lock-down because of the COVID19 outbreak. What to do?

Well, I spent some time on a small, silly, program that I had in my mind for a long, long time.

Being a fan of "retro" computing in general and vintage soundcards specifically, I wrote a program that takes a custom text file, parses it and generates an Ad Lib ROL file from it.

## A HISTORY LESSION

### About Ad Lib, Inc.

Ad Lib, Inc. from Quebec, Canada was the manufacterer of the first well known soundcard for the IBM PC, back in the mid to late '80s. Most copyright lines and timestamps in their README files I've seen, mention 1987 as the earliest date, but I believe the very first version of their soundcard actually was released back in 1986. Their card, dubbed the AdLib Music Synthesizer Card, was bundled with a Jukebox program that could playback pre-programmed songs saved in the Ad Lib ROL format. 

It's interesting to note that the AdLib soundcard is remembered as a soundcard for DOS games nowadays, but from early marketing materials, it looks like, at least initially, song creation and playback was the primary intended purpose of the card.

Early in its commercial life, Ad Lib Inc. released Ad Lib Visual Composer, a software package that made it possible to visually create and edit ROL files. Primitive by today's standards, but unlike most niche software released back then, it was very easy to use, with an impressive 2-color CGA menu-driven GUI. The program required music theory knowledge to make the best of it. Also, entering instrument changes, pitch and volume events, which had to be done by entering values manually on the PC keyboard, required some skill. 

Although there were several shareware programs that could produce ROL files, Visual Composer turned out to be the primary program used to compose those files. In the right skilled hands, excellent sounding songs could be produced in this simple, binary, format.

### Origin of ROL files

The AdLib Music Synthesizer Card was not Ad Lib's first product. Under their original company name "Les Editions Ad Lib, Inc.", they released the EXERCETTE computer around 1982. It was a microcomputer that aimed to help in teaching music theory. It apparently featured a touch-sensitive matrix and did not have the option to load or run user software. From the lack of information available on the web, it looks like it did not sell that well. If you know something more about this machine, which seems to be completely lost in time, please contact me! I'd love to know more.... And especially see photos!

I absolutely have not any proof to back this up, but I wonder whether Ad Lib ROL files originate from that computer, or at the least the work done on that project. The ROL file specifications hardly use any feature that is unique to the Yamaha YM3812 "OPL2" FM chip, the chip that is the heart of the Ad Lib Music Synthesizer Card, at all.

To me, the ROL file format looks more like an early take on a universal, but much more primitive, MIDI file alternative to store musical data (not the sound itself, but instructions to drive a tone generator, i.e. synthesizer), then a format designed for the OPL2 chip specifically. 

I could be completely off here, though. Maybe development just started before Ad Lib was sure which chip it could use eventually and therefore they were careful with their technical choices (I was once told (by a trustworthy source) that they wanted a better chip, but Yamaha insisted on the YM3812).

## ABOUT THE STEP-SEQUENCER

I wondered what would happen if one would approach the ROL format from a simple, monophonic step-sequencer point of view, where multiple voices are layered and triggered from one track (instead of the usual other way around, where each channel is treated as a single indivdual entity, producing rather thin  sounds... especially listening back at it now, literally dozens of years later).

This program is not here to be taken too seriously. I just wanted to see what I could bring to the table to this simple and quite obscure retro file format.

Feature-wise, my program cannot offer anything that cannot be done in Visual Composer itself (VC is really a clear example of "What you see is what you get" regarding ROL format features), but it approaches everything from a rather different angle.

The structure of a song:

A `patch` is an instrument. There can be many instruments defined in a song.

* A patch can have one or more voices, which all play at the same time (each voice can define its own offset to play earlier or later, however)
* Each voice has different parameters that affect that voice only:
  * pitch: a pitch variation of the triggered note. Range: 0.0 (1 note lower) to 2.0 (1 note higher), default is 1.0
  * volume: default volume of this voice. Range 0.0 (silence) to 1.0 (loudest), default 0.75.
  * transpose: the amount of notes that will be added or subtracted from the played note. 12 is one octave higher, -12 one octave lower.
  * instrument: the used instrument (usually loaded from an external BNK file, see below)
  * offset: the amount of ticks that will be added or substracted and affects when this voice starts playing

A `pattern` is a series of notes and events. There can be many patterns defined in a single song.

* The active patch can be changed during a pattern with the `patch(NAME)` function (note that no spaces are allowed at this time)
* The octave is hard-coded (for now) to `4` when a pattern is defined, can be switched with the `octave()` function: i.e. `octave(3)`
* Notes can be entered by their name: `C`, `D`, `C#`, `Db`...
* The octave can be overruled for a single note by prefixing `+` or `-` characters: `+++C` will transpose that `C` note (only!) `3` octaves higher
* The length can be appended to a note with a dash: `C-4` will translate to a `C` note of `4` ticks
* You can also add a `h` character to hold a note `C h` will be translated to a `C` note of `2` ticks. `C h-2` would create a `C` note of `3` ticks.
* The pitch can be changed dynamically by placing a `Px.xx` event (i.e. `P1.10` will change the pitch multiplier to `1.10`). This will always respect the pitch offsets of each voice. You can even add a duration: `P0.50-4`, this resets the pitch multiplier to the previous value after 4 ticks.
* A rest can be added by adding a `r` character. The duration can be added as well `r-4` for a rest of `4` ticks.
* Only note and rest events increases the current tick. So, you can define multiple events on a single tick, then proceed with either a note or a rest.

A `track` triggers the patterns

* There can only be one track in a song right now (the stepsequencer is mono-trimbral)
* A track triggers a series of patterns, which are played after each other
* The `fade-in` and `fade-out` functions can be triggered inside a track: `fade-in(16)` for a fade-in that takes `16` ticks. It respects the volume settings of each voice. Duration can also be specified in beats: `fade-in(4b)` or measures: `fade-in(1m)`

Plans for the future include: 
* Fixing bugs! It's early days for the program!
* Somehow repeat ranges inside a pattern and/or chain other pattern inside a pattern
* Using spare channels for fully configurable automatically generated delays
* Some primitive voice/pitch LFO-alike features for dynamic pitch and volume alteration of a voice
* Functions to introduce some randomness in notes, timing and/or pitch and volume settings
* Make the program multi-timbral (although when layering instruments, channels are used up quickly!)
* More functions!
* Maybe introduce a special track for chords
* Dedicated track type for percussion mode

A full manual documenting the input file will be written later. 

## BUILDING THE PROJECT

For now there's only this instructions to build the project and one very lame input test file that is used purely to test the features while developing. Since it's early days, I can introduce breaking changes to the input file format at any time (note the version number 0 definition in the file to reflect this).
 
Ideally I should have written this program in a language available for DOS. Maybe someday I will port it to DOS, but for now it's written in Java 11. It's a current design goal to write it in "pure" Java, with no external dependencies (except for unit testing/mocking).

Building was tested on a Windows 10 machine with Maven 3.6.3 and Eclipse OpenJ-powered OpenJDK 11 binaries, provided by https://adoptopenjdk.net

To build:

```
mvn clean compile test package
```

## USAGE

After building and packaging, you should be able run the packaged project:

```
java -jar target\adlib-rol-stepsequencer-java-0.0.1-SNAPSHOT-jar-with-dependencies.jar sample.mss.txt sample.rol
```

Sample output on the screen:

```
adlib-rol-stepsequencer (powered by: Eclipse OpenJ9 VM 11.0.9+11)

Parsing "sample.mss.txt"...

Converting events...

Warning: Moved patch change of voice "three" of patch "lead" to tick 0, because of its offset the calculated tick was < 0.
Warning: Discarded note on voice "three" of patch "lead", because calculated tick was < 0

Writing ROL file to "sample.rol"...

Successfully generated ROL file with 318 event(s).
```

The generated file should play back in any program that can playback Ad Lib ROL files (i.e. Ad Lib Visual Composer running on the DOSBox emulator, but also Foobar2000 with the AdPlug plugin installed).

Note that an instrument bank (usually called STANDARD.BNK) will be required for playback. In typical Ad Lib, Inc. style, a separate commercial product, called Ad Lib Instrument Maker, would have been needed to create and edit those instruments, Visual Composer can not do this. Since I'm not the author of any bank file, I can't add one to this repository under the open source license. It was a standard practice to share instrument banks on Bulletin Board Systems back in the day, so it should be easy to find one on the web. Feel free to poke me if you need a bank file.

## SPECIAL THANKS

Ad Lib, Inc. provided an official Programmers Manual back in the day. Later versions of this development kit actually documented the ROL format. Many bytes were not specified specifically though (just vague hints to fill those bytes with zeros). 

I must thank the people behind the DOS Game Modding Wiki project at <http://www.shikadi.net/moddingwiki> for [describing the ROL format in full](http://www.shikadi.net/moddingwiki/ROL_Format). While I discovered the meaning of most of the bytes myself over the years (simply by looking at a hex editor at files saved in Visual Composer), I was not aware of the counter bytes in the header.

Some more bytes, which were added to the ROL file when saving files with Visual Composer and the commercially separately available (yep, again...) Ad Lib MIDI Supplement Kit, remain undocumented for now, but official C headers that describe those bytes, written by Ad Lib's own engineers, is available from old copies of the diskette provided by the Programmers Manual, floating around the web.

## CONTACT ME

Drop me a note if you like what I'm doing here. Or feel free to tell me that it makes absolutely zero sense. Or any other feedback you'd have.

Twitter: [@vintzend](https://twitter.com/vintzend)

To be continued!
