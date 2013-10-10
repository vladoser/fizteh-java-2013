package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.util.Arrays;

public class DirCommand implements Command {
    public String getName() {
        return "dir";
    }

    public void execute(CurrentDirectory currentDirectory, String[] args) {
        if (currentDirectory.getCurDir().isDirectory()) {
            String[] fileList= currentDirectory.getCurDir().list();
            Arrays.sort(fileList);
            if (fileList.length > 0) {
                int i = 0;
                while (fileList[i].charAt(0) == '.') {
                    ++i;
                }
                int length = fileList.length;

                for (int j = i; j < length; ++j) {
                    System.out.println(fileList[j]);
                }
            }

        }
    }
    public int getArgsCount() {
        return 0;
    }
}
