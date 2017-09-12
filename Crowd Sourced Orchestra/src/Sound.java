/******************************************************************************
 *  Compilation:  javac Tone.java
 *  Execution:    java Tone hz duration
 *
 *  Play a note of the given freqency for the given duration.
 *
 *  % java Tone 440 1.5
 *
 ******************************************************************************/

public class Sound {

    // create a pure tone of the given frequency for the given duration
    public static double[] tone(double hz, double duration) {
        int n = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[n+1];
        for (int i = 0; i <= n; i++) {
            a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        }
        return a;
    }


    public static void main(String[] args) {

        double[] c = tone(523.25, 2);
        double[] e = tone(659.25, 2);
        double[] g = tone(783.99, 2);
        double[] cMajor = new double[c.length];
        for ( int i = 0; i < c.length; i++ ) {
            cMajor[i] = (c[i] + e[i] + g[i]) / 3;
        }

        StdAudio.play(cMajor);
    }
}