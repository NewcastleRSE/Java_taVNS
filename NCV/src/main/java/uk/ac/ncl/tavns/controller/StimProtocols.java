package uk.ac.ncl.tavns.controller;

import kirkwood.nidaq.access.NiDaqException;
import org.jfree.data.time.TimeSeriesCollection;
import uk.ac.ncl.tavns.view.ChartsPanel;

/**
 * Class containing stimulation protocols
 * Grounding: https://www.ni.com/en/support/documentation/supplemental/06/grounding-considerations---intermediate-analog-concepts.html
 */
public class StimProtocols {

    private static TimeSeriesCollection[] dataset;
    AnalogueThresholdWrite analogueThresholdWrite = null;
    public StimProtocols(ChartsPanel chartsPanel) {
        this.dataset = chartsPanel.getTimeSeriesCollection();
    }

    /**
     * Set output on specified device and channel to outputState
     * @param device Device to send output to
     * @param outputState state is 0 or 1
     * @param channel channel to set state of
     */
    public static void digitalOutSetState(String device, byte outputState, String channel) {
        byte[] data = {outputState, outputState};
        DigitalWrite digitalOutput = new DigitalWrite(device, data, channel);
        digitalOutput.start();

    }

    /**
     * Generate a ramp up test signal to specified device and channel. For the moment the ramp up is hard coded
     * in AnalogueRamp
     * @param outputDevice
     * @param outputChannel
     */
    public static void testRamp(String outputDevice, String outputChannel, int sleep) {
        Thread thread = new Thread(new AnalogueRamp(outputDevice, outputChannel,
                "AOTask", 10, sleep));
        thread.start();
    }

    /**
     * Send single analogue output signal of specified value to specified device
     * @param outputDevice device to send analogue output to
     * @param stimvalue value in Volt
     */
    public static void singleAnalogStim(String outputDevice, double stimvalue) {
        try {
            Thread thread = new Thread( new AnalogueWrite(outputDevice, "ao1",
                    "AOTask", stimvalue));
            thread.start();
        } catch (NiDaqException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean thresholdStimInit(String outputDevice, String outputChannel, double threshold, double stimValue) throws NiDaqException {
        analogueThresholdWrite = new AnalogueThresholdWrite(outputDevice, outputChannel,
                "ThresholdStim", stimValue, dataset[0], threshold);
        analogueThresholdWrite.setRunning(true);
        Thread thread = new Thread(analogueThresholdWrite);
        thread.start();
        return true;
    }


    public void thresholdStimStart() throws NiDaqException {
        System.out.println("Start stimulation");
        analogueThresholdWrite.setRunning(true);
    }

    public void thresholdStimStop() {
        System.out.println("Stop stimulation");
        analogueThresholdWrite.setRunning(false);
    }

    public AnalogueThresholdWrite getAnalogueThresholdWrite() {
        return analogueThresholdWrite;
    }

    public void setAnalogueThresholdWrite(AnalogueThresholdWrite analogueThresholdWrite) {
        this.analogueThresholdWrite = analogueThresholdWrite;
    }
}
