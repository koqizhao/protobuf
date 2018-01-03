package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.WireFormat.FieldType;

/**
 * @author koqizhao
 *
 *         Dec 19, 2017
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtobufMapSerializer<K, V> {

  protected static final int FIELD_NUMBER = 1;
  protected static final int ENTRY_TAG = WireFormat.makeTag(FIELD_NUMBER, FieldType.MESSAGE.getWireType());

  private MapEntry _defaultEntry;

  protected ProtobufMapSerializer(MapEntry defaultEntry) {
    _defaultEntry = defaultEntry;
  }

  public void serialize(OutputStream stream, Map<K, V> map) throws IOException {
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(stream);
    serialize(codedOutputStream, map);
  }

  public Map<K, V> deserialize(InputStream stream) throws IOException {
    CodedInputStream codedInputStream = CodedInputStream.newInstance(stream);
    return deserialize(codedInputStream);
  }

  public void serialize(byte[] bytes, Map<K, V> map) throws IOException {
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(bytes);
    serialize(codedOutputStream, map);
  }

  public Map<K, V> deserialize(byte[] bytes) throws IOException {
    CodedInputStream codedInputStream = CodedInputStream.newInstance(bytes);
    return deserialize(codedInputStream);
  }

  public void serialize(CodedOutputStream codedOutputStream, Map<K, V> map) throws IOException {
    for (Map.Entry entry : map.entrySet()) {
      MapEntry mapEntry = _defaultEntry.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build();
      codedOutputStream.writeMessage(FIELD_NUMBER, mapEntry);
    }

    codedOutputStream.flush();
  }

  public Map<K, V> deserialize(CodedInputStream codedInputStream) throws InvalidProtocolBufferException {
    try {
      Map map = new HashMap();
      while (true) {
        int tag = codedInputStream.readTag();
        if (tag == 0)
          break;

        if (tag == ENTRY_TAG) {
          MapEntry mapEntry = (MapEntry) codedInputStream.readMessage(_defaultEntry.getParserForType(),
              ExtensionRegistryLite.getEmptyRegistry());
          map.put(mapEntry.getKey(), mapEntry.getRealValue());
        }
      }

      return map;
    } catch (InvalidProtocolBufferException e) {
      throw e;
    } catch (IOException e) {
      throw new InvalidProtocolBufferException(e);
    }
  }

  public static <K, V> Builder<K, V> newBuilder() {
    return new Builder<K, V>();
  }

  public static class Builder<K, V> {
    private List<FieldType> _keyTypes;
    private FieldType _valueType;
    private Object _valueDefault;

    protected Builder() {
      _keyTypes = new ArrayList<FieldType>();
    }

    public Builder<K, V> keyTypes(FieldType... fieldTypes) {
      if (fieldTypes == null || fieldTypes.length == 0)
        throw new NullPointerException("fieldTypes is null.");

      for (FieldType fieldType : fieldTypes) {
        if (fieldType == null)
          throw new NullPointerException("fieldType is null.");

        if (!SerializerUtil.isSupportedKeyType(fieldType))
          throw new UnsupportedOperationException(fieldType + " is not supported for protobuf map key");

        _keyTypes.add(fieldType);
      }

      return this;
    }

    public Builder<K, V> valueType(FieldType fieldType) {
      if (fieldType == null)
        throw new NullPointerException("fieldType is null.");

      if (!SerializerUtil.isSupportedValueType(fieldType))
        throw new UnsupportedOperationException(fieldType + " is not supported for protobuf map value");

      _valueType = fieldType;
      return this;
    }

    public Builder<K, V> valueDefault(Object valueDefault) {
      if (valueDefault == null)
        throw new NullPointerException("valueDefault is null.");

      _valueDefault = valueDefault;
      return this;
    }

    public ProtobufMapSerializer<K, V> build() {
      MapEntry defaultEntry = buildDefaultEntry();
      return new ProtobufMapSerializer<K, V>(defaultEntry);
    }

    protected MapEntry buildDefaultEntry() {
      if (_keyTypes.size() == 0)
        throw new UnsupportedOperationException("keyTypes is not set.");

      if (_valueType == null)
        throw new UnsupportedOperationException("valueType is not set.");

      if (_valueDefault == null)
        _valueDefault = _valueType.getJavaType().getDefaultDefault();

      if (_valueDefault == null)
        throw new UnsupportedOperationException("valueType is " + _valueType + ", but valueDefault is not set.");

      MapEntry defaultEntry = null;
      for (int i = _keyTypes.size() - 1; i >= 0; i--) {
        FieldType keyType = _keyTypes.get(i);
        if (defaultEntry == null)
          defaultEntry = MapEntry.newDefaultInstance(keyType, keyType.getJavaType().getDefaultDefault(), _valueType,
              _valueDefault);
        else
          defaultEntry = MapEntry.newDefaultInstance(keyType, keyType.getJavaType().getDefaultDefault(),
              FieldType.MESSAGE, defaultEntry);
      }

      return defaultEntry;
    }
  }

}
