grammar Question;
@header {
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
}

question locals [QuestionType type = null;]:
    whereisquestion { $type = QuestionType.WHEREIS; } |
    notinquestion { $type = QuestionType.NOTIN;} ;

whereisquestion locals [String userName = null;]: whereis WS AT? username { $userName = $username.text; };
notinquestion locals [
    Date from = null,
    TimeConstant fromType = null,
    int fromDow = -1,
    Date to = null,
    String userName = null,
    String valueReason = null;]:
    NOT WS IN (WS time)? (WS reason)? {
        if ( $time.ctx != null ) {
            $from = $time.valueDate;
            $fromType = $time.value;
            $fromDow = $time.valueDow;
        } else {
            $fromType = TimeConstant.TODAY;
        }
        $valueReason = $reason.ctx != null ? $reason.text : "not in";
    } | AT? username WS IS WS NOT WS IN (WS time)? (WS reason)? {
        if ( $time.ctx != null ) {
            $from = $time.valueDate;
            $fromType = $time.value;
            $fromDow = $time.valueDow;
        } else {
            $fromType = TimeConstant.TODAY;
        }
        $userName = $username.text;
        $valueReason = $reason.ctx != null ? $reason.text : "not in";
    } | AT? username WS IS WS SICK {
        $fromType = TimeConstant.TODAY;
        $userName = $username.text;
        $valueReason = "sick";
    };

username: ANYLETTER+;
reason: ANYTHING+;

whereis: WHEREIS;
time returns [TimeConstant value, Date valueDate, int valueDow]
@init {
    TimeConstant value = null;
    Date valueDate = null;
    int valueDow = -1;
}:
    TODAY { $value = TimeConstant.TODAY; $valueDow = -1; } |
    TOMORROW { $value = TimeConstant.TOMORROW; $valueDow = -1; } |
    ON WS date { $value = TimeConstant.DATE; $valueDate = $date.value; $valueDow = -1; } |
    ON WS dow { $value = TimeConstant.DOW; $valueDow = $dow.value; };
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
dow returns [ int value ] @init { int value = -1; }:
    MONDAY { $value = Calendar.MONDAY; } |
    TUESDAY { $value = Calendar.TUESDAY; } |
    WEDNESDAY { $value = Calendar.WEDNESDAY; } |
    THURSDAY { $value = Calendar.THURSDAY; } |
    FRIDAY { $value = Calendar.FRIDAY; } |
    SATURDAY { $value = Calendar.SATURDAY; } |
    SUNDAY { $value = Calendar.SUNDAY; };
TODAY: 'today' ('\''?'s')?;
TOMORROW: 'tomorrow' ('\''?'s')?;
MONDAY: 'monday' ('\''?'s')?;
TUESDAY: 'tuesday' ('\''?'s')?;
WEDNESDAY: 'wednesday' ('\''?'s')?;
THURSDAY: 'thursday' ('\''?'s')?;
FRIDAY: 'friday' ('\''?'s')?;
SATURDAY: 'saturday' ('\''?'s')?;
SUNDAY: 'sunday' ('\''?'s')?;
WHEREIS: 'where is';
ANYLETTER: [A-Za-z];
AT: '@';
WS: [ \t\r\n]+;
NOT: 'not';
IN: 'in';
NUM: [0-9];
ON: 'on';
ANYTHING: .;
IS: 'is';
SICK: 'sick';
