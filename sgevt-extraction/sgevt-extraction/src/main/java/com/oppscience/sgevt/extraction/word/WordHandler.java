package com.oppscience.sgevt.extraction.word;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.htmlparser.jericho.Source;

//@Component since we require pure java class for word task,I am commenting @component annotation 
//and creating this class's object manually in documentwriter class
public class WordHandler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private final static String LEVEL_1_SPACE = "    ";
	
	public void createSimpleDocument(String fileOutputPath,String fileName) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		XWPFParagraph paragraph = WordUtils.createNewParagraph(document);
		XWPFRun run = WordUtils.getOrAddParagraphFirstRun(paragraph, true, false);

		for (int i = 0; i < 25; i++) {
			run = WordUtils.getOrAddParagraphFirstRun(paragraph, false, true);
			run.setText("This is Line # " + (i + 1));
		}
		WordUtils.saveDocument(document, fileOutputPath, fileName);
		LOGGER.info("Document Created ...... " + fileName);
	}
	
	public void createDocumentWithStyle(String fileOutputPath,String fileName) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		WordUtils.createHeader(document, "This is Header");
		WordUtils.createFooter(document, "This is Footer");

		XWPFParagraph paragraph1 = WordUtils.createNewParagraph(document);
		XWPFRun run = WordUtils.getOrAddParagraphFirstRun(paragraph1, true, false);
		WordUtils.applyBasicStyleInParagraph(run, true, true, false, null);

		run.setColor("FF0000");
		WordUtils.setParagraphTextFontInfo(paragraph1, false, true, "Para 1", "Verdana", "10");
		for (int i = 0; i < 10; i++) {
			run = WordUtils.getOrAddParagraphFirstRun(paragraph1, false, true);
			run.setFontSize(15);
			run.setText("This is Line # " + (i + 1));
		}
		paragraph1.setFontAlignment(2);
		WordUtils.saveDocument(document, fileOutputPath, fileName);
		LOGGER.info("Document Created ...... " + fileName);
	}
	
	public void createDocumentWithTable(String fileOutputPath,String fileName) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		// create table
		XWPFTable table = document.createTable();
		// create first row
		XWPFTableRow tableRowOne = table.getRow(0);
		tableRowOne.getCell(0).setText("col one, row one");
		tableRowOne.addNewTableCell().setText("col two, row one");
		tableRowOne.addNewTableCell().setText("col three, row one");
		// create second row
		XWPFTableRow tableRowTwo = table.createRow();
		tableRowTwo.getCell(0).setText("col one, row two");
		tableRowTwo.getCell(1).setText("col two, row two");
		tableRowTwo.getCell(2).setText("col three, row two");
		// create third row
		XWPFTableRow tableRowThree = table.createRow();
		tableRowThree.getCell(0).setText("col one, row three");
		tableRowThree.getCell(1).setText("col two, row three");
		tableRowThree.getCell(2).setText("col three, row three");
		WordUtils.saveDocument(document, fileOutputPath, fileName);
		LOGGER.info("Document Created ...... " + fileName);
	}
	
	public void createDocumentWithTable2(String fileOutputPath,String fileName) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		XWPFTable table = document.createTable(2, 3);
		table.setStyleID("LightList");
		WordUtils.setCellNewContent(table, 0, 0, "Row 1,Col 1");
		WordUtils.setCellNewContent(table, 0, 1, "Row 1,Col 2");
		WordUtils.setCellNewContent(table, 1, 2, "Row 2,Col 3");

		WordUtils.saveDocument(document, fileOutputPath, fileName);
		LOGGER.info("Document Created ...... " + fileName);
	}
	
	public void createDocumentWithTable3(String fileOutputPath,String fileName) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		int nRows = 6;
		int nCols = 3;
		XWPFTable table = document.createTable(nRows, nCols);

		// Set the table style. If the style is not defined, the table style will become
		// "Normal".
		CTTblPr tblPr = table.getCTTbl().getTblPr();
		CTString styleStr = tblPr.addNewTblStyle();
		styleStr.setVal("StyledTable");

		// Get a list of the rows in the table
		List<XWPFTableRow> rows = table.getRows();
		int rowCt = 0;
		int colCt = 0;
		for (XWPFTableRow row : rows) {
			// get table row properties (trPr)
			CTTrPr trPr = row.getCtRow().addNewTrPr();
			// set row height; units = twentieth of a point, 360 = 0.25"
			CTHeight ht = trPr.addNewTrHeight();
			ht.setVal(BigInteger.valueOf(360));

			// get the cells in this row
			List<XWPFTableCell> cells = row.getTableCells();
			// add content to each cell
			for (XWPFTableCell cell : cells) {
				// get a table cell properties element (tcPr)
				CTTcPr tcpr = cell.getCTTc().addNewTcPr();
				// set vertical alignment to "center"
				CTVerticalJc va = tcpr.addNewVAlign();
				va.setVal(STVerticalJc.CENTER);

				// create cell color element
				CTShd ctshd = tcpr.addNewShd();
				ctshd.setColor("auto");
				ctshd.setVal(STShd.CLEAR);
				if (rowCt == 0) {
					// header row
					ctshd.setFill("A7BFDE");
				} else if (rowCt % 2 == 0) {
					// even row
					ctshd.setFill("D3DFEE");
				} else {
					// odd row
					ctshd.setFill("EDF2F8");
				}

				// get 1st paragraph in cell's paragraph list
				XWPFParagraph para = cell.getParagraphs().get(0);
				// create a run to contain the content
				XWPFRun rh = para.createRun();
				// style cell as desired
				if (colCt == nCols - 1) {
					// last column is 10pt Courier
					rh.setFontSize(10);
					rh.setFontFamily("Courier");
				}
				if (rowCt == 0) {
					// header row
					rh.setText("header row, col " + colCt);
					rh.setBold(true);
					para.setAlignment(ParagraphAlignment.CENTER);
				} else if (rowCt % 2 == 0) {
					// even row
					rh.setText("row " + rowCt + ", col " + colCt);
					para.setAlignment(ParagraphAlignment.LEFT);
				} else {
					// odd row
					rh.setText("row " + rowCt + ", col " + colCt);
					para.setAlignment(ParagraphAlignment.LEFT);
				}
				colCt++;
			} // for cell
			colCt = 0;
			rowCt++;
		} // for row
		WordUtils.saveDocument(document, fileOutputPath, fileName);
	}
	
	public void createWordFromJson(String jsonFileInputPath,String jsonFileName,String wordFileOutputPath,String wordOutFileName) throws Exception {
		String jsonStr = new String(Files.readAllBytes(Paths.get(jsonFileInputPath+jsonFileName)));
		File outputFile = new File(wordFileOutputPath+wordOutFileName);
		generateWord(jsonStr, outputFile);
		LOGGER.info("File Created ...... " + outputFile.getName());
	}
	
	public void convertDocxFileToPDF(String inputFilePath, String outputFilePath, String tempDirPath) throws Exception {
		WordUtils.generatePDFFromDocxFile(inputFilePath,outputFilePath,tempDirPath);
		LOGGER.info("File Converted from Docx to PDF ...... ");
	}
	
	public void generateWord(String jsonStr, File outPutWord) throws Exception {
		XWPFDocument document = WordUtils.createNewDocument();
		WordUtils.setPageMargin(document, 720L, 720L, 1440L, 1440L);
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode = objectMapper.readValue(jsonStr, JsonNode.class);
		if (rootNode != null && rootNode.size() > 0) {
			Iterator<String> keys = rootNode.fieldNames();
			while (keys.hasNext()) {
				iterateAndManageJson(document, keys, rootNode, 1);
			}
		}
		WordUtils.saveDocument(document, outPutWord);
	}
	
	private void iterateAndManageJson(XWPFDocument document, Iterator<String> keys, JsonNode rootNode, int level) {
		List<Object> result = createParagraphForJsonNode(document, keys, rootNode, level);
		if (result != null && result.size() == 2) {
			Iterator<String> subKeys = (Iterator<String>) result.get(0);
			JsonNode subNode = (JsonNode) result.get(1);
			if (level == 3) { // process final json (html json)
				createParagraphForFinalJsonNode(document, subNode);
			}
			while (subKeys.hasNext()) {
				iterateAndManageJson(document, subKeys, subNode, level + 1);
			}
		}
	}

	private List<Object> createParagraphForJsonNode(XWPFDocument document, Iterator<String> keys, JsonNode node,int level) {
		List<Object> list = new ArrayList();
		String paraContent = keys.next();
		JsonNode subNode = node.get(paraContent);
		Iterator<String> subKeys = subNode.fieldNames();
		createAndApplyStyleInParagraph(document, paraContent, level);
		list.add(subKeys);
		list.add(subNode);
		return list;
	}

	private void createParagraphForFinalJsonNode(XWPFDocument document, JsonNode subNode2) {
		if (subNode2 != null && subNode2.size() > 0) {
			for (int i = 0; i < subNode2.size(); i++) {
				JsonNode finalNode = subNode2.get(i);
				if (finalNode != null && finalNode.get("html") != null) {
					createAndApplyStyleInParagraph(document, finalNode.get("html").asText(), 4);
				}
			}
		}
	}

	private void createAndApplyStyleInParagraph(XWPFDocument document, String paraContent, int level) {
		boolean isNewLine = (level == 1) ? true : false;
		boolean isBold = (level == 1) ? true : false;
		XWPFParagraph paragraph = WordUtils.createNewParagraph(document);
		XWPFRun run = WordUtils.getOrAddParagraphFirstRun(paragraph, true, false);
		WordUtils.applyBasicStyleInParagraph(run, isBold, false, false, null);
		run = WordUtils.getOrAddParagraphFirstRun(paragraph, false, isNewLine);
		if (level == 1) {
			run.setText(paraContent);
		} else if (level == 2) {
			run.setText(LEVEL_1_SPACE + paraContent);
		} else if (level == 3) {
			paragraph.setIndentFromLeft(600);
			String paraStr= "Page " + paraContent;
			WordUtils.appendExternalHyperlink("http://www.google.com",paraStr,paragraph,true);
		} else if (level == 4) {
			paragraph.setIndentFromLeft(1700);
//			paragraph.setSpacingBetween(1);
//			RichTextString temp = HTMLtoWord.fromHtmlToCellValue(paraContent, run);
//			Source source = new Source(temp.toString());
			Source source = new Source(paraContent);
			String parsedHTMLText = source.getTextExtractor().toString();
			run.setText(parsedHTMLText);
		}
	}

}
