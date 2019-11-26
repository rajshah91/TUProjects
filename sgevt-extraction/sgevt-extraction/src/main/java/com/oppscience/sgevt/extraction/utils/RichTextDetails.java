package com.oppscience.sgevt.extraction.utils;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class RichTextDetails {
    private String richText;
    private Map<Integer, XWPFRun> fontMap;

    public RichTextDetails(String richText,
            Map<Integer, XWPFRun> fontMap2) {
        this.richText = richText;
        this.fontMap = fontMap2;
    }

    public String getRichText() {
        return richText;
    }
    public void setRichText(String richText) {
        this.richText = richText;
    }
    public Map<Integer, XWPFRun> getFontMap() {
        return fontMap;
    }

    public void setFontMap(Map<Integer, XWPFRun> fontMap) {
        this.fontMap = fontMap;
    }
    
    @Override
    public int hashCode() {
     
        // The goal is to have a more efficient hashcode than standard one.
        return richText.hashCode();
    }
}