package com.wxmp.wxmoreway.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class XmlUtil {
    public static String toXml(Map<String, String> params) {
        //ISO8859-1
        StringBuffer xml = new StringBuffer();
        //xml.append("<?xml version='1.0' encoding='ISO8859-1' standalone='yes' ?><xml>");
        xml.append("<xml>");
        ArrayList<String> arr = new ArrayList<String>();
        for (String key : params.keySet()) {
            if (params.get(key) != null && !params.get(key).equals("")) {
                arr.add(key);
            }
        }
        Collections.sort(arr);
        for (int i = 0; i < arr.size(); i++) {
            String k = arr.get(i);
            if (params.get(k) != null) {
                String v = params.get(k);
                xml.append("<" + k + ">" + v + "</" + k + ">");
            }
        }

        xml.append("</xml>");
        String xml2 = "";
        try {
            xml2 = new String(xml.toString().getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xml2;
    }
    //xml字符串转map集合
    public static Map<String, Object> toMap(String xml) {


        Map<String, Object> result = new HashMap<String, Object>();

        if(xml.equals("")){
            return result;
        }

        try {
            StringReader read = new StringReader(xml);
            InputSource source = new InputSource(read);
            SAXBuilder sb = new SAXBuilder();

            Document doc = (Document) sb.build(source);
            Element root = doc.getRootElement();
            result.put(root.getName(), root.getText());
            result = parse(root, result);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.remove("xml");
        return result;
    }

    private static Map<String, Object> parse(Element root,
                                             Map<String, Object> result) {
        List<Element> nodes = root.getChildren();
        int len = nodes.size();
        if (len == 0) {
            result.put(root.getName(), root.getText());
        } else {
            for (int i = 0; i < len; i++) {
                Element element = (Element) nodes.get(i);// 循环依次得到子元素
                result.put(element.getName(), element.getText());
                // parse(element,result);
            }
        }
        return result;
    }
}
