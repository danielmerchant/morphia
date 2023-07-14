package dev.morphia.aggregation.codecs.stages;

import dev.morphia.aggregation.stages.Count;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

public class CountCodec extends StageCodec<Count> {
    @Override
    public Class<Count> getEncoderClass() {
        return Count.class;
    }

    @Override
    protected void encodeStage(BsonWriter writer, Count value, EncoderContext encoderContext) {
        writer.writeString(value.getName());
    }
}
