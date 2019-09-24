import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class Main {

    public static void main(String[] args) throws UnknownHostException {

        Options options = new Options();

        options.addOption(OptionBuilder.isRequired().hasArgs(1).withDescription("target address or domain name").create('t'));
        options.addOption(OptionBuilder.isRequired().hasArgs(1).withDescription("target port").create('p'));
        options.addOption(OptionBuilder.isRequired().hasArgs(1).withDescription("file name").create('f'));

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

        File file = new File(commandLine.getOptionValue('f'));
        if(!file.exists() || file.isDirectory()){
            System.out.println("No such file");
            return;
        }

        InetSocketAddress targetAddress = new InetSocketAddress(
                InetAddress.getByName(commandLine.getOptionValue('t')),
                Integer.parseInt(commandLine.getOptionValue('p'))
        );

        FileExchangeClient fileExchangeClient = new FileExchangeClient();
        fileExchangeClient.send(targetAddress, file);
    }
}
