package zzz.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TableSchemaXml2MarkdownParser {

    final Prop prop;

    public TableSchemaXml2MarkdownParser() {
        this.prop = Prop.P;
    }

    public void parse() throws IOException {

        ForkJoinPool pool = new ForkJoinPool();

        Files.walkFileTree(Paths.get(prop.getSrcDir()), EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                if (attrs.isDirectory()) {
                    return FileVisitResult.CONTINUE;
                }

                pool.invoke(new RecursiveAction() {

                    @Override
                    protected void compute() {
                        try {
                            __parse(file.toFile().getAbsolutePath());

                        } catch (SAXException | IOException | ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void __parse(final String xmlFilePath) throws SAXException, IOException, ParserConfigurationException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        final List<String> markdown = new ArrayList<>();

        parser.parse(new FileInputStream(new File(xmlFilePath)), new DefaultHandler() {

            @Override
            public void startDocument() throws SAXException {
                markdown.add("# " + new File(xmlFilePath).getName());
                markdown.add("");
            }

            boolean flag = false;
            int idx = 0;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equals("table")) {
                    markdown.add("***");
                    markdown.add("## " + attributes.getValue("name"));
                    markdown.add("### " + attributes.getValue("description"));

                    markdown.add("");

                    flag = true;
                    idx = 0;

                } else if (qName.equals("column")) {
                    if (flag) {
                        markdown.add("|No.|カラム名|説明|型|サイズ|必須|NOT NULL|デフォルト値|");
                        markdown.add("|:-:|:-|:-|:-:|-:|:-:|:-:|:-|");
                    }

                    markdown.add(String.format("|%s|%s|%s|%s|%s|%s|%s|%s|",
                            String.valueOf(++idx),
                            StringUtils.trimToEmpty(attributes.getValue("name")),
                            StringUtils.trimToEmpty(attributes.getValue("description")),
                            StringUtils.trimToEmpty(attributes.getValue("data-type")),
                            StringUtils.trimToEmpty(attributes.getValue("size")),
                            Boolean.valueOf(attributes.getValue("required")) ? "○" : "",
                            Boolean.valueOf(attributes.getValue("nullable")) ? "" : "○",
                            StringUtils.trimToEmpty(attributes.getValue("default-value"))));
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equals("table")) {
                    markdown.add("");
                } else if (qName.equals("column")) {
                    flag = false;
                }
            }
        });

        String markdownFilePath = prop.getDestDir()+"/"+new File(xmlFilePath).getName().replaceFirst("\\.xml$", ".md");
        IOUtils.writeLines(markdown, "\n", new FileOutputStream(new File(markdownFilePath)), "UTF-8");
    }
}
