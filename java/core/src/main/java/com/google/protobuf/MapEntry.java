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
        MapEntry<K, V> defaultInstance,
        WireFormat.FieldType keyType,
        WireFormat.FieldType valueType) {
      super(keyType, defaultInstance.key, valueType, defaultInstance.values.get(0));
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

    @Override
    protected boolean determineIsNested() {
      return defaultValue != null && defaultValue instanceof MapEntry;
    }
  }

  private final K key;
  private final List<V> values;
  private final Metadata<K, V> metadata;

  /** Create a default MapEntry instance. */
  private MapEntry(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    this.key = defaultKey;
    this.values = toList(defaultValue);
    this.metadata = new Metadata<K, V>(descriptor, this, keyType, valueType);
  }

  /** Create a MapEntry with the provided key and value. */
  private MapEntry(Metadata metadata, K key, V value) {
    this(metadata, key, toList(value));
  }

  /** Create a MapEntry with the provided key and values. */
  private MapEntry(Metadata metadata, K key, List<V> values) {
    this.key = key;
    this.values = values == null ? new ArrayList<V>() : values;
    this.metadata = metadata;
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
      List<V> temp = metadata.isNested ? (List<V>) entry.getValue() : toList((V) entry.getValue());
      this.values = temp == null ? new ArrayList<V>() : temp;
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
    if (defaultValue != null && defaultValue instanceof MapEntry)
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
    if (isNested())
      throw new UnsupportedOperationException("getValue is not supported for nested map!");

    return values.get(0);
  }
  
  public Map getMapValue() {
    if (!isNested())
      throw new UnsupportedOperationException("getMapValue is not supported for non-nested map!");

    Map map = new HashMap();
    for (MapEntry entry : (List<MapEntry>) values) {
      map.put(entry.getKey(), entry.getRealValue());
    }

    return map;
  }

  public Object getRealValue() {
    return isNested() ? getMapValue() : getValue();
  }

  private Object internalGetValue() {
    return isNested() ? values : values.get(0);
  }

  private volatile int cachedSerializedSize = -1;

  @Override
  public int getSerializedSize() {
    if (cachedSerializedSize != -1) {
      return cachedSerializedSize;
    }

    int size = MapEntryLite.computeSerializedSize(metadata, key, internalGetValue());
    cachedSerializedSize = size;
    return size;
  }

  @Override
  public void writeTo(CodedOutputStream output) throws IOException {
    MapEntryLite.writeTo(output, metadata, key, internalGetValue());
  }

  @Override
  public boolean isInitialized() {
    return isInitialized(metadata, values);
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
    return new Builder<K, V>(metadata, key, new ArrayList<V>(values));
  }

  @Override
  public MapEntry<K, V> getDefaultInstanceForType() {
    return new MapEntry<K, V>(metadata, metadata.defaultKey, metadata.defaultValue);
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
    Object result = field.getNumber() == 1 ? getKey() : internalGetValue();
    // Convert enums to EnumValueDescriptor.
    if (field.getType() == FieldDescriptor.Type.ENUM) {
      result = field.getEnumType().findValueByNumberCreatingIfUnknown(
          (java.lang.Integer) result);
    }
    return result;
  }

  @Override
  public int getRepeatedFieldCount(FieldDescriptor field) {
    List<?> value = getNestedValue(field);
    return value == null ? 0 : value.size();
  }

  @Override
  public Object getRepeatedField(FieldDescriptor field, int index) {
    List<?> value = getNestedValue(field);
    if (value == null) {
      throw new IndexOutOfBoundsException();
    } else {
      return value.get(index);
    }
  }
  
  private List<?> getNestedValue(FieldDescriptor field) {
     if (!isNested()) {
      throw new RuntimeException(
          "There is no repeated field in a map entry message.");
    }

    if (field.getNumber() != 2) {
      throw new RuntimeException(
          "There is no repeated key field in a map entry message.");
    }

    return values;
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
    private List<V> values;

    private Builder(Metadata<K, V> metadata) {
      this(metadata, metadata.defaultKey, metadata.defaultValue);
    }

    private Builder(Metadata<K, V> metadata, K key, V value) {
      this(metadata, key, toList(value));
    }

    private Builder(Metadata<K, V> metadata, K key, List<V> values) {
      this.metadata = metadata;
      this.key = key;
      this.values = values == null ? new ArrayList<V>() : values;
    }
    
    public boolean isNested() {
      return metadata.isNested;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      if (isNested())
        throw new UnsupportedOperationException("getValue is not supported for nested map!");

      return values.get(0);
    }

    public Map getMapValue() {
      if (!isNested())
        throw new UnsupportedOperationException("getMapValue is not supported for non-nested map!");

      Map map = new HashMap();
      for (MapEntry entry : (List<MapEntry>) values) {
        map.put(entry.getKey(), entry.getRealValue());
      }

      return map;
    }

    public Object getRealValue() {
      return isNested() ? getMapValue() : getValue();
    }
    
    private Object internalGetValue() {
      return isNested() ? values : values.get(0);
    }

    public Builder<K, V> setKey(K key) {
      this.key = key;
      return this;
    }

    public Builder<K, V> clearKey() {
      this.key = metadata.defaultKey;
      return this;
    }

    public Builder<K, V> setValue(Object value) {
      this.values.clear();

      if (!isNested()) {
        this.values.add((V)value);
        return this;
      }

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
        this.values.add((V)builder.build());
      }

      return this;
    }

    public Builder<K, V> clearValue() {
      this.values.clear();
      if (!isNested())
        this.values.add(metadata.defaultValue);
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
      return new MapEntry<K, V>(metadata, key, values);
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
      return ((Message) metadata.defaultValue).newBuilderForType();
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

        values.clear();
        if (isNested()) {
          if (value != null)
            values.addAll((List<V>) value);
        }
        else
          values.add((V)value);
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
      if (!isNested()) {
        throw new RuntimeException(
            "There is no repeated field in a map entry message.");
      }

      values.set(index, (V)value);
      return this;
    }

    @Override
    public Builder<K, V> addRepeatedField(FieldDescriptor field, Object value) {
      if (!metadata.isNested) {
        throw new RuntimeException(
            "There is no repeated field in a map entry message.");
      }

      values.add((V)value);
      return this;
    }

    @Override
    public Builder<K, V> setUnknownFields(UnknownFieldSet unknownFields) {
      // Unknown fields are discarded for MapEntry message.
      return this;
    }

    @Override
    public MapEntry<K, V> getDefaultInstanceForType() {
      return new MapEntry<K, V>(metadata, metadata.defaultKey, metadata.defaultValue);
    }

    @Override
    public boolean isInitialized() {
      return MapEntry.isInitialized(metadata, values);
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
      return true;
    }

    @Override
    public Object getField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      Object result = field.getNumber() == 1 ? getKey() : internalGetValue();
      // Convert enums to EnumValueDescriptor.
      if (field.getType() == FieldDescriptor.Type.ENUM) {
        result = field.getEnumType().findValueByNumberCreatingIfUnknown((Integer) result);
      }
      return result;
    }

    @Override
    public int getRepeatedFieldCount(FieldDescriptor field) {
      List<?> value = getNestedValue(field);
      return value == null ? 0 : value.size();
    }

    @Override
    public Object getRepeatedField(FieldDescriptor field, int index) {
      List<?> value = getNestedValue(field);
      if (value == null) {
        throw new IndexOutOfBoundsException();
      } else {
        return value.get(index);
      }
    }
  
    private List<?> getNestedValue(FieldDescriptor field) {
      if (!isNested()) {
        throw new RuntimeException(
            "There is no repeated field in a map entry message.");
      }

      if (field.getNumber() != 2) {
        throw new RuntimeException(
            "There is no repeated key field in a map entry message.");
      }

      return values;
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
      return UnknownFieldSet.getDefaultInstance();
    }

    @Override
    public Builder<K, V> clone() {
      return new Builder<K, V>(metadata, key, new ArrayList<V>(values));
    }
  }

  private static <V> boolean isInitialized(Metadata metadata, List<V> values) {
    if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE) {
      for (V value : values) {
        if(!((MessageLite) value).isInitialized())
          return false;
      }
    }
    return true;
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
      Type type = typeDescriptorInfo.valueType == null ? Type.TYPE_MESSAGE : toType(typeDescriptorInfo.valueType);
      valueBuilder.setType(type);
      if (type == Type.TYPE_MESSAGE)
        valueBuilder.setLabel(Label.LABEL_REPEATED).setTypeName("NodeMapValueFieldMapEntry");
      else
        valueBuilder.setLabel(Label.LABEL_OPTIONAL);
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

  private static <E> List<E> toList(E e) {
    List<E> list = new ArrayList<E>();
    list.add(e);
    return list;
  }

}
