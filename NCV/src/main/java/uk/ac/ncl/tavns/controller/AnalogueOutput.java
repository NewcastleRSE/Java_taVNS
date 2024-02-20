package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import java.nio.DoubleBuffer;

public class AnalogueOutput {
    private static NiDaq daq = new NiDaq();

    public AnalogueOutput(String inputDevice) throws NiDaqException, InterruptedException {
        Pointer doTask = daq.createTask("AOTask");
        int minVal = 0;
        int maxVal = 5;
        int sleep = 500;
        int stims = 20;
        String physicalChannel = inputDevice + "/ao0";
        System.out.println(physicalChannel);
        daq.resetDevice(inputDevice);
        daq.createAOVoltageChannel(doTask, physicalChannel, "", minVal, maxVal,
                Nicaiu.DAQmx_Val_Volts, null);
        for (int i = 0; i < stims; i++) {
            System.out.println("Pulse " + i);
            daq.startTask(doTask);
            double[] fbb = {0.1D, 0D};
            DoubleBuffer data = DoubleBuffer.wrap(fbb);
            System.out.println(data.get(0));
            daq.writeAnalogF64(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel,
                    data, 0);
            Thread.sleep(sleep);
            daq.stopTask(doTask);
            double[] fbb2 = {0D, 0D};
            DoubleBuffer data2 = DoubleBuffer.wrap(fbb2);
            System.out.println(data2.get(0));
            daq.writeAnalogF64(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel,
                    data2, 0);        // daq.write
            daq.stopTask(doTask);
            Thread.sleep(sleep);
        }
        daq.clearTask(doTask);
        System.out.print("Output stopped and cleared.");
    }

    public static void main(String[] args) throws NiDaqException, InterruptedException {
        AnalogueOutput analogueOutput = new AnalogueOutput("Dev2");
    }

}
