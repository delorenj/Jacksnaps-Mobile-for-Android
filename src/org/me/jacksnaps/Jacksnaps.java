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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 *
 * @author delorenj
 */
public class Jacksnaps extends Activity implements OnClickListener {
  private MediaPlayer mp;
  private Context context = this;
  private File jacksnapSoundFile;
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

  private int getNumHostedJacksnaps() {
    //TODO: Implement method to return number of Jacksnaps currently served by Amazon CloudFront
    return 100;
  }

  public void fetchJacksnap() {
    Log.d("Jacksnaps","Fetching Jacksnap!");
    String audioPath = null;
    if(mp != null) {
      mp.release();
    }
    try {
      Random rg = new Random();
      int suffix = rg.nextInt(getNumHostedJacksnaps());
      audioPath = getDataSource("http://jacksnaps.s3.amazonaws.com/js" + suffix + ".mp3");
      Log.d("Jacksnaps", "Set Data Source: " + audioPath);
    } catch(java.io.IOException e) {
      Log.e("Jacksnaps", "Error fetching Jacksnap audio!: " + e);
    }
    try {
      mp = new MediaPlayer();
      mp.setDataSource(audioPath);
      Log.d("Jacksnaps", "About to call Prepare() on file:" + audioPath);
      mp.prepare();
      mp.start();
    } catch(java.io.IOException e) {
      Log.e("Jacksnaps", "Error preparing Jacksnap audio!: " + e);
    }
  }

  	private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
      Log.d("Jacksnaps", "getDatSource(): " + path);
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null){
				throw new RuntimeException("stream is null");
      }
      if(jacksnapSoundFile != null) {
        Log.d("Jacksnaps","Jacksnap Audio file already initialized");
        if(jacksnapSoundFile.exists()) {
          Log.d("Jacksnaps","Jacksnaps file found on file system");
          if(mp != null) {
            Log.d("Jacksnaps","Releasing MediaPlayer");
            mp.release();
          }
          Log.d("Jacksnaps","Deleting Jacksnap audio file");
          jacksnapSoundFile.delete();
        }
      }
      Log.d("Jacksnaps","Creating a Jacksnap audio file");
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
				Log.e("Jacksnaps", "Error fetching Jacksnap!: " + ex.getMessage(), ex);
			}
      Log.d("Jacksnaps", "Temp File Length(bytes): " + jacksnapSoundFile.length());
			return jacksnapSoundFile.getAbsolutePath();
		}
	}
}
