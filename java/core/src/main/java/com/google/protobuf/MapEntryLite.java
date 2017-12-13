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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.WireFormat.FieldType;

/**
 * Implements the lite version of map entry messages.
 *
 * This class serves as an utility class to help do serialization/parsing of
 * map entries. It's used in generated code and also in the full version
 * MapEntry message.
 *
 * Protobuf internal. Users shouldn't use.
 */
@SuppressWarnings({ "unchecked" })
public class MapEntryLite<K, V> {

  static class Metadata<K, V> {
    public final WireFormat.FieldType keyType;
    public final K defaultKey;
    public final WireFormat.FieldType valueType;
    public final V defaultValue;
    public final boolean isNested;

    public Metadata(
        WireFormat.FieldType keyType, K defaultKey,
        WireFormat.FieldType valueType, V defaultValue) {
      this.keyType = keyType;
      this.defaultKey = defaultKey;
      this.valueType = valueType;
      this.defaultValue = defaultValue;
      isNested = determineIsNested();
    }
    
    protected boolean determineIsNested() {
      return defaultValue != null && defaultValue instanceof MapEntryLite;
    }

  }

  private static final int KEY_FIELD_NUMBER = 1;
  private static final int VALUE_FIELD_NUMBER = 2;

  private final Metadata<K, V> metadata;
  private final K key;
  private final V value;
  private final List<V> values;

  /** Creates a default MapEntryLite message instance. */
  private MapEntryLite(
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    this.metadata = new Metadata<K, V>(keyType, defaultKey, valueType, defaultValue);
    this.key = defaultKey;
    this.value = defaultValue;
    this.values = metadata.isNested ? new ArrayList<V>() : null;
  }

  /** Creates a new MapEntryLite message. */
  private MapEntryLite(Metadata<K, V> metadata, K key, V value) {
    this(metadata, key, value, null);
  }
  
  private MapEntryLite(Metadata<K, V> metadata, K key, List<V> values) {
    this(metadata, key, null, values);
  }

