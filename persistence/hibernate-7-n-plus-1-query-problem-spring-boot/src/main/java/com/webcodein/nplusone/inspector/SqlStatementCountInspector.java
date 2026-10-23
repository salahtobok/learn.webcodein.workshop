package com.webcodein.nplusone.inspector;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom Hibernate StatementInspector used to count and record executed SQL queries
 * during integration tests to detect and prevent N+1 query regressions.
 */
public class SqlStatementCountInspector implements StatementInspector {

    private static final AtomicInteger queryCount = new AtomicInteger(0);
    private static final List<String> queries = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String inspect(String sql) {
        queryCount.incrementAndGet();
        queries.add(sql);
        return sql;
    }

    public static void reset() {
        queryCount.set(0);
        queries.clear();
    }

    public static int getCount() {
        return queryCount.get();
    }

    public static List<String> getQueries() {
        return new ArrayList<>(queries);
    }
}
