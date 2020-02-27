package com.wind.meditor.xml;

import com.wind.meditor.utils.Log;
import com.wind.meditor.utils.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Windysha
 */
public class ResourceIdXmlReader {

    private static final Map<String, Integer> attrCachedMap = new HashMap<>();

    public static int parseIdFromXml(String name) {
        String filePath = "assets/public.xml";
        InputStream inputStream = Utils.getInputStreamFromFile(filePath);
        try {
            Integer cacherId = attrCachedMap.get(name);
            if (cacherId != null && cacherId > 0) {
                return cacherId;
            } else {
//                String id = findIdFromXmlFile(new FileInputStream(filePath2), "attr", name);
                String id = findIdFromXmlFile(inputStream, "attr", name);
                Log.d(String.format("name = %s, id = %s", name, id));
                if (id != null) {
                    int idInt = Integer.parseInt(id.substring(2), 16);
                    attrCachedMap.put(name, idInt);
                    return idInt;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String findIdFromXmlFile(InputStream inputStream, String type, String name) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (builder == null) {
            System.out.println("parse xml failed, DocumentBuilder is null");
            return null;
        }

        Document doc = null;
        try {
            doc = builder.parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (doc == null) {
            System.out.println("parse xml failed, Document is null");
            return null;
        }

        NodeList nodeList = doc.getElementsByTagName("public");

        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap nnm = node.getAttributes();
            String id = findIdByNameAndType(nnm, type, name);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    private static String findIdByNameAndType(NamedNodeMap map, String type, String name) {
        int length = map.getLength();
        String nodeName = null;
        String nodeType = null;
        String nodeId = null;
        for (int i = 0; i < length; i++) {
            Node node = map.item(i);
            if (node != null) {
                String attrName = node.getNodeName();
                String attrValue = node.getNodeValue();

                if ("type".equals(attrName)) {
                    nodeType = attrValue;
                } else if ("name".equals(attrName)) {
                    nodeName = attrValue;
                } else if ("id".equals(attrName)) {
                    nodeId = attrValue;
                }
            }
        }

        if (nodeName != null && nodeName.equals(name)
                && nodeType != null && nodeType.equals(type)) {
            try {
                return nodeId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
