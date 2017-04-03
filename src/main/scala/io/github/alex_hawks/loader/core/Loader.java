package io.github.alex_hawks.loader.core;

import io.github.alex_hawks.loader.api.Core;
import io.github.alex_hawks.loader.api.Load;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Loader
{
    private final Core core;
    private final String packages;
    
    public Loader(Core core, String packages)
    {
        this.core = core;
        this.packages = packages;
        System.out.println("Packages: " + packages);
    }
    
    @SuppressWarnings("unchecked")
    public void loadPlugins()
    {
        File[] files = Scanner.scanDir(new File("").getAbsolutePath());
    
        System.out.println("Path: " + new File("").getAbsolutePath());
        for (File file : files)
        {
            System.out.println("Found file: " + file.toString());
        }
        
        for (File file : files)
        {
            URLClassLoader cl = null;
            try
            {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> e = jarFile.entries();
                
                URL[] urls = {new URL("jar:file:" + file.toString() + "!/")};
                cl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());
                
                while (e.hasMoreElements())
                {
                    JarEntry je = e.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class"))
                    {
                        continue;
                    }
                    // -6 because of .class
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    cl.loadClass(className);
                    System.out.println("Loaded class: " + className);
                }
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .addUrls(ClasspathHelper.forClassLoader(cl))
                    .setScanners(new MethodAnnotationsScanner())
                    .filterInputsBy(new FilterBuilder().includePackage(packages + ".*"))
                    .addClassLoader(cl));
            Set<Method> annotated = reflections.getMethodsAnnotatedWith(Load.class);
            
            System.out.println("Size: " + annotated.size());
            
            for (Method md : annotated)
            {
                System.out.println("Method: " + md);
                try
                {
                    md.invoke(null, core); // better be static
                    System.out.println("Invoking Method: " + md.getName());
                } catch (InvocationTargetException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
