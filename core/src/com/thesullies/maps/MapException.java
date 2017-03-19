package com.thesullies.maps;

/**
 * This exception is thrown when an error occurs trying to load the map
 * Created by kosullivan on 27/02/2017.
 */
public class MapException extends Exception {
    private String mapFileName;
    public MapException(String message, String mapFileName) {
        super(message);
        this.mapFileName = mapFileName;
    }
}
