package com.cyloyalpoint.internal;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CyAppManager {

    private final CyModel cyModel;
    private final CyServices cyServices;

    public CyAppManager(CyModel cyModel, CyServices cyServices) {
        this.cyModel = cyModel;
        this.cyServices = cyServices;

//        cyModel.setSettingsPath(initSettingsPath());
    }

    /**
     * Convenience method to quickly get a formatted current time string
     *
     * @return
     */
    public static String getTimeStamp() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-hh:mm:ss");
        return sdf.format(now);
    }

    /**
     * Convenience method to get a reference to the Cytoscape desktop window.
     * Handy for use as a parent for dialog windows
     *
     * @return
     */
    public static Frame getCytoscapeRootFrame() {
        Frame[] frames = Frame.getFrames();
        Frame csFrame = null;
        for (Frame frame : frames) {
            String className = frame.getClass().toString();
            if (className.endsWith("CytoscapeDesktop")) {
                csFrame = frame;
            }
        }
        return csFrame;
    }

    private Path initSettingsPath() {

        Path cyHomePath;
        Path localSettingsPath;

        //attempt 1: try to find CytoscapeConfiguration folder in user.dir
        cyHomePath = Paths.get(System.getProperty("user.dir"));
        localSettingsPath = getCyConfFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        //attempt 2: try to find CytoscapeConfiguration folder in user.home
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getCyConfFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        //attampt 3: Try to get a settings folder in the user home directory
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getHomeSettingsFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        return null;
    }

    private Path getCyConfFolder(Path searchPath) {

        Path cyConfPath = searchPath.resolve("CytoscapeConfiguration");
        Path localSettingsPath;

        //try to get a settings directory in the cytoscape config folder
        if (cyConfPath.toFile().isDirectory() && Files.isWritable(cyConfPath)) {
            localSettingsPath = cyConfPath.resolve(CyModel.APP_NAME + "_settings");
            //settins folder doesn't exists, so try to make it
            if (!localSettingsPath.toFile().exists()) {
                try {
                    Files.createDirectory(localSettingsPath);
                    return localSettingsPath;
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            } else if (localSettingsPath.toFile().isDirectory() && Files.isWritable(localSettingsPath)) {
                //settings folder exists, check if I can write there
                return localSettingsPath;
            }
        }
        return null;
    }

    private Path getHomeSettingsFolder(Path searchPath) {
        Path localSettingsPath = searchPath.resolve(CyModel.APP_NAME + "_settings");
        if (!localSettingsPath.toFile().exists()) {
            //settins folder doesn't exists, so try to make it
            try {
                Files.createDirectory(localSettingsPath);
                return localSettingsPath;
            } catch (IOException ex) {
                System.err.println(ex);
            }
        } else if (localSettingsPath.toFile().isDirectory() && Files.isWritable(localSettingsPath)) {
            //settings folder exists, check if I can write there
            return localSettingsPath;
        }
        return null;
    }

    /**
     * @return the cyModel
     */
    public CyModel getCyModel() {
        return cyModel;
    }

    /**
     * @return the cyServices
     */
    public CyServices getCyServices() {
        return cyServices;
    }
}
