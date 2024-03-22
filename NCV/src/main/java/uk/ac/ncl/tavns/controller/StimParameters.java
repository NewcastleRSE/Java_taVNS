package uk.ac.ncl.tavns.controller;

import org.jfree.data.time.TimeSeriesCollection;

public class StimParameters {

    String outputDevice;
    String outputChannel;
    String taskName;
    double stimValue;
    TimeSeriesCollection timeSeriesCollection;
    double stimStartThreshold;
    double stimEndThreshold;
    boolean rampUp;
    double stimDuration;

    public StimParameters(String outputDevice, String outputChannel, String taskName, double stimValue,
                          TimeSeriesCollection timeSeriesCollection, double stimStartThreshold,
                          double stimEndThreshold, boolean rampUp, double stimDuration) {
        this.outputDevice = outputDevice;
        this.outputChannel = outputChannel;
        this.taskName = taskName;
        this.stimValue = stimValue;
        this.timeSeriesCollection = timeSeriesCollection;
        this.stimStartThreshold = stimStartThreshold;
        this.stimEndThreshold = stimEndThreshold;
        this.rampUp = rampUp;
        this.stimDuration = stimDuration;
    }

    public StimParameters(String outputDevice) {
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

    public double getStimStartThreshold() {
        return stimStartThreshold;
    }

    public void setStimStartThreshold(double stimStartThreshold) {
        this.stimStartThreshold = stimStartThreshold;
    }

    public double getStimEndThreshold() {
        return stimEndThreshold;
    }

    public void setStimEndThreshold(double stimEndThreshold) {
        this.stimEndThreshold = stimEndThreshold;
    }

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
}
