package com.example.songchiyun.myapplication;

import android.location.Location;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by chiyo on 2016-08-05.
 */
public class XmlWriter {
    private String id;
    private String pw;
    private String newPw;
    private double lat, lng;
    private String heart_rate;
    private Document doc;
    private String type;
    public XmlWriter() {
        this.id = "";
        this.pw = "";
    }



    public Document getLoginXml(String id, String pw) {
        this.id = id;
        this.pw = pw;
        this.type = "Login";
        makeXMLforLogin();
        return doc;

    }
    public Document getXmlForData(String id, Location loc, String heart_rate) {
        this.id = id;
        this.heart_rate = heart_rate;
        lat = loc.getLatitude();
        lng = loc.getLongitude();
        this.type = "Sensor";
        makeXMLforRealtime();

        return doc;

    }

    private void makeXMLforLogin() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            Element root = doc.createElement("Request");
            Element typeElement = doc.createElement("Type");
            typeElement.appendChild(doc.createTextNode(type));
            root.appendChild(typeElement);
            Element idElement = doc.createElement("PatientID");
            idElement.appendChild(doc.createTextNode(id));
            root.appendChild(idElement);
            Element pwElement = doc.createElement("PW");
            pwElement.appendChild(doc.createTextNode(pw));
            root.appendChild(pwElement);
            doc.appendChild(root);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void makeXMLforRealtime() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            Element root = doc.createElement("Request");
            doc.appendChild(root);
            Element typeElement = doc.createElement("Type");
            typeElement.appendChild(doc.createTextNode(type));
            root.appendChild(typeElement);
            Element keyElement = doc.createElement("PatientID");
            keyElement.appendChild(doc.createTextNode(id));
            root.appendChild(keyElement);
            Element idElement = doc.createElement("Lat");
            idElement.appendChild(doc.createTextNode(String.valueOf(lat)));
            Element currentPwElement = doc.createElement("Lng");
            currentPwElement.appendChild(doc.createTextNode(String.valueOf(lng)));
            Element newPwElement = doc.createElement("Heart");
            newPwElement.appendChild(doc.createTextNode(heart_rate));

            root.appendChild(idElement);
            root.appendChild(currentPwElement);
            root.appendChild(newPwElement);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

}
