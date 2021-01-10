 package nl.vincentvanderleun.adlib.rol.stepsequencer;

import java.io.IOException;
import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.application.CommandLineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.application.ParsedCommandLine;
import nl.vincentvanderleun.adlib.rol.stepsequencer.application.ProgramMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.SongCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.SongParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.OutputFileRenderer;
import nl.vincentvanderleun.adlib.rol.stepsequencer.util.AdLibBankFileReader;

public class App {
    public static void main(String[] args) throws Exception {
    	printHeader();
    	
    	ParsedCommandLine parsedOptions = CommandLineParser.parseArguments(args);
    	
    	try {
	    	switch(parsedOptions.getMode()) {
	    		case CONVERT_SONG:
	    			convertSongToAdLibRolFile(parsedOptions.getInputPath(), parsedOptions.getOutputPath());
	    			break;
	    		case SHOW_BANK_INSTRUMENTS:
	    			showBankFileInstruments(parsedOptions.getBankFilePath());
	    			break;
	    		case SHOW_HELP:
	    			displayHelp();
	    			break;
	    		case SHOW_WRONG_USAGE_ERROR:
	    			displayError(parsedOptions.getErrorMessage());
	    			break;
	    		default:
	    			throw new IllegalStateException("Internal error: unknown mode: \"" + parsedOptions.getMode() + "\"");
			}

	    	System.exit(parsedOptions.getMode() == ProgramMode.SHOW_WRONG_USAGE_ERROR ? 1 : 0);
    	} catch(ParseException ex) {
    		System.out.println("Error occurred during parsing of input file: " + ex.getMessage());
    		exitWithErrorCodeOrThrowOnDebugMode(ex, parsedOptions.isDebugMode());
    	} catch(CompileException ex) {
    		System.out.println("Error occurred during conversion of input file: " + ex.getMessage());
    		exitWithErrorCodeOrThrowOnDebugMode(ex, parsedOptions.isDebugMode());
    	} catch(IOException ex) {
    		System.out.println("Error occurred during reading or writing of file: " + ex.getMessage());
    		exitWithErrorCodeOrThrowOnDebugMode(ex, parsedOptions.isDebugMode());
    	} catch(Exception ex) {
    		// Internal error
   			throw ex;
    	}
    }

    private static void printHeader() {
    	final String jvmString = System.getProperty("java.vm.name") + " " + System.getProperty("java.runtime.version");

    	System.out.println("adlib-rol-stepsequencer (powered by: " + jvmString + ")\n");
    }
    
    private static void exitWithErrorCodeOrThrowOnDebugMode(Exception ex, boolean debugMode) throws Exception {
		if(debugMode) {
			System.out.println();
			throw ex;
		}
    	System.exit(1);
    }
    
    private static void displayError(String msg) {
		System.out.println("ERROR: " + msg + "\n");
		displayHelp();    	
    }
    
    private static void displayHelp() {
		System.out.println("Usage:");
		System.out.println("- To convert a song:");
		System.out.println("     [path input song text file] [path output file]\n");
		System.out.println("- To list instruments stored in an external AdLib BNK file:");
		System.out.println("	--bank [path bank file]\n");
		System.out.println("- To show this help screen");
		System.out.println("	--help");
		System.out.println("\nYou can optionally add a --debug parameter that puts the program in debug mode and may help during troubleshooting.");
    }
    
    private static void convertSongToAdLibRolFile(String sourceFile, String destFile) throws Exception {
    	if(sourceFile.equals(destFile)) {
    		throw new IOException("Error: the source file must be different than the destination file.");
    	}

    	System.out.println("Parsing \"" + sourceFile + "\"...");
    	
 		ParsedSong parsedSong = SongParser.parse(sourceFile);

    	System.out.println("Converting events...");

		CompiledSong compiledSong = SongCompiler.compile(parsedSong);

    	System.out.println("Writing ROL file to \"" + destFile + "\"...\n");
		
		OutputFileRenderer.renderAdLibRolFile(compiledSong, destFile);
    }
    
    private static void showBankFileInstruments(String bankFile) throws IOException {
    	List<String> instruments = AdLibBankFileReader.readInstrumentNamesFrom(bankFile);
    	
    	System.out.println("Instruments stored in \"" + bankFile + "\":\n");
    	
    	int index = 0;
    	for(String instrument : instruments) {
    		System.out.println(++index + ") " + instrument);
    	}
    }
}
