package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class AnalogueThresholdWrite implements Runnable {
    private static NiDaq daq = new NiDaq();
    StimParameters stimParameters;
    private int minVal = 0;
    private int maxVal = 5;
    private Pointer analogueTask;
    private Pointer digitalTask;
    private boolean running = true;
    private TimeSeriesCollection timeSeriesCollection;
    /**
     * Physical analogue channel is a combination of the device and the analogue output channel
     */
    String physicalAnalogueChannel;
    /**
     * Physical digital channel is a combination of the device and the digital output channel
     */
    String physicalDigitalChannel;
    int sleep = 100;
    boolean ramp;
//    boolean rampup;

    public AnalogueThresholdWrite(StimParameters stimParameters) throws NiDaqException {
        this.stimParameters = stimParameters;
        this.timeSeriesCollection = stimParameters.getTimeSeriesCollection();
        ramp = stimParameters.isRampUp();
        try {
            physicalAnalogueChannel = stimParameters.getOutputDevice() + "/" + stimParameters.getAnalogueOutputChannel();
            physicalDigitalChannel = stimParameters.getOutputDevice() + "/" + stimParameters.getDigitalOutputChannel();
            System.out.println(physicalAnalogueChannel + " " + physicalDigitalChannel);
            analogueTask = daq.createTask(stimParameters.getAnalogueTask());
            digitalTask = daq.createTask(stimParameters.getDigitalTask());
            daq.resetDevice(stimParameters.getOutputDevice());
            daq.createAOVoltageChannel(analogueTask, physicalAnalogueChannel, "", minVal, maxVal,
                    Nicaiu.DAQmx_Val_Volts, null);
            daq.createDOChan(digitalTask, physicalDigitalChannel, "", Nicaiu.DAQmx_Val_ChanForAllLines);

        }catch (Exception e) {
            daq.stopTask(analogueTask);
            daq.clearTask(analogueTask);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        // Keep thread running
        try {
            while (true) {
                //System.out.println("Run thread");
                // Keep thread running
                // Sleep for 10 millis otherwise start it doesn't go into the running loop ??!?!?!?
                Thread.sleep(1);
                // Start stimulating when running is true
                long numberOfSpikes = stimParameters.getNumberOfSpikes();
                ramp = stimParameters.isRampUp();
                while (running) {
                    TimeSeries timeSeries = timeSeriesCollection.getSeries(0);
                    int itemCount = timeSeries.getItemCount();
                    Double datapoint = timeSeries.getDataItem(itemCount - 1).getValue().doubleValue();
                    daq.stopTask(analogueTask);
                    // check whether stimulation should happen on fall or rise of breath amplitude
                    if ((stimParameters.isRise() && datapoint >= stimParameters.getStimThreshold()) ||
                            (!stimParameters.isRise() && datapoint <= stimParameters.getStimThreshold())) {

                        // If ramp is selected do ramp up before stimulation
                        if (ramp) {
                            for (int i = 1; i <= numberOfSpikes; i++) {
                                daq.startTask(analogueTask);
                                double normalised = Utilities.normalise(i, 0, numberOfSpikes, 0, stimParameters.getStimValue());
                                System.out.println(i + " of " + numberOfSpikes + ": " + normalised);
                                daq.DAQmxWriteAnalogScalarF64(analogueTask, 1, 5, normalised, 0);
                                byte[] data1 = {1, 1};
                                daq.stopTask(analogueTask);
                                daq.startTask(digitalTask);
                                daq.writeDigitalLines(digitalTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data1);
                                Thread.sleep(stimParameters.getSpikeFrequency());
                                daq.stopTask(digitalTask);
                                daq.stopTask(digitalTask);
                                byte[] data0 = {0, 0};
                                daq.writeDigitalLines(digitalTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data0);
                                daq.stopTask(digitalTask);
                            }
                            ramp = false;
                        }

                        daq.startTask(analogueTask);
                        daq.startTask(digitalTask);
                        double value = stimParameters.getStimValue();
                        // Set analogue voltage in
                        daq.DAQmxWriteAnalogScalarF64(analogueTask, 1, 10, value, 0);
                        byte[] data1 = {1, 1};
                        // TTL: Digital stim for 100 microSeconds
                        daq.writeDigitalLines(digitalTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data1);
                        microSleep(100);
                        daq.stopTask(digitalTask);
                        daq.startTask(digitalTask);
                        byte[] data0 = {0, 0};
                        daq.writeDigitalLines(digitalTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data0);
                        daq.stopTask(analogueTask);
                        daq.stopTask(digitalTask);
                    } else ramp = stimParameters.isRampUp();

                }
                double zero = 0D;
                daq.DAQmxWriteAnalogScalarF64(analogueTask, 1, 10, zero, 0);
                daq.stopTask(analogueTask);

                // Calculate sleep time in milliseconds from frequency.
                Thread.sleep(1000 / stimParameters.getSpikeFrequency());
            }
        } catch (NiDaqException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void microSleep(long microTime) {
        long microseconds = microTime * 1000;
        long start;
        start = System.nanoTime();
        while(start + (microseconds * 1000) >= System.nanoTime());
    }

    public void nanoSleep(long millitime) {
//        long nanoseconds = millitime * 1000000;
//        long start;
//        start = System.nanoTime();
//        while(start + nanoseconds >= System.nanoTime());
        try {
            Thread.sleep(millitime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Return whether thread is stimulating or not. If running=true, it should be stimulating
     *
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Set running to true to start stimulating and false to stop.
     *
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
