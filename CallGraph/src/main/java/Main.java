import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import callGraph.DirectedGraph;

public class Main {

	private static String TARGET_JAR_LOCATION = "/Users/Rav/Desktop/stable-3-6/weka/weka.jar";
	// private static String TARGET_JAR_LOCATION =
	// "/Volumes/iPhoto/3rdPaper/Test/target/Test-0.0.1-SNAPSHOT.jar";
	private static String CALL_GRAPH_JAR = "/Users/Rav/Desktop/temp/java-callgraph/target/javacg-0.1-SNAPSHOT-static.jar";
	private static String STORING_CALL_GRAPH = "/Volumes/iPhoto/3rdPaper/CallGraph/";

	private static List<String> visitedMethods;

	public static void getCallGraph() {
		try {
			String[] cmd = {
					"/bin/sh",
					"-c",
					"java -jar "
							+ CALL_GRAPH_JAR
							+ " "
							+ TARGET_JAR_LOCATION
							+ " > "
							+ STORING_CALL_GRAPH
							+ TARGET_JAR_LOCATION.substring(
									TARGET_JAR_LOCATION.lastIndexOf("/"))
									.replaceAll(".jar", "") + ".txt" };

			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String s = null;
			// read the output from the command
			// System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {

			}

			// read any errors from the attempted command
			// System.out
			// .println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			stdInput.close();
			stdError.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DirectedGraph generateCallGraph() {
		DirectedGraph<String> graph = new DirectedGraph<String>();
		BufferedReader b = null;
		try {
			b = new BufferedReader(new FileReader(STORING_CALL_GRAPH
					+ TARGET_JAR_LOCATION.substring(
							TARGET_JAR_LOCATION.lastIndexOf("/")).replaceAll(
							".jar", "") + ".txt"));

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

	public static void searchParents(String method, DirectedGraph<String> graph) {
		Iterator it = graph.mGraph.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			// it.remove(); // avoids a ConcurrentModificationException
			// System.out.println(pair.getKey());
			if (!visitedMethods.contains(pair.getKey())) {

				if (graph.mGraph.get(pair.getKey()).contains(method)) {
					visitedMethods.add(pair.getKey().toString());
					System.out.println(pair.getKey());
					searchParents(pair.getKey().toString(), graph);
				}
			}

		}
	}

	public static void main(String[] args) {
		// getCallGraph();
		DirectedGraph<String> graph = generateCallGraph();
		visitedMethods = new ArrayList<String>();
		searchParents("weka.core.Capabilities:enableAll", graph);
	}

}
