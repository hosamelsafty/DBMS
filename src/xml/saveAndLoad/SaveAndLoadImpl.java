package xml.saveAndLoad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class SaveAndLoadImpl implements SaveAndLoad {

    ArrayList<String> coulmnNames, coulmnTypes;

    public void save(File file, ArrayList<ArrayList<String>> data, ArrayList<String> coulmnNames,
            ArrayList<String> coulmnTypes, String tableName) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(tableName);
            rootElement.setAttribute("sizeRow", data.size() + "");
            rootElement.setAttribute("sizeCol", coulmnNames.size() + "");
            makeTypesAttributes(rootElement, coulmnNames, coulmnTypes);
            document.appendChild(rootElement);
            for (int i = 0; i < data.size(); i++) {
                ArrayList<String> row = data.get(i);
                Element rowElement = getElement(document, row, i, coulmnNames);
                rootElement.appendChild(rowElement);
            }

            // XML file will be written on console
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, tableName + ".dtd");

            // To make XML File on Hard
            DOMSource source = new DOMSource(document);
            StreamResult resultFile = new StreamResult(file);
            transformer.transform(source, resultFile);

            // Save .dtd
            saveDTD(coulmnNames, tableName, file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<ArrayList<String>> load(File file) {
        Document document;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
            Element tableName = document.getDocumentElement();
            String sizeAtt = tableName.getAttribute("sizeRow");
            int sizeRow = Integer.parseInt(sizeAtt);
            sizeAtt = tableName.getAttribute("sizeCol");
            int sizeCol = Integer.parseInt(sizeAtt);
            coulmnTypes = (ArrayList<String>) FindTypesAttributes(tableName, sizeCol).clone();
            ArrayList<String> colName = loadDTD(file);
            coulmnNames = (ArrayList<String>) colName.clone();
            Element docElements = document.getDocumentElement();
            NodeList data = docElements.getElementsByTagName("row");
            for (int j = 0; j < sizeRow; j++) {
                Node node = data.item(j);
                Element element = (Element) node;
                tableData.add(toArrayList(element, colName));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableData;
    }

    private Element getElement(Document document, ArrayList<String> row, int id, ArrayList<String> coulmnNames) {
        Element rowElement = document.createElement("row");
        rowElement.setAttribute("id", "" + id);
        for (int i = 0; i < row.size(); i++) {
            Element cellElement = document.createElement(coulmnNames.get(i));
            cellElement.appendChild(document.createTextNode(row.get(i)));
            rowElement.appendChild(cellElement);
        }
        return rowElement;
    }

    private Element makeTypesAttributes(Element rootElement, ArrayList<String> coulmnNames,
            ArrayList<String> coulmnTypes) {
        for (int i = 0; i < coulmnNames.size(); i++) {
            rootElement.setAttribute("coulmn" + i, coulmnNames.get(i) + ":" + coulmnTypes.get(i));
        }
        return rootElement;
    }

    private ArrayList<String> FindTypesAttributes(Element rootElement, int sizeCol) {
        ArrayList<String> types = new ArrayList<String>();
        for (int i = 0; i < sizeCol; i++) {
            String type = rootElement.getAttribute("coulmn" + i);
            types.add(type.substring(type.indexOf(":") + 1, type.length()));
        }
        return types;
    }

    private ArrayList<String> toArrayList(Element row, ArrayList<String> colName) {
        ArrayList<String> rowData = new ArrayList<String>();
        NodeList cells = null;
        Node cell = null;
        Element cellVal = null;
        for (int i = 0; i < colName.size(); i++) {
            cells = row.getElementsByTagName(colName.get(i));
            cell = cells.item(0);
            cellVal = (Element) cell;
            rowData.add(cellVal.getTextContent());
        }
        return rowData;
    }

    public ArrayList<String> getCoulmnNames() {
        return coulmnNames;
    }

    public ArrayList<String> getCoulmnTypes() {
        return coulmnTypes;
    }

    public void saveDTD(ArrayList<String> colName, String tableName, String path) {
        path = path.substring(0, path.length() - 4 - tableName.length());
        try {
            File file = new File(path, "" + tableName + ".dtd");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            // * zer0 or more.
            String data = "<!ELEMENT " + tableName + " (row*)>";
            writer.println(data);
            data = "<!ELEMENT row (";
            for (int i = 0; i < colName.size(); i++) {
                data += colName.get(i) + ",";
            }
            data = data.substring(0, data.length() - 1);
            data += ")>";
            writer.println(data);
            for (int i = 0; i < colName.size(); i++) {
                data = "<!ELEMENT " + colName.get(i) + " (#PCDATA)>";
                writer.println(data);
            }
            writer.println();
            data = new String();
            for (int i = 0; i < colName.size(); i++) {
                data = "<!ATTLIST students coulmn" + i + " CDATA #REQUIRED >";
                writer.println(data);
            }
            writer.println("<!ATTLIST " + tableName + " sizeCol CDATA #REQUIRED >");
            writer.println("<!ATTLIST " + tableName + " sizeRow CDATA #REQUIRED >");
            writer.println();
            writer.println("<!ATTLIST row id CDATA #REQUIRED >");

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadDTD(File file) {
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.length() - 4);
        String tableName = fileName;
        fileName += ".dtd";
        String path = file.getPath();
        path = file.getParent();
        file = new File(path, fileName);
        ArrayList<String> colNames = new ArrayList<String>();
        try {
            InputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            try {
                br.readLine();
                String firstLine = br.readLine();
                // 15 & -2 to get rid of the unwanted characters
                firstLine = firstLine.substring(15, firstLine.length() - 2);
                int first = 0;
                for (int i = 0; i < firstLine.length(); i++) {
                    if (firstLine.charAt(i) == ',') {
                        colNames.add(firstLine.substring(first, i));
                        first = i + 1;
                    }
                }
                colNames.add(firstLine.substring(first, firstLine.length()));
                fis.close();
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return colNames;
    }
}
