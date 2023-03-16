package com.example.palette.bean;

public class TextJson {
    int width;
    int height;
    int posX;
    int posY;
    int fontSize;
    String content;
    String rollType;
    String fontColor;
    String backColor;
    String backColor2;
    boolean blink;

    public TextJson(int width, int height, int posX, int posY, int fontSize, String content, String rollType, String fontColor, String backColor, String backColor2, boolean blink) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.fontSize = fontSize;
        this.content = content;
        this.rollType = rollType;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.backColor2 = backColor2;
        this.blink = blink;
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

    public String getRollType() {
        return rollType;
    }

    public void setRollType(String rollType) {
        this.rollType = rollType;
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

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
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

    public String getBackColor2() {
        return backColor2;
    }

    public void setBackColor2(String backColor2) {
        this.backColor2 = backColor2;
    }
}
