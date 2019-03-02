package loadconf;

import java.io.BufferedReader;
import java.io.FileReader;

public class Loader {
    private static Loader loader;

    public static synchronized Loader getInstance() {
        if (loader == null) {
            loader = new Loader();
        }
        return loader;
    }

    public String getConf(String key) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/etc/ForumServer/ForumServer.conf"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(key) && line.charAt(0) != '#') {
                    if (";".equals(line.split(" = ")[1])) {
                        return "";
                    } else {
                        return line.split(" = ")[1].split(";")[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Loader() { }
}
