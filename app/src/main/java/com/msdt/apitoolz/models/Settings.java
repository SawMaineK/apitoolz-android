package com.msdt.apitoolz.models;

import com.msdt.apitoolz.Constant;
import com.msdt.apitoolz.utils.StorageDriver;

import java.io.Serializable;

/**
 * Created by sawma on 7/23/2017.
 */

public class Settings implements Serializable {
    private boolean isCached = false;
    private boolean isNoti = true;
    private String locale = "en";
    private String fontStyle = "default";

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    public boolean isNoti() {
        return isNoti;
    }

    public void setNoti(boolean noti) {
        isNoti = noti;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void save(){
        StorageDriver.getInstance().saveTo(Constant.settings, this);
    }
}
