# Argo — Business Rules Register (BR-001 to BR-226)

**Project:** Argo Maritime ERP
**As of:** May 2026, Epic 9 (Invoicing & Three-Way Match) closing
**Epics covered:** 1 (Vessel Registry) through 9 (Invoicing)

> **Source note:** Rules BR-001 to BR-036 are reproduced verbatim from the repository's `business_rules` resource files. BR-037 to BR-063 are reconstructed from the handoff summary (the verbatim text for certificates and operational hardening was not all available; entries marked † are summary-level and should be confirmed against source if exact wording matters). BR-064 onward are from the working register maintained across our sessions.

---

## Epic 1 — Vessel Registry

**BR-001:** IMO number is mandatory and globally unique. Format: 7 digits. The primary business identifier for a vessel.
*Rationale: IMO numbers are assigned by the International Maritime Organization and never change for the life of a vessel.*

**BR-002:** Vessel name is mandatory. Vessels can be renamed but must always have a name on record.

**BR-003:** MMSI number, if provided, must be exactly 9 digits and unique.
*Rationale: Maritime Mobile Service Identity is used for radio communications and vessel tracking.*

**BR-004:** Flag state is mandatory, recorded as ISO 3166-1 alpha-3 country code.
*Rationale: Determines which country's maritime regulations apply.*

**BR-005:** Gross tonnage must be a positive number if provided.
*Rationale: Used for port fees, safety regulations, and manning requirements.*

**BR-006:** Default page size is 20, maximum 100.
*Rationale: Balances usability with performance for fleets of hundreds of vessels.*

**BR-007:** Search by IMO is always exact match.
*Rationale: IMO numbers are precise identifiers — partial matching creates confusion.*

**BR-008:** IMO number is immutable after vessel creation.
*Rationale: Permanent identifiers; changing them breaks data integrity across the system.*

**BR-009:** Optimistic locking must be enforced. Updates fail with a clear conflict error if the vessel was modified since load.
*Rationale: In a multi-user environment, silent overwrites cause data loss.*

**BR-010:** Vessels are never hard-deleted.
*Rationale: Vessels have regulatory and financial history that must be preserved.*

**BR-011:** Deactivating a vessel does not affect related historical records.
*Rationale: Past data remains valid regardless of current vessel status.*

**BR-012:** Only INACTIVE vessels can be reactivated.
*Rationale: Prevents accidental state changes and keeps the status lifecycle clean.*

**BR-013:** Vessel name uniqueness is a formal business rule. †
*Rationale: Adopted during the project as a formal rule (referenced in working notes).*

---

## Epic 2 — Crew Registry (Seafarers)

**BR-014:** First name and last name are mandatory.
*Rationale: Legal identification requirement for maritime documentation.*

**BR-015:** Passport number is mandatory and unique.
*Rationale: Primary identification for international seafarers; required for port state control and immigration.*

**BR-016:** Passport expiry date must be in the future at registration.
*Rationale: Seafarers cannot be deployed with expired travel documents.*

**BR-017:** Nationality is mandatory, recorded as ISO 3166-1 alpha-3.
*Rationale: Determines applicable labor conventions and visa requirements.*

**BR-018:** Seaman book number, if provided, must be unique.
*Rationale: An official maritime document, individually assigned.*

**BR-019:** Seaman book expiry date, if provided, must be in the future.
*Rationale: Same logic as passport — expired documents prevent deployment.*

**BR-020:** Rank is mandatory.
*Rationale: Determines what positions the seafarer can fill; critical for manning compliance.*

**BR-021:** Date of birth, if provided, must be in the past.
*Rationale: Basic data integrity.*

**BR-022:** Search by passport number is always exact match.
*Rationale: Precise identifiers — same reasoning as IMO search.*

**BR-023:** Rank changes are tracked by overwriting current rank for now; formal promotion history deferred.
*Rationale: Documenting the limitation explicitly.*

**BR-024:** Deactivating a seafarer does not affect historical records.
*Rationale: Past data stays valid.*

**BR-025:** A seafarer cannot be reactivated if their passport has expired; the passport must be updated first.
*Rationale: Prevents reactivating crew who cannot legally be deployed.*

---

## Epic 3 — Crew-to-Vessel Assignments

