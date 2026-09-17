package gpio;

import uy.growbox.gpio.exceptions.ExecutionException;

import java.io.IOException;

@Deprecated
public interface GPIOApi extends MinimalGPIOApi {
    //TODO this should be part of another api, and be restricted
    @Deprecated
    String executeCommand(String[] cmd) throws ExecutionException, IOException, InterruptedException;


    @Deprecated
    String executeCommand(String[] cmd, boolean checkResult) throws ExecutionException, IOException, InterruptedException;
}
