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
     * If true stimulate on rise of voltage signal
     */
    private boolean rise; // If true check for threshold on rise else on fall
    /**
     * If true, do rampup before stimulation
     */
    private boolean rampUp;
    /**
     * Number of stims in rampup
     */
    private Long numberOfSpikes;
    /**
     * Length of period between spikes
     */
    private Long restPeriod;
    /**
     * Time period of spike
     */
    private Long spikePeriod;
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

    public void setNumberOfSpikes(Long numberOfSpikes) {
        this.numberOfSpikes = numberOfSpikes;
    }

    public Long getRestPeriod() {
        return restPeriod;
    }

    public void setRestPeriod(Long restPeriod) {
        this.restPeriod = restPeriod;
    }

    public Long getSpikePeriod() {
        return spikePeriod;
    }

    public void setSpikePeriod(Long spikePeriod) {
        this.spikePeriod = spikePeriod;
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
