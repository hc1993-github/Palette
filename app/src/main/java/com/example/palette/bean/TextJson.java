package com.example.palette.bean;

public class TextJson {
    int elementType;
    String content;
    boolean autoRoll;
    String rollType;
    String businessPropertyKey;
    int msgType;
    String guiderKeys;
    String systemId;
    int posX;
    int posY;
    int width;
    int height;
    String fontColor;
    String backColor;
    int fontSize;
    String fontName;
    String elementld;
    int dockStyle;
    int needRoll;

    public TextJson(int elementType, String content, boolean autoRoll, String rollType, String businessPropertyKey, int msgType, String guiderKeys, String systemId, int posX, int posY, int width, int height, String fontColor, String backColor, int fontSize, String fontName, String elementld, int dockStyle, int needRoll) {
        this.elementType = elementType;
        this.content = content;
        this.autoRoll = autoRoll;
        this.rollType = rollType;
        this.businessPropertyKey = businessPropertyKey;
        this.msgType = msgType;
        this.guiderKeys = guiderKeys;
        this.systemId = systemId;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.elementld = elementld;
        this.dockStyle = dockStyle;
        this.needRoll = needRoll;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAutoRoll() {
        return autoRoll;
    }

    public void setAutoRoll(boolean autoRoll) {
        this.autoRoll = autoRoll;
    }

    public String getRollType() {
        return rollType;
    }

    public void setRollType(String rollType) {
        this.rollType = rollType;
    }

    public String getBusinessPropertyKey() {
        return businessPropertyKey;
    }

    public void setBusinessPropertyKey(String businessPropertyKey) {
        this.businessPropertyKey = businessPropertyKey;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getGuiderKeys() {
        return guiderKeys;
    }

    public void setGuiderKeys(String guiderKeys) {
        this.guiderKeys = guiderKeys;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getElementld() {
        return elementld;
    }

    public void setElementld(String elementld) {
        this.elementld = elementld;
    }

    public int getDockStyle() {
        return dockStyle;
    }

    public void setDockStyle(int dockStyle) {
        this.dockStyle = dockStyle;
    }

    public int getNeedRoll() {
        return needRoll;
    }

    public void setNeedRoll(int needRoll) {
        this.needRoll = needRoll;
    }

    @Override
    public String toString() {
        return "TextJson{" +
                "elementType=" + elementType +
                ", content='" + content + '\'' +
                ", autoRoll=" + autoRoll +
                ", rollType='" + rollType + '\'' +
                ", businessPropertyKey='" + businessPropertyKey + '\'' +
                ", msgType=" + msgType +
                ", guiderKeys='" + guiderKeys + '\'' +
                ", systemId='" + systemId + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", width=" + width +
                ", height=" + height +
                ", fontColor='" + fontColor + '\'' +
                ", backColor='" + backColor + '\'' +
                ", fontSize=" + fontSize +
                ", fontName='" + fontName + '\'' +
                ", elementld='" + elementld + '\'' +
                ", dockStyle=" + dockStyle +
                ", needRoll=" + needRoll +
                '}';
    }
}
