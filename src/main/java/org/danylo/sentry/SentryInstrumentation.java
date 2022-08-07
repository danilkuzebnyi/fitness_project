package org.danylo.sentry;

import io.sentry.ISpan;
import io.sentry.Sentry;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Component
public class SentryInstrumentation {
    public static Object createTransactionBoundToTheCurrentScope(Supplier<Object> method,
                                                                 Class<?> objectClass,
                                                                 String methodName) {
        ISpan span = Sentry.getSpan();
        if (span == null) {
            span = Sentry.startTransaction(objectClass.getName() + ": " + methodName, "method: " + methodName);
        }
        ISpan innerSpan = span.startChild("method: " + methodName);
        try {
            return method.get();
        } finally {
            innerSpan.finish();
        }
    }
}
