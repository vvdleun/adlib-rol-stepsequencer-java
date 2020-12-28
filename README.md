# adlib-rol-stepsequencer

It's 2020. My country is in lock-down because of the COVID19 outbreak. What to do?

Well, I spent some time on a small silly program that I had in my mind for a long, long time.

Being a fan of "retro" computing in general and vintage soundcards specifically, I wrote a program that takes a custom text file, parses it and generates an Ad Lib ROL file.

## A HISTORY LESSION

Ad Lib, Inc. from Canada was the manufacterer of the first well known soundcard for the IBM PC, back in the mid to late 80s. Most copyright lines and timestamps in their README files I've seen, mention 1987 as the earliest date, but I believe the very first version of the soundcard actually appeared in 1986 for the very first time. Their card, dubbed the Ad Lib Music Synthesizer Card, was bundled with a Jukebox program that could playback pre-programmed songs saved in the Ad Lib ROL format. A revolutionary graphical tool, the Ad Lib Visual Composer, was released commercially separately that could actually create and edit existing ROL files. Primitive by today's standards, but unlike most niche software back then, it was very easy to use: users could draw notes on a grid with a mouse, one channel at a time. Some events could be entered with the keyboard, but this required some skill and was quite a time consuming process.

What many people do not realize is that the Ad Lib Music Synthesizer Card was not Ad Lib's first product. Under their original company name "Les Editions Ad Lib, Inc.", they released the EXERCETTE computer in the early '80s (the pattent was filed on 1982). It was a microcomputer that aimed to help in teaching music theory. From the lack of information available on the web, it looks like it did not sell that well. If you know something more about this machine, please contact me! I'd love to know more....

I have absolute not any proof to back this up, but I wonder whether Ad Lib ROL files originate from that computer, or at the least the work on that project. The ROL file specifications hardly use any features that are unique for the Yamaha YM3812 "OPL2" FM chip (to continue the history lession: early versions of the Ad Lib MSC board tried to hide the origin of the Yamaha chip, by scratching the surface of the chip to destroy the readable text from the chip. This did not stop competitors from discovering the exact chip model of course and was the start of Ad Lib's demise in the early '90s. That's a whole other story though...). The ROL file format almost seems more like a early take on a universal MIDI file alternative.

Bottom line: if you want to produce the very best songs on the OPL2 chip known to mankind, you're probably better off looking at a dedicated OPL2 (or even better OPL3) tracker program. The ROL format is a little bit too static to make the most out of the OPL2 chip and most chip parameters can only be altered by switching instruments (which definitions are not even stored in the ROL file itself).

If this does not scare you away and still like to mess with the rather curious ROL format, read on...

## ABOUT THE STEPSEQUENCER

I wondered what would happen if one would approach the ROL format as a simple, monophonic stepsequencer, where multiple voices are stacked and triggered from one track (instead of the usual other way around, where each channel is treated as a single indivdual entity, producing rather thin sounds, especially listening back at it now, literally dozens of years later).

Back in the early '00s I bought the rather obscure MAM SQ16 hardware step-sequencer from the German company Music And More. It had some uber awesome crazy experimental features. I hope that I can turn this program slowly eventually into something experimental like that. It's not to be taken seriously, it's all about having some retro fun.

It's early days however. At this time, it can only produce notes and fade-in/out effects. About the first features I want to work on are adding digital delay processors and primitive voice/pitch LFO-alike features for individual voices. If the official Ad Lib ROL file players can cope up with the ideas I have remains to be seen, however.

Ideally I should have written this program in DOS. Maybe someday I will port it to DOS, but for now it's written in Java 11. It's a current design goal to write it in "pure" Java, with no external dependencies (except for unit testing/mocking).

## BUILDING THE PROJECT

A full manual documenting the input file will be written later. 

For now there's only this instructions to build the project and one very lame input test file that is used purely to test the features while developing.

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

The generated file should play back in any program that can playback Ad Lib ROL files (i.e. Ad Lib Visual Composer on MS-DOS emulators, but also Foobar with the AdPlug plugin...).

Note that an instrument bank (usually called STANDARD.BNK) will be required for playback. Since I'm not the author of any bank file, I can't add it to this repository under the open source license as far as I know. It should be easy to find such a file, feel free to poke me if you need a bank file. It was a standard practice to share instrument banks on Bulletin Board Systems back in the day, so it should be easy to find one.

## SPECIAL THANKS

While Ad Lib, Inc. provided an official Programmers Manual back in the day, later versions of this development kit actually documented the ROL format.Many bytes were not specified specifically though (just the hint to fill those with zeros). 

I must thank the people behind the DOS Game Modding Wiki project at http://www.shikadi.net/moddingwiki/ for [describing the ROL format in full](http://www.shikadi.net/moddingwiki/ROL_Format). While I discovered the meaning of most of the bytes myself (simply by looking at a hex editor at files saved in Visual Composer), I was not aware of the counter bytes in the header.

## CONTACT ME

Drop me a note if you like what I'm doing here. Or feel free to tell me that it makes absolutely zero sense. Or any other feedback you'd have.

Twitter: [@vintzend](https://twitter.com/vintzend)

To be continued!