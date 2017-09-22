package com.msdt.apitoolz.models;

import java.io.Serializable;

/**
 * Created by SMK on 9/23/2016.
 */
public class Lang implements Serializable {
    private int image;
    private String name;
    private String shortName;
    private String translateText;
    private String translatedText;

    public Lang() {
    }

    public Lang(int image, String name, String shortName) {
        this.image = image;
        this.name = name;
        this.shortName = shortName;
    }

    public Lang(int image, String name, String shortName, String translateText, String translatedText) {
        this.image = image;
        this.name = name;
        this.shortName = shortName;
        this.translateText = translateText;
        this.translatedText = translatedText;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTranslateText() {
        return translateText;
    }

    public void setTranslateText(String translateText) {
        this.translateText = translateText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    @Override
    public String toString() {
        return "Lang{" +
                "image=" + image +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", translateText='" + translateText + '\'' +
                ", translatedText='" + translatedText + '\'' +
                '}';
    }
}
