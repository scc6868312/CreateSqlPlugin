package com.scc.createsqlplugin.config;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * @author: scc
 * @description:
 * @date:2023/3/6
 */
public class ChoiceTable {

    public static Table<Pair<String, String>, String, String> parseChoiceTable(String path) {
        Table<Pair<String, String>, String, String> table = new RowKeyTable<>();
        Document document = XmlUtil.readXML(path + "table.xml");
        Element documentElement = document.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            putTable(table, node);
        }

        return table;
    }

    private static void putTable(Table<Pair<String, String>, String, String> table, Node node) {

        String name = node.getAttributes().getNamedItem("name").getNodeValue();
        String type = node.getAttributes().getNamedItem("type").getNodeValue();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String value = childNode.getTextContent();
            String dbType = childNode.getNodeName();
            if (dbType.contains("#")) {
                continue;
            }
            if (name.equalsIgnoreCase("DDL")) {
                value = File.separator + dbType + File.separator + "TablePatch" + File.separator + value;
            } else {
                value = File.separator + dbType + File.separator + "DataPatch" + File.separator + value;
            }
            table.put(Pair.of(name, type), dbType, value);
        }


    }


}
