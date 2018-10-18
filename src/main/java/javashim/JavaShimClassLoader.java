package javashim;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class JavaShimClassLoader extends ClassLoader {

    static final String LOGLEVEL_PROPERTY = "javashim.loglevel";
    private static final String logLevel = System.getProperty(LOGLEVEL_PROPERTY, "WARN");
    private HashMap<String, Class> definedClasses;

    public JavaShimClassLoader(ClassLoader parent) {
        super(parent);
        definedClasses = new HashMap<>();
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException classNotFound) {
            log.debug("javashim#loadClass: ******* : " + name);
            String classFileName = name.replace(".", "/") + ".class";
            Class<?> definedClass;
            definedClass = definedClasses.get(classFileName);
            if (definedClass != null) {
                return definedClass;
            }
            try (InputStream is = JavaShimClassLoader.class.getClassLoader().getResourceAsStream(classFileName)) {
                if(is == null){
                    getParent().loadClass(name); // this will rethrow a CNFE
                }
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

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        Class<?> targetClass;
        try {
            targetClass = super.findClass(name);
            return targetClass;
        } catch (ClassNotFoundException notFoundException) {
            log.trace("javashim#findClass: " + name);
            if (name.contains("coldfusion") || name.contains("lucee"))
                log.debug("javashim#findClass *** cfengine class: " + name);
            if (name.startsWith("sun.")) {
                log.debug("javashim#findClass: ******* Sun Class: " + name);
                return loadClass(name);
            }
        }
        return null;
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