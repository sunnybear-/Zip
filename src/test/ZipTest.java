package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import threadpool.ExecutorPoolType;
import threadpool.ExecutorProcessPool;

public class ZipTest {
	private static long zipStartTime = 0L;
	private static long unZipStartTime = 0L;

	public static void main(String[] args) throws ZipException {
		System.out.println("分卷压缩开始");
		zipStartTime = System.currentTimeMillis();
		zipSubsection("C:\\Users\\1261267\\Downloads", "C:\\zipTest\\zipTest.zip", 1024 * 1024 * 100);
		System.out.println("分卷压缩结束,用时:" + (System.currentTimeMillis() - zipStartTime) + "ms");

		upload(ExecutorProcessPool.newInstance(ExecutorPoolType.SCHEDULED), traverseFolder("C:\\zipTest"),
				new UploadCallback() {
					@Override
					public void onUploadSuccess() {
						try {
							Thread.sleep(3 * 1000);
							System.out.println("全部上传完成");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});

		// System.out.println("解压缩开始");
		// unZipStartTime = System.currentTimeMillis();
		// unZipSubsection("C:\\zipTest\\zipTest.zip", "C:\\zipTest");
		// System.out.println("解压缩结束,用时:" + (System.currentTimeMillis() -
		// unZipStartTime) + "ms");
	}

	public static void zipSubsection(String src, String dest, long splitLength) throws ZipException {
		ZipFile zipFile = new ZipFile(dest);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
		File srcFile = new File(src);
		if (srcFile.isDirectory())
			zipFile.createZipFileFromFolder(src, parameters, true, splitLength);
		else
			zipFile.createZipFile(srcFile, parameters, true, splitLength);
	}

	public static void unZipSubsection(String zipFile, String dest) throws ZipException {
		ZipFile zFile = new ZipFile(zipFile);
		zFile.setFileNameCharset("UTF-8");
		if (!zFile.isValidZipFile())
			throw new ZipException("压缩文件不合法,可能被损坏.");

		File destDir = new File(dest);
		if (destDir.isDirectory() && !destDir.exists())
			destDir.mkdir();
		zFile.extractAll(dest);
	}

	private static List<File> mFiles;

	static {
		mFiles = new ArrayList<>();
	}

	/**
	 * 遍历文件夹获得全部文件
	 *
	 * @param dirPath
	 *            文件夹路径
	 * @return 文件夹中的全部文件集合
	 */
	public static List<File> traverseFolder(String dirPath) {
		File dir = new File(dirPath);
		if (dir.exists()) {
			File[] fs = dir.listFiles();
			if (fs != null)
				if (fs.length == 0)
					return new ArrayList<>();
				else
					for (File file : fs) {
						if (file.isDirectory()) {
							System.out.println("文件夹:" + file.getAbsolutePath());
							traverseFolder(file.getAbsolutePath());
						} else {
							System.out.println("文件:" + file.getAbsolutePath());
							mFiles.add(file);
						}
					}
		} else {
			System.out.println("文件夹不存在");
		}
		return mFiles;
	}

	private static int index = 0;

	public static void upload(ExecutorProcessPool pool, List<File> files, UploadCallback callback) {
		final int count = files.size();
		for (File file : files) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("-------------这里执行上传任务，Callable TaskName = " + file.getAbsolutePath()
								+ "-------------" + "线程名:" + Thread.currentThread().getName());
						System.out.println("上传文件:" + file.getAbsolutePath());
						Thread.sleep(10 * 1000);
						System.out.println("上传文件:" + file.getAbsolutePath() + "成功");
						index++;
						if (index == count)
							callback.onUploadSuccess();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
	}
}
