import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import callGraph.DirectedGraph;

public class Main {
	private static String CALL_GRAPH_JAR = "/Users/Rav/Desktop/temp/java-callgraph/target/javacg-0.1-SNAPSHOT-static.jar";
	private static String TARGET_JAR_LOCATION = "/Users/Rav/Desktop/hadoop-common-2.2.0.jar";
	private static String STORING_CALL_GRAPH = "/Users/Rav/git/CallGraphAndControlFlow/CallGraph/";
	private static List<String> visitedMethods;
	private static List<String> finalVisitedMethods;
	private static String logFile = STORING_CALL_GRAPH
			+ TARGET_JAR_LOCATION
					.substring(TARGET_JAR_LOCATION.lastIndexOf("/") + 1)
					.replaceAll(".jar", "").replaceAll(".war", "") + ".txt";

	private static String START, END;

	public static void getCallGraph() {
		String cmd1 = "java -jar " + CALL_GRAPH_JAR + " " + TARGET_JAR_LOCATION
				+ " > " + logFile;

		System.out.println(cmd1);
		try {
			String[] cmd = { "/bin/sh", "-c", cmd1 };

			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {

			}

			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			stdInput.close();
			stdError.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DirectedGraph<String> generateCallGraph() {
		DirectedGraph<String> graph = new DirectedGraph<String>();
		BufferedReader b = null;
		try {
			b = new BufferedReader(new FileReader(logFile));

			String line = b.readLine();

			while (line != null) {
				StringTokenizer token = new StringTokenizer(line, " ");
				String source = token.nextToken().substring(2);
				String dest = token.nextToken();

				if (dest.startsWith("(O)") || dest.startsWith("(M)")
						|| dest.startsWith("(S)") || dest.startsWith("(I)")) {
					dest = dest.substring(3);
				}

				// System.out.println(source+" "+dest);
				graph.addNode(source);
				graph.addNode(dest);
				graph.addEdge(source, dest);
				line = b.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return graph;
	}

	@SuppressWarnings("rawtypes")
	public static void searchParents(String method, DirectedGraph<String> graph)
			throws Exception {
		BufferedWriter r = new BufferedWriter(new FileWriter(new File(START
				+ ".txt"), true));
		Iterator<?> it = graph.mGraph.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pair = (Map.Entry) it.next();

			if (!finalVisitedMethods.contains(pair.getKey())) {
				if (graph.mGraph.get(pair.getKey()).contains(method)) {
					System.out.println(pair.getKey());
					finalVisitedMethods.add(pair.getKey().toString());
					r.append(pair.getKey().toString());
					r.newLine();
					searchParents(pair.getKey().toString(), graph);
				}
			}

		}

		r.close();
	}

	public static void main(String[] args) throws Exception {

		// getCallGraph();
		DirectedGraph<String> graph = generateCallGraph();
		visitedMethods = new ArrayList<String>();
		finalVisitedMethods = new ArrayList<String>();
		START = "org.apache.hadoop.conf.Configuration:findSubVariable";
		searchParents(START, graph);

	}

}
