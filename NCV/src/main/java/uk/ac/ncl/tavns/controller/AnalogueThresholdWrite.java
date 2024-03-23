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
    String physicalChannel;
    int sleep = 100;
    boolean ramp;
//    boolean rampup;

    public AnalogueThresholdWrite(StimParameters stimParameters) throws NiDaqException {
        this.stimParameters = stimParameters;
        this.timeSeriesCollection = stimParameters.getTimeSeriesCollection();
//        this.rampup = stimParameters.isRampUp();
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
                while (running) {
                    TimeSeries timeSeries = timeSeriesCollection.getSeries(0);
                    int itemCount = timeSeries.getItemCount();
                    Double datapoint = timeSeries.getDataItem(itemCount - 1).getValue().doubleValue();

                    if ((stimParameters.isRise() && datapoint >= stimParameters.getStimThreshold()) ||
                    (!stimParameters.isRise() && datapoint <= stimParameters.getStimThreshold())) {
                        System.out.printf("Stim when: " + stimParameters.isRise() + " and " + stimParameters.getStimThreshold());
                        // If ramp is selected do ramp up before stimulation
                        if (ramp) {
                            for (int i = 0; i < stimParameters.getStimDuration(); i++) {
                                daq.startTask(doTask);
                                daq.DAQmxWriteAnalogScalarF64(doTask, 1, 5,
                                        Utilities.normalise(i, 0, stimParameters.getStimDuration(), 0,
                                                stimParameters.getStimValue()), 0);
                                Thread.sleep(sleep);
                                daq.stopTask(doTask);
                                double zero = 0D;
                                daq.DAQmxWriteAnalogScalarF64(doTask, 1, 5, zero, 0);
                                daq.stopTask(doTask);
                                Thread.sleep(sleep);
                            }
                            ramp = false;
                        }

                        daq.startTask(doTask);
                        double value = 1D;
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, stimParameters.getStimValue(), 0);
                        long start = System.nanoTime();
                        long nanoseconds = 200000000;
                        while(start + nanoseconds >= System.nanoTime());
//                        Thread.sleep(sleep);
                        daq.stopTask(doTask);
                        double zero = 0D;
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, zero, 0);
                        daq.stopTask(doTask);
                        start = System.nanoTime();
                        while(start + nanoseconds >= System.nanoTime());
//                        Thread.sleep(sleep);

                    } else ramp = stimParameters.isRampUp();
                }

            } catch (NiDaqException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
