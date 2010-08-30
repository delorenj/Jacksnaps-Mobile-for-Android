/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.jacksnapsmobile;

import android.media.MediaPlayer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.webkit.URLUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Random;

/**
 *
 * @author delorenj
 */
public class JacksnapRequest implements Runnable {
  private static final String TAG = "JacksnapRequest";
  private final JacksnapsActivity jacksnapsActivity;
  private int jacksnapId;
  private MediaPlayer mp;
  private File jacksnapSoundFile;

  JacksnapRequest(JacksnapsActivity jacksnapsActivity) {
    this.jacksnapsActivity = jacksnapsActivity;
  }

  public void run() {
    String jacksnapAudio = getJacksnapAudio();
    String jacksnapText = getJacksnapText(this.jacksnapId);
    playJacksnapAudio(jacksnapAudio);
    displayJacksnapText(jacksnapText);
  }

  private int getNumHostedJacksnaps() {
    //TODO: Implement method to return number of JacksnapsActivity currently served by Amazon CloudFront
    return 57;
  }

  public String getJacksnapAudio() {
    Log.d(TAG,"Fetching Jacksnap Audio!");
    boolean validAudioFile = false;
    String audioPath = null;
    if(mp != null) {
      mp.release();
    }
    while(!validAudioFile)
    {
      try {
        Random rg = new Random();
        int suffix = rg.nextInt(getNumHostedJacksnaps())+1;
        audioPath = getDataSource("http://jacksnaps.s3.amazonaws.com/js" + suffix + ".mp3");
        validAudioFile = true;
        this.jacksnapId = suffix;
        Log.d(TAG, "Set Data Source: " + audioPath);
      } catch(java.io.IOException e) {
        Log.e(TAG, "Tried to fetch invalid Jacksnap audio file!: " + e);
      }
    }
    return audioPath;
  }

  private String getJacksnapText(int jacksnapId) {
    return "This is hot-cous test.";
  }

  private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
      Log.d(TAG, "getDatSource(): " + path);
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null){
				throw new RuntimeException("stream is null");
      }
      if(jacksnapSoundFile != null) {
        Log.d(TAG,"Jacksnap Audio file already initialized");
        if(jacksnapSoundFile.exists()) {
          Log.d(TAG,"Jacksnaps file found on file system");
          if(mp != null) {
            Log.d(TAG,"Releasing MediaPlayer");
            mp.release();
          }
          Log.d(TAG,"Deleting Jacksnap audio file");
          jacksnapSoundFile.delete();
        }
      }
      Log.d(TAG,"Creating a Jacksnap audio file");
      jacksnapSoundFile = File.createTempFile("jacksnap",null);
			jacksnapSoundFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(jacksnapSoundFile);
			byte buf[] = new byte[128];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
			} catch (IOException ex) {
				Log.e(TAG, "Error fetching Jacksnap!: " + ex.getMessage(), ex);
			}
      Log.d(TAG, "Temp File Length(bytes): " + jacksnapSoundFile.length());
			return jacksnapSoundFile.getAbsolutePath();
		}
	}

  private void playJacksnapAudio(String audioPath) {
    try {
      mp = new MediaPlayer();
      mp.setDataSource(audioPath);
      Log.d(TAG, "About to call Prepare() on file:" + audioPath);
      mp.prepare();
      mp.start();
    } catch(java.io.IOException e) {
      Log.e(TAG, "Error preparing Jacksnap audio!: " + e);
    }
  }

  private void displayJacksnapText(String jacksnapText) {
    Log.d("JacksnapRequest", "Displaying Jacksnap Text: " + jacksnapText);
//    jacksnapsActivity.setCaption(jacksnapText);
  }
}
