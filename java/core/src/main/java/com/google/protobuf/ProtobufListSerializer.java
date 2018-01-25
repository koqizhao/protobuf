package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.WireFormat.FieldType;

/**
 * @author koqizhao
 *
 *         Dec 19, 2017
 */
@SuppressWarnings("unchecked")
public class ProtobufListSerializer<V> {

  protected static final int FIELD_NUMBER = 1;

  private FieldType _valueType;
  private V _valueDefault;
  private int _valueTag;

  protected ProtobufListSerializer(FieldType valueType, V valueDefault) {
    _valueType = valueType;
    _valueDefault = valueDefault;
    _valueTag = WireFormat.makeTag(FIELD_NUMBER, _valueType.getWireType());
  }

  public void serialize(OutputStream stream, List<V> list) throws IOException {
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(stream);
    serialize(codedOutputStream, list);
  }

  public List<V> deserialize(InputStream stream) throws IOException {
    CodedInputStream codedInputStream = CodedInputStream.newInstance(stream);
    return deserialize(codedInputStream);
  }

  public void serialize(byte[] bytes, List<V> list) throws IOException {
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(bytes);
    serialize(codedOutputStream, list);
  }

  public List<V> deserialize(byte[] bytes) throws IOException {
    CodedInputStream codedInputStream = CodedInputStream.newInstance(bytes);
    return deserialize(codedInputStream);
  }

  public void serialize(CodedOutputStream codedOutputStream, List<V> list) throws IOException {
    for (V entry : list) {
      FieldSet.writeElement(codedOutputStream, _valueType, FIELD_NUMBER, entry);
    }

    codedOutputStream.flush();
  }

  public List<V> deserialize(CodedInputStream codedInputStream) throws InvalidProtocolBufferException {
    try {
      List<V> list = new ArrayList<V>();
      while (true) {
        int tag = codedInputStream.readTag();
        if (tag == 0)
          break;

        if (tag == _valueTag) {
          V entry = SerializerUtil.parseField(codedInputStream, _valueType, _valueDefault);
          list.add(entry);
        }
      }

      return list;
    } catch (InvalidProtocolBufferException e) {
      throw e;
    } catch (IOException e) {
      throw new InvalidProtocolBufferException(e);
    }
  }

  public static <V> Builder<V> newBuilder() {
    return new Builder<V>();
  }

  public static class Builder<V> {
    private FieldType _valueType;
    private V _valueDefault;

    protected Builder() {

    }

    public Builder<V> valueType(FieldType fieldType) {
      if (fieldType == null)
        throw new NullPointerException("fieldType is null.");

      if (!SerializerUtil.isSupportedValueType(fieldType))
        throw new UnsupportedOperationException(fieldType + " is not supported for protobuf list value");

      _valueType = fieldType;
      return this;
    }

    public Builder<V> valueDefault(V valueDefault) {
      if (valueDefault == null)
        throw new NullPointerException("valueDefault is null.");

      _valueDefault = valueDefault;
      return this;
    }

    public ProtobufListSerializer<V> build() {
      if (_valueType == null)
        throw new UnsupportedOperationException("valueType is not set.");

      if (_valueDefault == null)
        _valueDefault = (V) _valueType.getJavaType().getDefaultDefault();

      if (_valueDefault == null)
        throw new UnsupportedOperationException("valueType is " + _valueType + ", but valueDefault is not set.");

      return new ProtobufListSerializer<V>(_valueType, _valueDefault);
    }
  }
}
