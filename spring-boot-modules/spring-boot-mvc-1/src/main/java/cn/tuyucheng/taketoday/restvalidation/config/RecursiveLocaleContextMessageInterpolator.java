package cn.tuyucheng.taketoday.restvalidation.config;

import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import javax.validation.MessageInterpolator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveLocaleContextMessageInterpolator extends AbstractMessageInterpolator {

   private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\{([^}]+)\\}");

   private final MessageInterpolator interpolator;

   public RecursiveLocaleContextMessageInterpolator(ResourceBundleMessageInterpolator interpolator) {
      this.interpolator = interpolator;
   }

   @Override
   public String interpolate(Context context, Locale locale, String message) {
      int level = 0;
      while (containsPlaceholder(message) && (level++ < 2)) {
         message = this.interpolator.interpolate(message, context, locale);
      }
      return message;
   }

   private boolean containsPlaceholder(String code) {
      Matcher matcher = PATTERN_PLACEHOLDER.matcher(code);
      return matcher.find();
   }
}