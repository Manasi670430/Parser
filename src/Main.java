import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        File file = new File(".\\out\\production\\Parser\\uml-parser-test-1");

        File a[] = file.listFiles();
        String b="";
        for (File F : a) {
            if (F.getName().endsWith(".java")) {
                //System.out.println(F.getCanonicalPath());
                CompilationUnit cu = JavaParser.parse(F);
                //System.out.println(cu.toString());
                b += "class " +cu.getTypes().get(0).getName() + "\n";
                System.out.println(b + "\n");
                //System.out.println();

            }
        }



        generateUML(b);


    }



    private static void generateUML(String b) throws Exception {
        OutputStream png = new FileOutputStream("output.png");

        System.out.println(b);

        String source = "@startuml\n";
        source += b;
        source += "@enduml\n";
        System.out.println(source);

//
//        String source = "@startuml\n";
//        source += "class ClassA {\n" +
//            source +=   "\tint id\n " +
//            source +=    "}\n" +
//        source += "@enduml\n";

                System.out.println(source);

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
