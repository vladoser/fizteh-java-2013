package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public class CommandPwd implements Command {
    public String getName() {
        return "pwd";
    }

    public void run(Shell shell, String[] empty) throws IOException {
        if (empty.length > 0) {
            throw new IOException("pwd: Too many arguments.");
        }
        System.out.println(shell.getState());
    }
}