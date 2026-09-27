package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

// GOOD - Clean service name (Example 9)
public class LoanService {

    // GOOD - Precise, unambiguous business verbs (Example 3)
    public void approveLoan(Loan loan) {
        loan.approve();
        // Implementation here
    }

    // GOOD - Precise, unambiguous business verbs (Example 3)
    public void rejectLoan(Loan loan) {
        loan.reject();
        // Implementation here
    }
}