**BR-026:** A seafarer can have at most one active assignment at any time (active = no actual sign-off date recorded).
*Rationale: A person cannot be on two vessels simultaneously; also a regulatory requirement.*

**BR-027:** The assignment rank may differ from the seafarer's current rank.
*Rationale: Operational flexibility — records the role actually being performed.*

**BR-028:** Only ACTIVE vessels can receive crew assignments.
*Rationale: Cannot staff a vessel out of service.*

**BR-029:** Only ACTIVE seafarers can be assigned to vessels.
*Rationale: Inactive seafarers are not available for deployment.*

**BR-030:** Sign-on date is mandatory.
*Rationale: Every assignment needs a clear start date for regulatory, payroll, and operational tracking.*

**BR-031:** Sign-off date must be on or after the sign-on date.
*Rationale: A seafarer cannot leave before they boarded.*

**BR-032:** Sign-off is a one-time action; completed assignments cannot be modified.
*Rationale: Historical records used for regulatory reporting and payroll.*

**BR-033:** Signing off does not change the seafarer's status; they remain ACTIVE.
*Rationale: Completing a voyage is normal operations, not a status change.*

**BR-034:** The crew roster shows only active assignments (actual sign-off date is null).
*Rationale: The roster answers "who is on board right now."*

**BR-035:** Only ACTIVE assignments can be cancelled.
*Rationale: Regulatory and payroll compliance.*

**BR-036:** Cancellation is a soft status change, not a delete.
*Rationale: Traceability — the action trail stays visible.*

---

## Epic 4 — Document & Certificate Management †

**BR-037:** Certificates belong to either a person or a vessel (discriminated subtypes `PersonCertificateT` / `VesselCertificateT`). †

**BR-038:** Certificate number, issuing authority, issue date, and expiry date are recorded per certificate. †

**BR-039:** Certificate type is mandatory and drawn from a fixed enumeration per holder type (person vs vessel). †

**BR-040:** Certificate status is computed from expiry date: VALID, EXPIRING_SOON, or EXPIRED. †

**BR-041:** Expiry status is computed at read time, not stored. †
*Rationale: Always-correct; avoids stale state and scheduled-sweep complexity.*

**BR-042:** EXPIRING_SOON threshold is 90 days before expiry (global threshold). †
*Rationale: Standard maritime lead time for certificate renewal.*

**BR-043:** Certificate update enforces optimistic locking. †

**BR-044:** Certificate management (update) restricted to `FOM` role. †

**BR-045–049:** (Additional certificate / overview rules — confirm against source if exact text needed.) †

---

## Epic 5 — Operational Hardening †

**BR-045:** A vessel with active crew assignments cannot be deactivated. †
*Rationale: Cross-domain integrity — can't retire a vessel that's currently crewed.*

**BR-046:** A seafarer with an active assignment cannot be deactivated. †
*Rationale: Can't retire crew who are currently deployed.*

**BR-047:** Detail responses surface dependency counts (e.g., active assignments, certificates). †
*Rationale: Operators need to see why an action is blocked.*

**BR-048:** Certificate expiry warnings surface on relevant detail responses. †

**BR-049:** Dependency counts appear on detail responses only, not list/summary responses. †
*Rationale: Too expensive to compute at list-query time.*

> *Epics 4 and 5 rules are reconstructed from summary. The exact BR numbering and wording in this range should be reconciled against the repository's `business_rules` files for certificates and operational hardening when precision is needed.*

---

## Epic 6 — Procurement & Requisitions

### Sprint 6 — Item Catalog Foundation

**BR-050:** Item categories are fixed in the system (not user-configurable).

**BR-051:** Units of measure are fixed in the system.

**BR-052:** Item name mandatory, max 200 chars.

**BR-053:** Item code is system-generated, pattern `<CATEGORY_PREFIX>-<SEQUENCE>`, immutable.
*Rationale: Stable, human-readable catalog identifier.*

**BR-054:** Part number optional, unique per manufacturer when provided.

**BR-055:** Manufacturer is free-text (max 100 chars). No manufacturer master entity — deliberately scoped out; `SupplierT` reserved for Epic 7.

**BR-056:** Default unit of measure mandatory on every catalog item.

