package com.oppscience.sgevt.extraction;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.oppscience.sgevt.extraction.model.CustomExcelTab;
import com.oppscience.sgevt.extraction.utils.AppUtility;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SgevtExtractionApplicationTests {

	@Autowired
	private DataParserMain dataParserMain;

	@Autowired
	private AppUtility appUtility;

	/*@Test
	public void contextLoads() {
		System.out.println("Start");

		System.out.println("End");
	}*/

	@Test
	public void checkMetadataFileFinalName() {
		CustomExcelTab cet = getDummyObjectForNameTest();
		assertEquals("filename.tabname." + appUtility.getEnvironmentValue("OUTPUT_FIELD_FILE_EXTENSION"),
				dataParserMain.getMetadataFileFinalName(cet));
	}

	@Test
	public void checkDataFileFinalName() {
		CustomExcelTab cet = getDummyObjectForNameTest();
		assertEquals("filename.tabname." + appUtility.getEnvironmentValue("OUTPUT_DATA_FILE_EXTENSION"),
				dataParserMain.getDataFileFinalName(cet));
	}

	@Test
	public void checkCellValue() {
		Row row1 = getDummyRow();
		Cell cell1 = row1.createCell(0);
		cell1.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
		cell1.setCellValue("test");
		assertEquals("test", dataParserMain.getCellValue(cell1));

		Cell cell2 = row1.createCell(1);
		cell2.setCellType(org.apache.poi.ss.usermodel.CellType.NUMERIC);
		cell2.setCellValue(5);
		assertEquals(new Double(5), dataParserMain.getCellValue(cell2));

		Cell cell3 = row1.createCell(2);
		cell3.setCellType(org.apache.poi.ss.usermodel.CellType.BLANK);
		assertEquals(null, dataParserMain.getCellValue(cell3));

		Cell cell4 = row1.createCell(3);
		cell4.setCellType(org.apache.poi.ss.usermodel.CellType.BOOLEAN);
		cell4.setCellValue(false);
		assertEquals(new Boolean(false), dataParserMain.getCellValue(cell4));

		Cell cell5 = row1.createCell(4);
		cell5.setCellType(org.apache.poi.ss.usermodel.CellType.ERROR);
		assertEquals(null, dataParserMain.getCellValue(cell5));

	}

	@Test
	public void checkFirstCellHasMetadataInfo() {
		Cell cell = getDummyCell();
		cell.setCellType(CellType.STRING);
		cell.setCellValue("CODGEO");
		assertEquals(true, dataParserMain.isFirstCellStartsWithMetadataInfo(cell));

		cell.setCellValue("testValue");
		assertEquals(false, dataParserMain.isFirstCellStartsWithMetadataInfo(cell));

		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(5);
		assertEquals(false, dataParserMain.isFirstCellStartsWithMetadataInfo(cell));
	}

	@Test
	public void testFileListToBeProcessed() {
		File testDirPath = new File("src/test/resources/");
		// get all test files to be processed(xls,xlsx type)
		List<String> fileList = dataParserMain.getFileListToBeProcessed(testDirPath.getAbsolutePath().toString());
		assertEquals(2, fileList.size());

	}

	private CustomExcelTab getDummyObjectForNameTest() {
		CustomExcelTab cet = new CustomExcelTab();
		cet.setFileName("filename");
		cet.setTabName("tabname");
		return cet;
	}

	private Row getDummyRow() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		// create spreadsheet with a name
		XSSFSheet spreadsheet = workbook.createSheet("test sheet");
		// create first row on a created spreadsheet
		XSSFRow row = spreadsheet.createRow(0);
		return row;
	}

	private Cell getDummyCell() {
		return getDummyRow().createCell(0);
	}
}
