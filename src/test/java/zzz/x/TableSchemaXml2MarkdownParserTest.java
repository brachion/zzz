package zzz.x;

import static org.junit.Assert.*;

import org.junit.Test;

public class TableSchemaXml2MarkdownParserTest {

    @Test
    public void xmlファイルが入力なんすよ() throws Exception {
        new TableSchemaXml2MarkdownParser().parse();
        assertTrue(true);
    }
}
