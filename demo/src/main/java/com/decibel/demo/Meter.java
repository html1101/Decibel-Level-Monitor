package com.decibel.demo;

// This manages continuously monitoring audio levels.

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

// Runnable is a class that can be put into a thread so that this can be executed separately from main.
public class Meter {
    // We can change stopped to stop running our audio level thread 
    private boolean stopped = false;

    // Use to keep track of the time minute-by-minute
    private RMSTimestamp r_time = new RMSTimestamp();

    // HashMap containing hour, then the days & their measurement in that hour.
    // Sample: { "9:00": { "3-21-22": [0.1, 0.3, 0.1] }  }
    private HashMap<String, HashMap<String,  ArrayList<Float>>> calc = new HashMap<String, HashMap<String,  ArrayList<Float>>>();

    // hh:mm
    public String timeToString(int hr, int min) {
        return String.format("%2s", hr).replace(" ", "0") + ":" + String.format("%2s", min).replace(" ", "0");
    }

    // dd-mm-yyyy
    public String getDate() {
        DateTimeFormatter formatMethod = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return LocalDateTime.now().format(formatMethod);
    }

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
                // Minute passed, find average of points that passed in this moment
                float avg_rms = sum_rms / counter;
                String t = timeToString(n.getHour(), 0);
                
                // Place this in calc.
                // 1) Check if there are existing records at this hour.
                if(calc.containsKey(t)) {
                    // There is! Now check if there are some for this date.
                    if(!calc.get(t).containsKey(getDate())) {
                        // 2) Create array for this date.
                        System.out.println("There is not an existing array for date: " + getDate() + " at hour " + t);
                        // There isn't an array existing for this date, create it.
                        calc.get(t).put(getDate(), new ArrayList<Float>());
                    }
                } else {
                    // There aren't existing records at this hour, create them.
                    HashMap<String, ArrayList<Float>> new_hr = new HashMap<String, ArrayList<Float>>();
                    new_hr.put(getDate(), new ArrayList<Float>());
                    calc.put(t, new_hr);
                }
                calc.get(t).get(getDate()).add((float)(20 * Math.log10(avg_rms)));

                // Reset
                sum_rms = 0;
                counter = 0;

                // Push new hour that we are at now
                alreadyResp = min;
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

    public HashMap<String,ArrayList<Float>> getRMSList(String time) {
        for(String t : calc.keySet()) {
            for(String k : calc.get(t).keySet()) {
                System.out.println("Time: " + t + ", " + k + ": " + calc.get(t).get(k));
            }
        }
        if(calc.containsKey(time)) {
            // Return avg rms points separated by date
            return calc.get(time);
        }
        return new HashMap<>();
    }
}
