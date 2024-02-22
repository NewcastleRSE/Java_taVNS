package uk.ac.ncl.tavns.controller;

public class StimProtocols {

    public StimProtocols() {
    }

    public static void digitalOutSetState(String device, byte outputState, String channel) {
        byte[] data = {outputState, outputState};
        DigitalWrite digitalOutput = new DigitalWrite(device, data, channel);
        digitalOutput.start();

    }
}
