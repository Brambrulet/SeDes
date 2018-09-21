package study.inno.sedes;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        PlainObj plainObj = new PlainObj((Long) 0l, (Integer) 1, (short) 2, (byte) 3, (long) 4l, (int) 5, (short) 6, (byte) 7, (double) 1.2d, (Float) 1.4f, (double) 1.6d, (float) 1.8f, true, false, 'a', 'z',
                "Глаокая куздра штеко быдланула бокра и куздрячит бокрёнка <>");
        ChildObject childObject = new ChildObject((Long) 0l, (Integer) 1, (short) 2, (byte) 3, (long) 4l, (int) 5, (short) 6, (byte) 7, (double) 1.2d, (Float) 1.4f, (double) 1.6d, (float) 1.8f, true, false, 'a', 'z',
                "Глаокая куздра штеко быдланула бокра и куздрячит бокрёнка <>",
                (Long) 1l, (Integer) 2, (short) 4, (byte) 6, (long) 8l, (int) 10, (short) 12, (byte) 14, (double) 2.4d, (Float) 2.8f, (double) 3.2d, (float) 3.6f, true, false, 'ё', 'Ё',
                "Глаокая куздра штеко быдланула бокра и куздрячит бокрёнка <>", plainObj
        );
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("мама");
        arrayList.add("мыла");
        arrayList.add("раму");

        XmlSeDes seDes = new XmlSeDes();
        Object array = Array.newInstance(Class.forName("java.lang.String"), 4);

        ((String[]) array)[0] = "asdf";


        serializeTest(seDes, new String[]{"ма<ма", "м>ыла", "раму<asdf >"}, "ArrayOfString.xml", "\r\nString[]:");
        serializeTest(seDes, new Long[]{1l, 2l, 3l}, "ArrayOfLong.xml", "\r\nLong[]:");
        serializeTest(seDes, new Integer[]{1, 2, 3}, "ArrayOfInteger.xml", "\r\nInteger[]:");
        serializeTest(seDes, new Short[]{1, 2, 3}, "ArrayOfShort.xml", "\r\nShort[]:");
        serializeTest(seDes, new Byte[]{1, 2, 3}, "ArrayOfByte.xml", "\r\nByte[]:");
        serializeTest(seDes, new Double[]{1.2, 2.4, 3.6}, "ArrayOfDouble.xml", "\r\nDouble[]:");
        serializeTest(seDes, new Float[]{1.2f, 2.4f, 3.6f}, "ArrayOfFloat.xml", "\r\nFloat[]:");
        serializeTest(seDes, new Character[]{'s', 't', 'Ё'}, "ArrayOfCharacter.xml", "\r\nCharacter[]:");
        serializeTest(seDes, new Boolean[]{true, false, true}, "ArrayOfBoolean.xml", "\r\nBoolean[]:");
        serializeTest(seDes, new long[]{1, 2, 3}, "ArrayOf_long.xml", "\r\nlong[]:");
        serializeTest(seDes, new int[]{1, 2, 3}, "ArrayOf_int.xml", "\r\nint[]:");
        serializeTest(seDes, new short[]{1, 2, 3}, "ArrayOf_short.xml", "\r\nshort[]:");
        serializeTest(seDes, new byte[]{1, 2, 3}, "ArrayOf_byte.xml", "\r\nbyte[]:");
        serializeTest(seDes, new char[]{'s', 't', 'Ё'}, "ArrayOf_char.xml", "\r\nchar[]:");
        serializeTest(seDes, new double[]{1.2, 2.4, 3.6}, "ArrayOf_double.xml", "\r\ndouble[]:");
        serializeTest(seDes, new float[]{1.2f, 2.4f, 3.6f}, "ArrayOf_float.xml", "\r\nfloat[]:");
        serializeTest(seDes, new boolean[]{true, false, true}, "ArrayOf_boolean.xml", "\r\nboolean[]:");
        serializeTest(seDes, plainObj, "PlainObj.xml", "\r\nPlainObject:");
        serializeTest(seDes, childObject, "ChildObject.xml", "\r\nChildObject:");
        serializeTest(seDes, null, "NullObject.xml", "\r\nNull object:");
        serializeTest(seDes, arrayList, "ArrayList.xml", "\r\nArrayList:");
    }

    static void serializeTest(XmlSeDes seDes, Object obj, String fileName, String message) {
        System.out.println(message);
        try {
            seDes.serialize(obj, fileName);

            System.out.println("Читаем взад");
            Object newObj = seDes.deSerialize(fileName);

            if (obj == null) {
                System.out.println(newObj == null ? ":) объекты идентичны" : ":( что-то не то прочитали");
            } else if (obj.getClass().getName().charAt(0) != '[') {
                System.out.println(newObj.equals(obj) ? ":) объекты идентичны" : ":( что-то не то прочитали");
            } else {
                //сравнение массивов непростая штука
                System.out.println(deepEquals0(obj, newObj) ? ":) объекты идентичны" : ":( что-то не то прочитали");
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(message + " - сериализация/десериализация завершены с ошибкой");
    }

    //Взято из Arrays - там оно почемуто сокрыто от глаз
    static boolean deepEquals0(Object e1, Object e2) {
        assert e1 != null;
        boolean eq;
        if (e1 instanceof Object[] && e2 instanceof Object[])
            eq = Arrays.deepEquals((Object[]) e1, (Object[]) e2);
        else if (e1 instanceof byte[] && e2 instanceof byte[])
            eq = Arrays.equals((byte[]) e1, (byte[]) e2);
        else if (e1 instanceof short[] && e2 instanceof short[])
            eq = Arrays.equals((short[]) e1, (short[]) e2);
        else if (e1 instanceof int[] && e2 instanceof int[])
            eq = Arrays.equals((int[]) e1, (int[]) e2);
        else if (e1 instanceof long[] && e2 instanceof long[])
            eq = Arrays.equals((long[]) e1, (long[]) e2);
        else if (e1 instanceof char[] && e2 instanceof char[])
            eq = Arrays.equals((char[]) e1, (char[]) e2);
        else if (e1 instanceof float[] && e2 instanceof float[])
            eq = Arrays.equals((float[]) e1, (float[]) e2);
        else if (e1 instanceof double[] && e2 instanceof double[])
            eq = Arrays.equals((double[]) e1, (double[]) e2);
        else if (e1 instanceof boolean[] && e2 instanceof boolean[])
            eq = Arrays.equals((boolean[]) e1, (boolean[]) e2);
        else
            eq = e1.equals(e2);
        return eq;
    }
}
