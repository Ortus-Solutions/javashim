package javashim;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class JavaShimClassLoader extends ClassLoader {

    static final String LOGLEVEL_PROPERTY = "javashim.loglevel";
    private static final String logLevel = System.getProperty(LOGLEVEL_PROPERTY, "WARN");
    private static final String initialJavaVersion = System.getProperty("java.version", "");
    private final String javaVersion;
    private HashMap<String, Class> definedClasses;
    public volatile boolean enabled;

    public JavaShimClassLoader(ClassLoader parent) {
        super(parent);
        javaVersion = System.getProperty("java.version", "");
        definedClasses = new HashMap<>();
        if (!javaVersion.equals(initialJavaVersion)) {
            log.trace(String.format("javashim: Java version has been changed from %s to %s", initialJavaVersion, javaVersion));
        }
        if (isUnderJava9()) {
            log.debug("javashim: Java " + System.getProperty("java.version") + " is 1.8 or below, not doing anything");
            enabled = false;
        } else {
            log.debug("javashim: Java " + System.getProperty("java.version") + " is 1.9 or above, intercepting class lookups");
            enabled = true;
        }
    }

    public static boolean versionLessThanVersion(String javaVersion, String version) {
        return version.matches("^1\\.[" + javaVersion.replace('.','-')+"](\\..*)?");
    }

    static boolean isUnderJava9(String version) {
        return versionLessThanVersion(version, "1.8");
    }

    static boolean isAboveJava8(String version) {
        return !isUnderJava9(version);
    }

    private boolean isUnderJava9() {
        return isUnderJava9(javaVersion);
    }

    private boolean isAboveJava8() {
        return isAboveJava8(javaVersion);
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        if (isAboveJava8()) {
            log.trace("javashim#loadClass: " + name);
            if (name.startsWith("sun.")) {
                log.debug("javashim#loadClass: ******* Sun Class: " + name);
                String classFileName = name.replace(".", "/") + ".class";
                Class<?> definedClass;
                definedClass = definedClasses.get(classFileName);
                if (definedClass != null) {
                    return definedClass;
                }
                try (InputStream is = JavaShimClassLoader.class.getClassLoader().getResourceAsStream(classFileName)) {
                    byte[] buf = new byte[10000];
                    int len = is.read(buf);
                    definedClass = defineClass(name, buf, 0, len);
                    definedClasses.put(classFileName, definedClass);
                    return definedClass;
                } catch (IOException e) {
                    throw new ClassNotFoundException("", e);
                }
            }
        }
        return super.loadClass(name);
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        if (isUnderJava9()) {
            return super.findClass(name);
        }
        log.trace("javashim#findClass: " + name);
        if (name.contains("coldfusion") || name.contains("lucee"))
            log.debug("javashim#findClass *** cfengine class: " + name);
        if (name.startsWith("sun.")) {
            log.debug("javashim#findClass: ******* Sun Class: " + name);
            return loadClass(name);
        }
        return super.findClass(name);
    }

    private static class log {
        static void debug(String message) {
            if (logLevel.equalsIgnoreCase("debug") || logLevel.equalsIgnoreCase("trace")) {
                System.out.println(message);
            }
        }

        static void trace(String message) {
            if (logLevel.equalsIgnoreCase("trace")) {
                System.out.println(message);
            }
        }
    }

}