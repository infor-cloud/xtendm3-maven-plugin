package com.infor.m3.xtendm3.maven.plugin.util;

import com.infor.m3.xtendm3.maven.plugin.model.type.ErrorCode;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveUtils {
  private final Supplier<Log> logger;

  public ArchiveUtils(Supplier<Log> logger) {
    this.logger = logger;
  }

  public void zipDirectory(String archiveName, String directory) throws MojoFailureException {
    logger.get().debug(String.format("Archiving directory %s to a zip file named %s", directory, archiveName));
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(archiveName);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      File fileToZip = new File(directory);
      zipFile(fileToZip, fileToZip.getName(), zipOut);
      zipOut.close();
      fos.close();
    } catch (IOException e) {
      AssertionUtils.getInstance().fail(ErrorCode.INTERNAL_ERROR, e);
    }
  }

  private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
    if (fileToZip.isHidden()) {
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName));
        zipOut.closeEntry();
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }
      File[] children = fileToZip.listFiles();
      if (children != null) {
        for (File childFile : children) {
          zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
        }
      }
      return;
    }
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }
}
