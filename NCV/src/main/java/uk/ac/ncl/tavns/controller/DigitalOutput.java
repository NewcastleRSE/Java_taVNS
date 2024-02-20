package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

public class DigitalOutput  {
    private static NiDaq daq = new NiDaq();
    private static String outputDevice;

    public DigitalOutput(String outputDevice) {
        System.out.println("Init time series");
        try {
            writeDigitalOut(new byte[]{1, 2, 3, 4}, outputDevice);
        } catch (NiDaqException e) {
            System.out.println("Output device " + outputDevice + " failed");
//            throw new RuntimeException(e);
        }
    //    System.out.println("Initialise Digital Output");
      //  this.outputDevice = outputDevice;
    }


    /**
     * Write the specified data to the digital out lines.
     * @param data
     * @throws NiDaqException
     */
    public static void writeDigitalOut(byte[] data, String port) throws NiDaqException {
        Pointer doTask = daq.createTask("DOTask");
        daq.createDOChan(doTask, outputDevice + port, "", Nicaiu.DAQmx_Val_ChanForAllLines);
        daq.startTask(doTask);
        daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
        daq.stopTask(doTask);
        daq.clearTask(doTask);
    }

}
