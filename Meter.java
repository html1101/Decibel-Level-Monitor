import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;

// Runnable is a class that can be put into a thread so that this can be executed separately from main.
public class Meter {
    // We can change stopped to stop running our audio level thread 
    private static boolean stopped = false;
    private static ArrayList<Float> rms_list;
    
    // Run method must be defined to run thread.
    public static void main(String args[]) {
        rms_list = new ArrayList<Float>();
        // Specifies arrangement of data in sound stream.
        // # of samples/sec, # of bits per sample, # of channels(mono = 1)
        AudioFormat format = new AudioFormat(44100f, 16, 1, true, false);
        final int bufferByteSize = 2048;

        // Data line from which audio can be read
        TargetDataLine line;
        try {
            line = AudioSystem.getTargetDataLine(format);

            // Attempt to open data line, catch error if issues
            line.open(format, bufferByteSize);
        } catch(LineUnavailableException e) {
            System.out.println("Unable to create data line! Likely no microphone connected.");
            System.err.println(e);
            return;
        }

        // Create buffer of size bufferByteSize from which to read data
        byte[] buffer = new byte[bufferByteSize];
        float[] samples = new float[bufferByteSize / 2];

        // Start reading!
        line.start();

        // Keep recording data until stopped
        while(!stopped) {
            // Get bytes
            int b = line.read(buffer, 0, buffer.length);

            // Convert bytes to samples, source: https://stackoverflow.com/questions/26574326/how-to-calculate-the-level-amplitude-db-of-audio-signal-in-java
            for(int i = 0, s = 0; i < b;) {
                int sample = 0;

                sample |= buffer[i++] & 0xFF;
                sample |= buffer[i++] << 8;

                // Normalize samples to range of +/-1.0f
                samples[s++] = sample / 32768f;
            }

            float rms = 0f;

            // Iterate through samples
            for(float sample : samples) {
                rms += sample * sample;
            }

            rms = (float)Math.sqrt(rms / samples.length);
            rms_list.add(rms);

            System.out.println(rms);
        }
    }
}
