package epc.labs.jacksnaps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import epc.labs.jacksnaps.R;
//import com.admob.android.ads.AdManager;
/**
 *
 * @author delorenj
 */
public class JacksnapsActivity extends Activity implements OnClickListener {
  private View fetchButton;
  private static final String TAG = "Jacksnaps";
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
//    AdManager.setTestDevices( new String[] { "9237AEAE1FEDAD90E738A5776A8B07D7" } );
    initThreading();
    findViews();
    setListeners();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
  return true;
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.fetch_button:
    	new FetchJacksnapTask().execute("random");
      break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
//    case R.id.settings:
//    startActivity(new Intent(this, Prefs.class));
//    return true;
    case R.id.about:
    startActivity(new Intent(this, About.class));
    return true;

    // More items go here (if any) ...
    }
    return false;
  }

  private void findViews() {
    fetchButton = (View) findViewById(R.id.fetch_button);
  }

  private void setListeners() {
    fetchButton.setOnClickListener(this);
  }

  private void initThreading() {
  }
}
