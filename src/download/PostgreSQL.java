package download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostgreSQL {

    public static final Charset DOCUMENT_CHARSET = StandardCharsets.UTF_8;

    private String domain;
    private String root;

    public PostgreSQL(String domain, String to) {
        this.domain = domain;
        this.root = to + '\\';
        File root = new File(this.root);
        System.out.println("创建目录" + root + (root.mkdir() ? "成功" : "失败"));
    }

    public String downloadAndNextPage(String path)
            throws IOException {
        URL url = new URL(domain + path);
        System.out.println("下载：" + path);
        String string, next = null;
        StringBuilder builder = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), DOCUMENT_CHARSET));
        while ((string = br.readLine()) != null) builder.append(string);
        br.close();

        string = builder.toString();
        int index = string.indexOf("下一页");
        if (index != -1) {
            index = string.lastIndexOf("href", index) + 6;
            next = string.substring(index, string.indexOf('"', index));
        }

        File page = new File(root + path);
        if (page.createNewFile()) System.out.println("写入：" + path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(page), DOCUMENT_CHARSET));
        bw.write(string);
        bw.close();

        return next;
    }

    public static void main(String[] args) throws IOException {
        PostgreSQL document = new PostgreSQL("http://www.postgres.cn/docs/10/", "postgres");
        String page = "index.html";
        while (page != null) page = document.downloadAndNextPage(page);
    }
}
