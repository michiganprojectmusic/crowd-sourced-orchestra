import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Sound {

	public static void main(String[] args) throws Exception {
		JSONObject json = new JSONObject();
		JSONObject oldJSON = new JSONObject();
		while (true) {
			oldJSON = json;
			json = readJsonFromUrl("https://crowd-sourced-orchestra.firebaseio.com/users.json");
			if (!oldJSON.toString().equals(json.toString())) {
				System.out.println(json.toString());
				//TODO: Need to add some code to stop the old notes from playing
				play(json.toString());
			}
			Thread.sleep(100);
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	// create a pure tone of the given frequency for the given duration
	public static double[] tone(double hz, double duration) {
		int n = (int) (StdAudio.SAMPLE_RATE * duration);
		double[] a = new double[n + 1];
		for (int i = 0; i <= n; i++) {
			a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
		}
		return a;
	}

	public static void play(String jsonText) {

		ArrayList<double[]> tones = new ArrayList<double[]>();
		int prev = 0, index = 1;
		index = jsonText.indexOf("\"freq\"", prev);

		do {
			index += 8; // shift to data
			prev = index;
			double freq = Integer.parseInt(jsonText.substring(index, jsonText.indexOf("\"", index)));

			//TODO: Improve latency. Adding these huge arrays slows the program down significantly.
			tones.add(tone(freq, 20));

			index = jsonText.indexOf("\"freq\"", prev);
		} while (index > 0);

		if (!tones.isEmpty()) {

			double[] combo = new double[tones.get(0).length];

			for (int i = 0; i < tones.size(); i++) {
				for (int j = 0; j < tones.get(0).length; j++) {
					combo[j] += tones.get(i)[j];
				}
			}
			for (int j = 0; j < tones.get(0).length; j++) {
				combo[j] /= tones.size();
			}

			StdAudio.play(combo);

		}
	}

}
