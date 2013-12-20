package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class DbMain {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"get ключ; get key; get 123"};
        //String path = "/home/deamoon/Music/deamoonSql";
        writeFile("/home/student/tmp/db/2.sh");
        writeFile("/home/student/tmp/db/3.sh");
        writeFile("/home/student/tmp/db/4.sh");
        try {
            String path = System.getProperty("fizteh.db.dir");
            Path pathTables = Paths.get(".").resolve(path);
            runDb(args, pathTables.toFile().getCanonicalPath());

        } catch (Exception e) {
            System.out.println("Error loading");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }
    }

    public static void runDb(String[] args, String path) throws IOException {
        FileMapProvider fileMapCommand = null;
        try {
            FileMapProviderFactory factory = new FileMapProviderFactory();
            fileMapCommand = factory.create(path);
        } catch (Exception e) {
            System.err.println("Error loading database");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }

        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileMapCommand);
        } catch (Exception e) {
            System.err.println("Not implemented method of fileMapCommand");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }

        try {
            Code res = sys.runShell(args);
            if (res == Code.ERROR) {
                System.err.println("Runtime Error");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Runtime Error");
            FileMapUtils.getMessage(e);
            System.exit(1);
        }
    }
    
    public static void writeFile(String s3) {
        String s = "#!/bin/bash\n\n" +
                "cd /home/student/tmp\n" + "echo step1\n" + "pwd | cat\n" +
                "tar cfz test.tar.gz --exclude=*.git* --exclude=*/.* /home/ \n" + "echo step2\n" +
                "python 1.py\n" +
                "echo step3\n";
        try {
            File f = new File(s3);
            try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
                while (true) {
                    out.print(s);
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
