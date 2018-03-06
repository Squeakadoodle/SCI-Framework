package sci;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import org.reflections.Reflections;

import sci.modules.Module;
import sci.variables.Configuration;
import scilib.objects.Group;
import scilib.objects.TeamData;
import scilib.utilities.DataParser;
import scilib.utilities.Tokenizer;

/**
 * Main class for the ScoutingComputerInterface.
 * @author Auxiliatrix
 *
 */
public class SCI {
	
	private static ArrayList<TeamData> masterList;
	
	public static Configuration configuration;
	public static DecimalFormat df;
	public static Tokenizer t;
	public static Scanner sc;
	public static HashMap<String, TeamData> teamAccessMap;
	public static HashMap<String, Module> modules;
	public static ArrayList<String[]> invokerSets;
	public static HashMap<String, Group> groups;
	public static void main(String[] args) {
		configuration = new Configuration();
		df = new DecimalFormat(configuration.decimalFormat);
		t = new Tokenizer();
		sc = new Scanner(System.in);
		teamAccessMap = new HashMap<String, TeamData>();
		modules = new HashMap<String, Module>();
		invokerSets = new ArrayList<String[]>();
		groups = new HashMap<String, Group>();
		init();
		System.out.println(configuration.motd);
		System.out.print("SCI@" + configuration.team + ": ");
		while( run() ) {
			System.out.println();
			System.out.print("SCI@" + configuration.team + ": ");
		}
		System.out.println("-------------------------------------------");
		System.out.println("Terminating Scouting Computer Interface...");
		shutdown();
		System.out.println("Program terminated.");
		System.out.println("-------------------------------------------");
	}
	
	/**
	 * Initializes all necessary features of the program
	 */
	private static void init() {
		System.out.println("-------------------------------------------");
		System.out.println("Initializing Scouting Computer Interface...");
		System.out.println("> Loading modules...");
		Reflections reflect = new Reflections("sci.modules");
		for( Class<?> c : reflect.getSubTypesOf( sci.modules.Module.class ) ) {
			Module m = null;
			try {
				m = (Module) c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if( ( m != null ) ) {
				System.out.println("=> Loaded module: " + m.getName());
				invokerSets.add(m.getInvokers());
				for( String invoker : m.getInvokers() ) {
					modules.put(invoker, m);
				}
			}
		}
		System.out.println("> Modules loaded!");
		DataParser dp = new DataParser();
		System.out.println("> Loading Data File...");
		masterList = dp.process(configuration.dataFile);
		for( TeamData td : masterList ) {
			teamAccessMap.put(td.teamNumber.toLowerCase(), td);
		}
		Group g = new Group("all", masterList);
		g.immutable = true;
		groups.put("all", g);
		System.out.println("=> File: " + configuration.dataFile);
		System.out.println("> Data File loaded!");
		System.out.println("Initialization complete.");
		System.out.println("-------------------------------------------");
	}

	/**
	 * Safely shuts down the program
	 */
	private static void shutdown() {
		// TODO: Close all GUIs
	}
	
	/**
	 * Processes the input and produces and output
	 * @param line String input of command to process
	 * @return whether the program should still be running after this iteration
	 */
	private static boolean run() { 
		boolean live = true;
		String response = process();
		if (response != null) {
			System.out.println(response);
		} else {
			live = false;
		}
		return live;
	}
	
	/**
	 * Iterates through modules and obtains an appropriate response.
	 * @return response generated by modules
	 */
	private static String process() {
		String line = sc.nextLine();
		ArrayList<String> tokens = t.parse(line);
		String invoker = tokens.remove(0);
		line = line.substring(invoker.length()); // remove the first word and space
		line.trim();
		String response = "Unrecognized command: \"" + invoker + "\". Type \"help\" to see a list of commands.";
		for( String s : modules.keySet() ) {
			if( s.equalsIgnoreCase(invoker) ) {
				Module m = modules.get(s);
				response = m.process(line, tokens);
			}
		}
		return response;
	}
	
	/**
	 * Obtains user input and iterates through modules and obtains an appropriate response. 
	 * Takes in an ArrayList of Modules to prevent recursion when called from another Module.
	 * @param exclude ArrayList of Modules to skip from the process
	 * @return response generated by modules
	 */
	public static String subprocess(Collection<Module> exclude) {
		String line = sc.nextLine();
		Tokenizer t = new Tokenizer();
		ArrayList<String> tokens = t.parse(line);
		tokens.remove(0);
		String invoker = tokens.remove(0);
		line = line.substring(invoker.length()); // remove the first word and space
		line.trim();
		String response = "Unrecognized command: \"" + invoker + "\". Type \"help\" to see a list of commands.";
		for( String s : modules.keySet() ) {
			if( s.equalsIgnoreCase(invoker) ) {
				Module m = modules.get(s);
				if( !exclude.contains(m) ) {
					response = m.process(line, tokens);
				}
			}
		}
		return response;
	}
}
