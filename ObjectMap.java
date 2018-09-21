package study.inno.sedes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//читалка не понимает комментов
//долго
public class ObjectMap {
    private static Pattern firstTag = Pattern.compile("(?m)^\\s*(<([^>]*?)>)\\s*(.*)"); //"<([^>]*?)>(.*)$");
    private static Pattern whiteP = Pattern.compile("(?m)^(\\S+)\\s+(.*)$");
    private static Pattern eqP = Pattern.compile("(?m)^([^=]+)=(.*)$");
    private static Pattern quoteP = Pattern.compile("(?m)^([^\"]*)\"(.*)$");
    private static Pattern valueP = Pattern.compile("(?m)^\\s*([^<]+)(<.*)$");
    private boolean singleton = false;
    private String xmlSrc = "";
    private String value = "";
    private TreeMap<String, String> attrs = new TreeMap<>();
    private String tagName = "";
    private Object object = null;
    private ObjectMap parent = null;
    private ArrayList<ObjectMap> children = new ArrayList<>();
    private Matcher matcher;

    public ObjectMap(ObjectMap parent) {
        this.parent = parent;
    }

    public static String unescapeXmlStr(String src) {
        return src.replaceAll("&quot;", "\"").
                replaceAll("&apos;", "\'").
                replaceAll("&lt;", "<").
                replaceAll("&gt;", ">").
                replaceAll("&amp;", "&").trim();
    }

    public static String escapeXmlStr(String src) {
        return src.replaceAll("&", "&amp;").
                replaceAll("\"", "&quot;").
                replaceAll("\'", "&apos;").
                replaceAll("<", "&lt;").
                replaceAll(">", "&gt;").
                trim();
    }

    public String getValue() {
        return value;
    }

    public String getTagName() {
        return tagName;
    }

    public String getAttr(String attrName) {
        return attrs.getOrDefault(attrName, "");
    }

    public Object getObject() {
        return object;
    }

