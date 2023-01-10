package cn.tuyucheng.taketoday.picocli.git.commands.declarative;

import cn.tuyucheng.taketoday.picocli.git.commands.subcommands.GitAddCommand;
import cn.tuyucheng.taketoday.picocli.git.commands.subcommands.GitCommitCommand;
import cn.tuyucheng.taketoday.picocli.git.commands.subcommands.GitConfigCommand;
import cn.tuyucheng.taketoday.picocli.git.model.ConfigElement;
import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.RunLast;

@Command(
	name = "git",
	subcommands = {
		GitAddCommand.class,
		GitCommitCommand.class,
		GitConfigCommand.class
	}
)
public class GitCommand implements Runnable {
	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new GitCommand());
		commandLine.registerConverter(ConfigElement.class, ConfigElement::from);

		commandLine.parseWithHandler(new RunLast(), args);
	}

	@Override
	public void run() {
		System.out.println("The popular git command");
	}
}
