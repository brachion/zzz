package zzz.x;

import java.io.InputStream;
import java.util.Properties;

public enum Prop {
    P,
    ;

    private static final String FILE_NAME = "/zzz.properties";
    private final Properties PROP = new Properties();

    private Prop() {

        try (InputStream in = Prop.class.getResourceAsStream(FILE_NAME)) {

            PROP.load(in);

            System.out.println(PROP);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSrcDir() {
        return PROP.getProperty("src_dir", "/sample/xml");
    }

    public String getDestDir() {
        return PROP.getProperty("dest_dir", "/sample/md");
    }
}
