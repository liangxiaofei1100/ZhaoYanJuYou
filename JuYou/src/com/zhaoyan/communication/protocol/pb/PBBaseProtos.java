// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: message_type.proto

package com.zhaoyan.communication.protocol.pb;

public final class PBBaseProtos {
  private PBBaseProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf enum {@code com.zhaoyan.juyou.PBType}
   */
  public enum PBType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>LOGIN_REQUEST = 100;</code>
     */
    LOGIN_REQUEST(0, 100),
    /**
     * <code>LOGIN_RESPOND = 101;</code>
     */
    LOGIN_RESPOND(1, 101),
    /**
     * <code>LOGOUT_CLIENT = 102;</code>
     */
    LOGOUT_CLIENT(2, 102),
    /**
     * <code>LOGOUT_SERVER = 103;</code>
     */
    LOGOUT_SERVER(3, 103),
    /**
     * <code>UPDATE_USER_ID = 200;</code>
     */
    UPDATE_USER_ID(4, 200),
    /**
     * <code>UPDATE_USER_INFO = 201;</code>
     */
    UPDATE_USER_INFO(5, 201),
    /**
     * <code>SEND_MESSAGE_TO_SINGLE = 300;</code>
     */
    SEND_MESSAGE_TO_SINGLE(6, 300),
    /**
     * <code>SEND_MESSAGE_TO_ALL = 301;</code>
     */
    SEND_MESSAGE_TO_ALL(7, 301),
    /**
     * <code>FILE_TRANSPORT_SEND = 400;</code>
     */
    FILE_TRANSPORT_SEND(8, 400),
    /**
     * <code>FILE_TRANSPORT_CANCEL_SEND = 401;</code>
     */
    FILE_TRANSPORT_CANCEL_SEND(9, 401),
    /**
     * <code>FILE_TRANSPORT_CANCEL_RECEIVE = 402;</code>
     */
    FILE_TRANSPORT_CANCEL_RECEIVE(10, 402),
    /**
     * <code>HEART_BEAT = 500;</code>
     */
    HEART_BEAT(11, 500),
    ;

    /**
     * <code>LOGIN_REQUEST = 100;</code>
     */
    public static final int LOGIN_REQUEST_VALUE = 100;
    /**
     * <code>LOGIN_RESPOND = 101;</code>
     */
    public static final int LOGIN_RESPOND_VALUE = 101;
    /**
     * <code>LOGOUT_CLIENT = 102;</code>
     */
    public static final int LOGOUT_CLIENT_VALUE = 102;
    /**
     * <code>LOGOUT_SERVER = 103;</code>
     */
    public static final int LOGOUT_SERVER_VALUE = 103;
    /**
     * <code>UPDATE_USER_ID = 200;</code>
     */
    public static final int UPDATE_USER_ID_VALUE = 200;
    /**
     * <code>UPDATE_USER_INFO = 201;</code>
     */
    public static final int UPDATE_USER_INFO_VALUE = 201;
    /**
     * <code>SEND_MESSAGE_TO_SINGLE = 300;</code>
     */
    public static final int SEND_MESSAGE_TO_SINGLE_VALUE = 300;
    /**
     * <code>SEND_MESSAGE_TO_ALL = 301;</code>
     */
    public static final int SEND_MESSAGE_TO_ALL_VALUE = 301;
    /**
     * <code>FILE_TRANSPORT_SEND = 400;</code>
     */
    public static final int FILE_TRANSPORT_SEND_VALUE = 400;
    /**
     * <code>FILE_TRANSPORT_CANCEL_SEND = 401;</code>
     */
    public static final int FILE_TRANSPORT_CANCEL_SEND_VALUE = 401;
    /**
     * <code>FILE_TRANSPORT_CANCEL_RECEIVE = 402;</code>
     */
    public static final int FILE_TRANSPORT_CANCEL_RECEIVE_VALUE = 402;
    /**
     * <code>HEART_BEAT = 500;</code>
     */
    public static final int HEART_BEAT_VALUE = 500;


    public final int getNumber() { return value; }

