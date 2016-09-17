package au.id.vanlaatum.botter.connector.weather.openweather.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.Test;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;

public class ForecastTest {
  public static final String JSON = "{\n" +
      "  \"city\": {\n" +
      "    \"id\": 2078025,\n" +
      "    \"name\": \"Adelaide\",\n" +
      "    \"coord\": {\n" +
      "      \"lon\": 138.600006,\n" +
      "      \"lat\": -34.933331\n" +
      "    },\n" +
      "    \"country\": \"AU\",\n" +
      "    \"population\": 0\n" +
      "  },\n" +
      "  \"cod\": 200,\n" +
      "  \"message\": \"0.0286\",\n" +
      "  \"cnt\": 7,\n" +
      "  \"list\": [\n" +
      "    {\n" +
      "      \"dt\": 1473645600,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 10.13,\n" +
      "        \"min\": 8.15,\n" +
      "        \"max\": 10.13,\n" +
      "        \"night\": 8.15,\n" +
      "        \"eve\": 10.13,\n" +
      "        \"morn\": 10.13\n" +
      "      },\n" +
      "      \"pressure\": 1014.69,\n" +
      "      \"humidity\": 99,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 501,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"moderate rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 5.21,\n" +
      "      \"deg\": 149,\n" +
      "      \"clouds\": 92,\n" +
      "      \"rain\": 6.15\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1473732000,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 7.82,\n" +
      "        \"min\": 7.48,\n" +
      "        \"max\": 8.32,\n" +
      "        \"night\": 7.65,\n" +
      "        \"eve\": 7.89,\n" +
      "        \"morn\": 7.62\n" +
      "      },\n" +
      "      \"pressure\": 1010.14,\n" +
      "      \"humidity\": 100,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 501,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"moderate rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 4.35,\n" +
      "      \"deg\": 152,\n" +
      "      \"clouds\": 92,\n" +
      "      \"rain\": 5.73\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1473818400,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 11.26,\n" +
      "        \"min\": 6.38,\n" +
      "        \"max\": 11.26,\n" +
      "        \"night\": 6.38,\n" +
      "        \"eve\": 9.24,\n" +
      "        \"morn\": 6.72\n" +
      "      },\n" +
      "      \"pressure\": 1009.34,\n" +
      "      \"humidity\": 98,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 801,\n" +
      "          \"main\": \"Clouds\",\n" +
      "          \"description\": \"few clouds\",\n" +
      "          \"icon\": \"02d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 7.86,\n" +
      "      \"deg\": 247,\n" +
      "      \"clouds\": 12\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1473904800,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 10.59,\n" +
      "        \"min\": 5.09,\n" +
      "        \"max\": 12.07,\n" +
      "        \"night\": 9.12,\n" +
      "        \"eve\": 12.07,\n" +
      "        \"morn\": 5.09\n" +
      "      },\n" +
      "      \"pressure\": 1022.28,\n" +
      "      \"humidity\": 0,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 500,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"light rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 4.03,\n" +
      "      \"deg\": 68,\n" +
      "      \"clouds\": 13,\n" +
      "      \"rain\": 1.37\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1473991200,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 11.82,\n" +
      "        \"min\": 8.14,\n" +
      "        \"max\": 14.3,\n" +
      "        \"night\": 10.52,\n" +
      "        \"eve\": 14.3,\n" +
      "        \"morn\": 8.14\n" +
      "      },\n" +
      "      \"pressure\": 1013.12,\n" +
      "      \"humidity\": 0,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 500,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"light rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 2.15,\n" +
      "      \"deg\": 288,\n" +
      "      \"clouds\": 0,\n" +
      "      \"rain\": 0.35\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1474077600,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 9.96,\n" +
      "        \"min\": 8.79,\n" +
      "        \"max\": 10.9,\n" +
      "        \"night\": 8.79,\n" +
      "        \"eve\": 9.85,\n" +
      "        \"morn\": 10.9\n" +
      "      },\n" +
      "      \"pressure\": 1013.94,\n" +
      "      \"humidity\": 0,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 501,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"moderate rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 7.92,\n" +
      "      \"deg\": 247,\n" +
      "      \"clouds\": 88,\n" +
      "      \"rain\": 6.37\n" +
      "    },\n" +
      "    {\n" +
      "      \"dt\": 1474164000,\n" +
      "      \"temp\": {\n" +
      "        \"day\": 12.36,\n" +
      "        \"min\": 9.57,\n" +
      "        \"max\": 13.4,\n" +
      "        \"night\": 10.09,\n" +
      "        \"eve\": 13.4,\n" +
      "        \"morn\": 9.57\n" +
      "      },\n" +
      "      \"pressure\": 1024.74,\n" +
      "      \"humidity\": 0,\n" +
      "      \"weather\": [\n" +
      "        {\n" +
      "          \"id\": 500,\n" +
      "          \"main\": \"Rain\",\n" +
      "          \"description\": \"light rain\",\n" +
      "          \"icon\": \"10d\"\n" +
      "        }\n" +
      "      ],\n" +
      "      \"speed\": 5.8,\n" +
      "      \"deg\": 241,\n" +
      "      \"clouds\": 26,\n" +
      "      \"rain\": 0.83\n" +
      "    }\n" +
      "  ]\n" +
      "}";

  @Test
  public void name () throws Exception {
    ObjectMapper mapper = new ObjectMapper ();
    mapper.enable ( SerializationFeature.INDENT_OUTPUT );
    final Forecast currentWeather = mapper.readValue ( JSON, Forecast.class );
    assertJsonEquals ( JSON, mapper.valueToTree ( currentWeather ), when ( Option.TREATING_NULL_AS_ABSENT ) );
  }
}
