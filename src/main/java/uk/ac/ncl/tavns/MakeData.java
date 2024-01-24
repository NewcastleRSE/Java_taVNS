package uk.ac.ncl.tavns;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import com.sun.jna.Pointer;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class MakeData implements Runnable {
    static double lastValue = 100.0;
    static final double factor = 0.90 + 0.2 * Math.random();
    private static TimeSeries series;
    private static NiDaq daq = new NiDaq();

    public MakeData(TimeSeries series) {
        MakeData.series = series;
    }

    public static double[] readAnalogueIn(int inputBufferSize) throws NiDaqException {
        Pointer aiTask = null;
        try {
            aiTask = daq.createTask("AITask");
            daq.createAIVoltageChannel(aiTask, "Dev1/ai0:0", "",
                    Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                    null);
            daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                    8);
            daq.startTask(aiTask);
            Integer read = Integer.valueOf(0);
            double[] buffer = new double[inputBufferSize];

            DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
            IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
            daq.readAnalogF64(aiTask, -1, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer,
                    inputBufferSize, samplesPerChannelRead);

            daq.stopTask(aiTask);
            daq.clearTask(aiTask);
            return buffer;

        } catch (NiDaqException e) {
            try {
                daq.stopTask(aiTask);
                daq.clearTask(aiTask);
                return null;
            } catch (NiDaqException e2) {
            }
            throw (e);
        }
    }

    @Override
    public void run() {
        final Millisecond now = new Millisecond();
        System.out.println(" started");
        while (true) {
//            final double factor = 0.90 + 0.2 * Math.random();
//            lastValue = lastValue * factor;
//            System.out.println("Now = " + now.toString());
//            series.add(new Millisecond(), lastValue);
            try {
                double[] data = readAnalogueIn(8);
                if (data != null) {
                    for (int i = 0; i < data.length; i++) {
                        System.out.println("AI" + i + " = " + (data[i] < 0.01 ? "" : data[i]));
                    }
                    Thread.sleep(1000);
                }
            } catch (NiDaqException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
