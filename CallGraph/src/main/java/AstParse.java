import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jface.text.Document;

public class AstParse {

	public static void main(String[] args) throws Exception {
		new AstParse().run("");
	}

	private void run(String args) throws Exception {
	

		File src = new File(
				"/Volumes/iPhoto/3rdPaper/CallGraph/src/main/java/Main.java");
		updateFile(src);
	}

	public String getText(File src) throws Exception {
		String text = "";
		BufferedReader b = new BufferedReader(new FileReader(src));
		String line = b.readLine();
		while (line != null) {
			text += line + "\n";
			line = b.readLine();
		}
		b.close();
		return text;
	}

	private void updateFile(File src) throws Exception {
		System.out.println("Updating: " + src);
		String text = getText(src);
		Document doc = new Document(text);

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(doc.get().toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration md) {
				// initialize the field to 0
				// m_nStatementCount = 0;
				return true;
			}

			public void endVisit(MethodDeclaration md) {
				System.out.println("declaring method '" + md.getName() + "' that returns " + md.getReturnType2());
                List<String> parameters = new ArrayList<String>();
                for (Object parameter : md.parameters()) {
                    VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
                    String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY)
                            .toString();
                    for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
                        type += "[]";
                    }
                    parameters.add(type);
                }

                System.out.println(parameters);


			}

			// the visitors below increment the statement count field
			public boolean visit(ReturnStatement node) {
				// m_nStatementCount++;
				return true;
			}

			public boolean visit(ExpressionStatement node) {
				System.out.println("Printing "+node);
				// m_nStatementCount++;
				return true;
			}

			public boolean visit(IfStatement node) {
				// m_nStatementCount++;
				return true;
			}
		});

	}

}