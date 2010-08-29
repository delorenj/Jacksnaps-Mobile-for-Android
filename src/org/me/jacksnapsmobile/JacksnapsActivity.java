/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.jacksnapsmobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;



/**
 *
 * @author delorenj
 */
public class JacksnapsActivity extends Activity implements OnClickListener {
  private View fetchButton;
  private TextView jacksnapCaption;
  private Handler guiThread;
  private ExecutorService jacksnapRequestThread;
  private Runnable updateRequest;
  private Future jacksnapRequestPending;
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
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
      // Cancel previous update if it hasn't started yet
      guiThread.removeCallbacks(updateRequest);
      // Start an update if nothing happens after a few milliseconds
      guiThread.postDelayed(updateRequest, 500);
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
    jacksnapCaption = (TextView) findViewById(R.id.jacksnap_caption);
  }

  private void setListeners() {
    fetchButton.setOnClickListener(this);
  }

  private void initThreading() {
    guiThread = new Handler();
    jacksnapRequestThread = Executors.newSingleThreadExecutor();
    updateRequest = new Runnable() {
      public void run() {
        // Get text to translate
        if (jacksnapRequestPending != null) {
          jacksnapRequestPending.cancel(true);
        }
        jacksnapCaption.setText(R.string.fetch_notice);
        try {
          JacksnapRequest jacksnapRequest = new JacksnapRequest(JacksnapsActivity.this);
          jacksnapRequestPending = jacksnapRequestThread.submit(jacksnapRequest);
        } catch (RejectedExecutionException e) {
          // Unable to start new task
          jacksnapCaption.setText(R.string.fetch_error);
        }
      }
    };
  }

  /** All changes to the GUI must be done in the GUI thread */
  private void guiSetText(final TextView view, final String text) {
    guiThread.post(new Runnable() {
      public void run() {
        view.setText(text);
      }
    });
  }

  /** Modify text on the screen (called from another thread) */
  public void setCaption(String text) {
    guiSetText(jacksnapCaption, text);
  }
}
