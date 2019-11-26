package com.oppscience.sgevt.extraction;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oppscience.sgevt.extraction.utils.AppUtility;
import com.oppscience.sgevt.extraction.utils.HTMLtoWord;
import com.oppscience.sgevt.extraction.word.WordHandler;
import com.oppscience.sgevt.extraction.word.WordUtils;

import net.htmlparser.jericho.Source;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.codec.Base64;

@Component
@SuppressWarnings("all")
public class DocumentWriter {
	
	@Autowired
	private AppUtility appUtility;
	
//	@Autowired
	private final static WordHandler wordHandler=new WordHandler();
	
	public void createSimpleDocument() throws Exception {
		String fileName = "simpleDocument.docx";
		wordHandler.createSimpleDocument(appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH"), fileName);
	}

	public void createDocumentWithStyle() throws Exception {
		String fileName = "documentWithStyle.docx";
		wordHandler.createDocumentWithStyle(appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH"), fileName);
	}

	public void createDocumentWithTable() throws Exception {
		String fileName = "document1WithTable.docx";
		wordHandler.createDocumentWithTable(appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH"), fileName);
	}

	public void createDocumentWithTable2() throws Exception {
		String fileName = "document2WithTable.docx";
		wordHandler.createDocumentWithTable2(appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH"), fileName);
	}

	public void createDocumentWithTable3() throws Exception {
		String fileName = "document3WithTable.docx";
		wordHandler.createDocumentWithTable3(appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH"), fileName);
	}

	public void createWordFromJson() throws Exception {
		String jsonFileInputPath=appUtility.getEnvironmentValue("JSON_FILE_INPUT_PATH") ;
		String jsonFileName="pad.json";
		String wordFileOutputPath=appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH");
		String wordOutFileName="wordfromjson.docx";
		wordHandler.createWordFromJson(jsonFileInputPath, jsonFileName, wordFileOutputPath, wordOutFileName);
	}
	
	public void convertDocxFileToPDF() throws Exception {
		String inputFilePath =appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH") + "wordfromjson.docx";
		String outputFilePath = appUtility.getEnvironmentValue("WORD_FILE_OUTPUT_PATH") + "wordfromjson.pdf";
		String tempDirPath=appUtility.getEnvironmentValue("TEMP_DIR_PATH");
		wordHandler.convertDocxFileToPDF(inputFilePath, outputFilePath, tempDirPath);
	}
	
	
}
