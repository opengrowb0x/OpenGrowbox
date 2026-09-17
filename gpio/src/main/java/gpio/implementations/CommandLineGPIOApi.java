package gpio.implementations;

import uy.growbox.gpio.exceptions.ExecutionException;
import gpio.GPIOApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static uy.growbox.gpio.utilities.ProcessUtils.getProcessErrorOutput;
import static uy.growbox.gpio.utilities.ProcessUtils.getProcessOutput;

@Deprecated
public class CommandLineGPIOApi implements GPIOApi {

    public static final String LOCAL_GPIO_BIN_PI_3 = "/usr/bin/gpio";
    public static final String LOCAL_GPIO_BIN_PI_2 = "/usr/local/bin/gpio";

    private static final Logger log = Logger.getLogger(CommandLineGPIOApi.class.getSimpleName());

    private final Collection<Process> startedProcesses = new LinkedList<>();

    public CommandLineGPIOApi() {
    }

    @Override
    public Boolean readOutputPort(int sensorPort) {
        String[] cmd = new String[]{getLocalGPIOBin(), "-g", "read", String.valueOf(sensorPort)};
        Boolean result = null;
        try {
            String output = executeCommand(cmd);
            log.finer("Reading GPIO pin " + String.valueOf(sensorPort) + " value: " + output);
            result = output.equalsIgnoreCase("0");
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return result;
    }


    @Override
    public int readInputPort(int sensorPort) {
        try {
            String[] cmd = new String[]{getLocalGPIOBin(), "-g", "read", String.valueOf(sensorPort)};
            String output = executeCommand(cmd);
            log.info("reading GPIO pin " + sensorPort + " value: " + output);
            return Integer.parseInt(output);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        //FIXME?
        return -1;
    }

    @Override
    public void initializeOutputPortMode(int port) {
        initializePortMode(port, "out");
    }

    private void initializePortMode(int port, String mode) {
        final String[] setPinAsOutCmd = new String[]{getLocalGPIOBin(), "-g", "mode", String.valueOf(port), mode};
        try {
            executeCommand(setPinAsOutCmd);
            log.fine("setting GPIO pin " + port + " mode to " + mode);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    @Override
    public void initializeInputPortMode(int port) {
        initializePortMode(port, "in");
    }

    @Override
    public void writeOutputPort(int port, boolean state) {
        String gpioPort = String.valueOf(port);
        String[] cmd = getOutputWriteCommand(state, gpioPort);
        try {
            log.fine("writing GPIO pin " + port + " to " + state);
            executeCommand(cmd);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private static String[] getOutputWriteCommand(boolean state, String gpioPort) {
        return new String[]{getLocalGPIOBin(), "-g", "write", gpioPort, state ? "0" : "1"};
    }

    @Override
    public final void terminate() {
        log.info("terminating pending processes...");
        startedProcesses.forEach(process -> {
            log.severe("terminating process " + process.toString());
            process.destroyForcibly();
        });
    }

    public final String executeCommand(String[] cmd) throws IOException, InterruptedException, ExecutionException {
        return executeCommand(cmd, true);
    }

    /**
     * This method is blocking
     * @param cmd command with its argument list
     * @return
     */
    public final String executeCommand(String[] cmd, boolean checkResult) throws IOException, InterruptedException, ExecutionException {
        String command = getCommandArray(cmd);
        log.finer("executing command " + command);
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        Process pr = builder.start();
        startedProcesses.add(pr);
        String result = getProcessOutput(pr, false);
        String errorResult = getProcessErrorOutput(pr);
        pr.waitFor();
        startedProcesses.remove(pr);
        log.finer("command execution result " + pr.exitValue() + " " + result);
        if (pr.exitValue() != 0) {
            if (checkResult) {
                final String commandString = Arrays.stream(cmd).collect(Collectors.joining(" "));
                log.info("command: " + cmd[0] + " returned non zero: " + pr.exitValue() + " cmd: " + commandString + " error: " + errorResult + " response: " + result);
                throw new ExecutionException(errorResult);
            }
        }
        return result;
    }

    public static String getCommandArray(String[] commandArray) {
        return String.join(" ", commandArray);
    }


    private static String getLocalGPIOBin() {
        if (Files.exists(Paths.get(LOCAL_GPIO_BIN_PI_2))) {
            return LOCAL_GPIO_BIN_PI_2;
        }
        else return LOCAL_GPIO_BIN_PI_3;
    }


}
