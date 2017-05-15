package csust.teacher.download;

import java.io.File;
/**
 * 
 * @author 
 *
 */
public interface DownloadListener {

	void onStart(int fileByteSize);

	void onPause();

	void onResume();

	void onProgress(int receivedBytes);

	void onFail();

	void onSuccess(File file);

	void onCancel();
}
