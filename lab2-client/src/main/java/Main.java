import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {

        Options options = new Options();
        options.addOption(new Option("t", "target", false, "target "));
        options.addOption(new Option("p", "port", false, "port "));
        options.addOption(new Option("f", "file", false, "file "));

        CommandLineParser cmdLinePosixParser = new PosixParser();// создаем Posix парсер
        CommandLine commandLine;
        try {
            commandLine = cmdLinePosixParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar", options);
            return;
        }

        InetAddress targetAddress = InetAddress.getByName(commandLine.getOptionValue('p'));

    }
}
