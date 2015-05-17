package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Denis on 13-May-15.
 */
public class SerializableTypeConverter
        extends AbstractTypeConverter<Serializable, byte[]>
        implements FieldTypeMappingRegistrar.Matcher {

    public static SerializableTypeConverter INSTANCE = new SerializableTypeConverter();

    public SerializableTypeConverter() {
        super(byte[].class);
    }

    @Override
    protected Serializable fromInbound(byte[] bytes) {
        try {
            return deserialize(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected byte[] toOutbound(Serializable value) {
        try {
            return serialize(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Serializable deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bytes)) {
            try (ObjectInput objStream = new ObjectInputStream(byteArrayStream)) {
                return (Serializable)objStream.readObject();
            }
        }
    }

    private byte[] serialize(Serializable obj) throws IOException {
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
            try (ObjectOutput objStream = new ObjectOutputStream(byteArrayStream)) {
                objStream.writeObject(obj);
            }
            return byteArrayStream.toByteArray();
        }
    }

    @Override
    public boolean match(Field field) {
        return Serializable.class.isAssignableFrom(field.metaInfo().getValueType());
    }
}
