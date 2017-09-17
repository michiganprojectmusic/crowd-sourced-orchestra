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
				// TODO: Need to add some code to stop the old notes from
				// playing
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

	// create a triangle wave of the given frequency for the given duration
	public static double[] triangle(double hz, double duration) {
		int n = (int) (StdAudio.SAMPLE_RATE * duration);
		double[] a = new double[n + 1];
		int period = (int) (StdAudio.SAMPLE_RATE / hz);
		boolean up = true;
		a[0] = 0;
		for (int i = 1; i <= n; i++) {
			if (up) {
				a[i] = a[i - 1] + (2. / period);
			} else {
				a[i] = a[i - 1] - (2. / period);
			}

			if (i % (period / 2) == 0) {
				up = !up;
			}
		}
		return a;
	}

	// create a square wave of the given frequency for the given duration
	public static double[] square(double hz, double duration) {
		int n = (int) (StdAudio.SAMPLE_RATE * duration);
		double[] a = new double[n + 1];
		int period = (int) (StdAudio.SAMPLE_RATE / hz);
		double val = .1;
		for (int i = 0; i <= n; i++) {
			a[i] = val;

			if (i % (period / 2) == 0) {
				val = -val;
			}
		}
		return a;
	}

	public static void play(String jsonText) {

		ArrayList<double[]> tones = new ArrayList<double[]>();
		int startIndex = 1;

		startIndex = jsonText.indexOf(":{", 0);

		do {
			int activeIndex = jsonText.indexOf("\"active\"", startIndex) + 9;
			boolean active = true;
			if (activeIndex != 8 && activeIndex < (startIndex + 38)) { 
				// May have	calculated offset wrong, errors likey with short uniqnames
				active = Boolean.parseBoolean(jsonText.substring(activeIndex, jsonText.indexOf(",", activeIndex)));
			}
			if (active) {

				int freqIndex = jsonText.indexOf("\"freq\"", startIndex) + 8;
				double freq = Double.parseDouble(jsonText.substring(freqIndex, jsonText.indexOf("\"", freqIndex)));

				int typeIndex = jsonText.indexOf("\"type\"", startIndex) + 8;
				String type = jsonText.substring(typeIndex, jsonText.indexOf("\"", typeIndex));

				// TODO: Improve latency. adding() these huge arrays slows the
				// program down significantly.
				if (typeIndex > (startIndex + 38)) { 
					// Inaccurate offset, but should work since type comes after active
					// old data without type field
					tones.add(tone(freq, 1));
				} else if (type.equals("Sine")) {
					tones.add(tone(freq, 1));
				} else if (type.equals("Triangle")) {
					tones.add(triangle(freq, 1));
				} else if (type.equals("Square")) {
					tones.add(square(freq, 1));
				} else {
					// Accounts for -1 return for typeIndex
					tones.add(tone(freq, 1));
				}

			}

			startIndex = jsonText.indexOf(":{", startIndex + 1);
		} while (startIndex > 0);

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
