package com.yxy.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * 压缩工具类
 */
public class CompressUtil {

	public static final int BUFFER = 1024 * 8;

	/**
	 * GZip 压缩
	 */
	public static byte[] compressGZip(byte[] bContent) {
		byte[] data = null;
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gOut = new GZIPOutputStream(out, bContent.length);

			gOut.write(bContent);
			gOut.finish();
			gOut.flush();
			gOut.close();

			data = out.toByteArray();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * GZip 解压
	 */
	public static byte[] decompressGZip(byte[] bContent) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try {

			ByteArrayInputStream in = new ByteArrayInputStream(bContent);
			GZIPInputStream pIn = new GZIPInputStream(in);

			int count = 0;
			byte[] data = new byte[BUFFER];

			while ((count = pIn.read(data, 0, BUFFER)) != -1) {
				result.write(data, 0, count);
			}

			pIn.close();
			in.close();
			byte[] unGzip = result.toByteArray();
			result.close();
			return unGzip;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * ZIP 压缩
	 */
	public static byte[] compressZip(byte[] bContent) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(bos);
			ZipEntry entry = new ZipEntry("zip");
			entry.setSize(bContent.length);
			zip.putNextEntry(entry);
			zip.write(bContent);
			zip.closeEntry();
			zip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/***
	 * Zip 解压
	 */
	public static byte[] decompressZip(byte[] bContent) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
			ZipInputStream zip = new ZipInputStream(bis);

			while (zip.getNextEntry() != null) {
				byte[] buf = new byte[BUFFER];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = zip.read(buf, 0, BUFFER)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
			}
			zip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * BZip2 压缩
	 */
	public static byte[] compressBZip2(byte[] bContent) {
		byte[] data = null;
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BZip2CompressorOutputStream zip2out = new BZip2CompressorOutputStream(
					out);
			zip2out.write(bContent);
			zip2out.flush();
			zip2out.close();
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * BZIP2 解压
	 */
	public static byte[] decompressBZip2(byte[] bContent) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
			BZip2CompressorInputStream zip = new BZip2CompressorInputStream(bis);
			ByteArrayOutputStream decompre = new ByteArrayOutputStream();
			decompress(zip, decompre);
			b = decompre.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * 压缩
	 * 
	 * @param is
	 *            需要压缩的流
	 * @param cos
	 *            压缩流
	 * @throws Exception
	 */
	public static void compress(InputStream is, CompressorOutputStream cos)
			throws Exception {
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = is.read(data, 0, BUFFER)) != -1) {
			cos.write(data, 0, count);
		}
		cos.finish();
		cos.flush();
		cos.close();

		is.close();
	}

	/**
	 * 解压
	 * 
	 * @param cis
	 *            需要解压的流
	 * @param os
	 *            保存解压完的流
	 */
	public static void decompress(CompressorInputStream cis, OutputStream os) {

		try {
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = cis.read(data, 0, BUFFER)) != -1) {
				os.write(data, 0, count);
			}
			cis.close();

			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把流转换为 byte[]
	 * 
	 * @param ins
	 * @return
	 * @throws IOException
	 */
	public static byte[] readByte(InputStream ins) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int count = 0;
		byte[] buff = new byte[BUFFER];
		while ((count = ins.read(buff, 0, BUFFER)) > 0) {
			os.write(buff, 0, count);
		}
		os.flush();
		os.close();
		return os.toByteArray();
	}
}
