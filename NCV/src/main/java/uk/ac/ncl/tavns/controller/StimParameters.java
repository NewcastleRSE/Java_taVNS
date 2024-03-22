package uk.ac.ncl.tavns.controller;

import org.jfree.data.time.TimeSeriesCollection;

public class StimParameters {

    private String outputDevice;
    private String outputChannel;
    private String taskName;
    private double stimValue;
    private TimeSeriesCollection timeSeriesCollection;
    private double stimThreshold;
    private boolean rise; // If true check for threshold on rise else on fall
//    private double stimEndThreshold;
    private boolean rampUp;
    private double stimDuration;

    public StimParameters(String outputDevice, String outputChannel, String taskName, double stimValue,
                          TimeSeriesCollection timeSeriesCollection, double stimThreshold,
                          double stimEndThreshold, boolean rampUp, double stimDuration) {
        this.outputDevice = outputDevice;
        this.outputChannel = outputChannel;
        this.taskName = taskName;
        this.stimValue = stimValue;
        this.timeSeriesCollection = timeSeriesCollection;
        this.stimThreshold = stimThreshold;
//        this.stimEndThreshold = stimEndThreshold;
        this.rampUp = rampUp;
        this.stimDuration = stimDuration;
    }

    public StimParameters() {
    }

    public String getOutputDevice() {
        return outputDevice;
    }

    public void setOutputDevice(String outputDevice) {
        this.outputDevice = outputDevice;
    }

    public String getOutputChannel() {
        return outputChannel;
    }

    public void setOutputChannel(String outputChannel) {
        this.outputChannel = outputChannel;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public double getStimValue() {
        return stimValue;
    }

    public void setStimValue(double stimValue) {
        this.stimValue = stimValue;
    }

    public TimeSeriesCollection getTimeSeriesCollection() {
        return timeSeriesCollection;
    }

    public void setTimeSeriesCollection(TimeSeriesCollection timeSeriesCollection) {
        this.timeSeriesCollection = timeSeriesCollection;
    }

    public double getStimThreshold() {
        return stimThreshold;
    }

    public void setStimThreshold(double stimThreshold) {
        this.stimThreshold = stimThreshold;
    }

//    public double getStimEndThreshold() {
//        return stimEndThreshold;
//    }
//
//    public void setStimEndThreshold(double stimEndThreshold) {
//        this.stimEndThreshold = stimEndThreshold;
//    }

    public boolean isRampUp() {
        return rampUp;
    }

    public void setRampUp(boolean rampUp) {
        this.rampUp = rampUp;
    }

    public double getStimDuration() {
        return stimDuration;
    }

    public void setStimDuration(double stimDuration) {
        this.stimDuration = stimDuration;
    }

    /**
     * If ture, check for threshold on rise, if false, check for rise on fall
     * i.e. rise > threshold, fall < threshold
     * @return
     */
    public boolean isRise() {
        return rise;
    }

    public void setRise(boolean rise) {
        this.rise = rise;
    }
}
