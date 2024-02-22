package uk.ac.ncl.tavns.controller;

import kirkwood.nidaq.access.NiDaqException;

public class StimProtocols {

    public StimProtocols() {
    }

    public static void digitalOutSetState(String device, byte outputState, String channel) {
        byte[] data = {outputState, outputState};
        DigitalWrite digitalOutput = new DigitalWrite(device, data, channel);
        digitalOutput.start();

    }

    public static void testRamp(String outputDevice, String outputChannel) {
        Thread thread = new Thread(new AnalogueRamp(outputDevice, outputChannel,
                "AOTask", 10, 200));
        thread.start();
    }

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
