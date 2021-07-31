package com.ims.task.demo.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class FileUtils {

	public static String getFileContentType(String fileName, InputStream sourceInputStream)
			throws IOException, SAXException, TikaException {

		ContentHandler contenthandler = new BodyContentHandler();

		Metadata metadata = new Metadata();

		metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);

		Parser parser = new AutoDetectParser();

		parser.parse(sourceInputStream, contenthandler, metadata, null);

		String contentType = metadata.get(Metadata.CONTENT_TYPE);

		return contentType;
	}

	public static boolean validateFileType(String filecontentType) {

		switch (filecontentType) {
		case "image/jpg":
			return true;
		case "image/jpeg":
			return true;
		case "image/png":
			return true;
		case "image/gif":
			return true;
		default:
			return false;
		}
	}

	public static boolean checkFileSize(long fileSizeWithByte, Integer validSizeWithMB) {

		double fileSizeWithMega = (fileSizeWithByte) / (1000 * 1000.0);

		if (Float.parseFloat(String.format("%.2f", fileSizeWithMega)) <= validSizeWithMB) {

			return true;

		} else {

			return false;
		}
	}

	public static File convertMultiPartToFile(MultipartFile inputFile, String folderPath) {

		try {

			String OriginalFileName = inputFile.getOriginalFilename();
			String fileName = inputFile.getName();

			String pathAsString = null;

			if (OriginalFileName != null && !OriginalFileName.equals(""))
				pathAsString = folderPath + OriginalFileName;
			else if (fileName != null && !fileName.equals(""))
				pathAsString = folderPath + fileName;

			if (pathAsString != null && !pathAsString.equals("")) {

				File folder = new File(folderPath);

				if (!folder.exists())
					folder.mkdir();

				File file = Files.write(Path.of(pathAsString), inputFile.getBytes()).toFile();

				return file;

			} else {

				return null;
			}

		} catch (Exception ex) {

			ex.printStackTrace();

			return null;
		}
	}

	public static boolean deleteFile(String path) {

		try {

			Files.deleteIfExists(Path.of(path));

			return true;

		} catch (IOException e) {

			e.printStackTrace();

			return false;
		}
	}
	
	public static boolean deleteFolder(File file) {

		try {

			for (File subFile : file.listFiles()) {

				if (subFile.isDirectory())
					deleteFolder(subFile);
				else
					subFile.delete();

			}

			file.delete();

			return true;

		} catch (Exception ex) {

			ex.printStackTrace();

			return false;
		}
	}
}