    protected void makeObject() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (tagName.equals("object")) {
            //если объект должен быть массивом, то создаём массив нужного типа
            if (isArray()) {
                switch (getClassName()) {
                    case "[Z":
                        object = new boolean[children.size()];
                        break;
                    case "[B":
                        object = new byte[children.size()];
                        break;
                    case "[C":
                        object = new char[children.size()];
                        break;
                    case "[D":
                        object = new double[children.size()];
                        break;
                    case "[F":
                        object = new float[children.size()];
                        break;
                    case "[I":
                        object = new int[children.size()];
                        break;
                    case "[J":
                        object = new long[children.size()];
                        break;
                    case "[S":
                        object = new short[children.size()];
                        break;

                    default:
                        Matcher match = Pattern.compile("\\[L([^;]+);").matcher(getClassName());
                        if (match.find()) {
                            object = Array.newInstance(Class.forName(match.group(1)), children.size());
                        }
                        break;
                }
            }

            //создать обычный объект (не массив)
            else {
                object = Class.forName(getClassName()).newInstance();
            }
            return;
        }
        object = null;
    }

    private String getClassName() {
        return getAttr("class");
    }

    private boolean isArray() {
        String clazz = getClassName();
        return clazz.length() > 0 && clazz.charAt(0) == '[';
    }

    public Object get() throws Exception {
        if (object != null) {
            return object;
        } else if (isObjects()) {
            for (ObjectMap child : children) {
                child.makeObject();
            }

            for (ObjectMap child : children) {
                child.fillObject();
            }

            return findByHash(getAttr("root"));
        }
        return null;
    }

    private void fillObject() throws Exception {
        if (object != null && tagName.equals("object")) {
            if (isArray()) {
                for (int iChild = 0; iChild < children.size(); iChild++) {
                    setItemValue(iChild, children.get(iChild));
                }
            } else {
                for (Class clazz = object.getClass(); clazz.getName() != "java.lang.Object"; clazz = clazz.getSuperclass()) {
                    fieldIterator(clazz);
                }
            }
        }
    }

    private void fieldIterator(Class clazz) throws Exception {
        ObjectMap fieldsNode = getFieldsFor(clazz.getName());
        ObjectMap fieldNode;
        String attrValue;
        boolean isNull;

        if (fieldsNode != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fieldNode = fieldsNode.getFieldByName(field.getName());
                attrValue = fieldNode.getAttr("value");
                isNull = attrValue.equals("null");

                switch (field.getType().getName()) {
                    case "java.lang.Long":
                        field.set(object, isNull ? null : Long.parseLong(attrValue));
                        break;
                    case "java.lang.Integer":
                        field.set(object, isNull ? null : Integer.parseInt(attrValue));
                        break;
                    case "java.lang.Short":
                        field.set(object, isNull ? null : Short.parseShort(attrValue));
                        break;
                    case "java.lang.Byte":
                        field.set(object, isNull ? null : Byte.parseByte(attrValue));
                        break;
                    case "java.lang.Double":
                        field.set(object, isNull ? null : Double.parseDouble(attrValue));
                        break;
                    case "java.lang.Float":
                        field.set(object, isNull ? null : Float.parseFloat(attrValue));
                        break;
                    case "java.lang.Boolean":
                        field.set(object, isNull ? null : Boolean.parseBoolean(attrValue));
                        break;
                    case "java.lang.Character":
                        field.set(object, isNull ? null : (Character) (char) (short) Short.parseShort(attrValue));
                        break;
                    case "long":
                        field.setLong(object, Long.parseLong(attrValue));
                        break;
                    case "int":
                        field.setInt(object, Integer.parseInt(attrValue));
                        break;
                    case "short":
                        field.setShort(object, Short.parseShort(attrValue));
                        break;
                    case "byte":
                        field.setByte(object, Byte.parseByte(attrValue));
                        break;
                    case "float":
                        field.setFloat(object, Float.parseFloat(attrValue));
                        break;
                    case "double":
                        field.setDouble(object, Double.parseDouble(attrValue));
                        break;
                    case "boolean":
                        field.setBoolean(object, Boolean.parseBoolean(attrValue));
                        break;
                    case "char":
                        field.setChar(object, (char) (short) Short.parseShort(attrValue));
                        break;
                    case "java.lang.String":
                        field.set(object, fieldNode.value);
                        break;
                    default:
                        attrValue = fieldNode.getAttr("hash");
                        isNull = attrValue.equals("null");
                        field.set(object, isNull ? null : findByHash(attrValue));
                        break;
                }
            }
        } else {
            throw new Exception("Partial info - not found fields for [" + object.getClass().getName() + " -> " + clazz.getName() + "].");
        }
    }

    private ObjectMap getFieldByName(String name) {
        if (isFields()) {
            for (ObjectMap field : getChildrenByType("field")) {
                if (field.getAttr("name").equals(name)) {
                    return field;
                }
            }
        }

        return null;
    }

    private ObjectMap getFieldsFor(String className) {
        for (ObjectMap fields : this.getChildrenByType("fields")) {
            if (fields.getAttr("className").equals(className)) {
                return fields;
            }
        }

        return null;
    }

    private void setItemValue(int iChild, ObjectMap objectMap) {
        switch (getClassName()) {
            case "[Z":
                ((boolean[]) object)[iChild] = Boolean.valueOf(objectMap.getAttr("value"));
                break;
            case "[B":
                ((byte[]) object)[iChild] = Byte.valueOf(objectMap.getAttr("value"));
                break;
            case "[C":
                ((char[]) object)[iChild] = (char) (short) Short.valueOf(objectMap.getAttr("value"));
                break;
            case "[D":
                ((double[]) object)[iChild] = Double.valueOf(objectMap.getAttr("value"));
                break;
            case "[F":
                ((float[]) object)[iChild] = Float.valueOf(objectMap.getAttr("value"));
                break;
            case "[I":
                ((int[]) object)[iChild] = Integer.valueOf(objectMap.getAttr("value"));
                break;
            case "[J":
                ((long[]) object)[iChild] = Long.valueOf(objectMap.getAttr("value"));
                break;
            case "[S":
                ((short[]) object)[iChild] = Short.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Long;":
                ((Long[]) object)[iChild] = Long.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Integer;":
                ((Integer[]) object)[iChild] = Integer.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Short;":
                ((Short[]) object)[iChild] = Short.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Byte;":
                ((Byte[]) object)[iChild] = Byte.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Double;":
                ((Double[]) object)[iChild] = Double.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Float;":
                ((Float[]) object)[iChild] = Float.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Boolean;":
                ((Boolean[]) object)[iChild] = Boolean.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.Character;":
                ((Character[]) object)[iChild] = (Character) (char) (short) Short.valueOf(objectMap.getAttr("value"));
                break;
            case "[Ljava.lang.String;":
                ((String[]) object)[iChild] = objectMap.value;
                break;
            default:
                ((Object[]) object)[iChild] = findByHash(objectMap.getAttr("hash"));
                break;
        }
    }

    private Object findByHash(String hash) {
        ObjectMap objects = this;

        //ищем вершину
        while (objects != null && !objects.isObjects()) {
            objects = objects.parent;
        }

        if (objects == null) {
            return null;
        } else {
            for (ObjectMap child : objects.children) {
                if (child.getHash().equals(hash)) {
                    return child.object;
                }
            }
        }

        return null;
    }

    private boolean isObjects() {
        return tagName.equals("objects");
    }

    private boolean isObject() {
        return tagName.equals("object");
    }

    private boolean isField() {
        return tagName.equals("field");
    }

    private boolean isFields() {
        return tagName.equals("fields");
    }

    public ObjectMap getParent() {
        return parent;
    }

    public String getHash() {
        return getAttr("hash");
    }

    public int size() {
        return children.size();
    }

    public ObjectMap get(int itemNo) {
        return itemAt(itemNo);
    }

    public ObjectMap itemAt(int at) {
        return children.size() > at ? children.get(at) : null;
    }

    public ArrayList<ObjectMap> getChildrenByType(String type) {
        ArrayList<ObjectMap> result = new ArrayList<>();

        for (ObjectMap child : children) {
            if (child.tagName.equals(type)) result.add(child);
        }

        return result;
    }

    public String loadFromXmlString(String xmlSrc) throws Exception {
        clear();
        this.xmlSrc = xmlSrc.trim();

        if (removeComments() && removeHeaders() && loadTag() && loadValue() && loadTail()) {
            return this.xmlSrc;
        } else {
            throw new Exception("Invalid XML file.");
        }
    }

    private boolean loadTail() throws Exception {
        //синглетону не нужно грузить ни вложенные объекты ни значение
        if (singleton) {
            return true;

            //если же обьект таки составной,
            //то загружаем все подряд объекты
            //в процессе поиска своего хвоста
        } else {
            String tmp;
            while (xmlSrc.length() > 0 && (matcher = firstTag.matcher(xmlSrc)).find()) {
                tmp = matcher.group(1).trim();
                if (tmp.length() > 1 && tmp.charAt(1) == '/' && tmp.substring(2, tmp.length() - 1).trim().equals(tagName)) {
                    xmlSrc = matcher.replaceFirst("$3");
                    return true;
                } else {
                    ObjectMap child = new ObjectMap(this);
                    children.add(child);
                    xmlSrc = child.loadFromXmlString(xmlSrc).trim();
                }
            }
            return true;
        }
    }

    private boolean loadValue() {
//У синглетона не может быть значения
        if (singleton) {
            return true;

            //иначе забираем в значение всё до первого знака "<"
        } else {
            if ((matcher = valueP.matcher(xmlSrc)).find()) {
                value = unescapeXmlStr(matcher.group(1).trim());
                xmlSrc = matcher.replaceFirst("$2");
            }

            return true;
        }
    }

    private boolean loadTag() {
        //находим первый таг
        if ((matcher = firstTag.matcher(xmlSrc)).find()) {
            String tagSrc = matcher.group(2).trim();
            xmlSrc = matcher.replaceFirst("$3").trim();

            if (tagSrc.charAt(tagSrc.length() - 1) == '/') {
                singleton = true;
                tagSrc = tagSrc.substring(0, tagSrc.length() - 1);
            }

            //получаем имя тага
            if ((matcher = whiteP.matcher(tagSrc)).find()) {
                tagName = matcher.group(1).trim();
                tagSrc = matcher.replaceFirst("$2").trim();

                //убеждаемся, что таг не закрывающий
                if (tagName.length() > 0 && tagName.charAt(0) != '/') {
                    String attrName;

                    //парсим строку тага, пока она не закончится
                    while (tagSrc.length() > 0) {

                        //получаем имя параметра
                        if ((matcher = eqP.matcher(tagSrc)).find()) {
                            attrName = matcher.group(1).trim();
                            tagSrc = matcher.replaceFirst("$2").trim();

                            //если значение атрибута начинается на кавычку
                            //то и окончание нужно искать до кавычки
                            if (tagSrc.charAt(0) == '"') {
                                //нашли значение атрибута
                                if ((matcher = quoteP.matcher(tagSrc.substring(1))).find()) {
                                    attrs.put(attrName, unescapeXmlStr(matcher.group(1)));
                                    tagSrc = matcher.replaceFirst("$2").trim();

                                    //не нашли завершающей кавычки значения атрибута
                                } else return false;

                                //если значение начинается не с кавычки
                                //то ищем по пробелу
                            } else if ((matcher = whiteP.matcher(tagSrc)).find()) {
                                attrs.put(attrName, unescapeXmlStr(matcher.group(1)));
                                tagSrc = matcher.replaceFirst("$2").trim();

                                //а если пробела нет, то всё оставшееся и есть значение атрибута
                            } else {
                                attrs.put(attrName, unescapeXmlStr(tagSrc));
                                tagSrc = "";
                            }
                        }
                    }

                    return true;
                }
            }
        }

        //либо тага не нашлось, либо нашелся завершающий
        //либо строка просто пустая
        //либо название тага отсутствует
        return false;
    }

    private void clear() {
        singleton = false;
        xmlSrc = "";
        value = "";
        attrs.clear();
        tagName = "";
        object = null;
        children.clear();
    }

    /**
     * Метод удаляет заголовочные таги
     *
     * @return
     * @throws Exception
     */
    private boolean removeHeaders() {
        Matcher matcher = null;
        String tagName, attrsStr;

        while ((matcher = firstTag.matcher(xmlSrc)).find()) {
            tagName = matcher.group(2);
            if (tagName.charAt(0) == '?') {
                if (tagName.charAt(tagName.length() - 1) == '?') {
                    xmlSrc = matcher.replaceFirst("$3").trim();
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        return true;
    }

    /**
     * Для решаемой задачи реализация метода неактуальна
     **/
    private boolean removeComments() {
        return true;
    }
}
