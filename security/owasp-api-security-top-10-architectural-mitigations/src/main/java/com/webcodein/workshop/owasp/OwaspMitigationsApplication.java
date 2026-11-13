package com.webcodein.workshop.owasp;

import com.webcodein.workshop.owasp.model.AccountRecord;
import com.webcodein.workshop.owasp.model.UserContext;
import com.webcodein.workshop.owasp.service.AccountManagementService;

import java.util.Optional;
import java.util.Set;

/**
 * Console application demonstrating OWASP API Security Top 10 architectural mitigations in Java 25.
 */
public class OwaspMitigationsApplication {

    public static void main(String[] args) {
        System.out.println("=== OWASP API Security Top 10: BOLA & BOPLA Mitigations Demo ===");
        AccountManagementService service = new AccountManagementService();

        UserContext alice = new UserContext("USER_ALICE", "TENANT_ALPHA", Set.of("ROLE_USER"));
        UserContext bob = new UserContext("USER_BOB", "TENANT_ALPHA", Set.of("ROLE_USER"));

        // 1. Legitimate Access
        System.out.println("\n1. Alice accesses her own account ACC_1001:");
        Optional<AccountRecord> aliceAccount = service.getAccount(alice, "ACC_1001");
        aliceAccount.ifPresent(acc -> System.out.println("   [SUCCESS] Account retrieved: " + acc.accountId() + " with balance " + acc.balance()));

        // 2. BOLA Attack: Bob attempts to access Alice's account ACC_1001
        System.out.println("\n2. Bob attempts to access Alice's account ACC_1001 (BOLA exploit attempt):");
        try {
            service.getAccount(bob, "ACC_1001");
        } catch (SecurityException e) {
            System.out.println("   [BLOCKED BY BOLA GUARD] " + e.getMessage());
        }

        // 3. BOPLA Attack: Alice attempts to tamper with her account balance via Mass Assignment
        System.out.println("\n3. Alice attempts to inject 'balance: 999999.00' in update payload (BOPLA Mass Assignment):");
        String maliciousPayload = "{\"email\":\"alice_new@alpha.corp\",\"displayName\":\"Alice In Borderland\",\"balance\":\"999999.00\"}";
        try {
            service.updateAccount(alice, "ACC_1001", maliciousPayload);
        } catch (SecurityException e) {
            System.out.println("   [BLOCKED BY BOPLA INSPECTOR] " + e.getMessage());
        }

        // 4. Legitimate Update
        System.out.println("\n4. Alice updates profile using strictly permitted fields:");
        String legitPayload = "{\"email\":\"alice_verified@alpha.corp\",\"displayName\":\"Alice Verified\"}";
        AccountRecord updated = service.updateAccount(alice, "ACC_1001", legitPayload);
        System.out.println("   [SUCCESS] Account email updated to: " + updated.email() + " | Balance unchanged: " + updated.balance());
    }
}
