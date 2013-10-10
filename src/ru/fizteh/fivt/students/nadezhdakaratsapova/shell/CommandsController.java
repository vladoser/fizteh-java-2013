package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandsController {
    private Map<String, Command> commandsStorage = new HashMap<String, Command>();

    public CommandsController(){
        Command cd = new CdCommand();
        Command cp = new CpCommand();
        Command dir = new DirCommand();
        Command exit = new ExitCommand();
        Command mkdir = new MkdirCommand();
        Command mv = new MvCommand();
        Command pwd = new PwdCommand();
        Command rm = new RmCommand();
        commandsStorage.put(cd.getName(), cd);
        commandsStorage.put(cp.getName(), cp);
        commandsStorage.put(dir.getName(), dir);
        commandsStorage.put(exit.getName(), exit);
        commandsStorage.put(mkdir.getName(), mkdir);
        commandsStorage.put(mv.getName(), mv);
        commandsStorage.put(pwd.getName(), pwd);
        commandsStorage.put(rm.getName(), rm);
    }

     public void runCommand(CurrentDirectory currentDirectory, String[] command) throws IOException {
         Command cmd = commandsStorage.get(command[0]);
         if (cmd == null) {
             throw new IOException(command[0] + ": unknown command");
         } else {
             if ((command.length - 1) != cmd.getArgsCount()) {
                 throw new IOException(cmd.getName() + ": wrong number of arguments. It should be " + cmd.getArgsCount());
             } else {
                cmd.execute(currentDirectory, command);
             }
         }
     }


}
