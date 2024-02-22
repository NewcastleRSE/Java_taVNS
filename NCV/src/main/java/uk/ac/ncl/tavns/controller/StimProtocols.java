package uk.ac.ncl.tavns.controller;

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
}
