package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {

    MultiFileHashMapProvider curState;

    public GetCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "get";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            String value = curState.curDataBaseStorage.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } else {
            System.out.println("no table");
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
