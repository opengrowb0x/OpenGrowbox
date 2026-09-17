package utilities;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static uy.growbox.gpio.utilities.CommandExecutionUtilities.processDetails;

public class UtilsTest {

    @Test
    public void verifyProcessGrep() {
        final Optional<ProcessHandle> foundProcessSet = ProcessHandle
                .allProcesses()
                .filter(processHandle -> processDetails(processHandle).contains("java")).findFirst();
        foundProcessSet.ifPresent(processHandle -> System.out.println("process found " + processDetails(processHandle)));
        Assert.assertTrue(foundProcessSet.isPresent());
    }
}
