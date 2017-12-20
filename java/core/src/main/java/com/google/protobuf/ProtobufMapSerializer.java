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
      codedOutputStream.writeMessage(1, mapEntry);
    }

    codedOutputStream.flush();
  }

  public Map<K, V> deserialize(CodedInputStream codedInputStream) throws IOException {
    try {
      Map map = null;
      while (true) {
        int tag = codedInputStream.readTag();
        if (tag == 0)
          break;

        if (tag == 10) {
          if (map == null)
            map = new HashMap();

          MapEntry mapEntry = (MapEntry) codedInputStream.readMessage(_defaultEntry.getParserForType(),
              ExtensionRegistryLite.getEmptyRegistry());
          map.put(mapEntry.getKey(), mapEntry.getRealValue());
        }
      }

      if (map == null)
        throw new InvalidProtocolBufferException("No map data in the stream.");

      return map;
    } catch (java.io.IOException e) {
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

    private Builder() {
      _keyTypes = new ArrayList<FieldType>();
    }

    public Builder<K, V> keyTypes(FieldType... fieldTypes) {
      if (fieldTypes == null || fieldTypes.length == 0)
        throw new NullPointerException("fieldTypes is null.");

      for (FieldType fieldType : fieldTypes) {
        if (fieldType == null)
          throw new NullPointerException("fieldType is null.");

        if (!isSupportedKeyType(fieldType))
          throw new UnsupportedOperationException(fieldType + " is not supported for protobuf map key");

        _keyTypes.add(fieldType);
      }

      return this;
    }

    public Builder<K, V> valueType(FieldType fieldType) {
      if (fieldType == null)
        throw new NullPointerException("fieldType is null.");

      if (!isSupportedValueType(fieldType))
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
        _valueDefault = defaultValue(_valueType);

      if (_valueDefault == null)
        throw new UnsupportedOperationException("valueType is " + _valueType + ", but valueDefault is not set.");

      MapEntry defaultEntry = null;
      for (int i = _keyTypes.size() - 1; i >= 0; i--) {
        FieldType keyType = _keyTypes.get(i);
        if (defaultEntry == null)
          defaultEntry = MapEntry.newDefaultInstance(keyType, defaultValue(keyType), _valueType, _valueDefault);
        else
          defaultEntry = MapEntry.newDefaultInstance(keyType, defaultValue(keyType), FieldType.MESSAGE, defaultEntry);
      }

      return defaultEntry;
    }
  }

  public static Object defaultValue(FieldType fieldType) {
    switch (fieldType) {
      case BOOL:
        return false;
      case INT64:
      case UINT64:
      case FIXED64:
      case SFIXED64:
      case SINT64:
      case INT32:
      case FIXED32:
      case UINT32:
      case SFIXED32:
      case SINT32:
        return 0;
      case DOUBLE:
      case FLOAT:
        return 0.0;
      case STRING:
        return "";
      default:
        return null;
    }
  }

  public static boolean isSupportedKeyType(FieldType fieldType) {
    switch (fieldType) {
      case BOOL:
      case INT64:
      case UINT64:
      case FIXED64:
      case SFIXED64:
      case SINT64:
      case INT32:
      case FIXED32:
      case UINT32:
      case SFIXED32:
      case SINT32:
        return true;
      default:
        return false;
    }
  }

  public static boolean isSupportedValueType(FieldType fieldType) {
    switch (fieldType) {
      case GROUP:
        return false;
      default:
        return true;
    }
  }
}