**BR-057:** Role `PROCUREMENT_MANAGER` governs catalog management.

**BR-058:** Item code search is exact match.

**BR-059:** Part number search is exact match.

**BR-060:** Item code and category immutable after creation.

**BR-061:** Deactivated items can't be added to new requisitions; historical references preserved.

**BR-062:** Inactive items can be reactivated without consistency checks.

**BR-063:** Default unit of measure is mutable; existing requisition lines snapshot at creation and are unaffected.

**BR-064:** Part number requires accompanying manufacturer (cross-field rule).

### Sprint 7 — Requisition Workflow (base)

**BR-065:** Requisition number format `REQ-YYYY-NNNNNN` (year-scoped sequence).

**BR-066:** Requisition has a type — `VESSEL` (with target vessel) or `OFFICE` (no target vessel). Type is immutable.

**BR-067:** Requisition raised by exactly one `PersonT`, immutable after creation.

**BR-068:** Requisition must have at least one line item.

**BR-069:** Creation restricted to `FOM` and `PROCUREMENT_CLERK` roles.

**BR-070:** Priority levels: LOW, NORMAL, HIGH, URGENT (default NORMAL).

**BR-071:** Required-by date must not be in the past.

**BR-072:** Line item quantity must be > 0.

**BR-073:** Submission re-validates all references are ACTIVE.

**BR-074:** `submittedAt` / `submittedBy` (username string) — distinct from `raisedBy` and `createdBy`.

**BR-075:** APPROVED, FINALIZED, REJECTED, CANCELLED are terminal states.

**BR-076:** Approver/rejecter must differ from `createdBy` and `submittedBy` (separation of duties, by username).

**BR-077:** Rejection reason mandatory; approval remarks optional.

**BR-078:** Role `PROCUREMENT_APPROVER` covers both VESSEL and OFFICE approvals.

**BR-079:** Requisition number search is exact match.

**BR-080:** Historical lines preserved despite catalog deactivation.

**BR-081:** Only DRAFT requisitions can be cancelled; SUBMITTED go through rejection.

**BR-082:** CANCELLED is terminal.

**BR-083:** OFFICE requisitions must not have a target vessel.

**BR-084:** Cancellation flips line statuses to INACTIVE.

### Sprint 7 — Sequential Ladder Approval (Story 6.11)

**BR-085:** Each approval must come from a strictly more senior user than the previous approver in the ladder.

**BR-086 (revised):** To reject a requisition with at least one prior approval, the rejecter's level must be ≥ the most recent approver's level. Rejection from SUBMITTED (no prior approvals) is unrestricted.
*Rationale: A junior approver should not overturn a senior's endorsement; juniors escalate concerns to seniors who reject.*

**BR-087:** New/existing users default to `LEVEL_1`.

**BR-088:** Approval history is append-only and snapshots actor level at action time.

**BR-089:** First approver must be at least `LEVEL_2`; level-1 users cannot approve any requisition.

**BR-090 (revised):** The same user cannot perform two consecutive approvals; the level-monotonicity rule (BR-085) is the enforcing mechanism. The promotion-between-actions edge case is explicitly out of scope for Epic 6.

---

## Epic 7 — Procurement: Suppliers, Quotations & Purchase Orders

### Sprint 8 — Supplier Master Data

**BR-091:** Supplier company name is mandatory, max 200 chars.

**BR-092:** Supplier email is mandatory, valid format, unique.

**BR-093:** Supplier VAT number is mandatory and unique.

**BR-094:** Supplier contact person is mandatory.

**BR-095:** Supplier phone and address are mandatory.

**BR-096:** Suppliers are created in ACTIVE state by default.

**BR-097:** Only `PROCUREMENT_MANAGER` can manage suppliers (write operations).

**BR-098:** Supplier VAT number is immutable after creation.

**BR-099:** Inactive suppliers cannot be modified.

**BR-100:** Email uniqueness enforced on update as well as create.

**BR-101:** Supplier company name search is case-insensitive partial match.

**BR-102:** Supplier VAT and email searches are exact match.

**BR-103:** Supplier read access is unrestricted (any authenticated user).

**BR-104:** Suppliers are never hard-deleted.

**BR-105:** Inactive suppliers cannot be selected for new POs.

