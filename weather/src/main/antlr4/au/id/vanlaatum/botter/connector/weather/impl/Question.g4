grammar Question;

@header {
import au.id.vanlaatum.botter.connector.weather.impl.TimeConstant;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
}

question locals [QuestionType type = null;]:
    wetherquestion { $type = QuestionType.WEATHER; } |
    setlocation { $type = QuestionType.SETLOCATION;};

setlocation returns [String city, String country]
               @init{
                   String city = null;
                   String country = null;
               }: (SET | UPDATE) WS MY WS LOCATION WS TO WS location {
         $city = $location.city;
         $country = $location.country;
    };
wetherquestion locals [
        Subject valueSubject = null,
        Date valueDate = null,
        TimeConstant valueTime = null,
        String city = null,
        String country = null,
        int valueDow = -1;
    ]:
    whatis WS THE WS valueType=subject (WS LIKE)? (WS? time)? (WS IN WS valueLocation=location)? QMARK? EOF {
        $valueSubject = $valueType.value;
        if ( $time.ctx != null ) {
            $valueTime = $time.value;
            $valueDate = $time.valueDate;
            $valueDow = $time.valueDow;
        } else {
            $valueDow = -1;
        }
        $city = $valueLocation.ctx != null ? $valueLocation.city : null;
        $country = $valueLocation.ctx != null ? $valueLocation.country : null;
    } |
    whatis WS THE WS valueType=subject (WS LIKE)? (WS IN WS valueLocation=location) (WS? time) QMARK? EOF {
        $valueSubject = $valueType.value;
        if ( $time.ctx != null ) {
            $valueTime = $time.value;
            $valueDate = $time.valueDate;
            $valueDow = $time.valueDow;
        } else {
             $valueDow = -1;
        }
        $city = $valueLocation.ctx != null ? $valueLocation.city : null;
        $country = $valueLocation.ctx != null ? $valueLocation.country : null;
    }|
    whatis WS time WS valueType=subject (WS LIKE)? (WS IN WS valueLocation=location)? QMARK? EOF {
        $valueSubject = $valueType.value;
        $valueTime = $time.value;
        $valueDate = $time.valueDate;
        $valueDow = $time.valueDow;
        $city = $valueLocation.ctx != null ? $valueLocation.city : null;
        $country = $valueLocation.ctx != null ? $valueLocation.country : null;
    };
location
    returns [String city, String country]
    @init{
        String city = null;
        String country = null;
    }:
    valueCity=name (',' WS? valueCountry=name)? {
        $city = $valueCity.text;
        $country = $valueCountry.text;
    };
name: word | word WS name;
whatis: WHAT WS IS | WHATS;
subject returns [Subject value]
@init {
    Subject value = null;
}:
    TEMPERATURE { $value = Subject.TEMPERATURE; }|
    WEATHER { $value = Subject.WEATHER; };
time returns [TimeConstant value, Date valueDate, int valueDow]
@init {
    TimeConstant value = null;
    Date valueDate = null;
    int valueDow = -1;
}:
    TODAY { $value = TimeConstant.TODAY; $valueDow = -1; } |
    FOR WS TODAY { $value = TimeConstant.TODAY; $valueDow = -1; } |
    TOMORROW { $value = TimeConstant.TOMORROW; $valueDow = -1; } |
    FOR WS TOMORROW { $value = TimeConstant.TOMORROW; $valueDow = -1; } |
    FOR WS date { $value = TimeConstant.DATE; $valueDate = $date.value; $valueDow = -1; } |
    ON WS date { $value = TimeConstant.DATE; $valueDate = $date.value; $valueDow = -1; } |
    FOR WS dow { $value = TimeConstant.DOW; $valueDow = $dow.value; } |
    ON WS dow { $value = TimeConstant.DOW; $valueDow = $dow.value; };
dow returns [ int value ] @init { int value = -1; }:
    MONDAY { $value = Calendar.MONDAY; } |
    TUESDAY { $value = Calendar.TUESDAY; } |
    WEDNESDAY { $value = Calendar.WEDNESDAY; } |
    THURSDAY { $value = Calendar.THURSDAY; } |
    FRIDAY { $value = Calendar.FRIDAY; } |
    SATURDAY { $value = Calendar.SATURDAY; } |
    SUNDAY { $value = Calendar.SUNDAY; };
date returns [Date value]
 @init{
        Date value = null;
    }: NUM? NUM datesep NUM? NUM datesep NUM NUM NUM NUM {
        SimpleDateFormat sdf = new SimpleDateFormat("dd" + $datesep.text + "MM" + $datesep.text + "yyyy");
        try {
            $value = sdf.parse($text);
        } catch ( ParseException e ) {
            throw new InputMismatchException ( this );
        }
    } | NUM NUM NUM NUM datesep NUM? NUM datesep NUM? NUM {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + $datesep.text + "MM" + $datesep.text + "dd");
        try {
            $value = sdf.parse($text);
        } catch ( ParseException e ) {
            throw new InputMismatchException ( this );
        }
    };
datesep: ('/' | '-');
word: ANYLETTER+;
UPDATE: 'update';
IN: 'in';
WHAT: 'what';
IS: 'is';
WHATS: 'whats';
SET: 'set';
MY: 'my';
LOCATION: 'location';
TO: 'to';
TEMPERATURE: ('temperature' | 'temp');
WEATHER: 'weather';
QMARK: '?';
TODAY: 'today' ('\''?'s')?;
TOMORROW: 'tomorrow' ('\''?'s')?;
MONDAY: 'monday' ('\''?'s')?;
TUESDAY: 'tuesday' ('\''?'s')?;
WEDNESDAY: 'wednesday' ('\''?'s')?;
THURSDAY: 'thursday' ('\''?'s')?;
FRIDAY: 'friday' ('\''?'s')?;
SATURDAY: 'saturday' ('\''?'s')?;
SUNDAY: 'sunday' ('\''?'s')?;
NUM: [0-9];
WS: [ \t\r\n]+;
FOR: 'for';
ON: 'on';
THE: 'the';
LIKE: 'like';
ANYLETTER: [A-Za-z];
