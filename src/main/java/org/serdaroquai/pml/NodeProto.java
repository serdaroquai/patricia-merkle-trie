// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: node.proto

package org.serdaroquai.pml;

public final class NodeProto {
  private NodeProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface TreeNodeOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.serdaroquai.pml.TreeNode)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated bytes array = 1;</code>
     */
    java.util.List<com.google.protobuf.ByteString> getArrayList();
    /**
     * <code>repeated bytes array = 1;</code>
     */
    int getArrayCount();
    /**
     * <code>repeated bytes array = 1;</code>
     */
    com.google.protobuf.ByteString getArray(int index);
  }
  /**
   * Protobuf type {@code org.serdaroquai.pml.TreeNode}
   */
  public  static final class TreeNode extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.serdaroquai.pml.TreeNode)
      TreeNodeOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use TreeNode.newBuilder() to construct.
    private TreeNode(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private TreeNode() {
      array_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private TreeNode(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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
            case 10: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                array_ = new java.util.ArrayList<com.google.protobuf.ByteString>();
                mutable_bitField0_ |= 0x00000001;
              }
              array_.add(input.readBytes());
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          array_ = java.util.Collections.unmodifiableList(array_); // C
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.serdaroquai.pml.NodeProto.internal_static_org_serdaroquai_pml_TreeNode_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.serdaroquai.pml.NodeProto.internal_static_org_serdaroquai_pml_TreeNode_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.serdaroquai.pml.NodeProto.TreeNode.class, org.serdaroquai.pml.NodeProto.TreeNode.Builder.class);
    }

    public static final int ARRAY_FIELD_NUMBER = 1;
    private java.util.List<com.google.protobuf.ByteString> array_;
    /**
     * <code>repeated bytes array = 1;</code>
     */
    public java.util.List<com.google.protobuf.ByteString>
        getArrayList() {
      return array_;
    }
    /**
     * <code>repeated bytes array = 1;</code>
     */
    public int getArrayCount() {
      return array_.size();
    }
    /**
     * <code>repeated bytes array = 1;</code>
     */
    public com.google.protobuf.ByteString getArray(int index) {
      return array_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < array_.size(); i++) {
        output.writeBytes(1, array_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < array_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeBytesSizeNoTag(array_.get(i));
        }
        size += dataSize;
        size += 1 * getArrayList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.serdaroquai.pml.NodeProto.TreeNode)) {
        return super.equals(obj);
      }
      org.serdaroquai.pml.NodeProto.TreeNode other = (org.serdaroquai.pml.NodeProto.TreeNode) obj;

      if (!getArrayList()
          .equals(other.getArrayList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (getArrayCount() > 0) {
        hash = (37 * hash) + ARRAY_FIELD_NUMBER;
        hash = (53 * hash) + getArrayList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.serdaroquai.pml.NodeProto.TreeNode parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.serdaroquai.pml.NodeProto.TreeNode prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.serdaroquai.pml.TreeNode}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.serdaroquai.pml.TreeNode)
        org.serdaroquai.pml.NodeProto.TreeNodeOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.serdaroquai.pml.NodeProto.internal_static_org_serdaroquai_pml_TreeNode_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.serdaroquai.pml.NodeProto.internal_static_org_serdaroquai_pml_TreeNode_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.serdaroquai.pml.NodeProto.TreeNode.class, org.serdaroquai.pml.NodeProto.TreeNode.Builder.class);
      }

      // Construct using org.serdaroquai.pml.NodeProto.TreeNode.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        array_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.serdaroquai.pml.NodeProto.internal_static_org_serdaroquai_pml_TreeNode_descriptor;
      }

      @java.lang.Override
      public org.serdaroquai.pml.NodeProto.TreeNode getDefaultInstanceForType() {
        return org.serdaroquai.pml.NodeProto.TreeNode.getDefaultInstance();
      }

      @java.lang.Override
      public org.serdaroquai.pml.NodeProto.TreeNode build() {
        org.serdaroquai.pml.NodeProto.TreeNode result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.serdaroquai.pml.NodeProto.TreeNode buildPartial() {
        org.serdaroquai.pml.NodeProto.TreeNode result = new org.serdaroquai.pml.NodeProto.TreeNode(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          array_ = java.util.Collections.unmodifiableList(array_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.array_ = array_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.serdaroquai.pml.NodeProto.TreeNode) {
          return mergeFrom((org.serdaroquai.pml.NodeProto.TreeNode)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.serdaroquai.pml.NodeProto.TreeNode other) {
        if (other == org.serdaroquai.pml.NodeProto.TreeNode.getDefaultInstance()) return this;
        if (!other.array_.isEmpty()) {
          if (array_.isEmpty()) {
            array_ = other.array_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureArrayIsMutable();
            array_.addAll(other.array_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.serdaroquai.pml.NodeProto.TreeNode parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.serdaroquai.pml.NodeProto.TreeNode) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<com.google.protobuf.ByteString> array_ = java.util.Collections.emptyList();
      private void ensureArrayIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          array_ = new java.util.ArrayList<com.google.protobuf.ByteString>(array_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public java.util.List<com.google.protobuf.ByteString>
          getArrayList() {
        return ((bitField0_ & 0x00000001) != 0) ?
                 java.util.Collections.unmodifiableList(array_) : array_;
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public int getArrayCount() {
        return array_.size();
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public com.google.protobuf.ByteString getArray(int index) {
        return array_.get(index);
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public Builder setArray(
          int index, com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureArrayIsMutable();
        array_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public Builder addArray(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureArrayIsMutable();
        array_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public Builder addAllArray(
          java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
        ensureArrayIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, array_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes array = 1;</code>
       */
      public Builder clearArray() {
        array_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.serdaroquai.pml.TreeNode)
    }

    // @@protoc_insertion_point(class_scope:org.serdaroquai.pml.TreeNode)
    private static final org.serdaroquai.pml.NodeProto.TreeNode DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.serdaroquai.pml.NodeProto.TreeNode();
    }

    public static org.serdaroquai.pml.NodeProto.TreeNode getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<TreeNode>
        PARSER = new com.google.protobuf.AbstractParser<TreeNode>() {
      @java.lang.Override
      public TreeNode parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new TreeNode(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<TreeNode> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<TreeNode> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.serdaroquai.pml.NodeProto.TreeNode getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_serdaroquai_pml_TreeNode_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_serdaroquai_pml_TreeNode_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nnode.proto\022\023org.serdaroquai.pml\"\031\n\010Tre" +
      "eNode\022\r\n\005array\030\001 \003(\014B\013B\tNodeProtob\006proto" +
      "3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_org_serdaroquai_pml_TreeNode_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_serdaroquai_pml_TreeNode_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_serdaroquai_pml_TreeNode_descriptor,
        new java.lang.String[] { "Array", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
