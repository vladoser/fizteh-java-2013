package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.musin.shell.Shell;

import java.util.ArrayList;

public class ShellDatabaseHandler {
    FileMapProvider database;
    MultiFileMap current;

    public ShellDatabaseHandler(String location) {
        FileMapProviderFactory factory = new FileMapProviderFactory();
        database = factory.create(location);
        current = null;
    }

    void printException(Throwable e) {
        if (e.getCause() != null) {
            printException(e.getCause());
        }
        for (Throwable suppressed : e.getSuppressed()) {
            printException(suppressed);
        }
        System.err.println(e.getMessage());
    }

    ArrayList<String> parseArguments(int argCount, String argString) {
        ArrayList<String> args = new ArrayList<String>();
        int argsRead = 0;
        String last = "";
        int start = 0;
        for (int i = 0; i < argString.length(); i++) {
            if (Character.isWhitespace(argString.charAt(i))) {
                if (start != i) {
                    args.add(argString.substring(start, i));
                    argsRead++;
                }
                start = i + 1;
                if (argsRead == argCount - 1) {
                    last = argString.substring(start, argString.length());
                    break;
                }
            }
        }
        last = last.trim();
        if (!last.equals("")) {
            args.add(last);
        }
        return args;
    }

    private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("create", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("create: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("create: Too few arguments");
                        return -1;
                    }
                    try {
                        Table table = database.createTable(args.get(0));
                        if (table == null) {
                            System.out.printf("%s exists\n", args.get(0));
                            return 0;
                        }
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    System.out.println("created");
                    return 0;
                }
            }),
            new Shell.ShellCommand("drop", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("drop: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("drop: Too few arguments");
                        return -1;
                    }
                    try {
                        database.removeTable(args.get(0));
                    } catch (IllegalStateException e) {
                        System.out.printf("%s not exists\n", args.get(0));
                        return 0;
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    System.out.println("dropped");
                    return 0;
                }
            }),
            new Shell.ShellCommand("use", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("use: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("use: Too few arguments");
                        return -1;
                    }
                    if (current != null && current.uncommittedChanges() != 0) {
                        System.out.printf("%d unsaved changes\n", current.uncommittedChanges());
                        return 0;
                    }
                    try {
                        MultiFileMap newTable = database.getTable(args.get(0));
                        if (newTable != null) {
                            current = newTable;
                        } else {
                            System.out.printf("%s not exists\n", args.get(0));
                            return 0;
                        }
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    System.out.printf("using %s\n", args.get(0));
                    return 0;
                }
            }),
            new Shell.ShellCommand("commit", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("commit: Too many arguments");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.commit());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("rollback", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("rollback: Too many arguments");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.rollback());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("size", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        System.err.println("rollback: Too many arguments");
                        return -1;
                    }
                    try {
                        System.out.printf("%d\n", current.size());
                    } catch (RuntimeException e) {
                        printException(e);
                        return -1;
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("put", false, new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    args = parseArguments(2, args.get(0));
                    if (args.size() > 2) {
                        System.err.println("put: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("put: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("no table");
                        return 0;
                    }
                    String value = current.put(args.get(0), args.get(1));
                    if (value == null) {
                        System.out.println("new");
                    } else {
                        System.out.printf("overwrite\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("get", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("get: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("get: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("no table");
                        return 0;
                    }
                    String value = current.get(args.get(0));
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.printf("found\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("remove", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("remove: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("remove: Too few arguments");
                        return -1;
                    }
                    if (current == null) {
                        System.out.println("no table");
                        return 0;
                    }
                    if (current.remove(args.get(0)) != null) {
                        System.out.println("removed");
                    } else {
                        System.out.println("not found");
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("exit", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    shell.stop();
                    if (current != null) {
                        current.commit();
                    }
                    return 0;
                }
            })
    };

    public void integrate(Shell shell) {
        for (int i = 0; i < commands.length; i++) {
            shell.addCommand(commands[i]);
        }
    }
}
