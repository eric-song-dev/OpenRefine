# Submit Three Pull Requests (Test Cases)

**Project:** [OpenRefine](https://github.com/OpenRefine/OpenRefine)

**Team members:** Kingson Zhang, Mouhamed Osman, Zhenyu Song, Zian Xu

The three pull requests below add tests to three different layers of the system:

| PR  | Subsystem                              | Module                              |
| --- | -------------------------------------- | ----------------------------------- |
| 1   | GREL expression language (math)        | `modules/grel`                      |
| 2   | Browsing / faceted filtering           | `modules/core` — `browsing.facets`  |
| 3   | JSON value coercion utility            | `main` — `expr.util`                |

---

## PR 1 — GREL `combin` math function tests

- **Title:** `test(grel): cover combin math function`
- **Pull request:** _to be filled after push: `https://github.com/OpenRefine/OpenRefine/pull/<NUMBER>`_
- **Branch on fork:** [test-combin-grel-math](https://github.com/eric-song-dev/OpenRefine/tree/test-combin-grel-math)
- **New file:** `modules/grel/src/test/java/com/google/refine/expr/functions/math/CombinTests.java`

**What the test adds.** The `combin` function implements Pascal's-identity dynamic programming for binomial coefficients, complete with an overflow-detection branch, but it had zero test coverage. The new suite pins down seven behaviors: the boundary identities (`C(n, 0)`, `C(n, n)`, and `C(0, 0)` all equal 1), known values (`C(5, 2)`, `C(10, 3)`, `C(6, 3)`), the symmetry `C(n, k) == C(n, n − k)`, the `k > n` case that returns 0 from the unfilled upper triangle of the DP table, a larger in-range value (`C(30, 15) = 155 117 520`) that exercises the full DP fill, and the `EvalError` paths for wrong arity and non-`Number` arguments. A reader can learn the function's contract just by scanning the test names.

**Why this test matters.** Of the four GREL math functions still missing tests (`Pow`, `Round`, `Combin`, `GreatestCommonDenominator`), `combin` has the richest in-house logic: a two-dimensional DP, an explicit overflow guard, and a numerical identity (symmetry) that lets the suite cross-check itself. Tests on a thinner function like `Pow` would mostly be retesting `Math.pow`. Locking the documented identities here is more valuable because future refactors of the DP loop are caught immediately, and the overflow branch becomes a property of record rather than an undocumented quirk.

---

## PR 2 — `TextSearchFacet` filter tests

- **Title:** `test(browsing): cover TextSearchFacet filter modes`
- **Pull request:** _to be filled after push: `https://github.com/OpenRefine/OpenRefine/pull/<NUMBER>`_
- **Branch on fork:** [test-text-search-facet](https://github.com/eric-song-dev/OpenRefine/tree/test-text-search-facet)
- **New file:** `modules/core/src/test/java/com/google/refine/browsing/facets/TextSearchFacetTests.java` (also creates the `facets/` test directory)

**What the test adds.** Before this PR the `browsing/facets/` test directory did not exist — none of the individual facet classes had unit tests. `TextSearchFacet` has four orthogonal axes (plain-substring vs. regex × case-sensitive vs. insensitive × invert vs. not × valid vs. invalid input), and its filter is built on top of a custom `Evaluable` plus the `ExpressionStringComparisonRowFilter` base class. The new test class builds a small in-memory `Project`, walks each axis, and asserts the exact `boolean[]` of rows that pass the filter. The suite documents three behaviors that are surprising on first reading: null cells never match a non-inverted query, an empty query short-circuits to a `null` row filter (no filtering at all), and an invalid regex throws `IllegalArgumentException` at config-apply time rather than at filter time.

**Why this test matters.** Facets are the primary way users explore and slice their data in OpenRefine, and `TextSearchFacet` is the most common one — used by every column's "Text filter…" entry. A regression in this code is silent until a user notices that search results are wrong. Other facets in the same package (`RangeFacet`, `TimeRangeFacet`) need much denser project state to test their binning logic, which makes them less unit-testable; `TextSearchFacet`'s logic is by far the cleanest entry point into the package. Adding this file also opens the `facets/` test directory, which lowers the bar for future facet tests in the project.

---

## PR 3 — `JsonValueConverter` edge-case tests

- **Title:** `test(expr): cover JsonValueConverter numeric and container edge cases`
- **Pull request:** _to be filled after push: `https://github.com/OpenRefine/OpenRefine/pull/<NUMBER>`_
- **Branch on fork:** [test-jsonvalueconverter-edge-cases](https://github.com/eric-song-dev/OpenRefine/tree/test-jsonvalueconverter-edge-cases)
- **Modified file:** `main/tests/server/src/com/google/refine/expr/util/JsonValueConverterTests.java`

**What the test adds.** `JsonValueConverter` is the utility that coerces Jackson `JsonNode` values into Java primitives or `Comparable`s during GREL expression evaluation. The existing eight tests covered `convert()` only for the common path of an `ObjectMapper`-parsed JSON tree — they never tested `convert()` on a `null` `JsonNode` reference, never exercised the `BigInteger` or `BigDecimal` branches, and never called `convertComparable()` at all. The eight new tests close those gaps: `convert(null)`, `convert` on a `BigIntegerNode` (verifies the `asLong()` coercion) and `BigDecimalNode` (verifies `asDouble()`), the full primitive surface of `convertComparable` (int, double, text, boolean), `convertComparable` on a JSON null and a Java null, and the two `IllegalArgumentException` paths for object and array nodes that the public API contract documents.

**Why this test matters.** This class sits underneath every GREL expression that touches a JSON-shaped value — reconciliation results, structured importers, and HTTP fetch responses all flow through it. The bug we are guarding against is silent type-coercion drift: someone could replace `value.asLong()` with `value.asInt()` in the `BigInteger` branch, or remove the `convertComparable` exception in favor of a silent `null`, and the original tests would not notice because no existing test constructs those node types or calls the second method. Of the small utility classes in `expr/util` that had thin coverage, this one has the most branches (ten in `convertComparable` alone) and the largest downstream blast radius. A test here protects far more callers than equivalent tests on a self-contained helper would.

---

## Reproducing locally

```sh
# PR 1 — GREL combin
mvn -pl modules/grel -am -Dtest=CombinTests -Dsurefire.failIfNoSpecifiedTests=false test

# PR 2 — TextSearchFacet
mvn -pl modules/core -Dtest=TextSearchFacetTests -Dsurefire.failIfNoSpecifiedTests=false test

# PR 3 — JsonValueConverter
mvn -pl main -am -Dtest=JsonValueConverterTests -Dsurefire.failIfNoSpecifiedTests=false test
```

All three runs report `Tests run: N, Failures: 0, Errors: 0, Skipped: 0` on the corresponding branches.
