# Submit Team Pull Request - Markdown Table Exporter

**Project:** [OpenRefine](https://github.com/OpenRefine/OpenRefine)

**Team members:** Kingson Zhang, Mouhamed Osman, Zhenyu Song, Zian Xu

## 1. Pull Request Link

https://github.com/OpenRefine/OpenRefine/pull/7818

## 2. Project Guidelines Link

https://openrefine.org/docs

## 3. Implementation Reflection

The implementation stayed close to the original design. The strongest part of the design was that it fit OpenRefine's Strategy and Registry exporter structure: the new format could be added as one exporter class plus small registration, menu, and translation changes.

The main adjustment is social rather than technical. The course repository contains assignment files and earlier feature work, but an upstream OpenRefine pull request should be prepared from a clean `upstream/master` branch and include only the Markdown exporter code and tests. This follows the project guideline to avoid unrelated changes.

The feature also keeps the first version intentionally small. It does not add export options for alignment, column selection, or dialect-specific Markdown behavior. That limitation keeps the implementation easy to review and consistent with the existing one-click HTML table exporter.

The targeted exporter tests passed with `mvn -pl main -Dtest=MarkdownExporterTests test`.
