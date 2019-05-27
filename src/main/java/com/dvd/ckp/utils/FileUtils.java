package com.dvd.ckp.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Session;

public class FileUtils {

	private static final Logger logger = Logger.getLogger(FileUtils.class);

	private String filePathOutput;
	private String key;
	private static String SAVE_PATH;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFilePathOutput() {
		return filePathOutput;
	}

	public void setFilePathOutput(String filePathOutput) {
		this.filePathOutput = filePathOutput;
	}

	public void saveFile(Media media, Session session) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		File vfile;
		String uploadPath;
		try {

			SAVE_PATH = Constants.PATH_FILE_UPLOAD;

			final String vstrfileName = media.getName();

			// uploadPath = session.getWebApp().getRealPath(DOCUMENT_FORDER);
			uploadPath = SAVE_PATH;

			File baseDir = new File(uploadPath);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			vfile = new File(baseDir + File.separator + vstrfileName);
			filePathOutput = uploadPath + vstrfileName;

			if (!media.isBinary()) {
				Reader reader = media.getReaderData();

				Writer writer = new FileWriter(vfile);
				copyCompletely(reader, writer);
			} else {
				InputStream fin = media.getStreamData();
				in = new BufferedInputStream(fin);
				OutputStream fout = new FileOutputStream(vfile);
				out = new BufferedOutputStream(fout);
				byte buffer[] = new byte[1024];
				int ch = in.read(buffer);
				while (ch != -1) {
					out.write(buffer, 0, ch);
					ch = in.read(buffer);
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}

			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}

	}

	/*
	 * Ham copy file
	 */
	private void copyCompletely(Reader input, Writer output) throws IOException {
		char[] buf = new char[8192];
		while (true) {
			int length = input.read(buf);
			if (length < 0) {
				break;
			}
			output.write(buf, 0, length);
		}

		try {
			input.close();
		} catch (IOException ignore) {
			logger.error(ignore.getMessage(), ignore);
		}
		try {
			output.close();
		} catch (IOException ignore) {
			logger.error(ignore.getMessage(), ignore);
		}
	}

	/**
	 * Ham tra ve danh sach ten file anh
	 *
	 * @param path:
	 *            duong dan thu muc
	 * @return
	 */
	public static List<String> getImages(String path) {
		List<String> listFileExtend = new ArrayList<>();
		listFileExtend.add("jpg");
		listFileExtend.add("png");
		listFileExtend.add("gif");
		List<String> listFileImage = new ArrayList<>();
		File directoryImage = new File(path);
		File[] files = directoryImage.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					String[] tmp = file.getName().split("\\.");
					String fileExtend = tmp[tmp.length - 1];
					if (listFileExtend.contains(fileExtend.toLowerCase())) {
						listFileImage.add(file.getName());
					}
				}
			}
		}
		return listFileImage;
	}
}
