package com.oppscience.sgevt.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oppscience.sgevt.extraction.model.CustomExcelTab;
import com.oppscience.sgevt.extraction.utils.AppUtility;

@Component
public class DataParserMain {

	private AppUtility appUtility;

	@Autowired
	public void setAppUtility(AppUtility appUtility) {
		this.appUtility = appUtility;
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static List<Short> bgColorList = new ArrayList<>();
	private static List<Short> fgColorList = new ArrayList<>();

	static {
		bgColorList.add((short) 31);
		bgColorList.add((short) 64);

		fgColorList.add((short) 22);
		fgColorList.add((short) 44);
	}

	public void parseData() {
		String filePath=appUtility.getEnvironmentValue("INPUT_FILE_DIR_PATH");
		List<String> fileNamesList = getFileListToBeProcessed(filePath);
		for (String fName : fileNamesList) {
			File file = new File(fName);
			if (file != null) {
				readDataFromFile(file);
			}
		}
		LOGGER.info("------------------------------TASK COMPLETED------------------------------");
	}

	public boolean readDataFromFile(File file) {
		boolean readSuccess=false;
		try {

			FileInputStream excelFile = new FileInputStream(file);
			Workbook workbook = null;
			String fileNameWithOutExtension = AppUtility.getFileNameWithoutExtension(file);

			if (file.getName().endsWith(appUtility.getEnvironmentValue("FILE_EXTENSION_TYPE_XLS"))) {
				workbook = new HSSFWorkbook(excelFile);
			} else if (file.getName().endsWith(appUtility.getEnvironmentValue("FILE_EXTENSION_TYPE_XLSX"))) {
				workbook = new XSSFWorkbook(excelFile);
			}

			int totalSheetCount = workbook.getNumberOfSheets();

			// Iterate all sheets(tabs)
			for (int i = 0; i < totalSheetCount; i++) {

				Sheet currentSheet = workbook.getSheetAt(i);
				String sheetName = currentSheet.getSheetName();

				// skip sheet which has no data for use like formula,documentation
				if (!(AppConstants.SHEETS_TO_SKIP.contains(sheetName))) {

					CustomExcelTab customTab = new CustomExcelTab();
					customTab.setFileName(fileNameWithOutExtension);
					customTab.setTabName(sheetName);

					processCurrentSheet(currentSheet, customTab);
				}

			}
			readSuccess=true;

		} catch (FileNotFoundException e) {
			LOGGER.error("--------- FILE NOT FOUND ---------");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error("--------- " + e.getMessage() + " ---------");
			e.printStackTrace();
		}
		return readSuccess;
	}

	private void processCurrentSheet(Sheet currentSheet, CustomExcelTab customTab) {

		Iterator<Row> iterator = currentSheet.iterator();
		List<String> columnNames = new ArrayList<>();
		Map<Integer, ArrayList<Object>> rows = new HashMap<>();

		int fieldRowIndex = 0;

		// iterate all rows
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			int currentRowIndex = currentRow.getRowNum();

			Iterator<Cell> cellIterator = currentRow.iterator();

			// iterate all columns
			while (cellIterator.hasNext()) {

				Cell currentCell = cellIterator.next();

				if (currentCell != null) {

					if (isPossibleMetadataCell(currentCell)
							&& isFirstCellStartsWithMetadataInfo(currentRow.getCell(0))) {

						if (currentCell.getCellTypeEnum() == CellType.STRING) {
							columnNames.add(currentCell.getStringCellValue());
							fieldRowIndex = currentRowIndex;
						}
					} else if (fieldRowIndex != 0 && currentRowIndex > fieldRowIndex) {
						if (rows.containsKey(currentRowIndex)) {
							rows.get(currentRowIndex).add(getCellValue(currentCell));
						} else {
							ArrayList<Object> objList = new ArrayList<>();
							objList.add(getCellValue(currentCell));
							rows.put(currentRowIndex, objList);
						}
					}
				}
			}
		}

		if (columnNames.size() > 0 && !rows.isEmpty() && rows.size() > 0) {
			customTab.setColumns(columnNames);
			customTab.setRows(rows);
			generateOutputFiles(customTab);
		}
	}

	public List<String> getFileListToBeProcessed(String inputFilePath) {

		List<String> fileNamesList = new ArrayList<>();

		try (Stream<Path> walk = Files.walk(Paths.get(inputFilePath))) {

			fileNamesList = walk.map(x -> x.toString())
					.filter(f -> f.endsWith(appUtility.getEnvironmentValue("FILE_EXTENSION_TYPE_XLSX"))
							|| f.endsWith(appUtility.getEnvironmentValue("FILE_EXTENSION_TYPE_XLS")))
					.collect(Collectors.toList());

		} catch (FileNotFoundException e) {
			LOGGER.error("--------- FILE NOT FOUND ---------");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("--------- IO Exception ---------");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error("--------- " + e.getMessage() + " ---------");
			e.printStackTrace();
		}
		return fileNamesList;
	}

