package com.muu.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.widget.Toast;

import com.muu.cartoon.test.R;
import com.muu.server.MuuClient;
import com.muu.util.PropertyMgr;

import de.greenrobot.event.EventBus;

public class AppDownloader {
	public static final String sNewAppFileName = "muu_update.apk";
	
//	private static int sDownloadPercent = 0;
	public static void downloadApp(Context ctx, String appUrl) {
//		sDownloadPercent = 0;
		UpdateProgressEvent progressEvent = new UpdateProgressEvent(UpdateProgressEvent.sStatusBegin, 0);
		EventBus.getDefault().post(progressEvent);
		
		File updatePath = new File(PropertyMgr.getInstance().getUpdatePath());
		if (!updatePath.exists()) updatePath.mkdirs();
		
		File file = new File(PropertyMgr.getInstance().getUpdatePath(), sNewAppFileName);
		FileOutputStream fos = null;
		try {
			MuuClient client = new MuuClient();
			InputStream is = client.getNewAppInputStream(appUrl);
			if (is == null) {
				Toast.makeText(ctx, ctx.getString(R.string.fail_to_download),
						Toast.LENGTH_LONG).show();
				return;
			}
			
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			
			byte[] b = new byte[8192];
            int len = 0;
//            int downLength = 0;
            fos = new FileOutputStream(file);
            while ((len = is.read(b, 0, 8192)) != -1) {
                fos.write(b, 0, len);
                fos.flush();
//                downLength += len;
//
//                float length = (Integer) downLength / (1024.0f * 1024);
//                float mTotal = (float) value / (1024 * 1024.0f);
//                int progress = (int) (length * 100 / mTotal);

//                if (progress > sDownloadPercent) {
//                    sDownloadPercent = progress;
//                    EventBus.getDefault().post(
//                            new DownloadAppProgressEvent(DOWNLOAD_PROGRESS, downLength,
//                                    sDownloadPercent));
//                    SinaLog.d("post DownloadAppProgressEvent DOWNLOAD_PROGRESS", "DOWNLOAD_PROGRESS");
//                }
            }
            
//            sDownloadPercent = 100;
            progressEvent = new UpdateProgressEvent(UpdateProgressEvent.sStatusSucc, 100);
            EventBus.getDefault().post(progressEvent);

		} catch (IOException e) {
			e.printStackTrace();
			progressEvent = new UpdateProgressEvent(UpdateProgressEvent.sStatusFail, 100);
            EventBus.getDefault().post(progressEvent);
		} finally {
            if (null != fos) {
                try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
	}
}
