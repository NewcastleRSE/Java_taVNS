package uk.ac.ncl.tavns.controller;

import kirkwood.nidaq.access.NiDaqException;

/**
 * Class containing stimulation protocols
 */
public class StimProtocols {

    public StimProtocols() {
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
    public static void testRamp(String outputDevice, String outputChannel) {
        Thread thread = new Thread(new AnalogueRamp(outputDevice, outputChannel,
                "AOTask", 10, 200));
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
}
