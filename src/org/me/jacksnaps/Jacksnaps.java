/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.jacksnaps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.URLUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author delorenj
 */
public class Jacksnaps extends Activity implements OnClickListener {
  private MediaPlayer mp;
  private Context context = this;
  private File jacksnapSoundFile;
  private File activeFile;
  private int totalKbRead = 0;
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    View fetchButton = findViewById(R.id.fetch_button);
    fetchButton.setOnClickListener(this);
    View aboutButton = findViewById(R.id.about_button);
    aboutButton.setOnClickListener(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
  return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.settings:
    startActivity(new Intent(this, Prefs.class));
    return true;
    // More items go here (if any) ...
    }
    return false;
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.about_button:
      Intent i = new Intent(this, About.class);
      startActivity(i);
      break;
    case R.id.fetch_button:
      fetchJacksnap();
      break;
    }
  }

  public void fetchJacksnap() {
    Log.d("Jacksnaps","Fetching Jacksnap!");
    String audioPath = null;
    if(mp != null) {
      mp.release();
    }
    try {
//      mp.setDataSource("http://fmlrecovery.com/jacksnaps/raw/js02.wav");
//      mp.prepare();
//      mp.start();
//      downloadJacksnapFromUrl("http://fmlrecovery.com/jacksnaps/raw/js01.ogg");
//      downloadJacksnapFromUrl("http://www.robtowns.com/music/what_other_child.mp3");
      audioPath = getDataSource("http://fmlrecovery.com/jacksnaps/raw/js03.mp3");
      Log.d("Jacksnaps", "Set Data Source: " + audioPath);
    } catch(java.io.IOException e) {
      Log.e("Jacksnaps", "Error fetching Jacksnap audio!: " + e);
    }
    try {
      mp = new MediaPlayer();
      mp.setDataSource(audioPath);
//      mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
      Log.d("Jacksnaps", "About to call Prepare() on file:" + audioPath);
      mp.prepare();
      mp.start();
    } catch(java.io.IOException e) {
      Log.e("Jacksnaps", "Error preparing Jacksnap audio!: " + e);
    }
  }

  private void downloadJacksnapFromUrl(String url) throws java.io.IOException {
   	URLConnection cn = new URL(url).openConnection();
    cn.connect();
    InputStream stream = cn.getInputStream();
    if (stream == null) {
      Log.e(getClass().getName(), "Unable to create InputStream for url:" + url);
    }
		jacksnapSoundFile = new File(context.getCacheDir(),"jacksnapSoundFile.mp3");
    FileOutputStream out = new FileOutputStream(jacksnapSoundFile);
    byte buf[] = new byte[16384];
    int totalBytesRead = 0;
    do {
      int numread = stream.read(buf);
        if (numread <= 0)
            break;
        out.write(buf, 0, numread);
        totalBytesRead += numread;
        totalKbRead = totalBytesRead/1000;
    } while (true);
    Log.d("Jacksnaps", "jacksnapSoundFile Size(bytes): " + jacksnapSoundFile.length());
    Log.d("Jacksnaps", "jacksnapSoundFile Size(bytes): " + jacksnapSoundFile.getAbsolutePath());
    stream.close();
//    activeFile = new File("/sdcard/jacksnap.ogg");
//    moveFile(jacksnapSoundFile, activeFile);
//    Log.d("Jacksnaps", "Active File Size(bytes): " + activeFile.length());
//    Log.d("Jacksnaps", "Active File Size(bytes): " + activeFile.getAbsolutePath());

//    if (validateNotInterrupted()) {
//      fireDataFullyLoaded();
//    }
  }

  	private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("jacksnaptmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
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
				Log.e("Jacksnaps", "Error fetching Jacksnap!: " + ex.getMessage(), ex);
			}
      Log.d("Jacksnaps", "Temp File Length(bytes): " + temp.length());
			return tempPath;
		}
	}
}
