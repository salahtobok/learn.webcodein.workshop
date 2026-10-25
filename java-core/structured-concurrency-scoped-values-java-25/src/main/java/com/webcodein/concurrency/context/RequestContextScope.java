package com.webcodein.concurrency.context;

import com.webcodein.concurrency.model.RequestContext;
import java.lang.ScopedValue;

public final class RequestContextScope {

    public static final ScopedValue<RequestContext> CONTEXT = ScopedValue.newInstance();

    private RequestContextScope() {
    }

    public static RequestContext get() {
        return CONTEXT.isBound() ? CONTEXT.get() : RequestContext.anonymous();
    }

    public static boolean isBound() {
        return CONTEXT.isBound();
    }

    public static <T, X extends Throwable> T callWith(RequestContext context, ScopedValue.CallableOp<T, X> op) throws X {
        return ScopedValue.where(CONTEXT, context).call(op);
    }

    public static void runWith(RequestContext context, Runnable runnable) {
        ScopedValue.where(CONTEXT, context).run(runnable);
    }
}
