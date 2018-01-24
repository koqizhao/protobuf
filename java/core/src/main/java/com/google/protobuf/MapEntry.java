// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// https://developers.google.com/protocol-buffers/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.google.protobuf;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.WireFormat.FieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implements MapEntry messages.
 *
 * In reflection API, map fields will be treated as repeated message fields and
 * each map entry is accessed as a message. This MapEntry class is used to
 * represent these map entry messages in reflection API.
 *
 * Protobuf internal. Users shouldn't use this class.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class MapEntry<K, V> extends AbstractMessage {

  private static final class Metadata<K, V> extends MapEntryLite.Metadata<K, V> {

    public final Descriptor descriptor;
    public final Parser<MapEntry<K, V>> parser;
    
    public Metadata(
        Descriptor descriptor,
        WireFormat.FieldType keyType,
        K defaultKey,
        WireFormat.FieldType valueType,
        V defaultValue,
        boolean isNested) {
      super(keyType, defaultKey, valueType, defaultValue, isNested);
      this.descriptor = descriptor;
      this.parser = new AbstractParser<MapEntry<K, V>>() {
        @Override
        public MapEntry<K, V> parsePartialFrom(
            CodedInputStream input, ExtensionRegistryLite extensionRegistry)
            throws InvalidProtocolBufferException {
          return new MapEntry<K, V>(Metadata.this, input, extensionRegistry);
        }
      };
    }

  }

  private final K key;
  private final Object value;
  private final Metadata<K, V> metadata;

  /** Create a default MapEntry instance. */
  private MapEntry(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    this.metadata = new Metadata<K, V>(descriptor, keyType, defaultKey, valueType, defaultValue, isMapEntry(defaultValue));
    this.key = defaultKey;
    this.value = toDefaultValue(metadata);
  }

  /** Create a MapEntry with the provided key and value. */
  private MapEntry(Metadata metadata, K key, Object value) {
    this.metadata = metadata;
    this.key = key;
    this.value = value;
  }

  /** Parsing constructor. */
  private MapEntry(
      Metadata<K, V> metadata,
      CodedInputStream input,
      ExtensionRegistryLite extensionRegistry)
      throws InvalidProtocolBufferException {
    try {
      this.metadata = metadata;
      Map.Entry<K, Object> entry = MapEntryLite.parseEntry(input, metadata, extensionRegistry);
      this.key = entry.getKey();
      this.value = entry.getValue();
    } catch (InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (IOException e) {
      throw new InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(this);
    }
  }

  /**
   * Create a default MapEntry instance. A default MapEntry instance should be
   * created only once for each map entry message type. Generated code should
   * store the created default instance and use it later to create new MapEntry
   * messages of the same type.
   */
  public static <K, V> MapEntry<K, V> newDefaultInstance(
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    Descriptor descriptor = null;
    if (isMapEntry(defaultValue))
      descriptor = MapDescriptors.newDescriptorForParent(keyType);
    else
      descriptor = MapDescriptors.newDescriptorForLeaf(keyType, valueType);
    return newDefaultInstance(descriptor, keyType, defaultKey, valueType, defaultValue);
  }

  public static <K, V> MapEntry<K, V> newDefaultInstance(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    return new MapEntry<K, V>(descriptor, keyType, defaultKey, valueType, defaultValue);
  }

  public boolean isNested() {
    return metadata.isNested;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return (V) value;
  }
  
  public Map getMapValue() {
    return toMapValue(value);
  }

  public Object getRealValue() {
    return isNested() ? getMapValue() : getValue();
  }

  private volatile int cachedSerializedSize = -1;

  @Override
  public int getSerializedSize() {
    if (cachedSerializedSize != -1) {
      return cachedSerializedSize;
    }

    int size = MapEntryLite.computeSerializedSize(metadata, key, value);
    cachedSerializedSize = size;
    return size;
  }

  @Override
  public void writeTo(CodedOutputStream output) throws IOException {
    MapEntryLite.writeTo(output, metadata, key, value);
  }

  @Override
  public boolean isInitialized() {
    return isInitialized(metadata, value);
  }

  @Override
  public Parser<MapEntry<K, V>> getParserForType() {
    return metadata.parser;
  }

  @Override
  public Builder<K, V> newBuilderForType() {
    return new Builder<K, V>(metadata);
  }

  @Override
  public Builder<K, V> toBuilder() {
    return new Builder<K, V>(metadata, key, copyValue(metadata, value));
  }

  @Override
  public MapEntry<K, V> getDefaultInstanceForType() {
    return new MapEntry<K, V>(metadata, metadata.defaultKey, toDefaultValue(metadata));
  }

  @Override
  public Descriptor getDescriptorForType() {
    return metadata.descriptor;
  }

  @Override
  public Map<FieldDescriptor, Object> getAllFields() {
    TreeMap<FieldDescriptor, Object> result = new TreeMap<FieldDescriptor, Object>();
    for (final FieldDescriptor field : metadata.descriptor.getFields()) {
      if (hasField(field)) {
        result.put(field, getField(field));
      }
    }
    return Collections.unmodifiableMap(result);
  }

  private void checkFieldDescriptor(FieldDescriptor field) {
    if (field.getContainingType() != metadata.descriptor) {
      throw new RuntimeException(
          "Wrong FieldDescriptor \"" + field.getFullName()
          + "\" used in message \"" + metadata.descriptor.getFullName());
    }
  }

  @Override
  public boolean hasField(FieldDescriptor field) {
    checkFieldDescriptor(field);;
    // A MapEntry always contains two fields.
    return true;
  }

  @Override
  public Object getField(FieldDescriptor field) {
    checkFieldDescriptor(field);
    Object result = field.getNumber() == 1 ? getKey() : value;
    // Convert enums to EnumValueDescriptor.
    if (field.getType() == FieldDescriptor.Type.ENUM) {
      result = field.getEnumType().findValueByNumberCreatingIfUnknown(
          (java.lang.Integer) result);
    }
    return result;
  }

  @Override
  public int getRepeatedFieldCount(FieldDescriptor field) {
    List values = (List) value;
    return value == null ? 0 : values.size();
  }

  @Override
  public Object getRepeatedField(FieldDescriptor field, int index) {
    List values = (List) value;
    return values.get(index);
  }
  
  @Override
  public UnknownFieldSet getUnknownFields() {
    return UnknownFieldSet.getDefaultInstance();
  }

  /**
   * Builder to create {@link MapEntry} messages.
   */
  public static class Builder<K, V>
      extends AbstractMessage.Builder<Builder<K, V>> {
    private final Metadata<K, V> metadata;
    private K key;
    private Object value;
    private boolean hasKey;
    private boolean hasValue;

    private Builder(Metadata<K, V> metadata) {
      this(metadata, metadata.defaultKey, toDefaultValue(metadata), false, false);
    }

    private Builder(Metadata<K, V> metadata, K key, Object value, boolean hasKey, boolean hasValue) {
      this.metadata = metadata;
      this.key = key;
      this.value = value;
      this.hasKey = hasKey;
      this.hasValue = hasValue;
    }
 
    public boolean isNested() {
      return metadata.isNested;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return (V) value;
    }

    public Map getMapValue() {
      return toMapValue(value);
    }

    public Object getRealValue() {
      return isNested() ? getMapValue() : getValue();
    }

    public Builder<K, V> setKey(K key) {
      this.key = key;
      this.hasKey = true;
      return this;
    }

    public Builder<K, V> clearKey() {
      this.key = metadata.defaultKey;
      this.hasKey = false;
      return this;
    }

    public Builder<K, V> setValue(Object value) {
	  this.hasValue = true;
      if (!isNested()) {
        this.value = value;
        return this;
      }

      List<MapEntry> values = (List<MapEntry>) this.value;
      values.clear();

      if (value == null)
        return this;

      Map valueMap = (Map) value;
      if (valueMap.isEmpty())
        return this;

      MapEntry defaultValueEntry = (MapEntry) metadata.defaultValue;
      for (Map.Entry e : (Set<Map.Entry>) valueMap.entrySet()) {
        MapEntry.Builder builder = defaultValueEntry.newBuilderForType();
        builder.setKey(e.getKey());
        builder.setValue(e.getValue());
        values.add(builder.build());
      }

      return this;
    }

    public Builder<K, V> clearValue() {
      this.hasValue = false;
      if (isNested()) {
        List<MapEntry> values = (List<MapEntry>) this.value;
        values.clear();
      } else {
        this.value = metadata.defaultValue;
      }

      return this;
    }

    @Override
    public MapEntry<K, V> build() {
      MapEntry<K, V> result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public MapEntry<K, V> buildPartial() {
      return new MapEntry<K, V>(metadata, key, value);
    }

    @Override
    public Descriptor getDescriptorForType() {
      return metadata.descriptor;
    }

    private void checkFieldDescriptor(FieldDescriptor field) {
      if (field.getContainingType() != metadata.descriptor) {
        throw new RuntimeException(
            "Wrong FieldDescriptor \"" + field.getFullName()
            + "\" used in message \"" + metadata.descriptor.getFullName());
      }
    }

    @Override
    public Message.Builder newBuilderForField(FieldDescriptor field) {
      checkFieldDescriptor(field);;
      // This method should be called for message fields and in a MapEntry
      // message only the value field can possibly be a message field.
      if (field.getNumber() != 2
          || field.getJavaType() != FieldDescriptor.JavaType.MESSAGE) {
        throw new RuntimeException(
            "\"" + field.getFullName() + "\" is not a message value field.");
      }

      Message message = (Message) (isNested() ? metadata.defaultValue : value);
      return message.newBuilderForType();
    }

    @Override
    public Builder<K, V> setField(FieldDescriptor field, Object value) {
      checkFieldDescriptor(field);
      if (field.getNumber() == 1) {
        setKey((K) value);
      } else {
        if (field.getType() == FieldDescriptor.Type.ENUM) {
          value = ((EnumValueDescriptor) value).getNumber();
        }

        if (isNested()) {
          List<MapEntry> values = (List<MapEntry>) this.value;
          values.clear();
          if (value != null)
            values.addAll((List<MapEntry>) value);
        } else
          this.value = value;
      }
      return this;
    }

    @Override
    public Builder<K, V> clearField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      if (field.getNumber() == 1) {
        clearKey();
      } else {
        clearValue();
      }
      return this;
    }

    @Override
    public Builder<K, V> setRepeatedField(FieldDescriptor field, int index,
        Object value) {
      List values = (List) this.value;
      values.set(index, value);
      return this;
    }

    @Override
    public Builder<K, V> addRepeatedField(FieldDescriptor field, Object value) {
      List values = (List) this.value;
      values.add(value);
      return this;
    }

    @Override
    public Builder<K, V> setUnknownFields(UnknownFieldSet unknownFields) {
      // Unknown fields are discarded for MapEntry message.
      return this;
    }

    @Override
    public MapEntry<K, V> getDefaultInstanceForType() {
      return new MapEntry<K, V>(metadata, metadata.defaultKey, toDefaultValue(metadata));
    }

    @Override
    public boolean isInitialized() {
      return MapEntry.isInitialized(metadata, value);
    }

    @Override
    public Map<FieldDescriptor, Object> getAllFields() {
      final TreeMap<FieldDescriptor, Object> result = new TreeMap<FieldDescriptor, Object>();
      for (final FieldDescriptor field : metadata.descriptor.getFields()) {
        if (hasField(field)) {
          result.put(field, getField(field));
        }
      }
      return Collections.unmodifiableMap(result);
    }

    @Override
    public boolean hasField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      return field.getNumber() == 1 ? hasKey : hasValue;
    }

    @Override
    public Object getField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      Object result = field.getNumber() == 1 ? getKey() : value;
      // Convert enums to EnumValueDescriptor.
      if (field.getType() == FieldDescriptor.Type.ENUM) {
        result = field.getEnumType().findValueByNumberCreatingIfUnknown((Integer) result);
      }
      return result;
    }

    @Override
    public int getRepeatedFieldCount(FieldDescriptor field) {
      List values = (List) value;
      return values == null ? 0 : values.size();
    }

    @Override
    public Object getRepeatedField(FieldDescriptor field, int index) {
      List values = (List) value;
      return values.get(index);
    }
  
    @Override
    public UnknownFieldSet getUnknownFields() {
      return UnknownFieldSet.getDefaultInstance();
    }

    @Override
    public Builder<K, V> clone() {
      return new Builder<K, V>(metadata, key, copyValue(metadata, value));
    }
  }

  private static boolean isInitialized(Metadata metadata, Object value) {
    if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE) {
      if (metadata.isNested) {
        for (MessageLite e : (List<MessageLite>) value) {
          if(!((MessageLite) e).isInitialized())
            return false;
        }
      } else {
        return ((MessageLite) value).isInitialized();
      }
    }
    return true;
  }

  private static boolean isMapEntry(Object value) {
    return value != null && value instanceof MapEntry;
  }

  private static Object toDefaultValue(Metadata metadata) {
    return metadata.isNested ? new ArrayList() : metadata.defaultValue;
  }

  private static Map toMapValue(Object value) {
    Map map = new HashMap();
    for (MapEntry entry : (List<MapEntry>) value) {
      map.put(entry.getKey(), entry.getRealValue());
    }
    return map;
  }

  private static Object copyValue(Metadata metadata, Object value) {
    return metadata.isNested ? new ArrayList((List) value) : value;
  }

  public static class MapDescriptors {

    public static Descriptor newDescriptorForLeaf(FieldType keyType, FieldType valueType) {
      TypeDescriptorInfo typeDescriptorInfo = new TypeDescriptorInfo(
          "LeafMapEntry", "LeafMapEntry.proto", keyType, valueType);
      return newDescriptor(typeDescriptorInfo);
    }

    public static Descriptor newDescriptorForParent(FieldType keyType) {
      TypeDescriptorInfo typeDescriptorInfo = new TypeDescriptorInfo("NodeMapEntry", "NodeMapEntry.proto", keyType);
      return newDescriptor(typeDescriptorInfo);
    }

    private static Descriptor newDescriptor(TypeDescriptorInfo typeDescriptorInfo) {
      DescriptorProto.Builder nodeMapBuilder = DescriptorProto.getDefaultInstance().toBuilder().setName(typeDescriptorInfo.typeName);

      FieldDescriptorProto.Builder keyBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("key")
          .setNumber(1).setLabel(Label.LABEL_OPTIONAL).setType(toType(typeDescriptorInfo.keyType));
      nodeMapBuilder.addField(keyBuilder);

      FieldDescriptorProto.Builder valueBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("value")
          .setNumber(2);
      boolean isParent = typeDescriptorInfo.valueType == null;
      valueBuilder.setLabel(isParent ? Label.LABEL_REPEATED : Label.LABEL_OPTIONAL);
      Type type = isParent ? Type.TYPE_MESSAGE : toType(typeDescriptorInfo.valueType);
      valueBuilder.setType(type);
      if (type == Type.TYPE_MESSAGE)
        valueBuilder.setTypeName(isParent ? "NodeMapValueFieldMapEntry" : "LeafMapValueFieldMapEntry");
      nodeMapBuilder.addField(valueBuilder);

      FileDescriptorProto nodeMapFileProto = FileDescriptorProto.getDefaultInstance().toBuilder().setName(typeDescriptorInfo.fileName)
          .addMessageType(nodeMapBuilder).build();

      try {
        FileDescriptor nodeMapFileDescriptor = FileDescriptor.buildFrom(nodeMapFileProto, new FileDescriptor[] {}, true);
        return nodeMapFileDescriptor.getMessageTypes().get(0);
      } catch (Exception e) {
        throw new RuntimeException("Error occurred when creating map descriptor.", e);
      }
    }

    public static Type toType(FieldType fieldType) {
      if (fieldType == null)
        return null;

      final String TYPE_NAME_PREFIX = "TYPE_";
      String typeName = TYPE_NAME_PREFIX + fieldType.name();
      return Type.valueOf(typeName);
    }

    private static class TypeDescriptorInfo {
      public String typeName;
      public String fileName;
      public FieldType keyType;
      public FieldType valueType;

      public TypeDescriptorInfo(String typeName, String fileName, FieldType keyType) {
        this(typeName, fileName, keyType, null);
      }

      public TypeDescriptorInfo(String typeName, String fileName, FieldType keyType, FieldType valueType) {
        this.typeName = typeName;
        this.fileName = fileName;
        this.keyType = keyType;
        this.valueType = valueType;
      }
    }

  }

}
