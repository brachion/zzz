package zzz.x;


public class Main {

    public static void main(String[] args) {
        try {
            new TableSchemaXml2MarkdownParser().parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
