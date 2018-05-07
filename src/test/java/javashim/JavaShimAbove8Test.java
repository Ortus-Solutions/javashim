package javashim;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.net.URLClassLoader;

class JavaShimAbove8Test {

    @BeforeAll
    static void beforeAll() {
        System.setProperty(JavaShimClassLoader.LOGLEVEL_PROPERTY, "trace");
    }


    @Test
    void findInternalSunVM() throws ClassNotFoundException {
        JavaShimClassLoader loader;

        System.setProperty("java.version", "1.9");
        loader = new JavaShimClassLoader(new URLClassLoader(new URL[0]));
        loader.loadClass("sun.misc.VM");
        loader.findClass("sun.misc.VM");

        System.setProperty("java.version", "1.8");
        loader = new JavaShimClassLoader(new URLClassLoader(new URL[0]));
        loader.loadClass("sun.misc.VM");
        loader.findClass("sun.misc.VM");


    }
}
