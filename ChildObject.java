package study.inno.sedes;

import java.util.Objects;

public class ChildObject extends PlainObj {
    public Long LongField;
    public Integer IntegerField;
    public Short ShortField;
    public Byte ByteField;
    public long longField;
    public int intField;
    public short shortField;
    public byte byteField;
    public Double DoubleField;
    public Float FloatField;
    public double doubleField;
    public float floatField;
    public boolean booleanField;
    public Boolean BooleanField;
    public Character CharacterField;
    public char charField;
    public String StringField;

    public PlainObj mrFirst;
    public PlainObj mrSecond;

    public ChildObject() {
    }

    public ChildObject(Long longField, Integer integerField, Short shortField, Byte byteField, long longField1, int intField, short shortField1, byte byteField1, Double doubleField, Float floatField, double doubleField1, float floatField1, boolean booleanField, Boolean booleanField1, Character characterField, char charField, String stringField, Long longField2, Integer integerField1, Short shortField2, Byte byteField2, long longField3, int intField1, short shortField3, byte byteField3, Double doubleField2, Float floatField2, double doubleField3, float floatField3, boolean booleanField2, Boolean booleanField3, Character characterField1, char charField1, String stringField1, PlainObj another) {
        super(longField, integerField, shortField, byteField, longField1, intField, shortField1, byteField1, doubleField, floatField, doubleField1, floatField1, booleanField, booleanField1, characterField, charField, stringField);
        LongField = longField2;
        IntegerField = integerField1;
        ShortField = shortField2;
        ByteField = byteField2;
        this.longField = longField3;
        this.intField = intField1;
        this.shortField = shortField3;
        this.byteField = byteField3;
        DoubleField = doubleField2;
        FloatField = floatField2;
        this.doubleField = doubleField3;
        this.floatField = floatField3;
        this.booleanField = booleanField2;
        BooleanField = booleanField3;
        CharacterField = characterField1;
        this.charField = charField1;
        StringField = stringField1;
        this.mrFirst = another;
        this.mrSecond = another;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChildObject)) return false;
        if (!super.equals(o)) return false;
        ChildObject that = (ChildObject) o;
        return longField == that.longField &&
                intField == that.intField &&
                shortField == that.shortField &&
                byteField == that.byteField &&
                Double.compare(that.doubleField, doubleField) == 0 &&
                Float.compare(that.floatField, floatField) == 0 &&
                booleanField == that.booleanField &&
                charField == that.charField &&
                Objects.equals(LongField, that.LongField) &&
                Objects.equals(IntegerField, that.IntegerField) &&
                Objects.equals(ShortField, that.ShortField) &&
                Objects.equals(ByteField, that.ByteField) &&
                Objects.equals(DoubleField, that.DoubleField) &&
                Objects.equals(FloatField, that.FloatField) &&
                Objects.equals(BooleanField, that.BooleanField) &&
                Objects.equals(CharacterField, that.CharacterField) &&
                Objects.equals(StringField, that.StringField) &&
                Objects.equals(mrFirst, that.mrFirst) &&
                Objects.equals(mrSecond, that.mrSecond);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), LongField, IntegerField, ShortField, ByteField, longField, intField, shortField, byteField, DoubleField, FloatField, doubleField, floatField, booleanField, BooleanField, CharacterField, charField, StringField, mrFirst, mrSecond);
    }
}
