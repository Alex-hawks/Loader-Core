package io.github.alex_hawks.loader.core;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Scanner
{
    public static File[] scanDir(@NotNull String dir)
    {
        File fil = new File(dir);
        if (fil.isDirectory())
        {
            String[] fils = fil.list();
            File[] files = new File[fils.length];
            int i = 0;
            for (String sfile : fil.list())
            {
                if (sfile.endsWith(".jar") && !getJarFile().contains(sfile))
                {
                    files[i++] = new File(sfile);
                }
            }
            File[] filess = new File[i];
            System.arraycopy(files, 0, filess, 0, i);
            return filess;
        }
        return new File[0];
    }
    
    @NotNull private static String getJarFile()
    {
        try
        {
            String path = Scanner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            return decodedPath;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
