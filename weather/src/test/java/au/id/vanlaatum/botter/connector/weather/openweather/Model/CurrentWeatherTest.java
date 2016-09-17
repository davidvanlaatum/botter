package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

public class CurrentWeatherTest {
  public static final String JSON = "{\n" +
      "  \"coord\": {\n" +
      "    \"lon\": 138.6,\n" +
      "    \"lat\": -34.93\n" +
      "  },\n" +
      "  \"weather\": [\n" +
      "    {\n" +
      "      \"id\": 801,\n" +
      "      \"main\": \"Clouds\",\n" +
      "      \"description\": \"few clouds\",\n" +
      "      \"icon\": \"02d\"\n" +
      "    }\n" +
      "  ],\n" +
      "  \"base\": \"stations\",\n" +
      "  \"main\": {\n" +
      "    \"temp\": 14.72,\n" +
      "    \"pressure\": 1009,\n" +
      "    \"humidity\": 72,\n" +
      "    \"temp_min\": 14.44,\n" +
      "    \"temp_max\": 15\n" +
      "  },\n" +
      "  \"visibility\": 10000,\n" +
      "  \"wind\": {\n" +
      "    \"speed\": 6.2,\n" +
      "    \"deg\": 230\n" +
      "  },\n" +
      "  \"clouds\": {\n" +
      "    \"all\": 20\n" +
      "  },\n" +
      "  \"dt\": 1472801260,\n" +
      "  \"sys\": {\n" +
      "    \"type\": 1,\n" +
      "    \"id\": 8204,\n" +
      "    \"message\": 0.0112,\n" +
      "    \"country\": \"AU\",\n" +
      "    \"sunrise\": 1472763778,\n" +
      "    \"sunset\": 1472804871\n" +
      "  },\n" +
      "  \"id\": 2078025,\n" +
      "  \"name\": \"Adelaide\",\n" +
      "  \"cod\": 200,\n" +
      "  \"message\": null\n" +
      "}\n";

  @Test
  public void name () throws Exception {
    ObjectMapper mapper = new ObjectMapper ();
    final CurrentWeather currentWeather = mapper.readValue ( JSON, CurrentWeather.class );
    assertJsonEquals ( JSON, mapper.valueToTree ( currentWeather ) );
  }
}
