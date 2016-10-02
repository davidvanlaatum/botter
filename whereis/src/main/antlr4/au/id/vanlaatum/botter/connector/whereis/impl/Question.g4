grammar Question;
@header {
import java.util.Date;
import org.joda.time.DateTime;
import au.id.vanlaatum.botter.connector.whereis.impl.RelativeTime;
import org.joda.time.DateTimeConstants;
}

question locals [QuestionType type = null;]:
    whereisquestion { $type = QuestionType.WHEREIS; } |
    notinquestion { $type = QuestionType.NOTIN;} ;

whereisquestion locals [String userName = null;]: whereis AT? username { $userName = $username.text; };
notinquestion locals [
    RelativeTime from = null,
    RelativeTime to = null,
    String userName = null,
    String valueReason = null;]:
    NOT IN (time)? (reason)? {
        if ( $time.ctx != null ) {
            $from = $time.value;
        } else {
            $from = new RelativeTime(TimeConstant.TODAY);
        }
        $valueReason = $reason.ctx != null ? $reason.reasonValue : "not in";
    } | AT? username IS NOT IN (time)? (reason)? {
        if ( $time.ctx != null ) {
            $from = $time.value;
        } else {
            $from = new RelativeTime(TimeConstant.TODAY);
        }
        $userName = $username.text;
        $valueReason = $reason.ctx != null ? $reason.reasonValue : "not in";
    } | AT? username IS SICK {
        $from = new RelativeTime(TimeConstant.TODAY);
        $userName = $username.text;
        $valueReason = "sick";
    } | WILL BE IN BY time {
        $from = new RelativeTime(TimeConstant.NOW);
        $to = $time.value;
        $valueReason = $text;
    };

username: WORD;
reason returns [String reasonValue] @init{
    String reasonValue = null;
}: ('am' WS)? (SICK|WORD+) {
        if($WORD != null) {
            $reasonValue = $WORD.text;
        } else {
            $reasonValue = "sick";
        }
    };

whereis: WHERE WS? IS;
time returns [RelativeTime value]
@init {
    RelativeTime value = null;
}:
    TODAY { $value = new RelativeTime(TimeConstant.TODAY); } |
    TOMORROW { $value = new RelativeTime(TimeConstant.TOMORROW); } |
    ON date { $value = new RelativeTime($date.value); } |
    ON? dow { $value = new RelativeTime($dow.value); } |
    NUM+ { $value = new RelativeTime(Integer.parseInt($text),0); } |
    NUM+ ':' NUM+ { $value = new RelativeTime(Integer.parseInt($NUM(0).getText()),Integer.parseInt($NUM(1).getText())); };
date returns [DateTime value]
 @init{
        DateTime value = null;
    }: NUM? NUM datesep NUM? NUM datesep NUM NUM NUM NUM {
        $value = DateTime.parse ( $text );
    } | NUM NUM NUM NUM datesep NUM? NUM datesep NUM? NUM {
        $value = DateTime.parse ( $text );
    };
datesep: ('/' | '-');
dow returns [ int value ] @init { int value = -1; }:
    MONDAY { $value = DateTimeConstants.MONDAY; } |
    TUESDAY { $value = DateTimeConstants.TUESDAY; } |
    WEDNESDAY { $value = DateTimeConstants.WEDNESDAY; } |
    THURSDAY { $value = DateTimeConstants.THURSDAY; } |
    FRIDAY { $value = DateTimeConstants.FRIDAY; } |
    SATURDAY { $value = DateTimeConstants.SATURDAY; } |
    SUNDAY { $value = DateTimeConstants.SUNDAY; };
TODAY: 'today' ('\''?'s')?;
TOMORROW: 'tomorrow' ('\''?'s')?;
MONDAY: 'monday' ('\''?'s')?;
TUESDAY: 'tuesday' ('\''?'s')?;
WEDNESDAY: 'wednesday' ('\''?'s')?;
THURSDAY: 'thursday' ('\''?'s')?;
FRIDAY: 'friday' ('\''?'s')?;
SATURDAY: 'saturday' ('\''?'s')?;
SUNDAY: 'sunday' ('\''?'s')?;
WHERE: 'where';
ANYLETTER: [A-Za-z];
AT: '@';
WS: [ \t\r\n]+ -> skip;
NOT: 'not';
IN: 'in';
NUM: [0-9];
ON: 'on';
IS: 'is';
SICK: 'sick';
WILL: 'will';
BE: 'be';
BY: 'by';
WORD: [a-zA-Z]+;
