package uy.growbox.gpio.utilities;

import uy.growbox.gpio.exceptions.ExecutionException;
import gpio.GPIOApi;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class CommandExecutionUtilities {
    private static final Logger log = Logger.getLogger(CommandExecutionUtilities.class.getSimpleName());

    @Deprecated
    public static boolean isProcessRunning2(GPIOApi api, String processName) throws IOException, InterruptedException {
            String[] command = {"/usr/bin/pgrep", "-f", processName};
            final String result;
            try {
                result = api.executeCommand(command, false);
                if (result != null && result.length() > 2) {
                    log.finer("process " +  processName + " exist: " + result);
                    return true;
                }
                return false;
            } catch (ExecutionException e) {
                return false;
            }
    }

    public static void processNotFound() {
        log.finer("process not found for name");
    }

    /**
     * foundProcessSet.ifPresentOrElse(
     *                 processHandle -> log.finer("running process found " + processDetails(processHandle)),
     *                 CommandExecutionUtilities::processNotFound);
     * @param processName
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean isProcessRunning(String processName) throws IOException, InterruptedException {
        final Optional<ProcessHandle> foundProcessSet = ProcessHandle
                .allProcesses()
                .filter(processHandle -> processDetails(processHandle).contains(processName)).findFirst();
        return foundProcessSet.isPresent();
    }

    public static String processDetails(ProcessHandle process) {
        return String.format("%8d %8s %10s %26s %-40s",
                process.pid(),
                text(process.parent().map(ProcessHandle::pid)),
                text(process.info().user()),
                text(process.info().startInstant()),
                text(process.info().commandLine()));
    }

    public static String text(Optional<?> optional) {
        return optional.map(Object::toString).orElse("-");
    }

}
