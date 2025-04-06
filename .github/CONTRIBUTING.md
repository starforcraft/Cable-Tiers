# Contributing

## When submitting pull requests

* Split up multiple unrelated changes in multiple pull requests.

* If it's a fix for an unreported bug, make a bug report and link the pull request.

* Commits serve a clear purpose and have a fitting commit message.

## Formatting

### Follow the current syntax design

* Indent type: Tabs

* Tab size: 4

* Opening curly braces `{` at the end of the same line as the statement/condition.

* We use [Checkstyle](https://checkstyle.sourceforge.io/) in our build workflow to validate coding style.
It is recommended to import the [config/checkstyle/checkstyle.xml](../config/checkstyle/checkstyle.xml)
or [config/intellij-code-style.xml](../config/intellij-code-style.xml) file into your
IDE, so that formatting rules are respected.
Moreover, the [CheckStyle-IDEA plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) can be used to check
if there are no style violations.

## General guidelines

* Don't force a programming style. Use object-oriented, functional, data oriented, etc., where it's suitable.

* Use descriptive names for variables.

* Use comments if not very obvious what your code is doing.

* If using the logger functions, be sensible, only call it if something of importance has changed.

* Benchmark your code and look for alternatives if they cause a noticeable negative impact.

## License

* Code submitted to this project shall fall under the same license as the project.

* Any code taken from another existing project should maintain the license that comes it AND follow the terms of that license.