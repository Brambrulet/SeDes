package study.inno.sedes;

import java.util.Objects;

public class PlainObj {
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

    public PlainObj() {
    }

    public PlainObj(Long longField, Integer integerField, Short shortField, Byte byteField, long longField1, int intField, short shortField1, byte byteField1, Double doubleField, Float floatField, double doubleField1, float floatField1, boolean booleanField, Boolean booleanField1, Character characterField, char charField, String stringField) {
        LongField = longField;
        IntegerField = integerField;
        ShortField = shortField;
        ByteField = byteField;
        this.longField = longField1;
        this.intField = intField;
        this.shortField = shortField1;
        this.byteField = byteField1;
        DoubleField = doubleField;
        FloatField = floatField;
        this.doubleField = doubleField1;
        this.floatField = floatField1;
        this.booleanField = booleanField;
        BooleanField = booleanField1;
        CharacterField = characterField;
        this.charField = charField;
        StringField = stringField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlainObj)) return false;
        PlainObj plainObj = (PlainObj) o;
        return longField == plainObj.longField &&
                intField == plainObj.intField &&
                shortField == plainObj.shortField &&
                byteField == plainObj.byteField &&
                Double.compare(plainObj.doubleField, doubleField) == 0 &&
                Float.compare(plainObj.floatField, floatField) == 0 &&
                booleanField == plainObj.booleanField &&
                charField == plainObj.charField &&
                Objects.equals(LongField, plainObj.LongField) &&
                Objects.equals(IntegerField, plainObj.IntegerField) &&
                Objects.equals(ShortField, plainObj.ShortField) &&
                Objects.equals(ByteField, plainObj.ByteField) &&
                Objects.equals(DoubleField, plainObj.DoubleField) &&
                Objects.equals(FloatField, plainObj.FloatField) &&
                Objects.equals(BooleanField, plainObj.BooleanField) &&
                Objects.equals(CharacterField, plainObj.CharacterField) &&
                Objects.equals(StringField, plainObj.StringField);
    }

    @Override
    public int hashCode() {

        return Objects.hash(LongField, IntegerField, ShortField, ByteField, longField, intField, shortField, byteField, DoubleField, FloatField, doubleField, floatField, booleanField, BooleanField, CharacterField, charField, StringField);
    }
}
