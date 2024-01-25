package tavns;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class MakeData implements Runnable {
    static double lastValue = 100.0;
    static final double factor = 0.90 + 0.2 * Math.random();
    private static TimeSeries[] channels;
    private static NiDaq daq = new NiDaq();
    private static int numSampsPerChan = 8;

    public MakeData(TimeSeries[] channels, int numSampsPerChan) {
        MakeData.channels = channels;
        MakeData.numSampsPerChan = numSampsPerChan;
    }

    public static double[] readAnalogueIn(int inputBufferSize) throws NiDaqException {
        Pointer aiTask = null;
        try {
            aiTask = daq.createTask("AITask");
            daq.createAIVoltageChannel(aiTask, "Dev1/ai0:1", "",
                    Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                    null);
            daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                    8);
            daq.startTask(aiTask);
            int read =0;
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
        while (true) {
            try {
                int inputBufferSize = channels.length * numSampsPerChan;
                double[] data = readAnalogueIn(inputBufferSize);
                if (data != null) {
//                    for (int i = 0; i < 2; i++) {
//                        System.out.println("AI" + i + " = " + (data[i] < 0.01 ? "" : data[i]));
//                        series[i].add(new Millisecond(), data[i]);
//                    }
                    for (int i = 0; i < channels.length; i++) {
                        int start = i * numSampsPerChan;
                        int end = start + numSampsPerChan;
                        System.out.println("Start: " + start + ", End: " + end);
                        channels[i].add(new Millisecond(), mean(Arrays.copyOfRange(data, start, end)));
                    }
                }
            } catch (NiDaqException e) {
                System.out.println("ERROR:\n" + e.getMessage());
            }
        }
    }

    private double mean(double[] data) {
        return Arrays.stream(data).average().orElse(Double.NaN);

    }
}
