package com.oppscience.sgevt.extraction.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;


@Service
public class HTMLtoWord {

    private static final Pattern HEAVY_REGEX = Pattern.compile("(<br/>)|(</br>)|(<br />)|(< /br>)|(<br>)");
    private static final int START_TAG = 0;
    private static final int END_TAG = 1;
    private static final String NEW_LINE = System.getProperty("line.separator");

   public static RichTextString fromHtmlToCellValue(String html,XWPFRun run){
       Config.IsHTMLEmptyElementTagRecognised = true;
       
       Matcher m = HEAVY_REGEX.matcher(html);
       String replacedhtml =  m.replaceAll("");
       StringBuilder sb = new StringBuilder();
       sb.insert(0, "<div>");
       sb.append(replacedhtml);
       sb.append("</div>");
       String newhtml = sb.toString();
       Source source = new Source(newhtml);
       List<RichTextDetails> cellValues = new ArrayList<RichTextDetails>();
       for(Element el : source.getAllElements("div")){
           cellValues.add(createCellValue(el.toString(),run));
       }
       RichTextString cellValue = mergeTextDetails(cellValues);

       
       return cellValue;
   }
   
   // 


    private static RichTextString mergeTextDetails(List<RichTextDetails> cellValues) {
        Config.IsHTMLEmptyElementTagRecognised = true;
        StringBuilder textBuffer = new StringBuilder();
        Map<Integer, XWPFRun> mergedMap = new LinkedHashMap<Integer, XWPFRun>(550, .95f);
        int currentIndex = 0;
        for (RichTextDetails richTextDetail : cellValues) {
            //textBuffer.append(BULLET_CHARACTER + " ");
            currentIndex = textBuffer.length();
            for (Entry<Integer, XWPFRun> entry : richTextDetail.getFontMap().entrySet()) {
                mergedMap.put(entry.getKey() + currentIndex, entry.getValue());
            }
            textBuffer.append(richTextDetail.getRichText())
                .append(NEW_LINE);
        }
        RichTextString richText = new XSSFRichTextString(textBuffer.toString());
        return richText;
    }

    static RichTextDetails createCellValue(String html,XWPFRun run) {
        Config.IsHTMLEmptyElementTagRecognised  = true;
        Source source = new Source(html);
        Map<String, TagInfo> tagMap = new LinkedHashMap<String, TagInfo>(550, .95f);
        for (Element e : source.getChildElements()) {
            getInfo(e, tagMap);
        }

        StringBuilder sbPatt = new StringBuilder();
        sbPatt.append("(").append(StringUtils.join(tagMap.keySet(), "|")).append(")");
        String patternString = sbPatt.toString();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(html);

        StringBuffer textBuffer = new StringBuffer();
        List<RichTextInfo> textInfos = new ArrayList<RichTextInfo>();
        ArrayDeque<RichTextInfo> richTextBuffer = new ArrayDeque<RichTextInfo>();
        while (matcher.find()) {
            matcher.appendReplacement(textBuffer, "");
            TagInfo currentTag = tagMap.get(matcher.group(1));
            if (START_TAG == currentTag.getTagType()) {
                richTextBuffer.push(getRichTextInfo(currentTag, textBuffer.length()));
            } else {
                if (!richTextBuffer.isEmpty()) {
                    RichTextInfo info = richTextBuffer.pop();
                    if (info != null) {
                        info.setEndIndex(textBuffer.length());
                        textInfos.add(info);
                    }
                }
            }
        }
        matcher.appendTail(textBuffer);
        Map<Integer, XWPFRun> fontMap = buildFontMap(textInfos,run);

        return new RichTextDetails(textBuffer.toString(), fontMap);
    }

    private static Map<Integer, XWPFRun> buildFontMap(List<RichTextInfo> textInfos,XWPFRun run) {
        Map<Integer, XWPFRun> fontMap = new LinkedHashMap<Integer, XWPFRun>(550, .95f);

        for (RichTextInfo richTextInfo : textInfos) {
            if (richTextInfo.isValid()) {
                for (int i = richTextInfo.getStartIndex(); i < richTextInfo.getEndIndex(); i++) {
                    fontMap.put(i, mergeFont(run, richTextInfo.getFontStyle(), richTextInfo.getFontValue()));
                }
            }
        }

        return fontMap;
    }

    @SuppressWarnings("all")
    private static XWPFRun mergeFont(XWPFRun run, STYLES fontStyle, String fontValue) {
    	
    	switch (fontStyle) {
        case BOLD:
        case EM:
        case STRONG:
        	run.setBold(true);
            break;
        case UNDERLINE:
        	run.setUnderline(UnderlinePatterns.SINGLE);
            break;
        case ITALLICS:
        	run.setItalic(true);
            break;
        case PRE:
        	run.setFontFamily("Courier New");
        case COLOR:
            if (!isEmpty(fontValue)) {
            	run.setColor(fontValue);
            }
            break;
        default:
            break;
        }
    	return run;
    }

    private static RichTextInfo getRichTextInfo(TagInfo currentTag, int startIndex) {
        RichTextInfo info = null;
        switch (STYLES.fromValue(currentTag.getTagName())) {
        case SPAN:
            if (!isEmpty(currentTag.getStyle())) {
                for (String style : currentTag.getStyle()
                    .split(";")) {
                    String[] styleDetails = style.split(":");
                    if (styleDetails != null && styleDetails.length > 1) {
                        if ("COLOR".equalsIgnoreCase(styleDetails[0].trim())) {
                            info = new RichTextInfo(startIndex, -1, STYLES.COLOR, styleDetails[1]);
                        }
                    }
                }
            }
            break;
        default:
            info = new RichTextInfo(startIndex, -1, STYLES.fromValue(currentTag.getTagName()));
            break;
        }
        return info;
    }

    private static boolean isEmpty(String str) {
        return (str == null || str.trim()
            .length() == 0);
    }

    private static void getInfo(Element e, Map<String, TagInfo> tagMap) {
        tagMap.put(e.getStartTag()
            .toString(),
            new TagInfo(e.getStartTag()
                .getName(), e.getAttributeValue("style"), START_TAG));
        if (e.getChildElements()
            .size() > 0) {
            List<Element> children = e.getChildElements();
            for (Element child : children) {
                getInfo(child, tagMap);
            }
        }
        if (e.getEndTag() != null) {
            tagMap.put(e.getEndTag()
                .toString(),
                new TagInfo(e.getEndTag()
                    .getName(), END_TAG));
        } else {
            // Handling self closing tags
            tagMap.put(e.getStartTag()
                .toString(),
                new TagInfo(e.getStartTag()
                    .getName(), END_TAG));
        }
    }

}
