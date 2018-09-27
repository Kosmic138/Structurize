package com.structurize.coremod.repomanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

import com.structurize.api.util.Log;

import org.apache.commons.io.FileUtils;

public class NetUtils
{
    /**
     * Private constructor to hide implicit public one.
     */
    private NetUtils()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * HTTPS GET METHOD getter
     */
    public static String httpsGet(final String https_url) {
        String ret = "";

        try {
            URL url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            ret = new BufferedReader(new InputStreamReader(con.getInputStream())).lines().collect(Collectors.joining(" "));
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * HTTPS GET METHOD schematic getter
     */
    public static void schematicHttpsGet(final String https_url, final String pathToSave)
    {
        try
        {
            FileUtils.copyURLToFile(new URL(https_url), new File(pathToSave));
        }
        catch (IOException e)
        {
            Log.getLogger().warn("Failed to get: " + https_url);
        }
    }
}