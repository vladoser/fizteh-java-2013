package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * CopyCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class CopyCommand extends Command {
	CopyCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.CP;
	}

	@Override
	public void execute() throws Exception {
		receiver.copyCommand(args[0], args[1]);
	}
}
