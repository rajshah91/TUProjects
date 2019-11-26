package com.oppscience.sgevt.extraction.word;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute.Space;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class WordUtils {

	public static XWPFDocument createNewDocument() {
		return new XWPFDocument();
	}

	public static void setPageMargin(XWPFDocument document, Long left, Long right, Long top, Long bottom) {
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		CTPageMar pageMar = sectPr.addNewPgMar();
		pageMar.setLeft(BigInteger.valueOf(left));
		pageMar.setTop(BigInteger.valueOf(top));
		pageMar.setRight(BigInteger.valueOf(right));
		pageMar.setBottom(BigInteger.valueOf(bottom));
	}

	public static void saveDocument(XWPFDocument document, String outputDirecoryPath, String docFileName)
			throws Exception {
		File file = new File(outputDirecoryPath);
		if (!file.exists()) {
			file.mkdir();
		}
		FileOutputStream out = new FileOutputStream(new File(outputDirecoryPath + docFileName));
		document.write(out);
		out.close();
	}

	public static void saveDocument(XWPFDocument document, File file) throws Exception {
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		document.write(out);
		out.close();
	}

	public static XWPFDocument openDocument(String filePath) throws Exception {
		return new XWPFDocument(POIXMLDocument.openPackage(filePath));
	}

	public static XWPFParagraph createNewParagraph(XWPFDocument document) {
		return document.createParagraph();
	}

	public static void createHeader(XWPFDocument document, String headerValue) {
		XWPFParagraph p = document.createParagraph();
		XWPFRun r = p.createRun();
		CTP ctP = CTP.Factory.newInstance();
		CTText t = ctP.addNewR().addNewT();
		t.setStringValue(headerValue);
		XWPFParagraph[] pars = new XWPFParagraph[1];
		p = new XWPFParagraph(ctP, document);
		pars[0] = p;

		XWPFHeaderFooterPolicy hfPolicy = document.createHeaderFooterPolicy();
		hfPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, pars);
	}

	public static void createFooter(XWPFDocument document, String footerValue) {
		XWPFParagraph p = document.createParagraph();

		XWPFRun r = p.createRun();
		CTP ctP = CTP.Factory.newInstance();
		CTText t = ctP.addNewR().addNewT();
		XWPFParagraph[] pars = new XWPFParagraph[1];
		t.setStringValue(footerValue);
		pars[0] = new XWPFParagraph(ctP, document);
		XWPFHeaderFooterPolicy hfPolicy = document.createHeaderFooterPolicy();
		hfPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, pars);

	}

	public static void addParagraphContentBookmarkBasicStyle(XWPFParagraph paragraph, String content, BigInteger markId,
			String bookMarkName, boolean isInsert, boolean isNewLine, String fontFamily, String fontSize,
			String colorVal, boolean isBlod, boolean isUnderLine, String underLineColor, STUnderline.Enum underStyle,
			boolean isItalic, boolean isStrike) {
		CTBookmark bookStart = paragraph.getCTP().addNewBookmarkStart();
		bookStart.setId(markId);
		bookStart.setName(bookMarkName);

		XWPFRun pRun = getOrAddParagraphFirstRun(paragraph, isInsert, isNewLine);
		setParagraphRunFontInfo(paragraph, pRun, content, fontFamily, fontSize);
		setParagraphTextStyleInfo(paragraph, pRun, colorVal, isBlod, isUnderLine, underLineColor, underStyle, isItalic,
				isStrike, false, false, false, false, false, false, false, null, false, null, false, null, null, null,
				0, 0, 0);
		CTMarkupRange bookEnd = paragraph.getCTP().addNewBookmarkEnd();
		bookEnd.setId(markId);
	}

	public static void addParagraphContentBookmark(XWPFParagraph paragraph, String content, BigInteger markId,
			String bookMarkName, boolean isInsert, boolean isNewLine, String fontFamily, String fontSize,
			String colorVal, boolean isBlod, boolean isUnderLine, String underLineColor, STUnderline.Enum underStyle,
			boolean isItalic, boolean isStrike, boolean isDStrike, boolean isShadow, boolean isVanish, boolean isEmboss,
			boolean isImprint, boolean isOutline, boolean isEm, STEm.Enum emType, boolean isHightLight,
			STHighlightColor.Enum hightStyle, boolean isShd, STShd.Enum shdStyle, String shdColor,
			VerticalAlign verticalAlign, int position, int spacingValue, int indent) {
		CTBookmark bookStart = paragraph.getCTP().addNewBookmarkStart();
		bookStart.setId(markId);
		bookStart.setName(bookMarkName);

		XWPFRun pRun = getOrAddParagraphFirstRun(paragraph, isInsert, isNewLine);
		setParagraphRunFontInfo(paragraph, pRun, content, fontFamily, fontSize);
		setParagraphTextStyleInfo(paragraph, pRun, colorVal, isBlod, isUnderLine, underLineColor, underStyle, isItalic,
				isStrike, isDStrike, isShadow, isVanish, isEmboss, isImprint, isOutline, isEm, emType, isHightLight,
				hightStyle, isShd, shdStyle, shdColor, verticalAlign, position, spacingValue, indent);

		CTMarkupRange bookEnd = paragraph.getCTP().addNewBookmarkEnd();
		bookEnd.setId(markId);

	}

	public static void addParagraphTextHyperlinkBasicStyle(XWPFParagraph paragraph, String url, String text,
			String fontFamily, String fontSize, String colorVal, boolean isBlod, boolean isItalic, boolean isStrike) {
		addParagraphTextHyperlink(paragraph, url, text, fontFamily, fontSize, colorVal, isBlod, true, "0000FF",
				STUnderline.SINGLE, isItalic, isStrike, false, false, false, false, false, false, false, null, false,
				null, false, null, null, null, 0, 0, 0);
	}

	public static void addParagraphTextHyperlink(XWPFParagraph paragraph, String url, String text, String fontFamily,
			String fontSize, String colorVal, boolean isBlod, boolean isUnderLine, String underLineColor,
			STUnderline.Enum underStyle, boolean isItalic, boolean isStrike, boolean isDStrike, boolean isShadow,
			boolean isVanish, boolean isEmboss, boolean isImprint, boolean isOutline, boolean isEm, STEm.Enum emType,
			boolean isHightLight, STHighlightColor.Enum hightStyle, boolean isShd, STShd.Enum shdStyle, String shdColor,
			STVerticalAlignRun.Enum verticalAlign, int position, int spacingValue, int indent) {
		// Add the link as External relationship
		String id = paragraph.getDocument().getPackagePart()
				.addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation()).getId();
		// Append the link and bind it to the relationship
		CTHyperlink cLink = paragraph.getCTP().addNewHyperlink();
		cLink.setId(id);

		// Create the linked text
		CTText ctText = CTText.Factory.newInstance();
		ctText.setStringValue(text);
		CTR ctr = CTR.Factory.newInstance();
		CTRPr rpr = ctr.addNewRPr();

		if (StringUtils.isNotBlank(fontFamily)) {
			// Set font
			CTFonts fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
			fonts.setAscii(fontFamily);
			fonts.setEastAsia(fontFamily);
			fonts.setHAnsi(fontFamily);
		}
		if (StringUtils.isNotBlank(fontSize)) {
			// Set the font size
			CTHpsMeasure sz = rpr.isSetSz() ? rpr.getSz() : rpr.addNewSz();
			sz.setVal(new BigInteger(fontSize));

			CTHpsMeasure szCs = rpr.isSetSzCs() ? rpr.getSzCs() : rpr.addNewSzCs();
			szCs.setVal(new BigInteger(fontSize));
		}
		if (StringUtils.isNotBlank(colorVal)) {
			CTColor color = CTColor.Factory.newInstance();
			color.setVal(colorVal);
			rpr.setColor(color);
		}

		if (isBlod) {
			CTOnOff bCtOnOff = rpr.addNewB();
			bCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isUnderLine) {
			CTUnderline udLine = rpr.addNewU();
			udLine.setVal(underStyle);
			udLine.setColor(underLineColor);
		}

		if (isItalic) {
			CTOnOff iCtOnOff = rpr.addNewI();
			iCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isStrike) {
			CTOnOff sCtOnOff = rpr.addNewStrike();
			sCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isDStrike) {
			CTOnOff dsCtOnOff = rpr.addNewDstrike();
			dsCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isShadow) {
			CTOnOff shadowCtOnOff = rpr.addNewShadow();
			shadowCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isVanish) {
			CTOnOff vanishCtOnOff = rpr.addNewVanish();
			vanishCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isEmboss) {
			CTOnOff embossCtOnOff = rpr.addNewEmboss();
			embossCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isImprint) {
			CTOnOff isImprintCtOnOff = rpr.addNewImprint();
			isImprintCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isOutline) {
			CTOnOff isOutlineCtOnOff = rpr.addNewOutline();
			isOutlineCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isEm) {
			CTEm em = rpr.addNewEm();
			em.setVal(emType);
		}
		if (isHightLight) {
			if (hightStyle != null) {
				CTHighlight hightLight = rpr.addNewHighlight();
				hightLight.setVal(hightStyle);
			}
		}
		if (isShd) {

			CTShd shd = rpr.addNewShd();
			if (shdStyle != null) {
				shd.setVal(shdStyle);
			}
			if (shdColor != null) {
				shd.setColor(shdColor);
			}
		}

		if (verticalAlign != null) {
			rpr.addNewVertAlign().setVal(verticalAlign);
		}

		rpr.addNewPosition().setVal(new BigInteger(String.valueOf(position)));
		if (spacingValue != 0) {

			CTSignedTwipsMeasure ctSTwipsMeasure = rpr.addNewSpacing();
			ctSTwipsMeasure.setVal(new BigInteger(String.valueOf(spacingValue)));
		}
		if (indent > 0) {
			CTTextScale paramCTTextScale = rpr.addNewW();
			paramCTTextScale.setVal(indent);
		}
		ctr.setTArray(new CTText[] { ctText });
		cLink.setRArray(new CTR[] { ctr });
	}

	public static CTPPr getParagraphCTPPr(XWPFParagraph paragraph) {
		CTPPr pPPr = null;
		if (paragraph.getCTP() != null) {
			if (paragraph.getCTP().getPPr() != null) {
				pPPr = paragraph.getCTP().getPPr();
			} else {
				pPPr = paragraph.getCTP().addNewPPr();
			}
		}
		return pPPr;
	}

	public static CTRPr getRunCTRPr(XWPFParagraph paragraph, XWPFRun pRun) {
		CTRPr pRpr = null;
		if (pRun.getCTR() != null) {
			pRpr = pRun.getCTR().getRPr();
			if (pRpr == null) {
				pRpr = pRun.getCTR().addNewRPr();
			}
		} else {
			pRpr = paragraph.getCTP().addNewR().addNewRPr();
		}
		return pRpr;
	}

	public static XWPFRun getOrAddParagraphFirstRun(XWPFParagraph paragraph, boolean isInsert, boolean isNewLine) {
		XWPFRun pRun = null;
		if (isInsert) {
			pRun = paragraph.createRun();
		} else {
			if (paragraph.getRuns() != null && paragraph.getRuns().size() > 0) {
				pRun = paragraph.getRuns().get(0);
			} else {
				pRun = paragraph.createRun();
			}
		}
		if (isNewLine) {
			pRun.addBreak();
		}
		return pRun;
	}

	public static void setParagraphTextFontInfo(XWPFParagraph paragraph, boolean isInsert, boolean isNewLine,
			String content, String fontFamily, String fontSize) {
		XWPFRun pRun = getOrAddParagraphFirstRun(paragraph, isInsert, isNewLine);
		setParagraphRunFontInfo(paragraph, pRun, content, fontFamily, fontSize);
	}

	public static void setParagraphRunFontInfo(XWPFParagraph p, XWPFRun pRun, String content, String fontFamily,
			String fontSize) {
		CTRPr pRpr = getRunCTRPr(p, pRun);
		if (StringUtils.isNotBlank(content)) {
			pRun.setText(content);
		}
		// Set font
		CTFonts fonts = pRpr.isSetRFonts() ? pRpr.getRFonts() : pRpr.addNewRFonts();
		fonts.setAscii(fontFamily);
		fonts.setEastAsia(fontFamily);
		fonts.setHAnsi(fontFamily);

		// Set the font size
		CTHpsMeasure sz = pRpr.isSetSz() ? pRpr.getSz() : pRpr.addNewSz();
		sz.setVal(new BigInteger(fontSize));

		CTHpsMeasure szCs = pRpr.isSetSzCs() ? pRpr.getSzCs() : pRpr.addNewSzCs();
		szCs.setVal(new BigInteger(fontSize));
	}

	/*********************************************************************************************************************************
	 * Paragraph Text Stying methods
	 *********************************************************************************************************************************/
	public static void setParagraphTextBasicStyleInfo(XWPFParagraph paragraph, XWPFRun pRun, String colorVal,
			boolean isBlod, boolean isUnderLine, String underLineColor, STUnderline.Enum underStyle, boolean isItalic,
			boolean isStrike, boolean isHightLight, STHighlightColor.Enum hightStyle, boolean isShd,
			STShd.Enum shdStyle, String shdColor) {
		setParagraphTextStyleInfo(paragraph, pRun, colorVal, isBlod, isUnderLine, underLineColor, underStyle, isItalic,
				isStrike, false, false, false, false, false, false, false, null, isHightLight, hightStyle, isShd,
				shdStyle, shdColor, null, 0, 0, 0);
	}

	public static void setParagraphTextSimpleStyleInfo(XWPFParagraph paragraph, XWPFRun pRun, String colorVal,
			boolean isBlod, boolean isUnderLine, String underLineColor, STUnderline.Enum underStyle, boolean isItalic,
			boolean isStrike, boolean isHightLight, STHighlightColor.Enum hightStyle, boolean isShd,
			STShd.Enum shdStyle, String shdColor, VerticalAlign verticalAlign, int position, int spacingValue,
			int indent) {
		setParagraphTextStyleInfo(paragraph, pRun, colorVal, isBlod, isUnderLine, underLineColor, underStyle, isItalic,
				isStrike, false, false, false, false, false, false, false, null, isHightLight, hightStyle, isShd,
				shdStyle, shdColor, verticalAlign, position, spacingValue, indent);
	}

	public static void setParagraphTextStyleInfo(XWPFParagraph paragraph, XWPFRun pRun, String colorVal, boolean isBlod,
			boolean isUnderLine, String underLineColor, STUnderline.Enum underStyle, boolean isItalic, boolean isStrike,
			boolean isDStrike, boolean isShadow, boolean isVanish, boolean isEmboss, boolean isImprint,
			boolean isOutline, boolean isEm, STEm.Enum emType, boolean isHightLight, STHighlightColor.Enum hightStyle,
			boolean isShd, STShd.Enum shdStyle, String shdColor, VerticalAlign verticalAlign, int position,
			int spacingValue, int indent) {
		if (pRun == null) {
			return;
		}
		CTRPr pRpr = getRunCTRPr(paragraph, pRun);
		if (colorVal != null) {
			pRun.setColor(colorVal);
		}
		if (isBlod) {
			pRun.setBold(isBlod);
		}

		if (isItalic) {
			pRun.setItalic(isItalic);
		}

		if (isStrike) {
			pRun.setStrike(isStrike);
		}

		if (isDStrike) {
			CTOnOff dsCtOnOff = pRpr.isSetDstrike() ? pRpr.getDstrike() : pRpr.addNewDstrike();
			dsCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isShadow) {
			CTOnOff shadowCtOnOff = pRpr.isSetShadow() ? pRpr.getShadow() : pRpr.addNewShadow();
			shadowCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isVanish) {
			CTOnOff vanishCtOnOff = pRpr.isSetVanish() ? pRpr.getVanish() : pRpr.addNewVanish();
			vanishCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isEmboss) {
			CTOnOff embossCtOnOff = pRpr.isSetEmboss() ? pRpr.getEmboss() : pRpr.addNewEmboss();
			embossCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isImprint) {
			CTOnOff isImprintCtOnOff = pRpr.isSetImprint() ? pRpr.getImprint() : pRpr.addNewImprint();
			isImprintCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isOutline) {
			CTOnOff isOutlineCtOnOff = pRpr.isSetOutline() ? pRpr.getOutline() : pRpr.addNewOutline();
			isOutlineCtOnOff.setVal(STOnOff.TRUE);
		}

		if (isEm) {
			CTEm em = pRpr.isSetEm() ? pRpr.getEm() : pRpr.addNewEm();
			em.setVal(emType);
		}

		if (isUnderLine) {
			CTUnderline u = pRpr.isSetU() ? pRpr.getU() : pRpr.addNewU();
			if (underStyle != null) {
				u.setVal(underStyle);
			}
			if (underLineColor != null) {
				u.setColor(underLineColor);
			}
		}

		if (isHightLight) {
			if (hightStyle != null) {
				CTHighlight hightLight = pRpr.isSetHighlight() ? pRpr.getHighlight() : pRpr.addNewHighlight();
				hightLight.setVal(hightStyle);
			}
		}
		if (isShd) {

			CTShd shd = pRpr.isSetShd() ? pRpr.getShd() : pRpr.addNewShd();
			if (shdStyle != null) {
				shd.setVal(shdStyle);
			}
			if (shdColor != null) {
				shd.setColor(shdColor);
			}
		}

		if (verticalAlign != null) {
			pRun.setSubscript(verticalAlign);
		}

		pRun.setTextPosition(position);
		if (spacingValue > 0) {

			CTSignedTwipsMeasure ctSTwipsMeasure = pRpr.isSetSpacing() ? pRpr.getSpacing() : pRpr.addNewSpacing();
			ctSTwipsMeasure.setVal(new BigInteger(String.valueOf(spacingValue)));
		}
		if (indent > 0) {
			CTTextScale paramCTTextScale = pRpr.isSetW() ? pRpr.getW() : pRpr.addNewW();
			paramCTTextScale.setVal(indent);
		}
	}

	public static void setParagraphSpacingInfo(XWPFParagraph paragraph, boolean isSpace, String before, String after,
			String beforeLines, String afterLines, boolean isLine, String line, STLineSpacingRule.Enum lineValue) {
		CTPPr pPPr = getParagraphCTPPr(paragraph);
		CTSpacing pSpacing = pPPr.getSpacing() != null ? pPPr.getSpacing() : pPPr.addNewSpacing();
		if (isSpace) {

			if (before != null) {
				pSpacing.setBefore(new BigInteger(before));
			}

			if (after != null) {
				pSpacing.setAfter(new BigInteger(after));
			}

			if (beforeLines != null) {
				pSpacing.setBeforeLines(new BigInteger(beforeLines));
			}

			if (afterLines != null) {
				pSpacing.setAfterLines(new BigInteger(afterLines));
			}
		}

		if (isLine) {
			if (line != null) {
				pSpacing.setLine(new BigInteger(line));
			}
			if (lineValue != null) {
				pSpacing.setLineRule(lineValue);
			}
		}
	}

	public static void setParagraphBorders(XWPFParagraph paragraph, Borders lborder, Borders tBorders, Borders rBorders,
			Borders bBorders, Borders btborders) {
		if (lborder != null) {
			paragraph.setBorderLeft(lborder);
		}
		if (tBorders != null) {
			paragraph.setBorderTop(tBorders);
		}
		if (rBorders != null) {
			paragraph.setBorderRight(rBorders);
		}
		if (bBorders != null) {
			paragraph.setBorderBottom(bBorders);
		}
		if (btborders != null) {
			paragraph.setBorderBetween(btborders);
		}
	}

	public static void setParagraphAlignInfo(XWPFParagraph paragraph, ParagraphAlignment pAlign, TextAlignment valign) {
		if (pAlign != null) {
			paragraph.setAlignment(pAlign);
		}
		if (valign != null) {
			paragraph.setVerticalAlignment(valign);
		}
	}

	public static void setParagraphAlignInfo(XWPFParagraph paragraph, ParagraphAlignment pAlign) {
		setParagraphAlignInfo(paragraph, pAlign, TextAlignment.AUTO);
	}

	/*********************************************************************************************************************************
	 * Table related methods
	 *********************************************************************************************************************************/

	public static void deleteTableByIndex(XWPFDocument xdoc, int pos) {
		Iterator<IBodyElement> bodyElement = xdoc.getBodyElementsIterator();
		int eIndex = 0, tableIndex = -1;
		while (bodyElement.hasNext()) {
			IBodyElement element = bodyElement.next();
			BodyElementType elementType = element.getElementType();
			if (elementType == BodyElementType.TABLE) {
				tableIndex++;
				if (tableIndex == pos) {
					break;
				}
			}
			eIndex++;
		}
		xdoc.removeBodyElement(eIndex);
	}

	public static XWPFTable getTableByIndex(XWPFDocument xdoc, int index) {
		List<XWPFTable> tablesList = getAllTable(xdoc);
		if (tablesList == null || index < 0 || index > tablesList.size()) {
			return null;
		}
		return tablesList.get(index);
	}

	public static List<XWPFTable> getAllTable(XWPFDocument xdoc) {
		return xdoc.getTables();
	}

	public static List<List<String>> getTableRContent(XWPFTable table) {
		List<List<String>> tableContentList = new ArrayList<List<String>>();
		for (int rowIndex = 0, rowLen = table.getNumberOfRows(); rowIndex < rowLen; rowIndex++) {
			XWPFTableRow row = table.getRow(rowIndex);
			List<String> cellContentList = new ArrayList<String>();
			for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {
				XWPFTableCell cell = row.getCell(colIndex);
				CTTc ctTc = cell.getCTTc();
				if (ctTc.isSetTcPr()) {
					CTTcPr tcPr = ctTc.getTcPr();
					if (tcPr.isSetHMerge()) {
						CTHMerge hMerge = tcPr.getHMerge();
						if (STMerge.RESTART.equals(hMerge.getVal())) {
							cellContentList.add(getTableCellContent(cell));
						}
					} else if (tcPr.isSetVMerge()) {
						CTVMerge vMerge = tcPr.getVMerge();
						if (STMerge.RESTART.equals(vMerge.getVal())) {
							cellContentList.add(getTableCellContent(cell));
						}
					} else {
						cellContentList.add(getTableCellContent(cell));
					}
				}
			}
			tableContentList.add(cellContentList);
		}
		return tableContentList;
	}

	public static List<List<String>> getTableContent(XWPFTable table) {
		List<List<String>> tableContentList = new ArrayList<List<String>>();
		for (int rowIndex = 0, rowLen = table.getNumberOfRows(); rowIndex < rowLen; rowIndex++) {
			XWPFTableRow row = table.getRow(rowIndex);
			List<String> cellContentList = new ArrayList<String>();
			for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {
				XWPFTableCell cell = row.getCell(colIndex);
				cellContentList.add(getTableCellContent(cell));
			}
			tableContentList.add(cellContentList);
		}
		return tableContentList;
	}

	public static String getTableCellContent(XWPFTableCell cell) {
		StringBuffer sb = new StringBuffer();
		List<XWPFParagraph> cellPList = cell.getParagraphs();
		if (cellPList != null && cellPList.size() > 0) {
			for (XWPFParagraph xwpfPr : cellPList) {
				List<XWPFRun> runs = xwpfPr.getRuns();
				if (runs != null && runs.size() > 0) {
					for (XWPFRun xwpfRun : runs) {
						sb.append(xwpfRun.getText(0));
					}
				}
			}
		}
		return sb.toString();
	}

	public static XWPFTable createTable(XWPFDocument xdoc, int rowSize, int cellSize, boolean isSetColWidth,
			int[] colWidths) {
		XWPFTable table = xdoc.createTable(rowSize, cellSize);
		if (isSetColWidth) {
			CTTbl ttbl = table.getCTTbl();
			CTTblGrid tblGrid = ttbl.addNewTblGrid();
			for (int j = 0, len = Math.min(cellSize, colWidths.length); j < len; j++) {
				CTTblGridCol gridCol = tblGrid.addNewGridCol();
				gridCol.setW(new BigInteger(String.valueOf(colWidths[j])));
			}
		}
		return table;
	}

	public static void setTableWidthAndHAlign(XWPFTable table, String width, STJc.Enum enumValue) {
		CTTblPr tblPr = getTableCTTblPr(table);
		CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
		if (enumValue != null) {
			CTJc cTJc = tblPr.addNewJc();
			cTJc.setVal(enumValue);
		}
		tblWidth.setW(new BigInteger(width));
		tblWidth.setType(STTblWidth.DXA);
	}

	public static CTTblPr getTableCTTblPr(XWPFTable table) {
		CTTbl ttbl = table.getCTTbl();
		return ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();
	}

	public static CTTblBorders getTableBorders(XWPFTable table) {
		CTTblPr tblPr = getTableCTTblPr(table);
		return tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
	}

	public static void setTableBorders(XWPFTable table, CTBorder left, CTBorder top, CTBorder right, CTBorder bottom) {
		CTTblBorders tblBorders = getTableBorders(table);
		if (left != null) {
			tblBorders.setLeft(left);
		}
		if (top != null) {
			tblBorders.setTop(top);
		}
		if (right != null) {
			tblBorders.setRight(right);
		}
		if (bottom != null) {
			tblBorders.setBottom(bottom);
		}
	}

	public static void insertTableRowAtIndex(XWPFTable table, int index) {
		XWPFTableRow firstRow = table.getRow(0);
		XWPFTableRow row = table.insertNewTableRow(index);
		if (row == null) {
			return;
		}
		CTTbl ctTbl = table.getCTTbl();
		CTTblGrid tblGrid = ctTbl.getTblGrid();
		int cellSize = 0;
		boolean isAdd = false;
		if (tblGrid != null) {
			List<CTTblGridCol> gridColList = tblGrid.getGridColList();
			if (gridColList != null && gridColList.size() > 0) {
				isAdd = true;
				for (CTTblGridCol ctlCol : gridColList) {
					XWPFTableCell cell = row.addNewTableCell();
					setCellWidthAndVAlign(cell, ctlCol.getW().toString(), STTblWidth.DXA, null);
				}
			}
		}

		if (!isAdd) {
			cellSize = getCellSizeWithMergeNum(firstRow);
			for (int i = 0; i < cellSize; i++) {
				row.addNewTableCell();
			}
		}
	}

	public static void deleteTableRow(XWPFTable table, int index) {
		table.removeRow(index);
	}

	public static int getCellSizeWithMergeNum(XWPFTableRow row) {
		List<XWPFTableCell> firstRowCellList = row.getTableCells();
		int cellSize = firstRowCellList.size();
		for (XWPFTableCell xwpfTableCell : firstRowCellList) {
			CTTc ctTc = xwpfTableCell.getCTTc();
			if (ctTc.isSetTcPr()) {
				CTTcPr tcPr = ctTc.getTcPr();
				if (tcPr.isSetGridSpan()) {
					CTDecimalNumber gridSpan = tcPr.getGridSpan();
					cellSize += gridSpan.getVal().intValue() - 1;
				}
			}
		}
		return cellSize;
	}

	public static CTTrPr getRowCTTrPr(XWPFTableRow row) {
		CTRow ctRow = row.getCtRow();
		return ctRow.isSetTrPr() ? ctRow.getTrPr() : ctRow.addNewTrPr();
	}

	public static void setRowHidden(XWPFTableRow row, boolean hidden) {
		CTTrPr trPr = getRowCTTrPr(row);
		CTOnOff hiddenValue;
		if (trPr.getHiddenList() != null && trPr.getHiddenList().size() > 0) {
			hiddenValue = trPr.getHiddenList().get(0);
		} else {
			hiddenValue = trPr.addNewHidden();
		}
		if (hidden) {
			hiddenValue.setVal(STOnOff.TRUE);
		} else {
			hiddenValue.setVal(STOnOff.FALSE);
		}
		setRowAllCellHidden(row, hidden);
	}

	public static void setRowAllCellHidden(XWPFTableRow row, boolean isVanish) {
		for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {
			XWPFTableCell cell = row.getCell(colIndex);
			setCellHidden(cell, isVanish);
		}
	}

	public static void setCellNewContent(XWPFTable table, int rowIndex, int colIndex, String content) {
		XWPFTableCell cell = table.getRow(rowIndex).getCell(colIndex);
		XWPFParagraph p = getCellFirstParagraph(cell);
		if (p.getRuns() == null || p.getRuns().size() == 0) {
			p = cell.addParagraph();
			p.createRun().setText(content);
		}
		List<XWPFRun> cellRunList = p.getRuns();
		if (cellRunList == null || cellRunList.size() == 0) {
			return;
		}
		for (int i = cellRunList.size() - 1; i >= 1; i--) {
			p.removeRun(i);
		}
//		XWPFRun run = cellRunList.get(0);
//		run.setText(content);
	}

	public static void deleteCellContent(XWPFTable table, int rowIndex, int col) {
		XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
		XWPFParagraph p = getCellFirstParagraph(cell);
		List<XWPFRun> cellRunList = p.getRuns();
		if (cellRunList == null || cellRunList.size() == 0) {
			return;
		}
		for (int i = cellRunList.size() - 1; i >= 0; i--) {
			p.removeRun(i);
		}
	}

	public static void setHiddenCellContent(XWPFTable table, int rowIndex, int col) {
		XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
		setCellHidden(cell, true);
	}

	public static void setCellHidden(XWPFTableCell cell, boolean isVanish) {
		XWPFParagraph p = getCellFirstParagraph(cell);
		CTPPr pPPr = getParagraphCTPPr(p);
		CTParaRPr paRpr = pPPr.isSetRPr() ? pPPr.getRPr() : pPPr.addNewRPr();
		CTOnOff vanishCtOnOff = paRpr.isSetVanish() ? paRpr.getVanish() : paRpr.addNewVanish();
		if (isVanish) {
			vanishCtOnOff.setVal(STOnOff.TRUE);
		} else {
			vanishCtOnOff.setVal(STOnOff.FALSE);
		}
		List<XWPFRun> cellRunList = p.getRuns();
		if (cellRunList == null || cellRunList.size() == 0) {
			return;
		}
		for (XWPFRun xwpfRun : cellRunList) {
			CTRPr pRpr = getRunCTRPr(p, xwpfRun);
			vanishCtOnOff = pRpr.isSetVanish() ? pRpr.getVanish() : pRpr.addNewVanish();
			if (isVanish) {
				vanishCtOnOff.setVal(STOnOff.TRUE);
			} else {
				vanishCtOnOff.setVal(STOnOff.FALSE);
			}
		}
	}

	public static CTTcPr getCellCTTcPr(XWPFTableCell cell) {
		CTTc cttc = cell.getCTTc();
		return cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
	}

	public static void setCellVAlign(XWPFTableCell cell, STVerticalJc.Enum vAlign) {
		setCellWidthAndVAlign(cell, null, null, vAlign);
	}

	public static void setCellWidthAndVAlign(XWPFTableCell cell, String width, STTblWidth.Enum typeEnum,
			STVerticalJc.Enum vAlign) {
		CTTcPr tcPr = getCellCTTcPr(cell);
		CTTblWidth tcw = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
		if (width != null) {
			tcw.setW(new BigInteger(width));
		}
		if (typeEnum != null) {
			tcw.setType(typeEnum);
		}
		if (vAlign != null) {
			CTVerticalJc vJc = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr.addNewVAlign();
			vJc.setVal(vAlign);
		}
	}

	public static XWPFParagraph getCellFirstParagraph(XWPFTableCell cell) {
		XWPFParagraph p;
		if (cell.getParagraphs() != null && cell.getParagraphs().size() > 0) {
			p = cell.getParagraphs().get(0);
		} else {
			p = cell.addParagraph();
		}
		return p;
	}

	public static void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
		for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
			XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
			if (cellIndex == fromCell) {
				// The first merged cell is set with RESTART merge value
				getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one,are set with CONTINUE
				getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	public static void setDocumentbackground(XWPFDocument document, String bgColor) {
		CTBackground bg = document.getDocument().isSetBackground() ? document.getDocument().getBackground()
				: document.getDocument().addNewBackground();
		bg.setColor(bgColor);
	}

	public static void addNewPageBreak(XWPFDocument document, BreakType breakType) {
		XWPFParagraph xp = document.createParagraph();
		xp.createRun().addBreak(breakType);
	}

	public static void appendExternalHyperlink(String url, String text, XWPFParagraph paragraph) {
		appendExternalHyperlink(url, text, paragraph, false);
	}

	public static void appendExternalHyperlink(String url, String text, XWPFParagraph paragraph,
			boolean isApplyLinkColor) {

		// Add the link as External relationship
		String id = paragraph.getDocument().getPackagePart()
				.addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation()).getId();

		// Append the link and bind it to the relationship
		org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink cLink = paragraph.getCTP().addNewHyperlink();
		cLink.setId(id);

		// Create the linked text
		CTText ctText = CTText.Factory.newInstance();
		ctText.setStringValue(text);
		CTR ctr = CTR.Factory.newInstance();
		ctr.setTArray(new CTText[] { ctText });

		if (isApplyLinkColor) {
			// Create the formatting
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setAscii("Calibri Light");
			CTRPr rpr = ctr.addNewRPr();
			CTColor colour = CTColor.Factory.newInstance();
			colour.setVal("0000FF");
			rpr.setColor(colour);
			CTRPr rpr1 = ctr.addNewRPr();
			rpr1.addNewU().setVal(STUnderline.SINGLE);
		}

		// Insert the linked text into the link
		cLink.setRArray(new CTR[] { ctr });
	}

	public static void addHyperlink(XWPFParagraph para, String text, String bookmark) {
		// Create hyperlink in paragraph
		org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink cLink = para.getCTP().addNewHyperlink();
		cLink.setAnchor(bookmark);
		// Create the linked text
		CTText ctText = CTText.Factory.newInstance();
		ctText.setStringValue(text);
		CTR ctr = CTR.Factory.newInstance();
		ctr.setTArray(new CTText[] { ctText });

		// Create the formatting
		CTFonts fonts = CTFonts.Factory.newInstance();
		fonts.setAscii("Calibri Light");
		CTRPr rpr = ctr.addNewRPr();
		CTColor colour = CTColor.Factory.newInstance();
		colour.setVal("0000FF");
		rpr.setColor(colour);
		CTRPr rpr1 = ctr.addNewRPr();
		rpr1.addNewU().setVal(STUnderline.SINGLE);

		// Insert the linked text into the link
		cLink.setRArray(new CTR[] { ctr });
	}

	/*
	 * Changes the text of a given paragraph.
	 */
	public static void changeParagraphText(XWPFParagraph p, String newText) {
		if (p != null) {
			List<XWPFRun> runs = p.getRuns();
			for (int i = runs.size() - 1; i >= 0; i--) {
				p.removeRun(i);
			}

			if (runs.size() == 0) {
				p.createRun();
			}

			XWPFRun run = runs.get(0);
			run.setText(newText, 0);
		}
	}

	public static void applyBasicStyleInParagraph(XWPFRun run, boolean isBold, boolean isItalics, boolean isCapitalized,
			UnderlinePatterns underlinePattern) {
		run.setBold(isBold);
		run.setCapitalized(isCapitalized);
		run.setItalic(isItalics);
		if (underlinePattern != null) {
			run.setUnderline(underlinePattern);
		}
	}

	/*******************************************************************
	 * Document Conversion methods
	 * 
	 ********************************************************************/

	public static void generatePDFFromDocxFile(String inputFilePath, String outputFilePath, String tempDirPath)
			throws InterruptedException, ExecutionException, IOException {

		generatePDFFromDocxFile(inputFilePath, outputFilePath, tempDirPath, 20, 25, 1, TimeUnit.MINUTES, 5,
				TimeUnit.MINUTES, 1000);
	}

	public static void generatePDFFromDocxFile(String inputFilePath, String outputFilePath, String tempDirPath,
			int poolSize, int maxPoolSize, long keepAliveTime, TimeUnit keepAliveTimeUnit, long processTimeOut,
			TimeUnit processTimeOutTimeUnit, int priority)
			throws InterruptedException, ExecutionException, IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream in = new BufferedInputStream(new FileInputStream(inputFilePath));

		IConverter converter = LocalConverter.builder().baseFolder(new File(tempDirPath))
				.workerPool(poolSize, maxPoolSize, keepAliveTime, keepAliveTimeUnit)
				.processTimeout(processTimeOut, processTimeOutTimeUnit).build();

		Future<Boolean> conversion = converter.convert(in).as(DocumentType.DOCX).to(baos).as(DocumentType.PDF)
				.prioritizeWith(priority) // optional
				.schedule();
		conversion.get();

		try (OutputStream outputStream = new FileOutputStream(outputFilePath)) {
			baos.writeTo(outputStream);
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		in.close();
		baos.close();
		converter.shutDown();
	}

}
