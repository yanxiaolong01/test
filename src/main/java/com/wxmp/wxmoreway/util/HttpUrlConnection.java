package com.wxmp.wxmoreway.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnection {
    public static String httpXML(String url, String xml) {
        String result = null;
        try {
            InputStream is = httpPostXML(url, xml);
            BufferedReader in = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private static InputStream httpPostXML(String url, String xml) {
        InputStream is = null;

        PrintWriter out = null;
        try {
            URL u;
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // conn.setRequestProperty("Content-Type",
            // "application/json;charset=UTF-8");
            // conn.setRequestProperty("Accept-Charset", "UTF-8");
            // conn.setRequestProperty("contentType", "UTF-8");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(50000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            OutputStreamWriter outWriter = new OutputStreamWriter(
                    conn.getOutputStream(), "UTF-8");
            out = new PrintWriter(outWriter);

            out.print(xml);
            out.flush();
            out.close();
            is = conn.getInputStream();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return is;
    }
}
