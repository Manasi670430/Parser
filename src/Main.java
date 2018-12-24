import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.Type;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.List;

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

                b += "class " +cu.getTypes().get(0).getName()+ " { " + "\n" +accessMembers(cu.getTypes().get(0))+ " } "  + "\n";
                //System.out.println("dadddsdsds" +b );
                //System.out.println();

            }
        }

        generateUML(b);
    }


    private static String accessMembers(TypeDeclaration td){
        String am="";
        List<BodyDeclaration> members = td.getMembers();
        for(BodyDeclaration m : members){
            if(m.getClass().equals(FieldDeclaration.class)) {
                FieldDeclaration fd =(FieldDeclaration)m;
                if(fd.getModifiers()== Modifier.PUBLIC){
                    am += "+ " ;
                }
                else if(fd.getModifiers()== Modifier.PRIVATE){
                    am += "- " ;
                }
                else if(fd.getModifiers()== Modifier.PROTECTED){
                    am += "# " ;
                }

                am += fd.getVariables().get(0).toString()  ;

                am+=" : " + fd.getType().toString() + "\n";
            } else if(m.getClass().equals(MethodDeclaration.class)){

            }
        }

        //String s="";
        return am;
    }


    private static void generateUML(String b) throws Exception {
        OutputStream png = new FileOutputStream("output.png");

        //System.out.println(b);

        String source = "@startuml\n";
        source += b;
        source += "@enduml\n";
        System.out.println(source);

//        b="@startuml\n" +
//                "class A {\n" +
//                "-x\n" +
//                "-y\n" +
//                "}\n" +
//                "class B {\n" +
//                "}\n" +
//                "class C {\n" +
//                "}\n" +
//                "class D {\n" +
//                "}\n" +
//                "@enduml";

        //System.out.println(b);


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
