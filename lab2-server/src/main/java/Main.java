import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Slf4j
public class Main {

    public static void main(String[] args) {
        FileExchangeServer server = new FileExchangeServer();
        Options options = new Options();
        options.addOption(OptionBuilder.isRequired().hasArgs(1).withDescription("target port").create('p'));

        CommandLineParser cmdLinePosixParser = new BasicParser();
        CommandLine commandLine;
        try {
            log.info("parsing arguments..");
            commandLine = cmdLinePosixParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar", options);
            return;
        }

        FileExchangeServer fileExchangeServer = new FileExchangeServer();
        fileExchangeServer.run(Integer.parseInt(commandLine.getOptionValue('p')));
    }

}
