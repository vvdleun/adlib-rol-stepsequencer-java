# adlib-rol-stepsequencer

It's 2020. My country is in lock-down because of the COVID19 outbreak. What to do?

Well, I spent some time on a small, silly, program that I had in my mind for a long, long time.

Being a fan of "retro" computing in general and vintage soundcards specifically, I wrote a program that takes a custom text file, parses it and generates an Ad Lib ROL file from it.

## A HISTORY LESSION

### About Ad Lib, Inc.

Ad Lib, Inc. from Canada was the manufacterer of the first well known soundcard for the IBM PC, back in the mid to late 80s. Most copyright lines and timestamps in their README files I've seen, mention 1987 as the earliest date, but I believe the very first version of their soundcard actually were released back in 1986. Their card, dubbed the AdLib Music Synthesizer Card, was bundled with a Jukebox program that could playback pre-programmed songs saved in the Ad Lib ROL format. 

It's interesting to note that nowadays the AdLib soundcard is remembered as a soundcard for DOS games, but from early marketing materials, it looks like, at least initially, song creation and playback was the primary intended purpose of the card.

A revolutionary commercial software package, the Ad Lib Visual Composer, was released separately later, which made it possible to actually create and edit existing ROL files with a mouse. Primitive by today's standards, but unlike most niche software released back then, it was very easy to use: users could draw notes on a piano roll (undoubtedly that is where the name from the format was derived from) with a mouse, one channel at a time. Some events could be entered with the PC keyboard. The program required music theory knowledge to make the best of it and entering instrument changes, pitch and volume events required some skill. In conclusion, composing music with Visual Composer was quite a time consuming process. 

### Origin of ROL files

The AdLib Music Synthesizer Card was not Ad Lib's first product. Under their original company name "Les Editions Ad Lib, Inc.", they released the EXERCETTE computer in the early '80s (the pattent was filed back in 1982). It was a microcomputer that aimed to help in teaching music theory. It apparently featured a touch-sensitive matrix, instead of a user screen and did not provide the option to run other software than the program supplied. From the lack of information available on the web, it looks like it did not sell that well. If you know something more about this machine, which seems to be completely lost in time, please contact me! I'd love to know more.... And especially see a photo.

I absolutely have not any proof to back this up, but I wonder whether Ad Lib ROL files originate from that computer, or at the least the work done on that project. The ROL file specifications hardly use any features that is unique for the Yamaha YM3812 "OPL2" FM chip (to continue the history lession: early versions of the Ad Lib MSC board tried to hide the origin of the Yamaha chip, by scratching the surface of the chip to destroy the readable text. This did not stop competitors from discovering the exact chip model, of course, and was the start of Ad Lib's demise in the early '90s. That's a whole different story for another day, though....).

To me, the ROL file format looks more like an early take on a universal, but much more primitive, MIDI file alternative to store musical data (not the sound itself, but instructions to drive a tone generator, i.e. synthesizer), then a format designed solely to make the OPL2 chip shine. I could be completely off here, though.

### Are there better alternatives for OPL2 chiptunes?

If you want to produce the very best songs on the OPL2 chip known to mankind, you're probably better off looking at a dedicated OPL2 (or even better OPL3) "tracker" program, that let the user tweak most FM chip parameters in real-time. The ROL format is too static in nature to make the most out of the OPL2 chip: most chip parameters can only be altered by switching instruments. 

OPL2 instrument definitions are not even stored in the ROL file itself. In typical Ad Lib, Inc. style, the purchase of a separately available commercial add-on program was necessary to create new, or edit existing instruments: Ad Lib Instrument Maker. Instrument Maker saved the instrument in external files, later in bank files. Visual Composer could only import instrument (bank) files for loading purposes and did not offer any instrument editing feature.

If all this does not scare you away and still like to mess with the technically simple, but rather curious, ROL format, read on...

## ABOUT THE STEPSEQUENCER

I wondered what would happen if one would approach the ROL format as a simple, monophonic stepsequencer, where multiple voices are stacked and triggered from one track (instead of the usual other way around, where each channel is treated as a single indivdual entity, producing rather thin  sounds, especially listening back at it now, literally dozens of years later).

This program is not here to be taken too seriously, or take the retro world by storm. It's all about having some fun. Just wanted to see what I can bring to the table to this quite obscure retro file format.

Back in the early '00s I bought the rather obscure MAM SQ16 MIDI hardware step-sequencer from the German company Music And More. It had some awesome crazy experimental features. I used it to try to breath some life into some cheap digital sample-based synthesizers. This is exactly what I now attempt to do with this program and the ROL format.

It's early days however. At this time, the program can stack different sounds, produce notes and generate fade-in/out effects.

Plans for the future include: 
* Dynamically changing pitch and volume in patterns, easily per tick
* Using spare channels for fully configurable automatically generated delays
* Some primitive subtle voice/pitch LFO-alike features for pitch and volume
* Functions to introduce some randomness in notes, timing and/or pitch and volume settings
* Make the program multi-timbral (although when stacking instruments, channels are used up quickly)
* Maybe introduce chord tracks
* Special track type for percussion mode

I hope DOS-based ROL players will be able to cope with all those events, only one way to find out...

A full manual documenting the input file will be written later. 

## BUILDING THE PROJECT

For now there's only this instructions to build the project and one very lame input test file that is used purely to test the features while developing. Since it's early days, I can introduce breaking changes to the file format at any time (note the version number 0 definition in the file to reflect this).

Ideally I should have written this program in DOS. Maybe someday I will port it to DOS, but for now it's written in Java 11. It's a current design goal to write it in "pure" Java, with no external dependencies (except for unit testing/mocking).

Building was tested on a Windows 10 machine with Maven 3.6.3 and an Eclipse OpenJ-powered OpenJDK 11 binaries, provided by https://adoptopenjdk.net

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

Note that an instrument bank (usually called STANDARD.BNK) will be required for playback. Since I'm not the author of any bank file, I can't add it to this repository under the open source license as far as I know. It should be easy to find such a file. It was a standard practice to share instrument banks on Bulletin Board Systems back in the day, so it should be easy to find one on th web. Feel free to poke me if you need a bank file.

## SPECIAL THANKS

Ad Lib, Inc. provided an official Programmers Manual back in the day. Later versions of this development kit actually documented the ROL format. Many bytes were not specified specifically though (just vague hints to fill those bytes with zeros). 

I must thank the people behind the DOS Game Modding Wiki project at <http://www.shikadi.net/moddingwiki> for [describing the ROL format in full](http://www.shikadi.net/moddingwiki/ROL_Format). While I discovered the meaning of most of the bytes myself (simply by looking at a hex editor at files saved in Visual Composer), I was not aware of the counter bytes in the header.

Some more bytes, which were added to the ROL file when saving files with Visual Composer and the separately available (yep, again...) MIDI Supplement Kit, remain undocumented for now, but official source code by Ad Lib's own engineers is available from old copies of the diskette provided by the Programmers Manual, floating around the web.

## CONTACT ME

Drop me a note if you like what I'm doing here. Or feel free to tell me that it makes absolutely zero sense. Or any other feedback you'd have.

Twitter: [@vintzend](https://twitter.com/vintzend)

To be continued!
