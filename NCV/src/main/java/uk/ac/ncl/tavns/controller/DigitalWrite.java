package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

public class DigitalWrite extends Thread  {
    private static NiDaq daq = new NiDaq();
    private static Pointer doTask;
    private byte[] data;

    /**
     *
     * @param outputDevice
     * @param data
     * @param port
     * @throws NiDaqException
     */
    public DigitalWrite(String outputDevice, byte[] data, String port) {
        try {
            this.data = data;
            doTask = daq.createTask("DOTask");
            daq.createDOChan(doTask, outputDevice + port, "", Nicaiu.DAQmx_Val_ChanForAllLines);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Write the specified data to the digital out lines.
     * @throws NiDaqException
     */
    public void run() {
        try {
            daq.startTask(doTask);
            daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
            daq.stopTask(doTask);
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }

    }

}
