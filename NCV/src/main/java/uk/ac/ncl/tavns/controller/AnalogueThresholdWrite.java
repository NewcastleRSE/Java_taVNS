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
    private Pointer doTask;
    private boolean running = true;
    private TimeSeriesCollection timeSeriesCollection;
    /**
     * Physical channel is a combination of the device and the channel
     */
    String physicalChannel;
    int sleep = 100;
    boolean ramp;
//    boolean rampup;

    public AnalogueThresholdWrite(StimParameters stimParameters) throws NiDaqException {
        this.stimParameters = stimParameters;
        this.timeSeriesCollection = stimParameters.getTimeSeriesCollection();
        ramp = stimParameters.isRampUp();
        try {
            physicalChannel = stimParameters.getOutputDevice() + "/" + stimParameters.getOutputChannel();
            doTask = daq.createTask(stimParameters.getTaskName());
            daq.resetDevice(stimParameters.getOutputDevice());
            daq.createAOVoltageChannel(doTask, physicalChannel, "", minVal, maxVal,
                    Nicaiu.DAQmx_Val_Volts, null);
        } catch (Exception e) {
            daq.stopTask(doTask);
            daq.clearTask(doTask);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        // Keep thread running

        while (true) {
            try {
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
                    daq.stopTask(doTask);
                    // check whether stimulation should happen on fall or rise of breath amplitude
                    if ((stimParameters.isRise() && datapoint >= stimParameters.getStimThreshold()) ||
                    (!stimParameters.isRise() && datapoint <= stimParameters.getStimThreshold())) {
                        // If ramp is selected do ramp up before stimulation
                        if (ramp) {
                            for (int i = 1; i <= numberOfSpikes; i++) {
                                daq.startTask(doTask);
                                double normalised = Utilities.normalise(i, 0, numberOfSpikes, 0, stimParameters.getStimValue());
                                System.out.println(i + " of " + numberOfSpikes + ": " + normalised);
                                daq.DAQmxWriteAnalogScalarF64(doTask,1, 5, normalised, 0);
                                Thread.sleep(165);
                                daq.stopTask(doTask);
                                double zero = 0D;
                                daq.DAQmxWriteAnalogScalarF64(doTask,1, 5, zero, 0);
                                daq.stopTask(doTask);
                                Thread.sleep(165);
                            }
                            ramp = false;
                        }

                        daq.startTask(doTask);
                        double value = stimParameters.getStimValue();
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, value, 0);
                        Thread.sleep(165);
                        daq.stopTask(doTask);
                        double zero = 0D;
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, zero, 0);
                        daq.stopTask(doTask);
                        Thread.sleep(165);
                    } else ramp = stimParameters.isRampUp();
                }

            } catch (NiDaqException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

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
