import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;

public class Main {

    //Hashset is required to avoid duplicate edges for instance: A--C & C--A should be same
    static HashSet<Edge> hashSet = new HashSet<Edge>();


    public static void main(String[] args) throws Exception {
        File file = new File(".\\out\\production\\Parser\\uml-parser-test-2");

        File a[] = file.listFiles();
        String b = "";

        for (File F : a) {
            if (F.getName().endsWith(".java")) {
                CompilationUnit cu = JavaParser.parse(F);
                ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
                if(!cd.isInterface()) {
                    b += "class " + cu.getTypes().get(0).getName() + " { " + "\n" + accessMembers(cu.getTypes().get(0)) + " } " + "\n";
                }
                else{
                    b += "interface " + cu.getTypes().get(0).getName() + " { " + "\n" + accessMembers(cu.getTypes().get(0)) + " } " + "\n";

                }
            }
        }
        generateUML(b);
    }


    private static String accessMembers(TypeDeclaration td) {
        String am = "";
        List<BodyDeclaration> members = td.getMembers();
        for (BodyDeclaration m : members) {
            if (m.getClass().equals(FieldDeclaration.class)) {

                FieldDeclaration fd = (FieldDeclaration) m;
                Type t = fd.getVariables().get(0).getType();
                //Type t = fd.getElementType()  int[]-- int Does Wrapping of array hence changed the jar

                if (t.getClass().equals(PrimitiveType.class) || t.getClass().equals(ArrayType.class)) {
                    if (fd.isPublic()) {
                        am += "+ ";
                    } else if (fd.isPrivate()) {
                        am += "- ";
                    } else if (fd.isProtected()) {
                        am += "# ";
                    }

                    //Used to Parse the variable names
                    am += fd.getVariables().get(0).toString();

                    //Used to Parse the datatypes
                    am += " : " + fd.getElementType().toString();

                    //Used to Parse the variable names
                    if (t.getClass().equals(ArrayType.class)) {
                        am += "[]";
                    }

                    am += "\n";
                } else {
                    //Used to remove Collection Keyword from the class variables
                    if (!t.getElementType().asString().contains("Collection")) {
                        Edge e = new Edge();
                        e.addVertex(t.getElementType().asString());
                        e.addVertex(td.getName().asString());
                        e.setSrcCardnality("1");
                        e.setDestCardnality("1");
                        if (!hashSet.contains(e))
                            hashSet.add(e);
                    } else {
                        Edge e = new Edge();
                        e.addVertex(((ClassOrInterfaceType) t.getElementType().getChildNodes().get(1)).getName().asString());
                        e.addVertex(td.getName().asString());
                        e.setSrcCardnality("1");
                        e.setDestCardnality("*");
                        if (!hashSet.contains(e))
                            hashSet.add(e);
                    }
                }
            }
        }
        return am;
    }


    private static void generateUML(String b) throws Exception {
        OutputStream png = new FileOutputStream("output.png");

        //System.out.println(b);
        //
        String source = "@startuml\n";
        source += b;
        for (Edge e : hashSet) {
            source += e.getVertices().toArray()[0] + " \"" + e.getSrcCardnality() + "\" -- \"" + e.getDestCardnality() + "\" " + e.getVertices().toArray()[1] + "\n";
        }
        source += "@enduml\n";
        System.out.println(source);

        //        b="@startuml\n" +
        //                "class A {\n" +
        //                "-x\n" +
        //                "-y\n" +
        //                "}\n" +
        //
        //                "C --> \"1\" A \n" +
        //                "class B {\n" +
        //                "}\n" +
        //                "class C {\n" +
        //                "}\n" +
        //                "@enduml";
        //
        //        System.out.println(b);


        //        String source = "@startuml\n";
        //        source += "class ClassA {\n" +
        //            source +=   "\tint id\n " +
        //            source +=    "}\n" +
        //        source += "@enduml\n";


        //        String source = "@startuml\n";
        //        source += "Bob -> Alice : hello\n";
        //        source += "@enduml\n";

        //        String source = "@startuml\n";
        //        source += "class ClassA {\n" +
        //                "\tint id\n " +
        //                "}\n";
        //        source += "ClassA -- ClassC\n";
        //        source += "ClassA -- \"*\" ClassB\n";
        //        source += "ClassA -- \"*\" ClassD\n";
        //        source += "@enduml\n";

        //     source +=   "@enduml";*/

        SourceStringReader reader = new SourceStringReader(source);
        // Write the first image to "png"
        String desc = reader.outputImage(png).getDescription();
    }

}

