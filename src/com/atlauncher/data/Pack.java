/**
 * Copyright 2013 by ATLauncher and Contributors
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.atlauncher.data;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.atlauncher.App;
import com.atlauncher.gui.Utils;

public class Pack {

    private int id;
    private String name;
    private boolean createServer;
    private boolean leaderboards;
    private boolean logging;
    private boolean latestlwjgl;
    private String[] versions;
    private String[] noUpdateVersions;
    private String[] minecraftVersions;
    private String[] devVersions;
    private String[] devMinecraftVersions;
    private String[] testers;
    private String description;
    private String supportURL;
    private String websiteURL;

    private String xml; // The XML
    private String xmlVersion; // The version the XML is for

    public Pack(int id, String name, boolean createServer, boolean leaderboards, boolean logging,
            boolean latestlwjgl, String[] versions, String[] noUpdateVersions,
            String[] minecraftVersions, String[] devVersions, String[] devMinecraftVersions,
            String[] testers, String description, String supportURL, String websiteURL) {
        this.id = id;
        this.name = name;
        this.createServer = createServer;
        this.leaderboards = leaderboards;
        this.logging = logging;
        this.latestlwjgl = latestlwjgl;
        this.versions = versions;
        this.noUpdateVersions = noUpdateVersions;
        this.testers = testers;
        this.minecraftVersions = minecraftVersions;
        this.devVersions = devVersions;
        this.devMinecraftVersions = devMinecraftVersions;
        this.description = description;
        this.supportURL = supportURL;
        this.websiteURL = websiteURL;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ImageIcon getImage() {
        File imageFile = new File(App.settings.getImagesDir(), getSafeName().toLowerCase() + ".png");
        if (!imageFile.exists()) {
            imageFile = new File(App.settings.getImagesDir(), "defaultimage.png");
        }
        return Utils.getIconImage(imageFile);
    }

    /**
     * Gets a file safe and URL safe name which simply means replacing all non alpha numerical
     * characters with nothing
     * 
     * @return File safe and URL safe name of the pack
     */
    public String getSafeName() {
        return this.name.replaceAll("[^A-Za-z0-9]", "");
    }

    public String[] getVersions() {
        return this.versions;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSupportURL() {
        return this.supportURL;
    }

    public String getWebsiteURL() {
        return this.websiteURL;
    }

    public int getVersionCount() {
        return this.versions.length;
    }

    public int getDevVersionCount() {
        return this.devVersions.length;
    }

    public String getVersion(int index) {
        return this.versions[index];
    }

    public String getDevVersion(int index) {
        return this.devVersions[index];
    }

    public String getMinecraftVersion(int index) {
        return this.minecraftVersions[index];
    }

    public String getDevMinecraftVersion(int index) {
        return this.devMinecraftVersions[index];
    }

    public String getXML(String version) {
        return getXML(version, true);
    }

    public String getXML(String version, boolean redownload) {
        if (this.xml == null || !this.xmlVersion.equalsIgnoreCase(version)
                || (isTester() && redownload)) {
            String path = "packs/" + getSafeName() + "/versions/" + version + "/Configs.xml";
            String versionURL = App.settings.getFileURL(path); // The XML with path on server
            this.xml = Utils.urlToString(versionURL);
            while (xml == null) {
                if (App.settings.disableServerGetNext()) {
                    versionURL = App.settings.getFileURL(path); // The XML with path on server
                    this.xml = Utils.urlToString(versionURL); // Try again with another server
                } else {
                    return null;
                }
            }
            this.xmlVersion = version;
        }
        return this.xml;
    }

    public String getMinecraftVersion(String version) {
        String xml = getXML(version, false);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("minecraft");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList nodeList1 = element.getChildNodes();
                    return nodeList1.item(0).getNodeValue();
                }
            }
        } catch (SAXException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (ParserConfigurationException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (IOException e) {
            App.settings.getConsole().logStackTrace(e);
        }
        return null;
    }

    public int getMemory(String version) {
        String xml = getXML(version, false);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("memory");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList nodeList1 = element.getChildNodes();
                    return Integer.parseInt(nodeList1.item(0).getNodeValue());
                }
            }
        } catch (SAXException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (ParserConfigurationException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (IOException e) {
            App.settings.getConsole().logStackTrace(e);
        }
        return 0;
    }

    public int getPermGen(String version) {
        String xml = getXML(version, false);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("permgen");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList nodeList1 = element.getChildNodes();
                    return Integer.parseInt(nodeList1.item(0).getNodeValue());
                }
            }
        } catch (SAXException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (ParserConfigurationException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (IOException e) {
            App.settings.getConsole().logStackTrace(e);
        }
        return 0;
    }

    public String getColour(String version, String name) {
        String xml = getXML(version, false);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("colour");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getAttribute("name").equalsIgnoreCase(name)) {
                        return element.getAttribute("code").replace("#", "");
                    }
                }
            }
        } catch (SAXException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (ParserConfigurationException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (IOException e) {
            App.settings.getConsole().logStackTrace(e);
        }
        return null;
    }

    public ArrayList<Mod> getMods(String versionToInstall, boolean isServer) {
        ArrayList<Mod> mods = new ArrayList<Mod>(); // ArrayList to hold the mods
        String xml = getXML(versionToInstall);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("mod");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getAttribute("name");
                    String version = element.getAttribute("version");
                    String url = element.getAttribute("url");
                    String file = element.getAttribute("file");
                    String website = element.getAttribute("website");
                    String donation = element.getAttribute("donation");
                    Color colour = null;
                    if (element.hasAttribute("colour")) {
                        String tempColour = getColour(versionToInstall,
                                element.getAttribute("colour"));
                        if (tempColour != null && tempColour.length() == 6) {
                            int r, g, b;
                            try {
                                r = Integer.parseInt(tempColour.substring(0, 2), 16);
                                g = Integer.parseInt(tempColour.substring(2, 4), 16);
                                b = Integer.parseInt(tempColour.substring(4, 6), 16);
                                colour = new Color(r, g, b);
                            } catch (NumberFormatException e) {
                                colour = null;
                            }
                        }
                    }
                    String md5 = element.getAttribute("md5");
                    Type type = Type.valueOf(element.getAttribute("type").toLowerCase());
                    ExtractTo extractTo = null;
                    String decompFile = null;
                    DecompType decompType = null;
                    if (type == Type.extract) {
                        extractTo = ExtractTo.valueOf(element.getAttribute("extractto")
                                .toLowerCase());
                    } else if (type == Type.decomp) {
                        decompFile = element.getAttribute("decompFile");
                        decompType = DecompType.valueOf(element.getAttribute("decomptype")
                                .toLowerCase());
                    }
                    boolean client = true;
                    if (element.getAttribute("client").equalsIgnoreCase("no")) {
                        client = false;
                        if (!isServer) {
                            continue; // Don't add this mod as its specified as server only
                        }
                    }
                    boolean server = true;
                    String serverURL = null;
                    String serverFile = null;
                    Type serverType = null;
                    if (element.getAttribute("server").equalsIgnoreCase("seperate")) {
                        server = false;
                        serverURL = element.getAttribute("serverurl");
                        serverFile = element.getAttribute("serverfile");
                        serverType = Type.valueOf(element.getAttribute("servertype").toLowerCase());
                    } else if (element.getAttribute("server").equalsIgnoreCase("no")) {
                        server = false;
                        if (isServer) {
                            continue;
                        }
                    }
                    boolean optional = false;
                    if (element.getAttribute("optional").equalsIgnoreCase("yes")) {
                        optional = true;
                    }
                    boolean serverOptional = optional;
                    if (element.getAttribute("serveroptional").equalsIgnoreCase("yes")) {
                        serverOptional = true;
                    } else if (element.getAttribute("serveroptional").equalsIgnoreCase("no")) {
                        serverOptional = false;
                    }
                    Download download = Download.valueOf(element.getAttribute("download")
                            .toLowerCase());
                    boolean hidden = false;
                    if (element.getAttribute("hidden").equalsIgnoreCase("yes")) {
                        hidden = true;
                    }
                    boolean library = false;
                    if (element.getAttribute("library").equalsIgnoreCase("yes")) {
                        library = true;
                    }
                    String group = element.getAttribute("group");
                    String linked = element.getAttribute("linked");
                    String[] depends;
                    if (element.hasAttribute("depends")) {
                        String dependTemp = element.getAttribute("depends");
                        if (dependTemp.contains(",")) {
                            depends = dependTemp.split(",");
                        } else {
                            depends = new String[] { dependTemp };
                        }
                    } else {
                        depends = null;
                    }
                    boolean recommended = true;
                    if (element.getAttribute("recommended").equalsIgnoreCase("no")) {
                        recommended = false;
                    }

                    String description = element.getAttribute("description");
                    mods.add(new Mod(name, version, url, file, website, donation, colour, md5,
                            type, extractTo, decompFile, decompType, client, server, serverURL,
                            serverFile, serverType, optional, serverOptional, download, hidden,
                            library, group, linked, depends, recommended, description));
                }
            }
        } catch (SAXException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (ParserConfigurationException e) {
            App.settings.getConsole().logStackTrace(e);
        } catch (IOException e) {
            App.settings.getConsole().logStackTrace(e);
        }
        return mods;
    }

    public boolean hasVersions() {
        if (this.versions.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasTesters() {
        if (this.testers.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isTester() {
        Account account = App.settings.getAccount();
        if (account == null) {
            return false;
        }
        for (String tester : testers) {
            if (tester.equalsIgnoreCase(account.getMinecraftUsername())) {
                return true;
            }
        }
        return false;
    }

    public boolean canInstall() {
        if (hasVersions() || isTester()) {
            return true;
        }
        return false;
    }

    public boolean canCreateServer() {
        return this.createServer;
    }

    public boolean isNewInstallMethod(String version) {
        if (App.settings.getMinecraftInstallMethod(getMinecraftVersion(version)).equalsIgnoreCase(
                "new")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLatestVersionNoUpdate() {
        for (String version : noUpdateVersions) {
            if (getLatestVersion().equalsIgnoreCase(version)) {
                return true;
            }
        }
        return false;
    }

    public String getLatestVersion() {
        return this.versions[0];
    }

    public boolean isLeaderboardsEnabled() {
        return this.leaderboards;
    }

    public boolean isLoggingEnabled() {
        return this.logging;
    }

    public boolean isLatestLWJGLEnabled() {
        return this.latestlwjgl;
    }
}
