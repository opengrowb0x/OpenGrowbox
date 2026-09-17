package uy.growbox.gpio.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ProcessUtils {

    public static String getProcessErrorOutput(Process pr) throws IOException {
        return getProcessOutput(pr, true);
    }

    public static String getProcessOutput(Process pr, boolean error) throws IOException {
        BufferedReader in =
                new BufferedReader(new InputStreamReader(!error ? pr.getInputStream() : pr.getErrorStream()));
        StringBuilder result = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();
        return result.toString();
    }

    public static boolean isPiModeArgumentPresent(String[] args) {
        boolean piMode = false;
        try {
            if (args != null && args.length > 0) {
                final String firstArgument = args[0];
                if ("pi".equalsIgnoreCase(firstArgument)) {
                    piMode = true;
                }
            }
        } catch (Exception e) {
            Logger.getLogger("ProcessUtils").warning("error parsing arguments: " + e.getMessage());
        }
        return piMode;
    }
}