**BR-106:** Deactivated suppliers can be reactivated without revalidation.

### Sprint 9 — Quotations

**BR-107:** A quotation is bound to exactly one requisition line and one supplier.

**BR-108:** Quotation unit price must be > 0 and a currency must be set.

**BR-109:** Quotation `validUntil` date must be on or after creation date.

**BR-110:** Quotation state transitions (ACCEPTED, REJECTED) are terminal and capture actor + timestamp.

**BR-111:** Quotations can only be recorded against ACTIVE suppliers.

**BR-112:** Quotations can be recorded only against requisition lines whose parent requisition is FINALIZED.

**BR-113:** Multiple quotations per (requisition line, supplier) pair are allowed.

**BR-114:** Bulk quotation entries from one supplier share currency and validity-end date.

**BR-115:** Bulk quotation entries must reference lines from a single requisition.

**BR-116:** Bulk creation is atomic (all-or-nothing).

**BR-117:** Quotation listings include all states by default.

**BR-118:** Expired-but-not-rejected quotations are surfaced as EXPIRED in the comparison view, distinct from REJECTED.

**BR-119:** RECEIVED → ACCEPTED requires `validUntil` ≥ today.

**BR-120:** RECEIVED → REJECTED is allowed regardless of expiry.

**BR-121:** Rejection requires a reason.

**BR-122:** ACCEPTED and REJECTED are terminal states.

**BR-123:** Multiple quotations per line may be accepted (multi-supplier split); total accepted quantity validated at PO creation.

**BR-124:** Expiry is computed at read time, not stored as a state.

**BR-125:** No automatic rejection of expired quotations.

### Sprint 10 — Purchase Order Workflow

**BR-126:** PO number `PO-YYYY-NNNNNN`, year-scoped sequence with pessimistic locking.

**BR-127:** PO type (`STANDARD` / `URGENT`) is immutable after creation.

**BR-128:** PO type defaults to STANDARD.

**BR-129:** Each PO line is anchored to exactly one accepted quotation and one requisition line.

**BR-130:** PO line item details are snapshotted at creation; immune to catalog or requisition-line drift.

**BR-131:** A PO carries a single currency at the PO level; all lines share it.

**BR-132:** A PO is created from quotations belonging to a single supplier and a single requisition.

**BR-133 (revised):** A quotation can back at most one active PO line. Enforced in application code via `QuotationT.isLockedToActivePO()`, which checks whether any consuming PO line has a parent PO in a non-CANCELLED state. (DB-level unique constraint removed to allow reuse after cancellation while preserving the historical link.)

**BR-134:** 3-quote policy tiered by line total value: < 500 no enforcement; 500–10,000 soft rule (3 quotes OR justification override); > 10,000 hard rule (3 quotes required, URGENT bypasses).

**BR-135 (revised):** URGENT POs bypass the hard 3-quote rule (Tier 3) at PO creation. Bypass applies only to the quote-count rule, not to the audit requirement.

**BR-135a:** URGENT POs require a non-blank `justificationNotes` field at creation, regardless of tier.
*Rationale: URGENT is a compliance exception; the system must capture why.*

**BR-136:** Justification mandatory when soft rule is overridden, max 500 chars, persisted on the PO.

**BR-137:** Quotations must be valid (`validUntil` ≥ today) at PO creation.

**BR-138:** Supplier must be ACTIVE at PO creation, re-validated.

**BR-139:** `sentAt` immutable after SENT transition.

**BR-140:** Sending a PO re-checks supplier is ACTIVE.

**BR-141:** Supplier acknowledgement reference, when provided, is immutable.

**BR-142:** Acknowledgement is a deliberate clerk action, not implicit.

**BR-143 (revised):** PO line prices are immutable after creation regardless of PO type. All POs inherit price from their source accepted quotation; URGENT does not defer pricing in Argo's model (a quote is always required).

**BR-144:** STANDARD PO line prices are immutable. (Subsumed by revised BR-143.)

**BR-145:** PO total amount recomputes on any line price change.

**BR-146:** A PO cannot CLOSE while any line has null `unitPrice`.

**BR-147:** CLOSED is terminal (within Epic 7 scope; Epic 8 extends the state machine before CLOSED).