    public static PBType valueOf(int value) {
      switch (value) {
        case 100: return LOGIN_REQUEST;
        case 101: return LOGIN_RESPOND;
        case 102: return LOGOUT_CLIENT;
        case 103: return LOGOUT_SERVER;
        case 200: return UPDATE_USER_ID;
        case 201: return UPDATE_USER_INFO;
        case 300: return SEND_MESSAGE_TO_SINGLE;
        case 301: return SEND_MESSAGE_TO_ALL;
        case 400: return FILE_TRANSPORT_SEND;
        case 401: return FILE_TRANSPORT_CANCEL_SEND;
        case 402: return FILE_TRANSPORT_CANCEL_RECEIVE;
        case 500: return HEART_BEAT;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<PBType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<PBType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<PBType>() {
            public PBType findValueByNumber(int number) {
              return PBType.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.zhaoyan.communication.protocol.pb.PBBaseProtos.getDescriptor().getEnumTypes().get(0);
    }

    private static final PBType[] VALUES = values();

    public static PBType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private PBType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.zhaoyan.juyou.PBType)
  }

  public interface PBBaseOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required .com.zhaoyan.juyou.PBType type = 1;
    /**
     * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
     */
    boolean hasType();
    /**
     * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
     */
    com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType getType();

    // optional bytes message = 2;
    /**
     * <code>optional bytes message = 2;</code>
     */
    boolean hasMessage();
    /**
     * <code>optional bytes message = 2;</code>
     */
    com.google.protobuf.ByteString getMessage();
  }
  /**
   * Protobuf type {@code com.zhaoyan.juyou.PBBase}
   */
  public static final class PBBase extends
      com.google.protobuf.GeneratedMessage
      implements PBBaseOrBuilder {
    // Use PBBase.newBuilder() to construct.
    private PBBase(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PBBase(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PBBase defaultInstance;
    public static PBBase getDefaultInstance() {
      return defaultInstance;
    }

    public PBBase getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PBBase(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              int rawValue = input.readEnum();
              com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType value = com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                type_ = value;
              }
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              message_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.zhaoyan.communication.protocol.pb.PBBaseProtos.internal_static_com_zhaoyan_juyou_PBBase_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.zhaoyan.communication.protocol.pb.PBBaseProtos.internal_static_com_zhaoyan_juyou_PBBase_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.class, com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.Builder.class);
    }

    public static com.google.protobuf.Parser<PBBase> PARSER =
        new com.google.protobuf.AbstractParser<PBBase>() {
      public PBBase parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PBBase(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PBBase> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required .com.zhaoyan.juyou.PBType type = 1;
    public static final int TYPE_FIELD_NUMBER = 1;
    private com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType type_;
    /**
     * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
     */
    public boolean hasType() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
     */
    public com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType getType() {
      return type_;
    }

    // optional bytes message = 2;
    public static final int MESSAGE_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString message_;
    /**
     * <code>optional bytes message = 2;</code>
     */
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional bytes message = 2;</code>
     */
    public com.google.protobuf.ByteString getMessage() {
      return message_;
    }

    private void initFields() {
      type_ = com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType.LOGIN_REQUEST;
      message_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeEnum(1, type_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, message_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, message_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.zhaoyan.juyou.PBBase}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBaseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.zhaoyan.communication.protocol.pb.PBBaseProtos.internal_static_com_zhaoyan_juyou_PBBase_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.zhaoyan.communication.protocol.pb.PBBaseProtos.internal_static_com_zhaoyan_juyou_PBBase_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.class, com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.Builder.class);
      }

      // Construct using com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        type_ = com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType.LOGIN_REQUEST;
        bitField0_ = (bitField0_ & ~0x00000001);
        message_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.zhaoyan.communication.protocol.pb.PBBaseProtos.internal_static_com_zhaoyan_juyou_PBBase_descriptor;
      }

      public com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase getDefaultInstanceForType() {
        return com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.getDefaultInstance();
      }

      public com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase build() {
        com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase buildPartial() {
        com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase result = new com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.type_ = type_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.message_ = message_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase) {
          return mergeFrom((com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase other) {
        if (other == com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase.getDefaultInstance()) return this;
        if (other.hasType()) {
          setType(other.getType());
        }
        if (other.hasMessage()) {
          setMessage(other.getMessage());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasType()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBBase) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required .com.zhaoyan.juyou.PBType type = 1;
      private com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType type_ = com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType.LOGIN_REQUEST;
      /**
       * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
       */
      public boolean hasType() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
       */
      public com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType getType() {
        return type_;
      }
      /**
       * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
       */
      public Builder setType(com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .com.zhaoyan.juyou.PBType type = 1;</code>
       */
      public Builder clearType() {
        bitField0_ = (bitField0_ & ~0x00000001);
        type_ = com.zhaoyan.communication.protocol.pb.PBBaseProtos.PBType.LOGIN_REQUEST;
        onChanged();
        return this;
      }

      // optional bytes message = 2;
      private com.google.protobuf.ByteString message_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes message = 2;</code>
       */
      public boolean hasMessage() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional bytes message = 2;</code>
       */
      public com.google.protobuf.ByteString getMessage() {
        return message_;
      }
      /**
       * <code>optional bytes message = 2;</code>
       */
      public Builder setMessage(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        message_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes message = 2;</code>
       */
      public Builder clearMessage() {
        bitField0_ = (bitField0_ & ~0x00000002);
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.zhaoyan.juyou.PBBase)
    }

    static {
      defaultInstance = new PBBase(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.zhaoyan.juyou.PBBase)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_zhaoyan_juyou_PBBase_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_zhaoyan_juyou_PBBase_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022message_type.proto\022\021com.zhaoyan.juyou\"" +
      "B\n\006PBBase\022\'\n\004type\030\001 \002(\0162\031.com.zhaoyan.ju" +
      "you.PBType\022\017\n\007message\030\002 \001(\014*\247\002\n\006PBType\022\021" +
      "\n\rLOGIN_REQUEST\020d\022\021\n\rLOGIN_RESPOND\020e\022\021\n\r" +
      "LOGOUT_CLIENT\020f\022\021\n\rLOGOUT_SERVER\020g\022\023\n\016UP" +
      "DATE_USER_ID\020\310\001\022\025\n\020UPDATE_USER_INFO\020\311\001\022\033" +
      "\n\026SEND_MESSAGE_TO_SINGLE\020\254\002\022\030\n\023SEND_MESS" +
      "AGE_TO_ALL\020\255\002\022\030\n\023FILE_TRANSPORT_SEND\020\220\003\022" +
      "\037\n\032FILE_TRANSPORT_CANCEL_SEND\020\221\003\022\"\n\035FILE" +
      "_TRANSPORT_CANCEL_RECEIVE\020\222\003\022\017\n\nHEART_BE",
      "AT\020\364\003B5\n%com.zhaoyan.communication.proto" +
      "col.pbB\014PBBaseProtos"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_zhaoyan_juyou_PBBase_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_zhaoyan_juyou_PBBase_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_zhaoyan_juyou_PBBase_descriptor,
              new java.lang.String[] { "Type", "Message", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
