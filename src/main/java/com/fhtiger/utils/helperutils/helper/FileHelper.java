package com.fhtiger.utils.helperutils.helper;

import java.io.*;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 处理文件
 * 
 * @author LFH
 * @since 2019/7/30 14:41
 * @version 0.0.1
 */
public class FileHelper {

	/**
	 * 解析URL字符串
	 * 
	 * @param url URL字符串,可能是有%XX编码的字符串
	 * @return String
	 */
	public static String decodeURL(String url) {
		try {
			return URLDecoder.decode(url, SystemHelper.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			// 不支持的编码，则直接返回URL
		}
		return url;
	}

	/**
	 * 获取文件fileName对应的类型
	 * 
	 * @param fileName 文件名
	 * @return String
	 */
	public static String getContentType(String fileName) {
		String fileSuffix = getFileSuffix(fileName);
		return StringHelper.trim(ContentTypeHelper.get(fileSuffix));
	}

	/**
	 * 得到文件的contentType
	 * 
	 * @param file 文件
	 * @return String
	 */
	public static String getContentType(File file) {
		String fileSuffix = getFileSuffix(file);
		return StringHelper.trim(ContentTypeHelper.get(fileSuffix));
	}

	/**
	 * 将filePath转换为当前系统下的标准文件路径,将/和\替换为{@link File#separator}
	 * 
	 * @param filePath 文件路径
	 * @return String
	 * 
	 * @see File#separator
	 */
	public static String getSystemFilePath(String filePath) {
		return filePath.replace("/", File.separator).replace("\\", File.separator);
	}

	/**
	 * 得到文件后缀名，包括点号
	 * 
	 * @param file 文件
	 * @return String
	 */
	public static String getFileSuffix(File file) {
		return getFileSuffix(file.getName());
	}

	/**
	 * 得到文件后缀名,包括点号.
	 * 注意得到的后缀名全小写
	 * 
	 * @param fileName 文件名
	 * @return String
	 */
	public static String getFileSuffix(String fileName) {
		String path = getSystemFilePath(fileName);
		int indexPoint = path.lastIndexOf(File.separator);
		if (indexPoint <= 0) return ""; // 没有点号,则没有后缀名
		int indexSeparator = path.indexOf(File.separator, indexPoint);
		if (indexSeparator > -1) return ""; // 点号后面还发现了文件分隔符,即没有后缀名
		return path.substring(indexPoint).toLowerCase(); // 返回含有点号的后缀名
	}

	/**
	 * 得到文件名,不含后缀名
	 * 
	 * @param fileName 文件名
	 * @return String
	 */
	public static String getSimpleFileName(String fileName) {
		String path = getSystemFilePath(fileName);
		int indexSeparator = path.lastIndexOf(File.separator) + 1;
		int indexPoint = path.lastIndexOf(File.separator, path.length());
		if (indexPoint < indexSeparator) return path.substring(indexSeparator);
		return path.substring(indexSeparator, indexPoint);
	}

	/**
	 * 得到文件名,含后缀名
	 * 
	 * @param fileName 可能包含目录的文件名
	 * @return String
	 */
	public static String getFileName(String fileName) {
		String path = getSystemFilePath(fileName);
		int indexSeparator = path.lastIndexOf(File.separator);
		return indexSeparator < 0 ? fileName : path.substring(indexSeparator + 1);
	}

	/**
	 * 创建新文件,如果文件存在,则在后面加上fileName(i)的形式,直到得到一个不存在的新文件为止
	 * 
	 * @param filePath 要创建的文件全路径
	 * @return 文件对象
	 */
	public static File createNewFile(String filePath) {
		File file = new File(filePath);
		return FileHelper.createNewFile(file.getParentFile(), file.getName());
	}

	/**
	 * 在folder目录下创建新文件,如果文件已经存在,则对文件名后加上(index),直到创建一个新文件为止
	 * 
	 * @param folder 一个已经存在的目录
	 * @param fileName 在此目录下创建的文件名
	 * @return 新建的文件
	 */
	public static File createNewFile(File folder, String fileName) {
		return FileHelper.createNewFile(folder, fileName, true);
	}

	/**
	 * 
	 * 在folder目录下尝试创建新文件,如果文件已经存在,则对文件名后加上(index),直到一个不存在的新文件为止
	 * 
	 * @param folder 一个已经存在的目录
	 * @param fileName 在此目录下创建的文件名
	 * @param create 是否创建文件,如果为false,则仅返回可建立的文件,需要外部调用file.createNewFile()创建文件
	 * @return 可创建或已创建的文件
	 */
	public static File createNewFile(File folder, String fileName, boolean create) {
		File file = new File(folder, fileName);
		String simpleFileName = null;
		String fileSuffix = null;
		if (file.exists()) {
			// 文件存在则继续创建
			simpleFileName = getSimpleFileName(fileName);
			fileSuffix = getFileSuffix(fileName);
			int index = 2;
			do {
				file = new File(folder, simpleFileName + "(" + (index++) + ")" + fileSuffix);
			} while (file.exists());
		}
		if (create) {
			try {
				// 创建文件
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return file;
	}

	/** 进制,每个单位之间的基数 */
	public static final int RADIX = 1024;
	/** 字节 */
	public static final long B = 1;
	/** 1KB=RADIXB */
	public static final long KB = B * RADIX;
	/** 1MB=RADIXKB=RADIX*RADIXB */
	public static final long MB = KB * RADIX;
	/** GB */
	public static final long GB = MB * RADIX;
	/** TB */
	public static final long TB = GB * RADIX;
	/** 显示的文件单位 */
	public static final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

	/** 文件大小显示的数字样式 */
	public static final String NUMBER_FORMAT_PATTERN = "#,##0.### ";

	/**
	 * 将文件大小(单位为B字节)格式化为最近单位的文件大小显示,显示的格式如:"0.###" KB
	 * 
	 * @param size 文件大小,单位为B字节
	 * @return String
	 */
	public static String formatFileSize(double size) {
		int u = 0;
		String unit = UNITS[u++];
		double min = size;
		while (min >= RADIX) {
			unit = UNITS[u++];
			min = min / RADIX;
		}
		return NumberHelper.format(min, NUMBER_FORMAT_PATTERN) + unit;
	}

	/**
	 * 复制文件,将文件scr复制到目标文件去
	 * 
	 * @param src 源文件
	 * @param dest 目标文件
	 */
	public static void copy(File src, File dest) {
		try (FileInputStream in = new FileInputStream(src)) {
			write(dest, in);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将输入流的数据写入file中
	 * 
	 * @param file 目标文件
	 * @param in 文件流
	 */
	public static void write(File file, InputStream in) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			byte[] b = new byte[RADIX];
			int c = 0;
			while ((c = in.read(b)) > 0)
				out.write(b, 0, c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将内容content按照encodning编码写入文件file中
	 * 
	 * @param file     文件
	 * @param content 内容
	 * @param encoding 编码{@link java.nio.charset.StandardCharsets}
	 */
	public static void write(File file, String content, String encoding) {
		write(file, content, encoding, false);
	}

	/**
	 * 将内容content按照encodning编码写入文件file中
	 * 
	 * @param file     文件
	 * @param content 内容
	 * @param encoding 编码{@link java.nio.charset.StandardCharsets}
	 * @param append 是否再追加内容
	 */
	public static void write(File file, String content, String encoding, boolean append) {
		try (OutputStream out = new FileOutputStream(file, append);
				OutputStreamWriter writer = StringHelper.isEmpty(encoding) ? new OutputStreamWriter(out)
						: new OutputStreamWriter(out, encoding);) {
			writer.write(content);
			writer.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param file 文件
	 * @return String
	 */
	public static String read(File file) {
		return read(file, null);
	}

	/**
	 * 按照指定编码读取文件内容
	 * 
	 * @param file 文件
	 * @param encoding 读取文件使用的编码{@link java.nio.charset.StandardCharsets},可以为null
	 * @return String
	 */
	public static String read(File file, String encoding) {
		StringBuilder content = new StringBuilder();
		try (InputStream in = new FileInputStream(file);
				InputStreamReader reader = StringHelper.isEmpty(encoding) ? new InputStreamReader(in)
						: new InputStreamReader(in, encoding);) {
			char[] c = new char[RADIX];
			int len;
			while ((len = reader.read(c)) > 0) {
				content.append(c, 0, len);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return content.toString();
	}

	/**
	 * 将文件的编码改为encoding
	 * 
	 * @param file     文件
	 * @param encoding 要更改为的编码,不能为null{@link java.nio.charset.StandardCharsets}
	 */
	public static void encode(File file, String encoding) {
		encode(file, null, encoding);
	}

	/**
	 * 更改文件的编码
	 * 
	 * @param file          文件
	 * @param readEncoding 读取文件时使用的编码,可以为null
	 * @param writeEncoding 要更改为的编码,不能为null
	 */
	public static void encode(File file, String readEncoding, String writeEncoding) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if(files!=null){
				for (File f : files) {
					encode(f, readEncoding, writeEncoding);
				}
			}
			return;
		}
		if (!file.isFile()) return;

		String content = read(file, readEncoding);

		FileHelper.write(file, content, writeEncoding);
	}

	/**
	 * 压缩目录dir到同名的zip文件中
	 * 
	 * @param dir 压缩目录
	 * @throws IOException io异常:{@link IOException}
	 * @return File 压缩文件
	 */
	public static File zip(File dir) throws IOException {
		File zip = new File(dir.getParentFile(), dir.getName() + ".zip");
		zip = FileHelper.createNewFile(zip.getAbsolutePath());
		return zip(dir, zip);
	}

	/**
	 * 将dir目录下的文件压缩到ZIP文件中
	 * 
	 * @param dir 要压缩的目录
	 * @param zipFile zip文件
	 * @throws IOException io异常:{@link IOException}
	 * @return  File 压缩文件
	 */
	public static File zip(File dir, File zipFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), RADIX));
		zip(out, dir, null);
		out.close();
		return zipFile;
	}

	/**
	 * 输出file到zip中
	 * 
	 * @param out        压缩文件输出流
	 * @param file		压缩文件
	 * @param parentPath 上级目录名,可以为空或者已/结尾
	 * @throws IOException io异常:{@link IOException}
	 */
	public static void zip(ZipOutputStream out, File file, String parentPath) throws IOException {
		ZipEntry zip = null;
		String name = file.getName();
		name = (parentPath == null ? "" : parentPath) + name;
		if (file.isDirectory()) {
			// 目录的名字必须以/结尾
			name = name + "/";
			zip = new ZipEntry(name);
			out.putNextEntry(zip);
			out.closeEntry();
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					zip(out, f, name);
				}
			}
		} else if (file.isFile()) {
			zip = new ZipEntry(name);
			zip.setSize(file.length());
			out.putNextEntry(zip);
			FileInputStream in = new FileInputStream(file);
			byte[] buf = new byte[RADIX];
			int count;
			while ((count = in.read(buf)) > 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.closeEntry();
		}
		out.flush();
	}
}