**BR-148:** Cancellation allowed only from DRAFT or SENT.

**BR-149:** Cancellation reason mandatory.

**BR-150 (revised):** Cancelling a PO releases its source quotations for reuse. The PO transitions to CANCELLED; its lines and quotation references remain intact. The lock check (BR-133) filters out cancelled-PO lines, so released quotations become selectable again. No fields nulled, no records deleted.

**BR-151:** CANCELLED is terminal.

**BR-152:** PO read access unrestricted for authenticated users.

**BR-153:** PO number search is exact match.

---

## Epic 8 — Goods Receipt

### Sprint 11 — Goods Receipt Workflow

**BR-154:** Goods receipt number `GR-YYYY-NNNNNN`, year-scoped sequence with pessimistic locking.

**BR-155:** A receipt belongs to one PO; a receipt line belongs to one PO line.

**BR-156:** Receipt date is clerk-supplied and may differ from the system timestamp.

**BR-157:** Receipt condition defaults to `OK`.

**BR-158:** PO state machine extended: ACKNOWLEDGED → PARTIALLY_RECEIVED → FULLY_RECEIVED → CLOSED.

**BR-159:** Requisition state machine extended with FULFILLED after FINALIZED.

**BR-160:** Receipts only against POs in ACKNOWLEDGED, PARTIALLY_RECEIVED, or FULLY_RECEIVED state.

**BR-161:** Receipt lines must reference PO lines belonging to the specified PO header.

**BR-162:** Each PO line may appear at most once per receipt.

**BR-163:** Each receipt line carries a `ReceiptLineFlagEnum` (`UNDER_RECEIVED` / `WELL_RECEIVED` / `OVER_RECEIVED`) computed from cumulative received vs ordered quantity.

**BR-164:** `receivedQuantity` must be > 0.

**BR-165:** `receiptDate` cannot be in the future.

**BR-166:** PO state computed from cumulative non-cancelled receipts; transitions automatic.

**BR-167:** DAMAGED and WRONG_ITEM receipts count toward `receivedQuantity`.
*Rationale: Damage is a downstream concern; the goods physically arrived.*

**BR-168:** OVER_RECEIVED lines do not block FULLY_RECEIVED transition.

