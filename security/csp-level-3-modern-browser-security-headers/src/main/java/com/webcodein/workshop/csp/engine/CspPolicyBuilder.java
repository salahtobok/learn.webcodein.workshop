package com.webcodein.workshop.csp.engine;

import com.webcodein.workshop.csp.model.CspDirective;
import com.webcodein.workshop.csp.model.CspSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CspPolicyBuilder {

    private final Map<CspDirective, List<CspSource>> directives = new EnumMap<>(CspDirective.class);

    public CspPolicyBuilder addDirective(CspDirective directive, CspSource... sources) {
        Objects.requireNonNull(directive, "directive must not be null");
        directives.computeIfAbsent(directive, d -> new ArrayList<>()).addAll(List.of(sources));
        return this;
    }

    public static CspPolicyBuilder strictLevel3Policy(String nonce) {
        Objects.requireNonNull(nonce, "nonce must not be null");
        return new CspPolicyBuilder()
                .addDirective(CspDirective.DEFAULT_SRC, CspSource.self())
                .addDirective(
                        CspDirective.SCRIPT_SRC,
                        CspSource.nonce(nonce),
                        CspSource.strictDynamic(),
                        CspSource.unsafeInline(),
                        CspSource.https()
                )
                .addDirective(CspDirective.OBJECT_SRC, CspSource.none())
                .addDirective(CspDirective.BASE_URI, CspSource.none())
                .addDirective(CspDirective.FORM_ACTION, CspSource.self())
                .addDirective(CspDirective.FRAME_ANCESTORS, CspSource.none());
    }

    public String build() {
        return directives.entrySet().stream()
                .map(entry -> {
                    String directiveName = entry.getKey().headerName();
                    String sources = entry.getValue().stream()
                            .map(CspSource::value)
                            .collect(Collectors.joining(" "));
                    return sources.isEmpty() ? directiveName : directiveName + " " + sources;
                })
                .collect(Collectors.joining("; "));
    }
}
