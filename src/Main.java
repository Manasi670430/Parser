import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
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
    static HashSet<Edge> hashSetAttributes = new HashSet<Edge>();
    static HashSet<CommonEdge> hashSetImplements = new HashSet<CommonEdge>();
    static HashSet<CommonEdge> hashSetExtends = new HashSet<CommonEdge>();
    static HashSet<CommonEdge> hashSetParameters = new HashSet<CommonEdge>();


    public static void main(String[] args) throws Exception {
        File file = new File(".\\out\\production\\Parser\\uml-parser-test-5");

        File a[] = file.listFiles();
        String b = "";

        for (File F : a) {
            if (F.getName().endsWith(".java")) {
                CompilationUnit cu = JavaParser.parse(F);
                ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
                if (!cd.isInterface()) {
                    for (ClassOrInterfaceType c : cd.getImplementedTypes()) {
                        System.out.println(cu.getTypes().get(0).getName() + " Implements " + c.getName());
                        CommonEdge e = new CommonEdge(cu.getTypes().get(0).getName().asString(), c.getName().asString());
                        if (!hashSetImplements.contains(e))
                            hashSetImplements.add(e);
                    }

                    for (ClassOrInterfaceType c : cd.getExtendedTypes()) {
                        System.out.println(cu.getTypes().get(0).getName() + " extends " + c.getName());
                        CommonEdge e = new CommonEdge(cu.getTypes().get(0).getName().asString(), c.getName().asString());
                        if (!hashSetExtends.contains(e))
                            hashSetExtends.add(e);
                    }


                    //am += md.getType() + " : " + md.getName().asString() + "()";
                    //b += ((ClassOrInterfaceDeclaration) cu.getTypes().get(0)).getConstructors();
                    b += "class " + cu.getTypes().get(0).getName() + " { " + "\n" + accessMembers(cu.getTypes().get(0)) + accessMethods(cu.getTypes().get(0).getMethods()) + accessContructor(((ClassOrInterfaceDeclaration) cu.getTypes().get(0)).getConstructors()) + " } " + "\n";
                } else {
                    b += "interface " + cu.getTypes().get(0).getName() + " { " + "\n" + accessMembers(cu.getTypes().get(0)) + accessMethods(cu.getTypes().get(0).getMethods())+ " } " + "\n";
                }
            }
        }
        generateUML(b);
    }

    private static String accessContructor(List<ConstructorDeclaration> cd) {
        String string = "";



        for (ConstructorDeclaration cc : cd) {

            string += cc.getName().asString() + "(";

            if (!cc.getParameters().isEmpty()) {

                for (Parameter p : cc.getParameters()) {
                    if (!p.getType().asString().equals("String")) {

                        string += p.getType() + " : " + p.getName().asString() + ")\n";

                    }


                }
            }else {
                string += ")\n";
            }
        }
        return string;
    }

    private static String accessMethods(List<MethodDeclaration> md) {
        String string = "";

        for (MethodDeclaration mm : md) {

            if (!(mm.getName().asString().startsWith("set")) && !(mm.getName().asString().startsWith("get"))) {
                if (mm.getName().asString().equals(mm.getClass().getName())) {
                    System.out.println("Hey");
                }
                string += mm.getType() + " : " + mm.getName().asString() + "()\n";
            }
        }
        return string;
    }

    private static String accessMembers(TypeDeclaration td) {
        String am = "";
        List<BodyDeclaration> members = td.getMembers();
        for (BodyDeclaration m : members) {
            if (m.getClass().equals(FieldDeclaration.class)) {

                FieldDeclaration fd = (FieldDeclaration) m;
                Type t = fd.getVariables().get(0).getType();
                //Type t = fd.getElementType()  int[]-- int Does Wrapping of array hence changed the jar

                if ((t.getClass().equals(PrimitiveType.class) || t.getClass().equals(ArrayType.class) ||
                        ((ClassOrInterfaceType) t).getName().asString().equals("String"))) {

                    if (fd.isPrivate() || fd.isPublic()) {
                        if (fd.isPublic()) {
                            am += "+ ";
                        } else if (fd.isPrivate()) {
                            am += "- ";
                        }

                        //Used to Parse the variable names
                        am += fd.getVariables().get(0).toString();

                        //Used to Parse the datatypes
                        am += " : " + fd.getElementType().toString();

                        //Used to Parse the variable names
                        if (t.getClass().equals(ArrayType.class)) {
                            am += "[]";
                        }

//                        if(md.getName().asString().startsWith("set"))
//                        am += "saaa";
                        am += "\n";
                    }

                } else {
                    //Used to remove Collection Keyword from the class variables
                    if (!t.getElementType().asString().contains("Collection") &&
                            !((ClassOrInterfaceType) t).getName().asString().equals("String")) {
                        Edge e = new Edge();
                        e.addVertex(t.getElementType().asString());
                        e.addVertex(td.getName().asString());
                        e.setSrcCardnality("1");
                        e.setDestCardnality("1");
                        if (!hashSetAttributes.contains(e))
                            hashSetAttributes.add(e);
                    } else {
                        Edge e = new Edge();
                        e.addVertex(((ClassOrInterfaceType) t.getElementType().getChildNodes().get(1)).getName().asString());
                        e.addVertex(td.getName().asString());
                        e.setSrcCardnality("1");
                        e.setDestCardnality("*");
                        if (!hashSetAttributes.contains(e))
                            hashSetAttributes.add(e);
                    }
                }
            } else if (m.getClass().equals(MethodDeclaration.class)) {
                MethodDeclaration md = (MethodDeclaration) m;

                //Type t = md.getParameters().get(0).getType();
                if (!(md.getName().asString().startsWith("set")) && !(md.getName().asString().startsWith("get"))) {


                    if (!md.getParameters().isEmpty()) {
                        for (Parameter p : md.getParameters()) {
                            if ((!p.getType().asString().equals("String")) && (!p.getType().asString().equals("String[]"))) {


                                CommonEdge e = new CommonEdge(td.getName().asString(), p.getType().asString());
                                if (!hashSetParameters.contains(e))
                                    hashSetParameters.add(e);
                            }


                        }
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
        for (Edge e : hashSetAttributes) {
            source += e.getVertices().toArray()[0] + " \"" + e.getSrcCardnality() + "\" -- \"" + e.getDestCardnality() + "\" " + e.getVertices().toArray()[1] + "\n";
        }
        for (CommonEdge e : hashSetImplements) {
            source += e.getSource() + " ..|> " + e.getDestination() + "\n";
        }
        for (CommonEdge e : hashSetExtends) {
            source += e.getSource() + " --|> " + e.getDestination() + "\n";
        }
        for (CommonEdge e : hashSetParameters) {
            source += e.getSource() + " " + "\"uses\"" + " " + " ..> " + e.getDestination() + "\n";
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

