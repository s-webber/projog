package org.projog.core.function.time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 %QUERY convert_time(0, X)
 %ANSWER X=1970-01-01T00:00:00.000+0000
 %TRUE convert_time(0, '1970-01-01T00:00:00.000+0000')
 
 %QUERY convert_time(1000*60*60*24*500+(1000*60*72), X)
 %ANSWER X=1971-05-16T01:12:00.000+0000
 %TRUE convert_time(1000*60*60*24*500+(1000*60*72), '1971-05-16T01:12:00.000+0000')
 
 %QUERY convert_time(9223372036854775807, X)
 %ANSWER X=292278994-08-17T07:12:55.807+0000
 */
/**
 * <code>convert_time(X,Y)</code> - converts a timestamp to a textual representation.
 */
public final class ConvertTime extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term timestamp, Term text) {
      Date d = createDate(timestamp);
      Atom a = createAtom(d);
      return text.unify(a);
   }

   private Date createDate(Term timestamp) {
      return new Date(getKnowledgeBase().getNumeric(timestamp).getLong());
   }

   private Atom createAtom(Date d) {
      // TODO have overloaded versions of convert_time that allow the date format and timezone to be specified?
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      sdf.setTimeZone(TimeZone.getTimeZone("GMT-0"));
      return new Atom(sdf.format(d));
   }
}
