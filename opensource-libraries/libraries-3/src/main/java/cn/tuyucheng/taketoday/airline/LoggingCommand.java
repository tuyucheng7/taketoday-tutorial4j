package cn.tuyucheng.taketoday.airline;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

import javax.inject.Inject;

@Command(name = "setup-log", description = "Setup our log")
public class LoggingCommand implements Runnable {

	@Inject
	private HelpOption<LoggingCommand> help;

	@Option(name = {"-v", "--verbose"}, description = "Set log verbosity on/off")
	private boolean verbose = false;

	@Override
	public void run() {
		// skipping store user choice
		if (!help.showHelpIfRequested())
			System.out.println("Verbosity: " + verbose);
	}
}
