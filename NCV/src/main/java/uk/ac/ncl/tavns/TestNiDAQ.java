/**
 * Python repository with examples: https://github.com/ni/nidaqmx-python/tree/master
 */
package uk.ac.ncl.tavns;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TestNiDAQ {
    int numberOfChannels = 4;
    String inputDevice = "Dev2";
    static int numSampsPerChan = 8;
    static double lastValue = 100.0;
    static final double factor = 0.90 + 0.2 * Math.random();
    NiDaq daq = new NiDaq();
    int cycles = 100;

    public TestNiDAQ() throws NiDaqException {
        Pointer aiTask = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("values1.csv");
            aiTask = daq.createTask("AITask");
            String physicalChannel = inputDevice + "/ai0:" + numberOfChannels;
            System.out.println("Physical channel: " + physicalChannel);
            daq.createAIVoltageChannel(aiTask, physicalChannel, "abc",
                    Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                    null);
            daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                    numSampsPerChan);
            daq.startTask(aiTask);
            int inputBufferSize = numSampsPerChan  * numberOfChannels;
            System.out.println("Buffer size: " + inputBufferSize);
            ArrayList<Double>  arr_buffer = new ArrayList<>();
            for (int i = 0; i < cycles; i++) {
                double[] buffer = new double[inputBufferSize];
                int read = 0;

                DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
                IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
                daq.readAnalogF64(aiTask, -1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer,
                        inputBufferSize , samplesPerChannelRead);
                System.out.print(Arrays.toString(buffer));
                for (int b = 0; b < buffer.length; b++) {
                    fileWriter.write(buffer[b] + "\n");

                }
            }

            fileWriter.close();
            System.out.println("Close file");
//            System.out.println("Buffer: " + Arrays.toString(buffer));
            daq.stopTask(aiTask);
            daq.clearTask(aiTask);
        } catch (NiDaqException e) {
            e.printStackTrace();
            try {
                System.out.println("Close task");
                daq.stopTask(aiTask);
                daq.clearTask(aiTask);
                System.out.println("Close file");
                fileWriter.close();
            } catch (IOException f) {
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        try {
            TestNiDAQ testNiDAQ = new TestNiDAQ();
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }

}
