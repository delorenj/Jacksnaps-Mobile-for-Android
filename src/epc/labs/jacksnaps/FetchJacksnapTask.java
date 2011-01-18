package epc.labs.jacksnaps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

public class FetchJacksnapTask extends AsyncTask<String, Integer, Integer> {
  private static final String TAG = "JacksnapsAsyncTask";
  private MediaPlayer mp;
  private File jacksnapSoundFile;
  
  @Override
  protected void onPreExecute() {
		deleteJacksnaps();	//Delete old Jacksnap files
  }
  
	@Override
	protected Integer doInBackground(String... params) {
    String jacksnapAudio = downloadRandomJacksnapAudio();
    playJacksnapAudio(jacksnapAudio);  			
		return 0;
	}

	private void deleteJacksnaps() {
		File sdcard = new File("/sdcard");
		FilenameFilter tempfiles = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return (filename.startsWith("jacksnap") && filename.endsWith(".tmp"));
			}				
		};
		File[] snaps = sdcard.listFiles(tempfiles);
		for(File s : snaps) {
			Log.i(TAG,"Deleting Jacksnap: " + s.getName());
			s.delete();
		}
	}
	
  public String downloadRandomJacksnapAudio() {
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
        audioPath = downloadJacksnap("http://jacksnaps.s3.amazonaws.com/js" + suffix + ".mp3");
        validAudioFile = true;
        Log.d(TAG, "Set Data Source: " + audioPath);
      } catch(java.io.IOException e) {
        Log.e(TAG, "Tried to fetch invalid Jacksnap audio file!: " + e);
      }
    }
    return audioPath;
  }

  private String downloadJacksnap(String path) throws IOException {
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
  private int getNumHostedJacksnaps() {
    //TODO: Implement method to return number of JacksnapsActivity currently served by Amazon CloudFront
    return 57;
  }  
}