**BR-169:** PO close requires FULLY_RECEIVED state (replaces Sprint 10's ACKNOWLEDGED gate).

**BR-170:** Requisition transitions to FULFILLED when every line is fully received via at least one non-cancelled PO (line-level check).

**BR-171:** FULFILLED is terminal; receipt corrections do not revert it.

**BR-172:** Cancelled POs and cancelled receipts do not contribute to FULFILLED computation.

**BR-173:** Goods receipt read access unrestricted for authenticated users.

**BR-174:** Cancelled receipts appear in listings by default.

**BR-175:** Receipts are cancelled, not edited; correction is cancel-and-recreate.

**BR-176:** Cancellation reason mandatory.

**BR-177:** Receipt cancellation triggers PO state recomputation.

**BR-178:** Receipt cancellation does NOT trigger requisition recomputation if already FULFILLED.

**BR-179:** CANCELLED is terminal for goods receipts.

---

## Epic 9 — Invoicing & Three-Way Match

### Sprint 12 — Invoice Capture & Three-Way Match

**BR-180:** Invoice number `INV-YYYY-NNNNNN`, year-scoped sequence with pessimistic locking.

**BR-181:** Supplier invoice reference mandatory, recorded separately from Argo's number.

**BR-182:** `purchaseOrder` reference nullable at creation; mandatory before exiting RECEIVED.

**BR-183:** Invoice `totalAmount` must equal the sum of line totals.

**BR-184:** Invoice currency must equal PO currency when matched.

**BR-185:** Invoice lines' `purchaseOrderLine` reference set during matching, not creation.

**BR-186:** Supplier invoice reference unique per supplier.

**BR-187:** No invoicing against DRAFT, SENT, or CANCELLED POs.

**BR-188:** Invoicing against CLOSED POs is allowed.

**BR-189:** Match engine runs automatically when PO is provided at creation.

**BR-190:** PO association is immutable once set.

**BR-191:** Clerks may provide explicit line-to-line mappings; engine fills gaps via order-index heuristic.

**BR-192:** Three-way match: invoice price vs PO price, invoice quantity vs cumulative non-cancelled receipt quantity.

**BR-193:** Tolerance thresholds are percentages, not absolute values (price ±2%, quantity ±5% default; configurable via application properties).

**BR-194:** No receipts → quantity matching = UNMATCHED.

**BR-195:** Any line with `matchStatus != MATCHED` → invoice = DISPUTED.

**BR-196:** Match engine is deterministic given identical input.

**BR-197:** Invoice read access unrestricted for authenticated users.

**BR-198:** Cancelled invoices appear in listings by default.

**BR-199:** Manual match override requires `PROCUREMENT_MANAGER` role.

**BR-200:** Manual override requires written justification, persisted.

**BR-201:** Manual override only available for DISPUTED invoices.

**BR-202:** APPROVED and PAID invoices cannot be cancelled.

**BR-203:** Invoices are cancelled, not edited; correction is cancel-and-recreate.

**BR-204:** Cancellation reason mandatory.

**BR-205:** CANCELLED is terminal.

**BR-206:** Discrepancy summary aggregates at line level.

**BR-207:** Cross-currency aggregation not performed.

### Sprint 13 — Invoice Approval & Payment

**BR-208:** Invoice approval requires the `FINANCE_APPROVER` role.

**BR-209:** Approver cannot be the same user who created the invoice (segregation of duties).

**BR-210:** MATCHED invoices are approvable without further action.

**BR-211:** DISPUTED invoices are approvable by FINANCE_APPROVER without prior override or correction.

**BR-212:** Approval timestamp and approver identity persisted.

**BR-213 (revised):** Rejection transitions the invoice to a distinct `REJECTED` state (not DISPUTED).
*Rationale: A finance rejection is operationally distinct from a match-engine discrepancy; a separate state makes the queue clearer.*

**BR-214:** Rejection requires a written reason.

**BR-215 (revised):** Rejection is reversible — a `REJECTED` invoice can be approved by a FINANCE_APPROVER once the underlying concern is addressed. (`REJECTED` is an accepted input state for the approval transition.)

**BR-216:** Only APPROVED invoices can transition to PAID.

**BR-217:** Payment reference mandatory and unique per supplier.

**BR-218:** Payment date can be backdated but not future-dated.

**BR-219:** Payment recording requires `FINANCE_APPROVER` role.

**BR-220:** PAID is terminal.

**BR-221:** Bulk approval restricted to MATCHED invoices only. *(Story 9.12 — deferred to backlog; rule recorded for when implemented.)*

**BR-222:** Bulk approval limited to 50 invoices per call. *(Deferred with Story 9.12.)*

**BR-223:** Bulk approval is all-or-nothing. *(Deferred with Story 9.12.)*

**BR-224:** "Outstanding" = any invoice not in PAID or CANCELLED state.

**BR-225:** "Overdue" computed against today's date and `dueDate`.

**BR-226:** Outstanding view aggregates per currency, not globally.

---

## Notes on Deviations and Revisions

Several rules were revised mid-project as the design evolved. The most significant:

- **BR-086 / BR-090** — revised during Sprint 7 when the developer chose level-based rejection thresholds and level-monotonicity for double-action prevention.
- **BR-133 / BR-150** — revised during Sprint 10 when the quotation lock moved from a DB unique constraint to an application-level cancellation-aware check.
- **BR-135 / BR-143 / BR-135a** — revised during Sprint 10 when URGENT POs were defined to inherit price from quotations (rather than defer pricing) and to require justification.
- **BR-163** — the `ReceiptLineFlagEnum` was implemented as a three-value enum (UNDER/WELL/OVER_RECEIVED) rather than the originally-specced binary flag — developer's call, adopted as the rule.
- **BR-213 / BR-215** — revised during Sprint 13 when the developer introduced a distinct `REJECTED` invoice state; reversibility confirmed by making REJECTED an approvable input state.

## Deferred (rules recorded but feature not yet implemented)

- **BR-221, BR-222, BR-223** — bulk invoice approval (Story 9.12, Should-Have, deferred to backlog).

---

*End of register. Current high-water mark: BR-226. Next rule: BR-227.*
