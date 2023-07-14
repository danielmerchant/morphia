package dev.morphia.mapping.codec;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.MapCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Defines a provider of codecs for Morphia's types
 */
@SuppressWarnings("unchecked")
public class MorphiaTypesCodecProvider implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    /**
     * Create the provider
     *
     */
    public MorphiaTypesCodecProvider() {
        addCodec(new MorphiaDateCodec());
        addCodec(new MorphiaMapCodec());
        addCodec(new MorphiaLocalDateTimeCodec());
        addCodec(new MorphiaLocalTimeCodec());
        addCodec(new ClassCodec());
        addCodec(new CenterCodec());
        addCodec(new HashMapCodec());
        addCodec(new KeyCodec());
        addCodec(new LocaleCodec());
        addCodec(new ObjectCodec());
        addCodec(new ShapeCodec());
        addCodec(new URICodec());
        addCodec(new ByteWrapperArrayCodec());
        addCodec(new LegacyQueryCodec());
        addCodec(new BitSetCodec());

        List.of(boolean.class, Boolean.class,
                char.class, Character.class,
                double.class, Double.class,
                float.class, Float.class,
                int.class, Integer.class,
                long.class, Long.class,
                short.class, Short.class).forEach(c -> addCodec(new TypedArrayCodec(c)));
    }

    protected <T> void addCodec(Codec<T> codec) {
        codecs.put(codec.getEncoderClass(), codec);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        Codec<T> codec = (Codec<T>) codecs.get(clazz);
        if (codec != null) {
            return codec;
        } else if (AbstractMap.class.isAssignableFrom(clazz)) {
            return (Codec<T>) get(Map.class, registry);
        } else if (clazz.isArray() && !clazz.getComponentType().equals(byte.class)) {
            return (Codec<T>) new ArrayCodec(clazz);
        } else {
            return null;
        }
    }

    private static class HashMapCodec extends MapCodec {
        @Override
        public Class<Map<String, Object>> getEncoderClass() {
            return (Class<Map<String, Object>>) ((Class<?>) HashMap.class);
        }
    }

}
