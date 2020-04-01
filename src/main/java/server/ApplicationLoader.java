package server;

import com.artem.server.api.HttpHandler;
import com.artem.server.api.WebHandler;
import utils.CommonConstants;
import utils.HTTPUtils;

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

    private static final String JAR_FILE = "jar:file:";
    private static final String END_TO_FILE_PATH = "!/";
    private static final String CLASS = ".class";
    
    public ApplicationLoader() {

    }

    public Map<String, HttpHandler> load(String pathToURLModule) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, HttpHandler> urlHandlers = new HashMap<>();
        JarFile jarFile = new JarFile(pathToURLModule);
        Enumeration<JarEntry> enumeration = jarFile.entries();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(JAR_FILE +
                pathToURLModule + END_TO_FILE_PATH)});
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            if (jarEntry.getName().endsWith(CLASS)) {
                loadClass(urlClassLoader, jarEntry, urlHandlers);
            }
        }
        return urlHandlers;
    }

    private void loadClass(URLClassLoader urlClassLoader, JarEntry jarEntry, Map<String, HttpHandler> urlHandlers) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
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
