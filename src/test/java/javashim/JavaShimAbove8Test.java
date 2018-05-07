package javashim;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaShimAbove8Test {

    @BeforeAll
    static void beforeAll() {
        System.setProperty(JavaShimClassLoader.LOGLEVEL_PROPERTY, "trace");
    }

    @Test
    void isAboveJava8() {
        assertFalse(JavaShimClassLoader.isAboveJava8("1.7"));
        assertFalse(JavaShimClassLoader.isAboveJava8("1.7.3"));
        assertFalse(JavaShimClassLoader.isAboveJava8("1.7.45+33"));

        assertFalse(JavaShimClassLoader.isAboveJava8("1.8"));
        assertFalse(JavaShimClassLoader.isAboveJava8("1.8.3"));
        assertFalse(JavaShimClassLoader.isAboveJava8("1.8.45+33"));

        assertTrue(JavaShimClassLoader.isAboveJava8("1.9.0"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.10"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.10.0"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.10+12"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.19"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.11.1"));
        assertTrue(JavaShimClassLoader.isAboveJava8("1.9"));
    }

    @Test
    void isUnderJava9() {
        assertTrue(JavaShimClassLoader.isUnderJava9("1.7"));
        assertTrue(JavaShimClassLoader.isUnderJava9("1.7.3"));
        assertTrue(JavaShimClassLoader.isUnderJava9("1.7.45+33"));

        assertTrue(JavaShimClassLoader.isUnderJava9("1.8"));
        assertTrue(JavaShimClassLoader.isUnderJava9("1.8.3"));
        assertTrue(JavaShimClassLoader.isUnderJava9("1.8.45+33"));

        assertFalse(JavaShimClassLoader.isUnderJava9("1.9.0"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.10"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.10.0"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.10+12"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.19"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.11.1"));
        assertFalse(JavaShimClassLoader.isUnderJava9("1.9"));
    }

    @Test
    void enablement() {
        ClassLoader parent = new URLClassLoader(new URL[0]);

        System.setProperty("java.version", "1.8");
        JavaShimClassLoader loader = new JavaShimClassLoader(parent);
        assertFalse(loader.enabled);

        System.setProperty("java.version", "1.9");
        loader = new JavaShimClassLoader(parent);
        assertTrue(loader.enabled);

        System.setProperty("java.version", "1.10");
        loader = new JavaShimClassLoader(parent);
        assertTrue(loader.enabled);

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
