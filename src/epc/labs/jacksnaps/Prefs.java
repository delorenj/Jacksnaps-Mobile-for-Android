/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package epc.labs.jacksnaps;

import epc.labs.jacksnaps.R;

import android.preference.PreferenceActivity;
import android.os.Bundle;

/**
 *
 * @author delorenj
 */
public class Prefs extends PreferenceActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.settings);
    }

}
