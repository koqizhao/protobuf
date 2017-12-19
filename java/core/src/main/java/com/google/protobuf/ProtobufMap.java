package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.google.protobuf.WireFormat.FieldType;

/**
 * @author koqizhao
 *
 *         Dec 19, 2017
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtobufMap<K, V> {

  private MapEntry _defaultEntry;
  private MapField<K, V> _mapField;

  public ProtobufMap(FieldType valueType, Object defaultValue, FieldType... keyTypes) {
    this(newDefaultEntry(valueType, defaultValue, keyTypes));
  }

  public ProtobufMap(MapEntry defaultEntry) {
    _defaultEntry = defaultEntry;
    _mapField = MapField.newMapField(defaultEntry);
  }

  public void writeTo(OutputStream stream) throws IOException {
    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(stream);
    for (Map.Entry<K, V> entry : getMap().entrySet()) {
      MapEntry mapEntry = _defaultEntry.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build();
      codedOutputStream.writeMessage(1, mapEntry);
    }

    codedOutputStream.flush();
  }

  public void parseFrom(InputStream stream) throws IOException {
    clear();

    try {
      CodedInputStream codedOutStream = CodedInputStream.newInstance(stream);
      boolean done = false;
      while (!done) {
        int tag = codedOutStream.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10:
            MapEntry mapEntry = (MapEntry) codedOutStream.readMessage(_defaultEntry.getParserForType(),
                ExtensionRegistryLite.getEmptyRegistry());
            getMap().put((K) mapEntry.getKey(), (V) mapEntry.getMapValue());
            break;
          default:
            break;
        }
      }
    } catch (java.io.IOException e) {
      throw new InvalidProtocolBufferException(e);
    }
  }

  public Map<K, V> getMap() {
    return _mapField.getMutableMap();
  }

  public void setMap(Map<K, V> map) {
    clear();
    _mapField.getMutableMap().putAll(map);
  }

  public void clear() {
    if (!_mapField.getMutableMap().isEmpty())
      _mapField.clear();
  }

  public static MapEntry newDefaultEntry(FieldType valueType, Object defaultValue, FieldType... keyTypes) {
    MapEntry defaultEntry = null;
    for (int i = keyTypes.length - 1; i >= 0; i--) {
      if (defaultEntry == null)
        defaultEntry = MapEntry.newDefaultInstance(keyTypes[i], defaultValue(keyTypes[i]), valueType, defaultValue);
      else
        defaultEntry = MapEntry.newDefaultInstance(keyTypes[i], defaultValue(keyTypes[i]), FieldType.MESSAGE,
            defaultEntry);
    }

    return defaultEntry;
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
      case STRING:
        return "";
      default:
        throw new UnsupportedOperationException("FieldType " + fieldType + " is not supported for map key.");
    }
  }
}
