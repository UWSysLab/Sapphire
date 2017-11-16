package sapphire.appexamples.wordscramblewithfriends.device.scramble.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.Gravity;
import android.widget.Toast;

public final class ScrambleUtil {
	private static final String DICTIONARY_FILENAME = "words.txt";
	
	@SuppressLint("ShowToast")
	public static Toast createToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		return toast;
	}
	
	public static Map<String, List<String>> createDictionary(Context context) {
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(DICTIONARY_FILENAME);
			Scanner reader = new Scanner(is);
			Map<String, List<String>> map = new HashMap<String, List<String>>();

			while (reader.hasNextLine()) {
				String word = reader.nextLine();
				char[] keyArr = word.toCharArray();
				Arrays.sort(keyArr);
				String key = String.copyValueOf(keyArr);

				List<String> wordList = map.get(key);
				if (wordList == null) {
					wordList = new LinkedList<String>();
				}
				wordList.add(word);
				map.put(key, wordList);
			}
			reader.close();
			return map;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
