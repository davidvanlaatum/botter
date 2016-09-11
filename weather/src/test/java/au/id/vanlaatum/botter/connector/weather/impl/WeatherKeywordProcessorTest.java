package au.id.vanlaatum.botter.connector.weather.impl;

import au.id.vanlaatum.botter.api.Channel;
import au.id.vanlaatum.botter.api.Command;
import au.id.vanlaatum.botter.api.Message;
import au.id.vanlaatum.botter.api.Transport;
import au.id.vanlaatum.botter.api.User;
import au.id.vanlaatum.botter.connector.weather.api.WeatherConnector;
import au.id.vanlaatum.botter.connector.weather.api.WeatherDetails;
import au.id.vanlaatum.botter.connector.weather.api.WeatherFetchFailedException;
import au.id.vanlaatum.botter.connector.weather.api.WeatherLocation;
import au.id.vanlaatum.botter.connector.weather.api.WeatherSettings;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static au.id.vanlaatum.botter.connector.weather.impl.Subject.TEMPERATURE;
import static au.id.vanlaatum.botter.connector.weather.impl.Subject.WEATHER;
import static au.id.vanlaatum.botter.connector.weather.impl.TimeConstant.*;
import static junit.framework.TestCase.assertNotNull;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class WeatherKeywordProcessorTest {
  private Command lastCommand;
  private Message lastMessage;
  private Channel lastChannel;
  private User lastUser;
  private Transport lastTransport;

  @Test
  public void parseQuestion () throws Exception {
    WeatherKeywordProcessor processor = new WeatherKeywordProcessor ();
    assertWeatherQuestion ( processor, "what is the temperature today?", TODAY, null, -1, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "whats the temperature tomorrow?", TOMORROW, null, -1, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the weather like today?", TODAY, null, -1, WEATHER, null, null );
    assertWeatherQuestion ( processor, "what is the weather like tomorrow?", TOMORROW, null, -1, WEATHER, null, null );
    assertWeatherQuestion ( processor, "what is tomorrows weather like?", TOMORROW, null, -1, WEATHER, null, null );
    assertWeatherQuestion ( processor, "what is todays weather like?", TODAY, null, -1, WEATHER, null, null );
    assertWeatherQuestion ( processor, "what is the weather like?", TODAY, null, -1, WEATHER, null, null );
    assertWeatherQuestion ( processor, "what is the temperature?", TODAY, null, -1, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the temperature for tuesday?", DOW, null, Calendar.TUESDAY, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the temperature for Tuesday?", DOW, null, Calendar.TUESDAY, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the temperature on 01/01/2000?", DATE, date ( "01/01/2000" ), -1, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the temperature on 2000/1/1?", DATE, date ( "01/01/2000" ), -1, TEMPERATURE, null, null );
    assertWeatherQuestion ( processor, "what is the temperature on 2000/1/1 in Adelaide?", DATE, date ( "01/01/2000" ), -1, TEMPERATURE,
        "Adelaide", null );
    assertWeatherQuestion ( processor, "what is the temperature in sydney", TODAY, null, -1, TEMPERATURE, "sydney", null );
    assertWeatherQuestion ( processor, "what is the temperature in sydney, Australia", TODAY, null, -1, TEMPERATURE, "sydney",
        "Australia" );
    assertSetLocation ( processor, "Set my location to adelaide", "adelaide", null );
  }

  private Date date ( String date ) throws ParseException {
    return new SimpleDateFormat ( "dd/MM/yyyy" ).parse ( date );
  }

  private Question assertWeatherQuestion ( WeatherKeywordProcessor processor, String text, TimeConstant timeType, Date date, int dow,
                                           Subject subject, String city,
                                           String country ) {
    final Question question = processor.parseQuestion ( text );
    assertThat ( text, question, allOf (
        notNullValue ( Question.class ),
        hasProperty ( "type", equalTo ( QuestionType.WEATHER ) ),
        hasProperty ( "timeType", equalTo ( timeType ) ),
        hasProperty ( "date", equalTo ( date ) ),
        hasProperty ( "dow", equalTo ( dow ) ),
        hasProperty ( "subject", equalTo ( subject ) ),
        hasProperty ( "city", equalTo ( city ) ),
        hasProperty ( "country", equalTo ( country ) )
    ) );
    return question;
  }

  private Question assertSetLocation ( WeatherKeywordProcessor processor, String text, String city, String country ) {
    final Question question = processor.parseQuestion ( text );
    assertThat ( text, question, allOf (
        notNullValue ( Question.class ),
        hasProperty ( "type", equalTo ( QuestionType.SETLOCATION ) ),
        hasProperty ( "city", equalTo ( city ) ),
        hasProperty ( "country", equalTo ( country ) )
    ) );
    return question;
  }

  @Test ( expected = RuntimeException.class )
  public void parseQuestionInvalid () {
    WeatherKeywordProcessor processor = new WeatherKeywordProcessor ();
    processor.parseQuestion ( "hi" );
  }

  @Test
  public void checkForKeywords () throws WeatherFetchFailedException {
    PreferencesService preferences = mock ( PreferencesService.class );
    Preferences userPrefs = mock ( Preferences.class );
    when ( preferences.getUserPreferences ( "123" ) ).thenReturn ( userPrefs );
    when ( userPrefs.get ( "city", null ) ).thenReturn ( "Adelaide" );
    when ( userPrefs.get ( "country", null ) ).thenReturn ( "Australia" );
    WeatherConnector connector = mock ( WeatherConnector.class );
    WeatherDetails details = mock ( WeatherDetails.class );
    WeatherKeywordProcessor processor = new WeatherKeywordProcessor ();
    processor.setConnectors ( Collections.singletonList ( connector ) );
    processor.setPreferencesService ( preferences );
    when ( connector.isEnabled () ).thenReturn ( true );
    when ( connector.getCurrentWeather ( any ( WeatherLocation.class ), any ( WeatherSettings.class ) ) ).thenReturn ( details );
    when ( details.getTemperature () ).thenReturn ( new BigDecimal ( 10 ) );
    when ( details.getCity () ).thenReturn ( "Adelaide" );
    when ( details.getCountry () ).thenReturn ( "Australia" );
    assertTrue ( processor.checkForKeywords ( mockCommand ( "what is the temperature" ), null ) );
    verify ( connector ).getCurrentWeather ( refEq ( new CityLocation ( "Australia", "Adelaide" ) ), any ( WeatherSettings.class ) );
    verify ( lastCommand ).reply ( "The temperature is currently 10C in Adelaide, Australia" );
  }

  private Command mockCommand ( boolean direct, String... words ) {
    lastCommand = mock ( Command.class );
    lastMessage = mock ( Message.class );
    lastChannel = mock ( Channel.class );
    lastUser = mock ( User.class );
    lastTransport = mock ( Transport.class );
    when ( lastCommand.getCommandText () ).thenReturn ( join ( words ) );
    when ( lastCommand.getCommandParts () ).thenReturn ( Arrays.asList ( words ) );
    when ( lastCommand.getMessage () ).thenReturn ( lastMessage );
    when ( lastMessage.getChannel () ).thenReturn ( lastChannel );
    when ( lastChannel.isDirect () ).thenReturn ( direct );
    when ( lastCommand.getUser () ).thenReturn ( lastUser );
    when ( lastUser.getName () ).thenReturn ( "Joe" );
    when ( lastUser.getUniqID () ).thenReturn ( "123" );
    when ( lastCommand.getTransport () ).thenReturn ( lastTransport );
    when ( lastTransport.isMyName ( anyString () ) ).then ( new Answer<Object> () {
      @Override
      public Object answer ( InvocationOnMock invocation ) throws Throwable {
        return invocation.getArguments ()[0].equals ( "bot" );
      }
    } );
    return lastCommand;
  }

  private Command mockCommand ( String... words ) {
    return mockCommand ( true, words );
  }

}
