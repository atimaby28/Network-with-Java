package json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

public class CreateJson {
    public static void main(String[] args) throws IOException {
        JSONObject root = new JSONObject();

        root.put("id","atimaby28");
        root.put("name","홍길동");
        root.put("age",31);
        root.put("student",false);

        JSONObject tel = new JSONObject();
        tel.put("home","02-123-1234");
        tel.put("mobile","010-123-1234");
        root.put("tel",tel);

        JSONArray skill = new JSONArray();
        skill.put("java");
        skill.put("python");
        skill.put("c++");
        root.put("skill",skill);

        String json = root.toString();

        System.out.println(json);

        Writer writer = new FileWriter("./member.json", Charset.forName("UTF-8"));
        
        writer.write(json);
        writer.flush();
        writer.close();
    }
}