	public boolean isPossibleMetadataCell(Cell currentCell) {
		boolean possibleMetadataCell=false;
		if (currentCell.getCellStyle() != null 
				&& bgColorList.contains(currentCell.getCellStyle().getFillBackgroundColor()) 
				&& fgColorList.contains(currentCell.getCellStyle().getFillForegroundColor())) {
			possibleMetadataCell=true;
		}
		return possibleMetadataCell;	
	}

	public boolean isFirstCellStartsWithMetadataInfo(Cell firstCell) {
		if (firstCell.getCellTypeEnum() == CellType.STRING) {
			return AppConstants.STARTING_FIELDNAMES.contains(firstCell.getStringCellValue().trim());
		} else {
			return false;
		}
	}

	public Object getCellValue(Cell currentCell) {
		Object obj = null;
		if (currentCell.getCellTypeEnum() == CellType.STRING) {
			obj = currentCell.getStringCellValue();
		} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
			obj = currentCell.getNumericCellValue();
		} else if (currentCell.getCellTypeEnum() == CellType.BOOLEAN) {
			obj = currentCell.getBooleanCellValue();
		}
		return obj;

	}

	private void generateOutputFiles(CustomExcelTab customTab) {
		prepareMetadataFile(customTab);
		prepareDataFile(customTab);
	}

	// Prepare Column File
	private void prepareMetadataFile(CustomExcelTab customTab) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(customTab.getTabName());

		int fieldRowNum = 0;
		LOGGER.info("Preparing file ......" + getMetadataFileFinalName(customTab));
		int colNum = 0;
		for (String columnNames : customTab.getColumns()) {
			Row fieldRow = sheet.createRow(fieldRowNum++);
			Cell cell = fieldRow.createCell(colNum);
			cell.setCellValue((String) columnNames);
		}
		createFile(workbook, getMetadataFileFinalName(customTab));

	}

	// Prepare Data File
	private void prepareDataFile(CustomExcelTab customTab) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(customTab.getTabName());

		int rowNum = 0;
		Set<Entry<Integer, ArrayList<Object>>> itr = customTab.getRows().entrySet();
		LOGGER.info("Preparing file ......" + getDataFileFinalName(customTab));
		for (Map.Entry<Integer, ArrayList<Object>> map : itr) {
			Row row = sheet.createRow(rowNum++);
			ArrayList<Object> columnList = map.getValue();
			int colNum = 0;
			for (Object field : columnList) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer || field instanceof Double) {
					cell.setCellValue((Double) field);
				} else if (field instanceof Boolean) {
					cell.setCellValue((Boolean) field);
				} else if (field instanceof Date) {
					cell.setCellValue((Date) field);
				} else {
					// System.out.println("ERROR :: " + field);
				}
			}
		}
		createFile(workbook, getDataFileFinalName(customTab));

	}

	private void createFile(Workbook workbook, String fileName) {
		try {
			File file = new File(appUtility.getEnvironmentValue("OUTPUT_FILE_DIR_PATH"));
			if (!file.exists()) {
				file.mkdir();
			}
			// LOGGER.info("Now generating file ......" + fileName);
			FileOutputStream outputStream = new FileOutputStream(
					appUtility.getEnvironmentValue("OUTPUT_FILE_DIR_PATH") + fileName);
			workbook.write(outputStream);
			workbook.close();

			LOGGER.info("Success generating file ......" + fileName);
		} catch (FileNotFoundException e) {
			LOGGER.error("--------- FILE NOT FOUND : " + fileName + " ---------");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("--------- IO Exception: " + fileName + " ---------");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error("--------- " + e.getMessage() + " ---------");
			e.printStackTrace();
		}
	}

	public String getMetadataFileFinalName(CustomExcelTab customTab) {
		if (!AppUtility.isNullOrEmpty(customTab.getFileName()) && !AppUtility.isNullOrEmpty(customTab.getTabName())) {
			return customTab.getFileName() + "." + customTab.getTabName() + "."
					+ appUtility.getEnvironmentValue("OUTPUT_FIELD_FILE_EXTENSION");
		} else {
			return null;
		}
	}

	public String getDataFileFinalName(CustomExcelTab customTab) {
		if (!AppUtility.isNullOrEmpty(customTab.getFileName()) && !AppUtility.isNullOrEmpty(customTab.getTabName())) {
			return customTab.getFileName() + "." + customTab.getTabName() + "."
					+ appUtility.getEnvironmentValue("OUTPUT_DATA_FILE_EXTENSION");
		} else {
			return null;
		}
	}

}
