package com.oppscience.sgevt.extraction;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.oppscience.sgevt.extraction")
public class SgevtExtractionApplication {

	public static void main(String[] args) throws Exception {

		ApplicationContext ctx = SpringApplication.run(SgevtExtractionApplication.class, args);

//		DataParserMain dpm = ctx.getBean(DataParserMain.class);
//		dpm.parseData();
		
//		DocumentWriter dw = ctx.getBean(DocumentWriter.class);
//		dw.createSimpleDocument();
//		dw.createDocumentWithStyle();
//		dw.createDocumentWithTable();
//		dw.createDocumentWithTable2();
//		dw.createDocumentWithTable3();
//		dw.createWordFromJson();
//		dw.convertDocxFileToPDF();
		
		
		
			File file=new File("E:\\JIMMY\\files");
			if(file.exists()) {
				String absolutePath=file.getAbsolutePath();
				String name=file.getName();
				String path=file.getPath();
				String canonicalPath=file.getCanonicalPath();
				
				System.out.println("*****************  Path");
				System.out.println("Absolute Path : "+absolutePath  );
				System.out.println("name : "+name  );
				System.out.println("Path : "+path  );
				System.out.println("canonicalPath : "+canonicalPath  );
				
				System.out.println("*****************  List Roots (Total Drives)");
				Arrays.asList(File.listRoots()).forEach(System.out :: println);
				
				System.out.println("*****************  File List in Current Directory");
				Arrays.asList(file.listFiles()).forEach(System.out :: println);
				
				System.out.println("*****************  File List in Current Directory/Sub-Directory");
				Files.walk(Paths.get(file.getAbsolutePath())).forEach(System.out :: println);
				
//				Paths.get(file.getPath()).forEach(System.out :: println);
			}
		

	}
}
