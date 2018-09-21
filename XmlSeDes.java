package study.inno.sedes;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class XmlSeDes {
    private Map<Integer, Object> parseQueue = new TreeMap<>();
    private TreeSet<Integer> parsed = new TreeSet<>();
    private int rootObjHash;

    public XmlSeDes() {
    }


    private Object poll() {
        Iterator<Integer> iObj = parseQueue.keySet().iterator();

        if (iObj.hasNext()) {
            Integer hash = iObj.next();
            Object obj = parseQueue.get(hash);
            parseQueue.remove(hash);
            parsed.add(hash);
            return obj;
        } else return null;
    }

    private void add(Object obj) {
        if (obj == null) return;

        int hash = obj.hashCode();
        if (!parsed.contains(hash) && !parseQueue.containsKey(hash)) parseQueue.put(hash, obj);
    }

    public void serialize(Object obj, String fileName) throws IOException {
        String result = "";
        parseQueue.clear();

        add(obj);

        while (parseQueue.size() > 0) {
            try {
                result += objectToXmlString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
//
        result = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n<objects root=\"" + (obj == null ? "null" : obj.hashCode()) + "\">" + result + "\r\n</objects>";

        Path path = Paths.get(fileName);
        if (Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            Files.delete(path);
        }

        try (BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(fileName))) {
            byte[] byteArray = result.getBytes("utf-8");
            buff.write(byteArray, 0, byteArray.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    public Object deSerialize(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) throw new Exception("File [" + fileName + "] not exists.");
        else if (!Files.isRegularFile(path)) throw new Exception("File [" + fileName + "] is not regular file.");
        else {

            ObjectMap objectMap = new ObjectMap(null);
            objectMap.loadFromXmlString(new String(Files.readAllBytes(path), Charset.forName("UTF-8")));

            return objectMap.get();
//            for (ObjectMap object : objectMap.getChildrenByType("object")) {
//                System.out.println(object.getTagName() + " " + object.getValue());
//            }
//
//            int x = 5 + 6;
//            ++x;
//
//            return null;
//            return parseQueue.containsKey(rootObjHash) ? parseQueue.get(rootObjHash) : null;
        }
    }


    private String objectToXmlString() throws IllegalAccessException {
        Object obj = poll();

        String result = ("\r\n  <object hash=\"" + (obj == null ? "null" : obj.hashCode()) + "\" class=\"" + (obj == null ? "null" : obj.getClass().getName()) + "\"");

        return result + (obj == null ? "/>" : (">" + objectToXmlString(obj, obj.getClass()) + "\r\n   </object>"));
    }

    private String objectToXmlString(Object obj, Class clazz) throws IllegalAccessException {
        String result = "";

        switch (clazz.getName()) {
            case "[Ljava.lang.Long;":
            case "[Ljava.lang.Integer;":
            case "[Ljava.lang.Short;":
            case "[Ljava.lang.Byte;":
            case "[Ljava.lang.Double;":
            case "[Ljava.lang.Float;":
            case "[Ljava.lang.Boolean;":
            case "[Ljava.lang.Character;":
            case "[Ljava.lang.String;":
                for (Object value : (Object[]) obj) {
                    result += "\r\n         <field type=\"" + value.getClass().getName() + "\"" + fieldValueStr(value);
                }
                return result;

            case "[J":
                for (long value : ((long[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[I":
                for (int value : ((int[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[S":
                for (short value : ((short[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[B":
                for (byte value : ((byte[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[D":
                for (double value : ((double[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[F":
                for (float value : ((float[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[Z":
                for (boolean value : ((boolean[]) obj)) {
                    result += "\r\n         <field value=\"" + value + "\"/>";
                }
                return result;
            case "[C":
                for (char value : ((char[]) obj)) {
                    result += "\r\n         <field value=\"" + ((short) value) + "\"/>";
                }
                return result;

            //ссылочные типы
            default:
                //если объект является массивом
                if (clazz.getName().matches("\\[L.*;")) {
                    for (Object value : (Object[]) obj) {
                        result += "\r\n         <field " + fieldValueStr(value);
                    }
                } else {
                    for (; clazz.getName() != "java.lang.Object"; clazz = clazz.getSuperclass()) {
                        result += "\r\n     <fields className=\"" + clazz.getName() + "\">";
                        result += fieldIterator(obj, clazz.getDeclaredFields());
                        result += "\r\n     </fields>";
                    }

                }
                return result;
        }
    }

    private String fieldValueStr(Object value) {
        if (value == null) return "hash=\"null\"/>";

        String className = value.getClass().getName();
        String result = " class=\"" + className + "\"";

        switch (className) {
            case "java.lang.Long":
            case "java.lang.Integer":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "long":
            case "int":
            case "short":
            case "byte":
                result += " value=\"" + value + "\"/>";
                break;

            case "java.lang.Double":
            case "java.lang.Float":
            case "double":
            case "float":
                //неплохо бы с точкой разобраться и отдавать значение в hex
                result += " value=\"" + value + "\"/>";
                break;

            case "boolean":
            case "java.lang.Boolean":
                result += " value=\"" + value + "\"/>";
                break;

            case "java.lang.Character":
            case "char":
                result += " value=\"" + ((short) ((char) value)) + "\"/>";
                break;

            case "java.lang.String":
                result += ">" + ObjectMap.escapeXmlStr((String) value) + "</field>";
                break;

            //ссылочные типы
            default:
                result += " hash=\"" + (value == null ? "null" : value.hashCode()) + "\"/>";

                //добавить в список на обработку
                if (value != null) add(value);
                break;
        }
        return result;
    }

    private String fieldIterator(Object obj, Field[] fields) throws IllegalAccessException {
        String result = "";
        String type;
        Object value;

        for (Field field : fields) {
            field.setAccessible(true);
            type = field.getType().getName();
            result += "\r\n         <field name=\"" + field.getName() + "\" type=\"" + type + "\"" + fieldValueStr(field.get(obj));

        }
        return result;
    }
}
