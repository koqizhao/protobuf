package com.google.protobuf;

import java.io.IOException;

import com.google.protobuf.WireFormat.FieldType;

/**
 * @author koqizhao
 *
 *         Jan 2, 2018
 */
@SuppressWarnings("unchecked")
class SerializerUtil {

  static Object defaultValue(FieldType fieldType) {
    switch (fieldType) {
      case BOOL:
        return false;
      case STRING:
        return "";
      case INT32:
      case FIXED32:
      case UINT32:
      case SFIXED32:
      case SINT32:
      case INT64:
      case UINT64:
      case FIXED64:
      case SFIXED64:
      case SINT64:
        return 0;
      case DOUBLE:
      case FLOAT:
        return 0.0;
      default:
        return null;
    }
  }

  static boolean isSupportedKeyType(FieldType fieldType) {
    switch (fieldType) {
      case BOOL:
      case STRING:
      case INT32:
      case FIXED32:
      case UINT32:
      case SFIXED32:
      case SINT32:
      case INT64:
      case UINT64:
      case FIXED64:
      case SFIXED64:
      case SINT64:
        return true;
      default:
        return false;
    }
  }

  static boolean isSupportedValueType(FieldType fieldType) {
    switch (fieldType) {
      case GROUP:
        return false;
      default:
        return true;
    }
  }

  static <T> T parseField(CodedInputStream input, FieldType type, T value) throws IOException {
    switch (type) {
      case MESSAGE:
        MessageLite.Builder subBuilder = ((MessageLite) value).toBuilder();
        input.readMessage(subBuilder, ExtensionRegistryLite.getEmptyRegistry());
        return (T) subBuilder.buildPartial();
      case ENUM:
        return (T) (java.lang.Integer) input.readEnum();
      case GROUP:
        throw new RuntimeException("Groups are not allowed.");
      default:
        return (T) FieldSet.readPrimitiveField(input, type, true);
    }
  }

}
