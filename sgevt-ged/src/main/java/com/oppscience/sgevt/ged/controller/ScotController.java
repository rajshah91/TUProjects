package com.oppscience.sgevt.ged.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.oppscience.sgevt.ged.service.ScotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.oppscience.sgevt.ged.FileHandler;
import com.oppscience.sgevt.ged.model.ScotDataSheet;

@Controller
@RequestMapping(value = "/scot")
@SuppressWarnings("all")
public class ScotController {

	@Autowired
	ScotService scotService;

	@Value("${scotfile.upload.basedir.path}")
	String scotFolder;

	private final String defaultFrom = "0";
	private final String defaultSize = "10000";

	@RequestMapping("/addscot")
	public String home(Map<String, Object> model) {
		return "addscot";
	}

	@PostMapping(value = "/savescot")
	public ResponseEntity<String> saveScot(@RequestBody ScotDataSheet scotDataSheet) {
		try {
			scotService.saveScot(scotDataSheet);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred");
		}
	}

	@GetMapping(value = "/getall")
	public ResponseEntity<List<ScotDataSheet>> getAllScot(
			@RequestParam (value = "from",required = false ,defaultValue = defaultFrom) int from,
			@RequestParam (value = "size",required = false ,defaultValue = defaultSize) int size) {
		try {
			List<ScotDataSheet> sdsl = scotService.findAll(from,size);
			return ResponseEntity.ok().body(sdsl);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}

	@GetMapping(value = "/getscot/{siren_cp}")
	public ResponseEntity<ScotDataSheet> getScot(@PathVariable("siren_cp") String siren_cp) {
		ScotDataSheet sds = null;
		try {
			sds = scotService.findById(siren_cp);
			if(sds != null) {
				return ResponseEntity.status(HttpStatus.OK).body(sds);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping(value = "/deletescot/{siren_cp}")
	public ResponseEntity<String> deleteScot(@PathVariable("siren_cp") String siren_cp) {
		try {
			if (scotService.existsById(siren_cp)) {
				String basePath = buildScotFolder(siren_cp).getAbsolutePath();
				scotService.deleteById(siren_cp);
				FileHandler.deleteDirectoryIfExist(basePath);
				return ResponseEntity.status(HttpStatus.OK).body("Deleted");
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Does not exist");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred");
		}
	}

	@PostMapping(value = "/addscot/uploadfiles/{siren_cp}")
	public ResponseEntity<String> uploadScotFiles(@RequestParam(value = "file") MultipartFile[] files,
			@PathVariable("siren_cp") String siren_cp) throws IOException {
		File dir = buildScotFolder(siren_cp);
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			try {
				byte[] bytes = file.getBytes();

				if (!dir.exists()) {
					dir.mkdirs();
				}

				File uploadFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
				BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));
				outputStream.write(bytes);
				outputStream.close();
			} catch (Exception e) {
				scotService.deleteById(siren_cp);
				FileHandler.deleteDirectoryIfExist(dir.toString());
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred");
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("success");
	}

	private File buildScotFolder(@PathVariable("siren_cp") String siren_cp) {
		return new File(scotFolder, "SCOT_" + siren_cp);
	}

}
