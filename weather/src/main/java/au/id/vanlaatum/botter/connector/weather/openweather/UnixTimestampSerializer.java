package au.id.vanlaatum.botter.connector.weather.openweather;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UnixTimestampSerializer extends JsonSerializer<Date> {
  @Override
  public void serialize ( Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider ) throws IOException {
    jsonGenerator.writeNumber ( TimeUnit.MILLISECONDS.toSeconds ( date.getTime () ) );
  }
}
