package uk.ac.ncl.tavns.controller;

import org.jfree.data.time.TimeSeriesCollection;

public class StimParameters {

    /**
     * NiDAQ Output device
     */
    private String outputDevice;
    /**
     * NiDAQ Outuput channel
     */
    private String analogueOutputChannel;
    /**
     * NIDAQ digital output channel
     */
    private String digitalOutputChannel;
    /**
     * NiDAQ analogue task name
     */
    private String analogueTask;
    /**
     * NiDAQ digital task name;
     */
    private String digitalTask;
    /**
     * Voltage at which to stimulate
     */
    private double stimValue;
    /**
     * Recorded values
     */
    private TimeSeriesCollection timeSeriesCollection;
    /**
     * Threshold at which to stimulate
     */
    private double stimThreshold;
    /**
     * 0 for continuous stimulation
     * 1 for stimulation on inspiration
     * 2 for stimulation on expiration
     */
    private int stim; // If true check for threshold on rise else on fall
    /**
     * If true, do rampup before stimulation
     */
    private boolean rampUp;
    /**
     * Number of stims in rampup
     */
    private Long numberOfSpikes;
    /**
     * Time period of spike
     */
    private Long spikeFrequency;
     /**
     * Default constructor
     */
    public StimParameters() {
    }

    public String getOutputDevice() {
        return outputDevice;
    }

    public void setOutputDevice(String outputDevice) {
        this.outputDevice = outputDevice;
    }

    public String getAnalogueOutputChannel() {
        return analogueOutputChannel;
    }

    public void setAnalogueOutputChannel(String analogueOutputChannel) {
        this.analogueOutputChannel = analogueOutputChannel;
    }

    public String getAnalogueTask() {
        return analogueTask;
    }

    public void setAnalogueTask(String analogueTask) {
        this.analogueTask = analogueTask;
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

    public Long getNumberOfSpikes() {
        return numberOfSpikes;
    }

    public void setNumberOfSpikes(long numberOfSpikes) {
        this.numberOfSpikes = numberOfSpikes;
    }

    /**
     * 0 to stimulate continuously
     * 1 to stimulate on inspiration
     * 2 to stimulate on expiration
     * @return
     */
    public int getStim() {
        return stim;
    }

    public void setStim(int stim) {
        this.stim = stim;
    }

    public void setNumberOfSpikes(Long numberOfSpikes) {
        this.numberOfSpikes = numberOfSpikes;
    }

    public Long getSpikeFrequency() {
        return spikeFrequency;
    }

    public void setSpikeFrequency(Long spikeFrequency) {
        this.spikeFrequency = spikeFrequency;
    }

    public String getDigitalTask() {
        return digitalTask;
    }

    public void setDigitalTask(String digitalTask) {
        this.digitalTask = digitalTask;
    }

    public String getDigitalOutputChannel() {
        return digitalOutputChannel;
    }

    public void setDigitalOutputChannel(String digitalOutputChannel) {
        this.digitalOutputChannel = digitalOutputChannel;
    }
}
