package com.decibel.demo;

// This manages continuously monitoring audio levels.

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Runnable is a class that can be put into a thread so that this can be executed separately from main.
public class Meter {
    // We can change stopped to stop running our audio level thread 
    private boolean stopped = false;
    private RMSTimestamp r_time = new RMSTimestamp();
    private ArrayList<Float> avg_rms = new ArrayList<Float>();
    private ArrayList<String> freq = new ArrayList<String>();
        
    // How to format dates
    private DateTimeFormatter formatMethod = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    // Contains RMS list w/ some timestamp
    private class RMSTimestamp {
        private float sum_rms = 0;
        private int counter = 0;
        private ArrayList<LocalDateTime> timestamp = new ArrayList<LocalDateTime>();
        private int alreadyResp = 0;
        
        public void addRMS(float rms) {            
            // Tag every RMS sample with a timestamp
            // LocalDateTime.now().format(formatMethod);
            LocalDateTime n = LocalDateTime.now();
            timestamp.add(n);
            sum_rms += rms;
            counter++;
            int min = LocalDateTime.now().getMinute();

            if(n.getSecond() == 0 && alreadyResp != min) {
                // Passed an hour, time to stop
                System.out.println("Hour passed");
                avg_rms.add(sum_rms / counter);

                // Reset
                sum_rms = 0;
                counter = 0;

                // Push new hour that we are at now
                alreadyResp = min;
                freq.add(LocalDateTime.now().format(formatMethod));
            }
        }
    }

    // The continuous monitoring bit.
    public class RunIt implements Runnable {
        // Run method must be defined to run thread.
        public void run() {
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

                r_time.addRMS(rms);
            }
        }
    }

    public ArrayList<Float> getRMSList() {
        return avg_rms;
    }

    // Given from starting time to ending time, get the list of all rms points we found at that stage
    public ArrayList<Float> getRange(String point_1, String point_2) {
        LocalTime p1 = LocalTime.parse(point_1);
        LocalTime p2 = LocalTime.parse(point_2);
        
        // Go through points that range from these points
    }
}
