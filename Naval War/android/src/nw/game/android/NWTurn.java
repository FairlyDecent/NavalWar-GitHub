package nw.game.android;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NWTurn {

	public static final String TAG = "NWTurn";
	
	/** Turn counter which increments every turn of a match */
	public int turnCounter;
	
	public byte[] persist() {
		JSONObject retVal = new JSONObject();

        try {
            retVal.put("turnCounter", turnCounter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
	}
	
	public static NWTurn unpersist(byte[] byteArray) {
		if (byteArray == null) {
			Log.d(TAG, "Empty array---possible bug.");
			return new NWTurn();
		}
		
		String st = null;
		try {
			st = new String(byteArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		Log.d(TAG, "===UNPERSIST \n" + st);
		
		NWTurn retVal = new NWTurn();
		
		try {
			JSONObject obj = new JSONObject(st);
			
			if (obj.has("turnCounter")) {
				retVal.turnCounter = obj.getInt("turnCounter");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
}