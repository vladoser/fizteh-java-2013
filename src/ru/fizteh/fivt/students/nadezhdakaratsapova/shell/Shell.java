package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.*;

public class Shell {

    private CommandsController controller = new CommandsController();
    private CurrentDirectory currentDirectory = new CurrentDirectory();

    public void interactiveVersion() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(currentDirectory.getCurDir().getCanonicalPath() +"$ ");
            String inputString = scanner.nextLine();
            batchVersion(inputString);
        }
    }

    public void batchVersion(String inputString) throws IOException {

        String[] commands = inputString.split(";");
        for (String command: commands) {
            String[] splittedCommand = command.trim().split("\\s+");
            controller.runCommand(currentDirectory, splittedCommand);

        }


    }
}
