package server;

import com.artem.server.api.HttpHandler;
import com.artem.server.api.WebHandler;
import utils.CommonConstants;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationLoader {

    private static final String START_TO_FILE_PATH = "jar:file:";
    private static final String END_TO_FILE_PATH = "!/";
    private static final String CLASS_FILE = ".class";
    private static final String JAR_FILE = ".jar";
    private static final int START_OF_CLASS_NAME = 0;
    private static final int CLASS_TYPE_FILE_EXTENSION = 6;

    public ApplicationLoader() {

    }

    public Map<String, HttpHandler> load(String pathToCatalogWithURLModules) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, HttpHandler> urlHandlers = new HashMap<>();
        File catalog = new File(pathToCatalogWithURLModules);
        String[] filesInCatalog = catalog.list();
        assert filesInCatalog != null;
        for (String file : filesInCatalog) {
            if (file.contains(JAR_FILE)) {
                JarFile jarFile = new JarFile(pathToCatalogWithURLModules + file);
                Enumeration<JarEntry> enumeration = jarFile.entries();
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(START_TO_FILE_PATH + pathToCatalogWithURLModules +
                        file + END_TO_FILE_PATH)});
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = enumeration.nextElement();
                    if (jarEntry.getName().endsWith(CLASS_FILE)) {
                        loadClass(urlClassLoader, jarEntry, urlHandlers);
                    }
                }
            }
        }
        return urlHandlers;
    }

    private void loadClass(URLClassLoader urlClassLoader, JarEntry jarEntry, Map<String, HttpHandler> urlHandlers) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String className = jarEntry.getName().substring(START_OF_CLASS_NAME, jarEntry.getName().length() - CLASS_TYPE_FILE_EXTENSION);
        className = className.replace(CommonConstants.SLASH, CommonConstants.DOT);
        Class clazz = urlClassLoader.loadClass(className);
        Annotation annotation = clazz.getAnnotation(WebHandler.class);
        if (annotation instanceof WebHandler) {
            WebHandler mAnnotation = (WebHandler) annotation;
            Constructor constructor = clazz.getDeclaredConstructor();
            Object object = constructor.newInstance();
            urlHandlers.put(mAnnotation.url(), (HttpHandler) object);
        }
    }
}
