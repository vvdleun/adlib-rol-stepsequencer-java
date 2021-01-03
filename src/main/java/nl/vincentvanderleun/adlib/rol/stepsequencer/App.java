package nl.vincentvanderleun.adlib.rol.stepsequencer;

import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.SongCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.SongParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.OutputFileRenderer;

public class App {
    public static void main(String[] args) throws IOException {
    	String jvmString = System.getProperty("java.vm.name") + " " + System.getProperty("java.runtime.version");
    	
    	System.out.println("adlib-rol-stepsequencer (powered by: " + jvmString + ")\n");
    	
    	if(args.length != 2) {
    		System.out.println("Error: two paths must be specified on the command-line: one for the source file and one for the destination file.\n");
    		System.out.println("Beware that the specified destination file will be overwritten.");
    		System.exit(1);;
    	}
    	
    	String sourceFile = args[0];
    	String destFile = args[1];

    	if(sourceFile.equals(destFile)) {
    		System.out.println("Error: the source file must be different than the destination file.");
    		System.exit(1);;
    	}

    	try {
    		convertToAdLibROlFile(sourceFile, destFile);
    	} catch(Exception ex) {
    		System.out.println("ERROR: " + ex.getMessage());
    		throw ex;
    	}
    }
    
    private static void convertToAdLibROlFile(String inputFilePath, String outputFilePath) throws IOException {
    	System.out.println("Parsing \"" + inputFilePath + "\"...\n");
    	
 		ParsedSong parsedSong = SongParser.parse(inputFilePath);

    	System.out.println("Converting events...");

		CompiledSong compiledSong = SongCompiler.compile(parsedSong);

    	System.out.println("\nWriting ROL file to \"" + outputFilePath + "\"...\n");
		
		OutputFileRenderer.renderAdLibRolFile(compiledSong, outputFilePath);
    }
}
