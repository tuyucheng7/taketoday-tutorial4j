package cn.tuyucheng.taketoday.picocli.git.commands.programmative;

import cn.tuyucheng.taketoday.picocli.git.commands.subcommands.GitAddCommand;
import cn.tuyucheng.taketoday.picocli.git.commands.subcommands.GitCommitCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.RunLast;

@Command(name = "git")
@Component
public class GitCommand implements Runnable {
	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new GitCommand());
		commandLine.addSubcommand("add", new GitAddCommand());
		commandLine.addSubcommand("commit", new GitCommitCommand());

		commandLine.parseWithHandler(new RunLast(), args);
	}

	@Override
	public void run() {
		System.out.println("The popular git command");
	}
}
