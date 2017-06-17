package org.es.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipUtil {
	public static void compressZipfile(String sourceDir, String outputFile) throws IOException, FileNotFoundException {
		ZipOutputStream zipFile = null;
		try{
			zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
			compressDirectoryToZipfile(sourceDir, sourceDir, zipFile);
		}finally {
			IOUtils.closeQuietly(zipFile);
		}
		
		
	}

	private static void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out)
			throws IOException, FileNotFoundException {
		for (File file : new File(sourceDir).listFiles()) {
			if (file.isDirectory()) {
				compressDirectoryToZipfile(rootDir, sourceDir + file.getName() + File.separator, out);
			} else {
				ZipEntry entry = new ZipEntry(sourceDir.replace(rootDir, "") + file.getName());
				out.putNextEntry(entry);
				FileInputStream in = null;
				try{
					in = new FileInputStream(sourceDir + file.getName());
					IOUtils.copy(in, out);
				}finally {
					IOUtils.closeQuietly(in);
				}
				
			}
		}
	}
}
