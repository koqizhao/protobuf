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
        WireFormat.FieldType valueType,
        boolean isNested) {
      super(keyType, defaultInstance.key, valueType, defaultInstance.value, isNested);
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
  private final V value;
  private final List<V> values;
  private final Metadata<K, V> metadata;
  
  

  /** Create a default MapEntry instance. */
  private MapEntry(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
      this(descriptor, keyType, defaultKey, valueType, defaultValue, false);
  }

  private MapEntry(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue,
      boolean isNested) {
    this.key = defaultKey;
    this.value = defaultValue;
    this.values = null;
    this.metadata = new Metadata<K, V>(descriptor, this, keyType, valueType, isNested);
  }

  /** Create a MapEntry with the provided key and value. */
  private MapEntry(Metadata metadata, K key, V value) {
    this(metadata, key, value, null);
  }
  
  /** Create a MapEntry with the provided key and values. */
  private MapEntry(Metadata metadata, K key, List<V> values) {
    this(metadata, key, null, values);
  }

  private MapEntry(Metadata metadata, K key, V value, List<V> values) {
    this.key = key;
    this.value = value;
    this.values = values;
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
      if (metadata.isNested) {
        Map.Entry<K, List<V>> entry = MapEntryLite.parseEntryForNested(input, metadata, extensionRegistry);
        this.key = entry.getKey();
        this.value = null;
        this.values = entry.getValue();
      } else {
        Map.Entry<K, V> entry = MapEntryLite.parseEntry(input, metadata, extensionRegistry);
        this.key = entry.getKey();
        this.value = entry.getValue();
        this.values = null;
      }
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
      descriptor = MapDescriptors.newNodeMapDescriptor(keyType);
    else
      descriptor = MapDescriptors.newLeafMapDescriptor(keyType, valueType);
    return newDefaultInstance(descriptor, keyType, defaultKey, valueType, defaultValue);
  }

  public static <K, V> MapEntry<K, V> newDefaultInstance(
      Descriptor descriptor,
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    boolean isNested = defaultValue != null && defaultValue instanceof MapEntry;
    return new MapEntry<K, V>(descriptor, keyType, defaultKey, valueType, defaultValue, isNested);
  }
  
  public boolean isNested() {
    return metadata.isNested;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public List<V> getValues() {
    return values;
  }
  
  public Map getValueMap() {
    if (!isNested()) {
      throw new RuntimeException("Not a nested map entry.");
    }

    Map map = new HashMap();
    for (MapEntry entry : (List<MapEntry>) getValues()) {
      map.put(entry.getKey(), entry.isNested() ? entry.getValueMap() : entry.getValue());
    }
    
    return map;
  }

  private volatile int cachedSerializedSize = -1;

  @Override
  public int getSerializedSize() {
    if (cachedSerializedSize != -1) {
      return cachedSerializedSize;
    }

    if (isNested()) {
      int size = MapEntryLite.computeSerializedSize(metadata, key, values);
      cachedSerializedSize = size;
      return size;
    } else {
      int size = MapEntryLite.computeSerializedSize(metadata, key, value);
      cachedSerializedSize = size;
      return size;
    }
  }

  @Override
  public void writeTo(CodedOutputStream output) throws IOException {
    if (isNested())
      MapEntryLite.writeTo(output, metadata, key, values);
    else
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
    return new Builder<K, V>(metadata, key, value, values);
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
    Object result = field.getNumber() == 1 ? getKey() : (isNested() ? getValues() : getValue());
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

    return (List<?>) getField(field);
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
    private V value;
    private List<V> values;

    private Builder(Metadata<K, V> metadata) {
      this(metadata, metadata.defaultKey, metadata.defaultValue);
    }

    private Builder(Metadata<K, V> metadata, K key, V value) {
      this(metadata, key, value, null);
    }

    private Builder(Metadata<K, V> metadata, K key, V value, List<V> values) {
      this.metadata = metadata;
      this.key = key;
      this.value = value;
      this.values = values;
    }
    
    public boolean isNested() {
      return metadata.isNested;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    public List<V> getValues() {
      return values;
    }

    public Builder<K, V> setKey(K key) {
      this.key = key;
      return this;
    }

    public Builder<K, V> clearKey() {
      this.key = metadata.defaultKey;
      return this;
    }

    public Builder<K, V> setValue(V value) {
      this.value = value;
      return this;
    }

    public Builder<K, V> clearValue() {
      this.value = metadata.defaultValue;
      return this;
    }

    public Builder<K, V> setValues(List<V> values) {
      this.values = values;
      return this;
    }

    public Builder<K, V> clearValues() {
      this.values = null;
      return this;
    }
    
    public Map getValueMap() {
      if (!isNested()) {
        throw new RuntimeException("Not a nested map entry.");
      }

      Map map = new HashMap();
      for (MapEntry entry : (List<MapEntry>) getValues()) {
        map.put(entry.getKey(), entry.isNested() ? entry.getValueMap() : entry.getValue());
      }
      
      return map;
    }
    
    public Builder<K, V> setValueMap(Map valueMap) {
      if (!isNested()) {
        throw new RuntimeException("Not a nested map entry.");
      }
      
      if (valueMap == null || valueMap.isEmpty())
        setValues(null);
      else {
        MapEntry defaultValueEntry = (MapEntry) metadata.defaultValue;
        List<MapEntry> values = new ArrayList<MapEntry>();
        for (Map.Entry e : (Set<Map.Entry>) valueMap.entrySet()) {
          MapEntry.Builder builder = defaultValueEntry.newBuilderForType();
          builder.setKey(e.getKey());
          if (builder.isNested())
            builder.setValueMap((Map) e.getValue());
          else
            builder.setValue(e.getValue());
          values.add(builder.build());
        }
        setValues((List<V>)values);
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
      return new MapEntry<K, V>(metadata, key, value, values);
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
      return ((Message) value).newBuilderForType();
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
        
        if (isNested())
          setValues((List<V>) value);
        else
          setValue((V) value);
      }
      return this;
    }

    @Override
    public Builder<K, V> clearField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      if (field.getNumber() == 1) {
        clearKey();
      } else {
        if (isNested())
          clearValues();
        else
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
      
      List<V> values = getValues();
      values.set(index, (V)value);
      return this;
    }

    @Override
    public Builder<K, V> addRepeatedField(FieldDescriptor field, Object value) {
      if (!metadata.isNested) {
        throw new RuntimeException(
            "There is no repeated field in a map entry message.");
      }
      
      List<V> values = getValues();
      if (values == null) {
        values = new ArrayList<V>();
        setValues(values);
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
      return true;
    }

    @Override
    public Object getField(FieldDescriptor field) {
      checkFieldDescriptor(field);
      Object result = field.getNumber() == 1 ? getKey() : (metadata.isNested ? getValues() : getValue());
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

      return (List<?>) getField(field);
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
      return UnknownFieldSet.getDefaultInstance();
    }

    @Override
    public Builder<K, V> clone() {
      return new Builder<K, V>(metadata, key, value, values);
    }
  }

  private static <V> boolean isInitialized(Metadata metadata, V value) {
    if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE) {
      return ((MessageLite) value).isInitialized();
    }
    return true;
  }

  public static class MapDescriptors {

    public static Descriptor newLeafMapDescriptor(FieldType keyType, FieldType valueType) {
      DescriptorProto.Builder leafMapBuilder = DescriptorProto.getDefaultInstance().toBuilder().setName("LeafMap");

      FieldDescriptorProto.Builder keyBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("key")
          .setNumber(1).setLabel(Label.LABEL_REQUIRED).setType(toType(keyType));
      leafMapBuilder.addField(keyBuilder);

      FieldDescriptorProto.Builder valueBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("value")
          .setNumber(2).setLabel(Label.LABEL_REQUIRED).setType(toType(valueType));
      leafMapBuilder.addField(valueBuilder);

      FileDescriptorProto leafMapFileProto = FileDescriptorProto.getDefaultInstance().toBuilder().setName("LeafMap.proto")
          .addMessageType(leafMapBuilder).build();

      try {
        FileDescriptor leafMapFileDescriptor = FileDescriptor.buildFrom(leafMapFileProto, new FileDescriptor[] {}, true);
        return leafMapFileDescriptor.getMessageTypes().get(0);
      } catch (Exception e) {
        throw new RuntimeException("Error occurred when creating LeafMap descriptor.", e);
      }
    }

    public static Descriptor newNodeMapDescriptor(FieldType keyType) {
      DescriptorProto.Builder nodeMapBuilder = DescriptorProto.getDefaultInstance().toBuilder().setName("NodeMap");

      FieldDescriptorProto.Builder keyBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("key")
          .setNumber(1).setLabel(Label.LABEL_REQUIRED).setType(toType(keyType));
      nodeMapBuilder.addField(keyBuilder);

      FieldDescriptorProto.Builder valueBuilder = FieldDescriptorProto.getDefaultInstance().toBuilder().setName("value")
          .setNumber(2).setLabel(Label.LABEL_REPEATED).setType(Type.TYPE_MESSAGE).setTypeName("LeafMap");
      nodeMapBuilder.addField(valueBuilder);

      FileDescriptorProto nodeMapFileProto = FileDescriptorProto.getDefaultInstance().toBuilder().setName("NodeMap.proto")
          .addMessageType(nodeMapBuilder).build();

      try {
        FileDescriptor nodeMapFileDescriptor = FileDescriptor.buildFrom(nodeMapFileProto, new FileDescriptor[] {}, true);
        return nodeMapFileDescriptor.getMessageTypes().get(0);
      } catch (Exception e) {
        throw new RuntimeException("Error occurred when creating NodeMap descriptor.", e);
      }
    }

    public static Type toType(FieldType fieldType) {
      if (fieldType == null)
        return null;

      final String TYPE_NAME_PREFIX = "TYPE_";
      String typeName = TYPE_NAME_PREFIX + fieldType.name();
      return Type.valueOf(typeName);
    }
   
  }

}