  private MapEntryLite(Metadata<K, V> metadata, K key, V value, List<V> values) {
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

  /**
   * Creates a default MapEntryLite message instance.
   *
   * This method is used by generated code to create the default instance for
   * a map entry message. The created default instance should be used to create
   * new map entry messages of the same type. For each map entry message, only
   * one default instance should be created.
   */
  public static <K, V> MapEntryLite<K, V> newDefaultInstance(
      WireFormat.FieldType keyType, K defaultKey,
      WireFormat.FieldType valueType, V defaultValue) {
    return new MapEntryLite<K, V>(keyType, defaultKey, valueType, defaultValue);
  }

  static <K, V> void writeTo(CodedOutputStream output, Metadata<K, V> metadata, K key, V value)
      throws IOException {
    FieldSet.writeElement(output, metadata.keyType, KEY_FIELD_NUMBER, key);
    FieldSet.writeElement(output, metadata.valueType, VALUE_FIELD_NUMBER, value);
  }

  static <K, V> void writeTo(CodedOutputStream output, Metadata<K, V> metadata, K key, List<V> values)
      throws IOException {
    FieldSet.writeElement(output, metadata.keyType, KEY_FIELD_NUMBER, key);

    for (Object value : values) {
      FieldSet.writeElement(output, metadata.valueType, VALUE_FIELD_NUMBER, value);
    }
  }

  static <K, V> int computeSerializedSize(Metadata<K, V> metadata, K key, V value) {
    return FieldSet.computeElementSize(metadata.keyType, KEY_FIELD_NUMBER, key)
        + FieldSet.computeElementSize(metadata.valueType, VALUE_FIELD_NUMBER, value);
  }
  
  static <K, V> int computeSerializedSize(Metadata<K, V> metadata, K key, List<V> values) {
    int serializedSize = 0;
    serializedSize += FieldSet.computeElementSize(metadata.keyType, KEY_FIELD_NUMBER, key);
    for (Object value : values) {
      serializedSize += FieldSet.computeElementSize(metadata.valueType, VALUE_FIELD_NUMBER, value);
    }

    return serializedSize;
  }

  static <T> T parseField(
      CodedInputStream input, ExtensionRegistryLite extensionRegistry,
      FieldType type, T value) throws IOException {
    switch (type) {
      case MESSAGE:
        MessageLite.Builder subBuilder = ((MessageLite) value).toBuilder();
        input.readMessage(subBuilder, extensionRegistry);
        return (T) subBuilder.buildPartial();
      case ENUM:
        return (T) (java.lang.Integer) input.readEnum();
      case GROUP:
        throw new RuntimeException("Groups are not allowed in maps.");
      default:
        return (T) FieldSet.readPrimitiveField(input, type, true);
    }
  }

  /**
   * Serializes the provided key and value as though they were wrapped by a {@link MapEntryLite}
   * to the output stream. This helper method avoids allocation of a {@link MapEntryLite}
   * built with a key and value and is called from generated code directly.
   */
  public void serializeTo(CodedOutputStream output, int fieldNumber, K key, V value)
      throws IOException {
    output.writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
    output.writeUInt32NoTag(computeSerializedSize(metadata, key, value));
    writeTo(output, metadata, key, value);
  }
  
  public void serializeTo(CodedOutputStream output, int fieldNumber, K key, List<V> values)
      throws IOException {
    output.writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
    output.writeUInt32NoTag(computeSerializedSize(metadata, key, values));
    writeTo(output, metadata, key, values);
  }

  /**
   * Computes the message size for the provided key and value as though they were wrapped
   * by a {@link MapEntryLite}. This helper method avoids allocation of a {@link MapEntryLite}
   * built with a key and value and is called from generated code directly.
   */
  public int computeMessageSize(int fieldNumber, K key, V value) {
    return CodedOutputStream.computeTagSize(fieldNumber)
        + CodedOutputStream.computeLengthDelimitedFieldSize(
            computeSerializedSize(metadata, key, value));
  }
  
  public int computeMessageSize(int fieldNumber, K key, List<V> values) {
    return CodedOutputStream.computeTagSize(fieldNumber)
        + CodedOutputStream.computeLengthDelimitedFieldSize(
            computeSerializedSize(metadata, key, values));
  }

  /**
   * Parses an entry off of the input as a {@link Map.Entry}. This helper requires an allocation
   * so using {@link #parseInto} is preferred if possible.
   */
  public Map.Entry<K, V> parseEntry(ByteString bytes, ExtensionRegistryLite extensionRegistry)
      throws IOException {
    return parseEntry(bytes.newCodedInput(), metadata, extensionRegistry);
  }

  static <K, V> Map.Entry<K, V> parseEntry(
      CodedInputStream input, Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry)
          throws IOException{
    K key = metadata.defaultKey;
    V value = metadata.defaultValue;
    while (true) {
      int tag = input.readTag();
      if (tag == 0) {
        break;
      }
      if (tag == WireFormat.makeTag(KEY_FIELD_NUMBER, metadata.keyType.getWireType())) {
        key = parseField(input, extensionRegistry, metadata.keyType, metadata.defaultKey);
      } else if (tag == WireFormat.makeTag(VALUE_FIELD_NUMBER, metadata.valueType.getWireType())) {
        value = parseField(input, extensionRegistry, metadata.valueType, metadata.defaultValue);
      } else {
        if (!input.skipField(tag)) {
          break;
        }
      }
    }
    return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
  }
  
  public Map.Entry<K, List<V>> parseEntryForNested(ByteString bytes, ExtensionRegistryLite extensionRegistry)
      throws IOException {
    return parseEntryForNested(bytes.newCodedInput(), metadata, extensionRegistry);
  }

  static <K, V> Map.Entry<K, List<V>> parseEntryForNested(
      CodedInputStream input, Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry)
          throws IOException{
    K key = metadata.defaultKey;
    List<V> values = null;
    while (true) {
      int tag = input.readTag();
      if (tag == 0) {
        break;
      }
      if (tag == WireFormat.makeTag(KEY_FIELD_NUMBER, metadata.keyType.getWireType())) {
        key = parseField(input, extensionRegistry, metadata.keyType, metadata.defaultKey);
      } else if (tag == WireFormat.makeTag(VALUE_FIELD_NUMBER, metadata.valueType.getWireType())) {
        V value = parseField(input, extensionRegistry, metadata.valueType, metadata.defaultValue);
        if (values == null)
          values = new ArrayList<V>();
        values.add(value);
      } else {
        if (!input.skipField(tag)) {
          break;
        }
      }
    }
    return new AbstractMap.SimpleImmutableEntry<K, List<V>>(key, values);
  }

  /**
   * Parses an entry off of the input into the map. This helper avoids allocaton of a
   * {@link MapEntryLite} by parsing directly into the provided {@link MapFieldLite}.
   */
  public void parseInto(
      MapFieldLite<K, V> map, CodedInputStream input, ExtensionRegistryLite extensionRegistry)
          throws IOException {
    int length = input.readRawVarint32();
    final int oldLimit = input.pushLimit(length);
    K key = metadata.defaultKey;
    V value = metadata.defaultValue;

    while (true) {
      int tag = input.readTag();
      if (tag == 0) {
        break;
      }
      if (tag == WireFormat.makeTag(KEY_FIELD_NUMBER, metadata.keyType.getWireType())) {
        key = parseField(input, extensionRegistry, metadata.keyType, metadata.defaultKey);
      } else if (tag == WireFormat.makeTag(VALUE_FIELD_NUMBER, metadata.valueType.getWireType())) {
        value = parseField(input, extensionRegistry, metadata.valueType, metadata.defaultValue);
      } else {
        if (!input.skipField(tag)) {
          break;
        }
      }
    }

    input.checkLastTagWas(0);
    input.popLimit(oldLimit);
    map.put(key, value);
  }
  
  public void parseIntoForNested(
      MapFieldLite<K, List<V>> map, CodedInputStream input, ExtensionRegistryLite extensionRegistry)
          throws IOException {
    int length = input.readRawVarint32();
    final int oldLimit = input.pushLimit(length);
    K key = metadata.defaultKey;
    List<V> values = null;

    while (true) {
      int tag = input.readTag();
      if (tag == 0) {
        break;
      }
      if (tag == WireFormat.makeTag(KEY_FIELD_NUMBER, metadata.keyType.getWireType())) {
        key = parseField(input, extensionRegistry, metadata.keyType, metadata.defaultKey);
      } else if (tag == WireFormat.makeTag(VALUE_FIELD_NUMBER, metadata.valueType.getWireType())) {
        V value = parseField(input, extensionRegistry, metadata.valueType, metadata.defaultValue);
        if (values == null)
          values = new ArrayList<V>();
        values.add(value);
      } else {
        if (!input.skipField(tag)) {
          break;
        }
      }
    }
    input.checkLastTagWas(0);
    input.popLimit(oldLimit);
    map.put(key, values);
  }

}
